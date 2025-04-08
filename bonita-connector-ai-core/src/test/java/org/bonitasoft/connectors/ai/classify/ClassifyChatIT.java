/**
 * Copyright (C) 2025 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.connectors.ai.classify;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.connectors.utils.IOs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public abstract class ClassifyChatIT {

    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    ClassifyChat chat;

    @BeforeEach
    void setUp() {
        var configurationBuilder = AiConfiguration.builder().requestTimeout(3 * 60 * 1000);
        customize(configurationBuilder);
        var configuration = configurationBuilder.build();
        chat = getChat(configuration);
    }

    protected abstract ClassifyChat getChat(AiConfiguration configuration);

    protected void customize(AiConfiguration.AiConfigurationBuilder builder) {}

    @ParameterizedTest
    @CsvSource({
        "application/pdf,/data/classify/rib-sample.pdf,RIB",
        "application/pdf,/data/AgileManifesto.pdf,Unknown",
    })
    public void should_classify_pdf(String mimeType, String docPath, String expectedCategory) throws Exception {
        // Given
        var doc = new UserDocument(mimeType, IOs.readAllBytes(docPath));

        var categories = List.of("RIB", "Carte d'identité", "Justificatif de domicile", "Passeport", "Unknown");

        // When
        String category = chat.classify(categories, doc);

        // Then
        assertThat(category).isNotEmpty();
        Classification classification = mapper.readValue(category, Classification.class);
        assertThat(classification.category()).isEqualTo(expectedCategory);
        assertThat(classification.confidence()).isGreaterThan(0.5);
    }

    @Test
    public void should_classify_png_anon() throws Exception {
        // Given
        var doc = new UserDocument("image/jpg", IOs.readAllBytes("/data/classify/justificatif-anon.jpg"));

        var categories = List.of("RIB", "Carte d'identité", "Justificatif de domicile", "Passeport", "Unknown");

        // When
        String category = chat.classify(categories, doc);

        // Then
        assertThat(category).isNotEmpty();
        Classification classification = mapper.readValue(category, Classification.class);
        assertThat(classification.category()).isEqualTo("Justificatif de domicile");
        assertThat(classification.confidence()).isGreaterThan(0.5);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Classification(String category, Double confidence) {}
}
