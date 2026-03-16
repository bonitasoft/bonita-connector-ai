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
package org.bonitasoft.connectors.ai.azure;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.connectors.ai.classify.ClassifyChat;
import org.bonitasoft.connectors.ai.classify.ClassifyChatIT;
import org.bonitasoft.connectors.utils.IOs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class AzureOpenAiClassifyChatIT extends ClassifyChatIT {

    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    ClassifyChat classifyChat;

    @BeforeEach
    void setUpClassify() {
        var configurationBuilder = AiConfiguration.builder().requestTimeout(3 * 60 * 1000);
        configurationBuilder.apiKey(System.getenv("AZURE_OPENAI_API_KEY"));
        configurationBuilder.baseUrl(System.getenv("AZURE_OPENAI_ENDPOINT"));
        String modelName = System.getenv("AZURE_OPENAI_MODEL");
        if (modelName != null && !modelName.isEmpty()) {
            configurationBuilder.chatModelName(modelName);
        }
        var configuration = configurationBuilder.build();
        classifyChat = new AzureOpenAiClassifyChat(configuration);
    }

    @Override
    protected void customize(AiConfiguration.AiConfigurationBuilder builder) {
        builder.apiKey(System.getenv("AZURE_OPENAI_API_KEY"));
        builder.baseUrl(System.getenv("AZURE_OPENAI_ENDPOINT"));
        String modelName = System.getenv("AZURE_OPENAI_MODEL");
        if (modelName != null && !modelName.isEmpty()) {
            builder.chatModelName(modelName);
        }
    }

    @Override
    protected ClassifyChat getChat(AiConfiguration configuration) {
        return new AzureOpenAiClassifyChat(configuration);
    }

    @Test
    void should_classify_pdf_document_as_rib() throws Exception {
        var doc = new UserDocument("application/pdf", IOs.readAllBytes("/data/classify/rib-sample.pdf"));
        var categories = List.of("RIB", "Carte d'identite", "Justificatif de domicile", "Passeport", "Unknown");

        String category = classifyChat.classify(categories, doc);

        assertThat(category).isNotEmpty();
        Classification classification = objectMapper.readValue(category, Classification.class);
        log.debug("Classification result: {}", classification);
        assertThat(classification.category()).isEqualTo("RIB");
        assertThat(classification.confidence()).isGreaterThan(0.8);
    }

    @Test
    void should_classify_with_high_confidence() throws Exception {
        var doc = new UserDocument("application/pdf", IOs.readAllBytes("/data/classify/rib-sample.pdf"));
        var categories = List.of("INVOICE", "BANK_DOCUMENT", "CONTRACT", "OTHER");

        String category = classifyChat.classify(categories, doc);

        assertThat(category).isNotEmpty();
        Classification classification = objectMapper.readValue(category, Classification.class);
        log.debug("Classification result: {}", classification);
        assertThat(classification.category()).isEqualTo("BANK_DOCUMENT");
        assertThat(classification.confidence()).isGreaterThan(0.7);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Classification(String category, Double confidence) {}
}
