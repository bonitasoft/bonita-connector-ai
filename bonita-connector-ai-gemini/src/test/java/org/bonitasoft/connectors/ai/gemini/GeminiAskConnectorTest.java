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
package org.bonitasoft.connectors.ai.gemini;

import static org.assertj.core.api.Assertions.assertThat;

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.engine.connector.ConnectorException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GeminiAskConnectorTest {

    @Nested
    class ConnectorInstantiation {

        @Test
        void should_create_connector() throws Exception {
            GeminiAskConnector connector = new GeminiAskConnector();
            assertThat(connector).isNotNull();
        }

        @Test
        void should_connect_without_error() throws ConnectorException {
            GeminiAskConnector connector = new GeminiAskConnector();
            connector.setConfiguration(
                    AiConfiguration.builder().apiKey("test-key").build());

            connector.connect();

            // connect() initializes the internal chat - no exception means success
            assertThat(connector).isNotNull();
        }
    }

    @Nested
    class ChatModelCreation {

        @Test
        void should_create_chat_model_with_default_model() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            GoogleAiGeminiChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
        }

        @Test
        void should_use_default_model_name_constant() {
            assertThat(GeminiChat.DEFAULT_MODEL).isEqualTo("gemini-2.0-flash");
        }

        @Test
        void should_create_chat_with_custom_model() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("gemini-1.5-pro")
                    .build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getChatModelName()).hasValue("gemini-1.5-pro");
        }

        @Test
        void should_create_chat_with_custom_temperature() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(0.3)
                    .build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getModelTemperature()).hasValue(0.3);
        }

        @Test
        void should_create_chat_with_zero_temperature() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(0.0)
                    .build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getModelTemperature()).hasValue(0.0);
        }

        @Test
        void should_create_chat_with_max_temperature() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(1.0)
                    .build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getModelTemperature()).hasValue(1.0);
        }

        @Test
        void should_create_chat_with_custom_timeout() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .requestTimeout(60000)
                    .build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getRequestTimeout()).hasValue(60000);
        }

        @Test
        void should_create_chat_with_small_timeout() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .requestTimeout(1000)
                    .build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getRequestTimeout()).hasValue(1000);
        }

        @Test
        void should_create_chat_with_debug_logging_enabled() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .enableDebugLogging(true)
                    .build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().isEnableDebugLogging()).isTrue();
        }

        @Test
        void should_create_chat_with_all_options() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("gemini-1.5-pro")
                    .modelTemperature(0.7)
                    .requestTimeout(120000)
                    .build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            GoogleAiGeminiChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
            assertThat(chat.getConfiguration().getApiKey()).isEqualTo("test-key");
            assertThat(chat.getConfiguration().getChatModelName()).hasValue("gemini-1.5-pro");
            assertThat(chat.getConfiguration().getModelTemperature()).hasValue(0.7);
            assertThat(chat.getConfiguration().getRequestTimeout()).hasValue(120000);
        }

        @Test
        void should_create_chat_with_no_optional_fields() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getBaseUrl()).isEmpty();
            assertThat(chat.getConfiguration().getChatModelName()).isEmpty();
            assertThat(chat.getConfiguration().getModelTemperature()).isEmpty();
            assertThat(chat.getConfiguration().getRequestTimeout()).isEmpty();
        }
    }

    @Nested
    class CrossConnectorCreation {

        @Test
        void should_create_classifier_connector() {
            GeminiClassifyConnector connector = new GeminiClassifyConnector();
            assertThat(connector).isNotNull();
        }

        @Test
        void should_create_extractor_connector() {
            GeminiExtractDataConnector connector = new GeminiExtractDataConnector();
            assertThat(connector).isNotNull();
        }
    }

    @Nested
    class ConnectVariations {

        @Test
        void should_connect_with_debug_logging_disabled() throws ConnectorException {
            GeminiAskConnector connector = new GeminiAskConnector();
            connector.setConfiguration(AiConfiguration.builder()
                    .apiKey("test-key")
                    .enableDebugLogging(false)
                    .build());

            connector.connect();

            assertThat(connector).isNotNull();
        }

        @Test
        void should_connect_with_custom_model_name() throws ConnectorException {
            GeminiAskConnector connector = new GeminiAskConnector();
            connector.setConfiguration(AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("gemini-1.5-pro")
                    .build());

            connector.connect();

            assertThat(connector).isNotNull();
        }

        @Test
        void should_connect_with_custom_temperature() throws ConnectorException {
            GeminiAskConnector connector = new GeminiAskConnector();
            connector.setConfiguration(AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(0.5)
                    .build());

            connector.connect();

            assertThat(connector).isNotNull();
        }
    }

    @Nested
    class ConfigurationAccess {

        @Test
        void should_return_configuration_from_chat() {
            AiConfiguration config = AiConfiguration.builder().apiKey("my-key").build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getConfiguration()).isSameAs(config);
        }

        @Test
        void should_preserve_api_key_in_configuration() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("AIza-test123").build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getConfiguration().getApiKey()).isEqualTo("AIza-test123");
        }

        @Test
        void should_use_default_api_key_when_not_set() {
            AiConfiguration config = AiConfiguration.builder().build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getConfiguration().getApiKey()).isEqualTo("changeMe");
        }
    }
}
