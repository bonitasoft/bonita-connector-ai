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
package org.bonitasoft.connectors.ai.ask;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.connectors.utils.IOs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public abstract class AskChatIT {

    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    AskChat chat;

    @BeforeEach
    void setUp() {
        var configurationBuilder = AiConfiguration.builder().requestTimeout(3 * 60 * 1000);
        customize(configurationBuilder);
        var configuration = configurationBuilder.build();
        chat = getChat(configuration);
    }

    protected abstract AskChat getChat(AiConfiguration configuration);

    protected void customize(AiConfiguration.AiConfigurationBuilder builder) {}

    @Test
    void should_use_doc() throws IOException {
        // Given
        var document = new UserDocument("application/pdf", IOs.readAllBytes("/data/AgileManifesto.pdf"));
        var config = AskConfiguration.builder()
                .systemPrompt("You are an expert in Agile software methodology and development.")
                .userPrompt(
                        "What year was created Manifesto for Agile Software Development ? \n The manifesto is the document provided.")
                .sourceDocumentRef("123456abcdef")
                .outputJsonSchema(
                        """
                                {
                                    "$schema": "https://json-schema.org/draft/2020-12/schema",
                                    "title": "YearOfDocument",
                                    "type": "object",
                                    "required":["year"]
                                    "properties": {
                                      "year": {
                                        "description": "The document creation year",
                                        "type": "integer"
                                      }
                                    }
                                }
                                """)
                .build();

        // When
        var json = chat.ask(
                config.getSystemPrompt(),
                config.getUserPrompt(),
                config.getOutputJsonSchema().orElse(null),
                document);

        // Then
        assertThat(json).isNotEmpty().contains("2001");

        YearOfDocument value = objectMapper.readValue(json, YearOfDocument.class);
        assertThat(value.year()).isEqualTo("2001");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record YearOfDocument(String year) {}
}
