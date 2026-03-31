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
package org.bonitasoft.connectors.ai.anthropic;

import static org.assertj.core.api.Assertions.assertThat;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.engine.connector.ConnectorException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AnthropicExtractDataConnectorTest {

    @Nested
    class ConnectorInstantiation {

        @Test
        void should_create_connector() {
            AnthropicExtractDataConnector connector = new AnthropicExtractDataConnector();
            assertThat(connector).isNotNull();
        }

        @Test
        void should_connect_without_error() throws ConnectorException {
            AnthropicExtractDataConnector connector = new AnthropicExtractDataConnector();
            connector.setConfiguration(
                    AiConfiguration.builder().apiKey("test-key").build());

            connector.connect();

            assertThat(connector).isNotNull();
        }
    }

    @Nested
    class ChatModelCreation {

        @Test
        void should_create_extract_chat() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            AnthropicExtractChat chat = new AnthropicExtractChat(config);

            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_create_extract_chat_with_all_options() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("claude-sonnet-4-6")
                    .modelTemperature(0.2)
                    .requestTimeout(90000)
                    .baseUrl("https://extract-proxy.example.com")
                    .build();
            AnthropicExtractChat chat = new AnthropicExtractChat(config);

            AnthropicChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
            assertThat(chat.getConfiguration().getChatModelName()).hasValue("claude-sonnet-4-6");
        }

        @Test
        void should_create_extract_chat_with_no_optional_fields() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            AnthropicExtractChat chat = new AnthropicExtractChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getBaseUrl()).isEmpty();
            assertThat(chat.getConfiguration().getChatModelName()).isEmpty();
            assertThat(chat.getConfiguration().getModelTemperature()).isEmpty();
            assertThat(chat.getConfiguration().getRequestTimeout()).isEmpty();
        }

        @Test
        void should_return_configuration() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            AnthropicExtractChat chat = new AnthropicExtractChat(config);

            assertThat(chat.getConfiguration()).isSameAs(config);
        }
    }
}
