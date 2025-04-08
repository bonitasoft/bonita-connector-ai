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
package org.bonitasoft.connectors.ai.extract;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.connectors.utils.IOs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public abstract class ExtractChatIT {

    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    ExtractChat chat;

    @BeforeEach
    void setUp() {
        var configurationBuilder = AiConfiguration.builder().requestTimeout(3 * 60 * 1000);
        customize(configurationBuilder);
        var configuration = configurationBuilder.build();
        chat = getChat(configuration);
    }

    protected abstract ExtractChat getChat(AiConfiguration configuration);

    protected void customize(AiConfiguration.AiConfigurationBuilder builder) {}

    @Test
    public void should_extract_data_from_pdf() throws Exception {

        // Given
        var doc = new UserDocument(
                "application/pdf", IOs.readAllBytes("/data/justificatifs/justificatif_domicile_1.pdf"));
        var fields = List.of(
                "firstName",
                "lastName",
                "fullName",
                "fullAddress",
                "recentDate",
                "issuerName",
                "identificationNumber",
                "motherBirthday");

        // When
        String json = chat.extract(doc, fields);

        // Then
        log.debug("Json output: {}", json);
        assertThat(json).isNotEmpty();
        assertJsonContent(json);
    }

    @Test
    public void should_extract_data_from_pdf_with_json_schema() throws Exception {

        // Given
        var doc = new UserDocument(
                "application/pdf", IOs.readAllBytes("/data/justificatifs/justificatif_domicile_1.pdf"));
        var jsonSchema = IOs.readAsString("/extract/schema.json");

        // When
        String json = chat.extract(doc, jsonSchema);

        // Then
        log.debug("Json output: {}", json);
        assertThat(json).isNotEmpty();
        assertJsonContent(json);
    }

    @Test
    public void should_extract_data_from_png() throws Exception {
        // Given
        var doc = new UserDocument("image/png", IOs.readAllBytes("/data/justificatifs/justificatif_domicile_1.png"));
        var jsonSchema = IOs.readAsString("/extract/schema.json");

        // When
        String json = chat.extract(doc, jsonSchema);

        // Then
        log.debug("Json output: {}", json);
        assertThat(json).isNotEmpty();
        assertJsonContent(json);
    }

    private void assertJsonContent(String json) throws JsonProcessingException {
        var user = objectMapper.readValue(json, User.class);
        assertThat(user.firstName).isEqualTo("Jean");
        assertThat(user.lastName).isEqualTo("Dupont");
        assertThat(user.fullName).isEqualTo("Jean Dupont");
        assertThat(user.fullAddress).isEqualTo("12 Rue de la Paix, 75002 Paris");
        assertThat(user.issuerName).isEqualTo("Orange");
        assertThat(user.identificationNumber).isEqualTo("581325418");
        assertThat(user.motherBirthday).isEqualTo("Absent");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record User(
            String firstName,
            String lastName,
            String fullName,
            String fullAddress,
            String recentDate,
            String issuerName,
            String identificationNumber,
            String motherBirthday) {}
}
