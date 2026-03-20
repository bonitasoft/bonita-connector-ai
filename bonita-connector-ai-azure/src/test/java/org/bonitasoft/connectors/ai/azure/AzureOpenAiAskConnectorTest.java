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

class AzureOpenAiAskConnectorTest {

    @Nested
    class ConnectorInstantiation {

        @Test
        void should_create_connector_instance() {
            var connector = new AzureOpenAiAskConnector();
            assertThat(connector).isNotNull();
        }

        @Test
        void should_connect_without_error() throws ConnectorException {
            AzureOpenAiAskConnector connector = new AzureOpenAiAskConnector();
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
            var chat = new AzureOpenAiAskChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_use_default_api_version_when_not_specified() {
            var config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build();
            var chat = new AzureOpenAiAskChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_create_chat_with_custom_temperature() {
            var config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .modelTemperature(0.3)
                    .build();
            var chat = new AzureOpenAiAskChat(config);
            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getModelTemperature()).hasValue(0.3);
        }

        @Test
        void should_create_chat_with_zero_temperature() {
            var config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .modelTemperature(0.0)
                    .build();
            var chat = new AzureOpenAiAskChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_create_chat_with_max_temperature() {
            var config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .modelTemperature(1.0)
                    .build();
            var chat = new AzureOpenAiAskChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_create_chat_with_custom_timeout() {
            var config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .requestTimeout(60000)
                    .build();
            var chat = new AzureOpenAiAskChat(config);
            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getRequestTimeout()).hasValue(60000);
        }

        @Test
        void should_create_chat_with_all_options() {
            var config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .apiVersion("2024-10-21")
                    .modelTemperature(0.7)
                    .requestTimeout(120000)
                    .build();
            var chat = new AzureOpenAiAskChat(config);
            AzureOpenAiChatModel model = chat.getChatModel();
            assertThat(model).isNotNull();
            assertThat(chat.getConfiguration().getApiKey()).isEqualTo("test-key");
            assertThat(chat.getConfiguration().getChatModelName()).hasValue("gpt-4o");
        }

        @Test
        void should_create_chat_with_no_optional_fields() {
            var config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build();
            var chat = new AzureOpenAiAskChat(config);
            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getModelTemperature()).isEmpty();
            assertThat(chat.getConfiguration().getRequestTimeout()).isEmpty();
        }
    }

    @Nested
    class ConfigurationAccess {

        @Test
        void should_return_configuration_from_chat() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("my-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);
            assertThat(chat.getConfiguration()).isSameAs(config);
        }

        @Test
        void should_preserve_api_key_in_configuration() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("sk-azure-test123")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);
            assertThat(chat.getConfiguration().getApiKey()).isEqualTo("sk-azure-test123");
        }

        @Test
        void should_use_default_api_key_when_not_set() {
            AiConfiguration config = AiConfiguration.builder()
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);
            assertThat(chat.getConfiguration().getApiKey()).isEqualTo("changeMe");
        }
    }
}
