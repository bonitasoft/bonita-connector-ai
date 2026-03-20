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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AzureOpenAiChatTest {

    @Nested
    class DefaultApiVersion {

        @Test
        void should_have_correct_default_api_version_value() {
            assertThat(AzureOpenAiChat.DEFAULT_API_VERSION).isEqualTo("2024-10-21");
        }

        @Test
        void should_use_default_api_version_when_not_set() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build();
            assertThat(config.getApiVersion()).isEmpty();

            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }
    }

    @Nested
    class GetChatModelBranches {

        @Test
        void should_build_model_with_minimal_config() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);

            AzureOpenAiChatModel model = chat.getChatModel();
            assertThat(model).isNotNull();
        }

        @Test
        void should_build_model_with_custom_api_version() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .apiVersion("2024-06-01")
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getApiVersion()).isPresent().hasValue("2024-06-01");
        }

        @Test
        void should_build_model_with_endpoint_present() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://my-resource.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getBaseUrl()).isPresent().hasValue("https://my-resource.openai.azure.com");
        }

        @Test
        void should_build_model_with_deployment_name() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o-mini")
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getChatModelName()).isPresent().hasValue("gpt-4o-mini");
        }

        @Test
        void should_build_model_with_temperature_present() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .modelTemperature(0.5)
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getModelTemperature()).isPresent().hasValue(0.5);
        }

        @Test
        void should_build_model_with_temperature_absent() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getModelTemperature()).isEmpty();
        }

        @Test
        void should_build_model_with_timeout_present() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .requestTimeout(180000)
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getRequestTimeout()).isPresent().hasValue(180000);
        }

        @Test
        void should_build_model_with_timeout_absent() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getRequestTimeout()).isEmpty();
        }

        @Test
        void should_build_model_with_all_options_present() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("sk-azure-full-config")
                    .baseUrl("https://my-resource.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .apiVersion("2024-10-21")
                    .modelTemperature(0.8)
                    .requestTimeout(60000)
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);

            AzureOpenAiChatModel model = chat.getChatModel();
            assertThat(model).isNotNull();
        }

        @Test
        void should_build_model_with_only_required_options() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);

            AzureOpenAiChatModel model = chat.getChatModel();
            assertThat(model).isNotNull();
        }

        @Test
        void should_build_model_with_endpoint_and_deployment() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://proxy.example.com")
                    .chatModelName("gpt-4o-mini")
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_build_model_with_temperature_and_timeout() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .modelTemperature(0.7)
                    .requestTimeout(60000)
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_build_model_with_api_version_and_temperature() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .apiVersion("2024-06-01")
                    .modelTemperature(0.3)
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_build_model_with_deployment_temperature_and_timeout() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o-mini")
                    .modelTemperature(0.1)
                    .requestTimeout(120000)
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_build_model_with_zero_temperature() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .modelTemperature(0.0)
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);
            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getModelTemperature()).hasValue(0.0);
        }
    }

    @Nested
    class AllChatTypes {

        @Test
        void should_create_ask_chat_model() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build();
            AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_create_classify_chat_model() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build();
            AzureOpenAiClassifyChat chat = new AzureOpenAiClassifyChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_create_extract_chat_model() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build();
            AzureOpenAiExtractChat chat = new AzureOpenAiExtractChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_share_same_interface_across_chat_types() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build();

            assertThat(new AzureOpenAiAskChat(config)).isInstanceOf(AzureOpenAiChat.class);
            assertThat(new AzureOpenAiClassifyChat(config)).isInstanceOf(AzureOpenAiChat.class);
            assertThat(new AzureOpenAiExtractChat(config)).isInstanceOf(AzureOpenAiChat.class);
        }

        @Test
        void should_build_independent_models_per_chat_instance() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build();

            AzureOpenAiAskChat chat1 = new AzureOpenAiAskChat(config);
            AzureOpenAiAskChat chat2 = new AzureOpenAiAskChat(config);

            AzureOpenAiChatModel model1 = chat1.getChatModel();
            AzureOpenAiChatModel model2 = chat2.getChatModel();

            assertThat(model1).isNotSameAs(model2);
        }

        @Test
        void should_classify_chat_with_all_options() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o-mini")
                    .apiVersion("2024-06-01")
                    .modelTemperature(0.0)
                    .requestTimeout(30000)
                    .build();
            AzureOpenAiClassifyChat chat = new AzureOpenAiClassifyChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_extract_chat_with_all_options() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .apiVersion("2024-10-21")
                    .modelTemperature(0.2)
                    .requestTimeout(90000)
                    .build();
            AzureOpenAiExtractChat chat = new AzureOpenAiExtractChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }
    }
}
