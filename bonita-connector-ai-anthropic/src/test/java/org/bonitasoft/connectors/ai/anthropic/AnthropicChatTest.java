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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AnthropicChatTest {

    @Nested
    class DefaultModel {

        @Test
        void should_have_correct_default_model_value() {
            assertThat(AnthropicChat.DEFAULT_MODEL).isEqualTo("claude-sonnet-4-6");
        }

        @Test
        void should_use_default_model_when_chat_model_name_not_set() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            assertThat(config.getChatModelName()).isEmpty();

            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }
    }

    @Nested
    class GetChatModelBranches {

        @Test
        void should_build_model_with_only_api_key() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);

            AnthropicChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
        }

        @Test
        void should_build_model_with_base_url_present() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://custom.anthropic.com/v1")
                    .build();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getBaseUrl()).isPresent().hasValue("https://custom.anthropic.com/v1");
        }

        @Test
        void should_build_model_with_base_url_absent() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getBaseUrl()).isEmpty();
        }

        @Test
        void should_build_model_with_haiku_model() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("claude-haiku-4-5-20251001")
                    .build();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getChatModelName()).isPresent().hasValue("claude-haiku-4-5-20251001");
        }

        @Test
        void should_build_model_with_opus_model() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("claude-opus-4-6")
                    .build();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getChatModelName()).isPresent().hasValue("claude-opus-4-6");
        }

        @Test
        void should_build_model_with_sonnet_model() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("claude-sonnet-4-6")
                    .build();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getChatModelName()).isPresent().hasValue("claude-sonnet-4-6");
        }

        @Test
        void should_build_model_with_model_name_absent_uses_default() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();

            assertThat(config.getChatModelName()).isEmpty();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_build_model_with_temperature_present() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(0.5)
                    .build();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getModelTemperature()).isPresent().hasValue(0.5);
        }

        @Test
        void should_build_model_with_temperature_absent() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getModelTemperature()).isEmpty();
        }

        @Test
        void should_build_model_with_timeout_present() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .requestTimeout(180000)
                    .build();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getRequestTimeout()).isPresent().hasValue(180000);
        }

        @Test
        void should_build_model_with_timeout_absent() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getRequestTimeout()).isEmpty();
        }

        @Test
        void should_build_model_with_all_options_present() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("sk-ant-full-config")
                    .baseUrl("https://proxy.example.com")
                    .chatModelName("claude-opus-4-6")
                    .modelTemperature(0.8)
                    .requestTimeout(60000)
                    .build();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);

            AnthropicChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
        }

        @Test
        void should_build_model_with_all_options_absent() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);

            AnthropicChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
        }

        @Test
        void should_build_model_with_base_url_and_model_name() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://proxy.example.com")
                    .chatModelName("claude-opus-4-6")
                    .build();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_build_model_with_temperature_and_timeout() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(0.7)
                    .requestTimeout(60000)
                    .build();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_build_model_with_url_model_and_temperature() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://proxy.example.com")
                    .chatModelName("claude-opus-4-6")
                    .modelTemperature(0.3)
                    .build();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_build_model_with_model_temperature_and_timeout() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("claude-haiku-4-5-20251001")
                    .modelTemperature(0.1)
                    .requestTimeout(120000)
                    .build();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }
    }

    @Nested
    class AllChatTypes {

        @Test
        void should_create_ask_chat_model() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            AnthropicAskAiChat chat = new AnthropicAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_create_classify_chat_model() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            AnthropicClassifyChat chat = new AnthropicClassifyChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_create_extract_chat_model() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            AnthropicExtractChat chat = new AnthropicExtractChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_share_same_interface_across_chat_types() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();

            assertThat(new AnthropicAskAiChat(config)).isInstanceOf(AnthropicChat.class);
            assertThat(new AnthropicClassifyChat(config)).isInstanceOf(AnthropicChat.class);
            assertThat(new AnthropicExtractChat(config)).isInstanceOf(AnthropicChat.class);
        }

        @Test
        void should_build_independent_models_per_chat_instance() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();

            AnthropicAskAiChat chat1 = new AnthropicAskAiChat(config);
            AnthropicAskAiChat chat2 = new AnthropicAskAiChat(config);

            AnthropicChatModel model1 = chat1.getChatModel();
            AnthropicChatModel model2 = chat2.getChatModel();

            assertThat(model1).isNotSameAs(model2);
        }

        @Test
        void should_classify_chat_with_all_options() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("claude-haiku-4-5-20251001")
                    .modelTemperature(0.0)
                    .requestTimeout(30000)
                    .baseUrl("https://classify-proxy.example.com")
                    .build();
            AnthropicClassifyChat chat = new AnthropicClassifyChat(config);

            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_extract_chat_with_all_options() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("claude-sonnet-4-6")
                    .modelTemperature(0.2)
                    .requestTimeout(90000)
                    .baseUrl("https://extract-proxy.example.com")
                    .build();
            AnthropicExtractChat chat = new AnthropicExtractChat(config);

            assertThat(chat.getChatModel()).isNotNull();
        }
    }
}
