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

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.engine.connector.ConnectorException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AzureOpenAiClassifyConnectorTest {

    @Nested
    class ConnectorInstantiation {
        @Test
        void should_create_connector_instance() {
            var connector = new AzureOpenAiClassifyConnector();
            assertThat(connector).isNotNull();
        }

        @Test
        void should_connect_without_error() throws ConnectorException {
            AzureOpenAiClassifyConnector connector = new AzureOpenAiClassifyConnector();
            connector.setConfiguration(AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build());
            connector.connect();
            assertThat(connector).isNotNull();
        }
    }

    @Nested
    class ChatModelCreation {
        @Test
        void should_create_chat_model_with_configuration() {
            var config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .apiVersion("2024-10-21")
                    .build();
            var chat = new AzureOpenAiClassifyChat(config);
            AzureOpenAiChatModel model = chat.getChatModel();
            assertThat(model).isNotNull();
        }

        @Test
        void should_create_chat_with_all_options() {
            var config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .apiVersion("2024-10-21")
                    .modelTemperature(0.0)
                    .requestTimeout(30000)
                    .build();
            var chat = new AzureOpenAiClassifyChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }
    }
}
