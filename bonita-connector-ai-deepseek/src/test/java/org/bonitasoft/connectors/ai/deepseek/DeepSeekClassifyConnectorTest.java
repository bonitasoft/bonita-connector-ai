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
package org.bonitasoft.connectors.ai.deepseek;

import static org.assertj.core.api.Assertions.assertThat;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.engine.connector.ConnectorException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DeepSeekClassifyConnectorTest {

    @Nested
    class ConnectorInstantiation {

        @Test
        void should_create_connector() {
            DeepSeekClassifyConnector connector = new DeepSeekClassifyConnector();
            assertThat(connector).isNotNull();
        }

        @Test
        void should_connect_without_error() throws ConnectorException {
            DeepSeekClassifyConnector connector = new DeepSeekClassifyConnector();
            connector.setConfiguration(
                    AiConfiguration.builder().apiKey("test-key").build());

            connector.connect();

            assertThat(connector).isNotNull();
        }
    }

    @Nested
    class ConnectVariations {

        @Test
        void should_connect_with_custom_model() throws ConnectorException {
            DeepSeekClassifyConnector connector = new DeepSeekClassifyConnector();
            connector.setConfiguration(AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("deepseek-coder")
                    .build());

            connector.connect();

            assertThat(connector).isNotNull();
        }

        @Test
        void should_connect_with_custom_base_url() throws ConnectorException {
            DeepSeekClassifyConnector connector = new DeepSeekClassifyConnector();
            connector.setConfiguration(AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://custom.endpoint/v1/")
                    .build());

            connector.connect();

            assertThat(connector).isNotNull();
        }
    }

    @Nested
    class ChatModelCreation {

        @Test
        void should_create_classify_chat() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            DeepSeekClassifyChat chat = new DeepSeekClassifyChat(config);

            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_create_classify_chat_with_all_options() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("deepseek-chat")
                    .modelTemperature(0.1)
                    .requestTimeout(30000)
                    .build();
            DeepSeekClassifyChat chat = new DeepSeekClassifyChat(config);

            OpenAiChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
            assertThat(chat.getConfiguration().getChatModelName()).hasValue("deepseek-chat");
        }

        @Test
        void should_create_classify_chat_with_no_optional_fields() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            DeepSeekClassifyChat chat = new DeepSeekClassifyChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getChatModelName()).isEmpty();
            assertThat(chat.getConfiguration().getModelTemperature()).isEmpty();
            assertThat(chat.getConfiguration().getRequestTimeout()).isEmpty();
        }

        @Test
        void should_return_configuration() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            DeepSeekClassifyChat chat = new DeepSeekClassifyChat(config);

            assertThat(chat.getConfiguration()).isSameAs(config);
        }
    }
}
