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
package org.bonitasoft.connectors.ai.cohere;

import static org.assertj.core.api.Assertions.assertThat;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.engine.connector.ConnectorException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CohereAskConnectorTest {

    @Nested
    class ConnectorInstantiation {

        @Test
        void should_create_connector() throws Exception {
            CohereAskConnector connector = new CohereAskConnector();
            assertThat(connector).isNotNull();
        }

        @Test
        void should_connect_without_error() throws ConnectorException {
            CohereAskConnector connector = new CohereAskConnector();
            connector.setConfiguration(
                    AiConfiguration.builder().apiKey("test-key").build());

            connector.connect();

            assertThat(connector).isNotNull();
        }
    }

    @Nested
    class ChatModelCreation {

        @Test
        void should_create_chat_model_with_default_model() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            OpenAiChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
        }

        @Test
        void should_use_default_model_name_constant() {
            assertThat(CohereChat.DEFAULT_MODEL).isEqualTo("command-r-plus");
        }

        @Test
        void should_use_default_base_url_constant() {
            assertThat(CohereChat.DEFAULT_BASE_URL).isEqualTo("https://api.cohere.com/compatibility/v1/");
        }

        @Test
        void should_create_chat_with_custom_model() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("command-r")
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getChatModelName()).hasValue("command-r");
        }

        @Test
        void should_create_chat_with_custom_temperature() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(0.3)
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getModelTemperature()).hasValue(0.3);
        }

        @Test
        void should_create_chat_with_zero_temperature() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(0.0)
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getModelTemperature()).hasValue(0.0);
        }

        @Test
        void should_create_chat_with_max_temperature() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(1.0)
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getModelTemperature()).hasValue(1.0);
        }

        @Test
        void should_create_chat_with_custom_timeout() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .requestTimeout(60000)
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getRequestTimeout()).hasValue(60000);
        }

        @Test
        void should_create_chat_with_all_options() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("command-r-plus")
                    .modelTemperature(0.7)
                    .requestTimeout(120000)
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            OpenAiChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
            assertThat(chat.getConfiguration().getApiKey()).isEqualTo("test-key");
            assertThat(chat.getConfiguration().getChatModelName()).hasValue("command-r-plus");
            assertThat(chat.getConfiguration().getModelTemperature()).hasValue(0.7);
            assertThat(chat.getConfiguration().getRequestTimeout()).hasValue(120000);
        }

        @Test
        void should_create_chat_with_no_optional_fields() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(chat.getConfiguration().getChatModelName()).isEmpty();
            assertThat(chat.getConfiguration().getModelTemperature()).isEmpty();
            assertThat(chat.getConfiguration().getRequestTimeout()).isEmpty();
        }
    }

    @Nested
    class CrossConnectorCreation {

        @Test
        void should_create_classifier_connector() {
            CohereClassifyConnector connector = new CohereClassifyConnector();
            assertThat(connector).isNotNull();
        }

        @Test
        void should_create_extractor_connector() {
            CohereExtractDataConnector connector = new CohereExtractDataConnector();
            assertThat(connector).isNotNull();
        }
    }

    @Nested
    class ConnectVariations {

        @Test
        void should_connect_with_custom_model_name() throws ConnectorException {
            CohereAskConnector connector = new CohereAskConnector();
            connector.setConfiguration(AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("command-r")
                    .build());

            connector.connect();

            assertThat(connector).isNotNull();
        }

        @Test
        void should_connect_with_custom_temperature() throws ConnectorException {
            CohereAskConnector connector = new CohereAskConnector();
            connector.setConfiguration(AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(0.5)
                    .build());

            connector.connect();

            assertThat(connector).isNotNull();
        }

        @Test
        void should_connect_with_custom_base_url() throws ConnectorException {
            CohereAskConnector connector = new CohereAskConnector();
            connector.setConfiguration(AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://custom.endpoint/v1/")
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
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getConfiguration()).isSameAs(config);
        }

        @Test
        void should_preserve_api_key_in_configuration() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("cohere-test123").build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getConfiguration().getApiKey()).isEqualTo("cohere-test123");
        }

        @Test
        void should_use_default_api_key_when_not_set() {
            AiConfiguration config = AiConfiguration.builder().build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getConfiguration().getApiKey()).isEqualTo("changeMe");
        }
    }
}
