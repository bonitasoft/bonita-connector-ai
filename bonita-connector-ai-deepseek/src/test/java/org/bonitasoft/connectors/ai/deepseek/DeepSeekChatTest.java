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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DeepSeekChatTest {

    @Nested
    class DefaultValues {

        @Test
        void should_have_correct_default_base_url() {
            assertThat(DeepSeekChat.DEFAULT_BASE_URL).isEqualTo("https://api.deepseek.com");
        }

        @Test
        void should_have_correct_default_model_value() {
            assertThat(DeepSeekChat.DEFAULT_MODEL).isEqualTo("deepseek-chat");
        }

        @Test
        void should_use_default_model_when_chat_model_name_not_set() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            assertThat(config.getChatModelName()).isEmpty();

            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_use_default_base_url_when_not_set() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            assertThat(config.getBaseUrl()).isEmpty();

            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }
    }

    @Nested
    class GetChatModelBranches {

        @Test
        void should_build_model_with_only_api_key() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);

            OpenAiChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
        }

        @Test
        void should_build_model_with_deepseek_chat() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("deepseek-chat")
                    .build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getChatModelName()).isPresent().hasValue("deepseek-chat");
        }

        @Test
        void should_build_model_with_deepseek_coder() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("deepseek-coder")
                    .build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getChatModelName()).isPresent().hasValue("deepseek-coder");
        }

        @Test
        void should_build_model_with_deepseek_reasoner() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("deepseek-reasoner")
                    .build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getChatModelName()).isPresent().hasValue("deepseek-reasoner");
        }

        @Test
        void should_build_model_with_model_name_absent_uses_default() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();

            assertThat(config.getChatModelName()).isEmpty();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_build_model_with_temperature_present() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(0.5)
                    .build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getModelTemperature()).isPresent().hasValue(0.5);
        }

        @Test
        void should_build_model_with_temperature_absent() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getModelTemperature()).isEmpty();
        }

        @Test
        void should_build_model_with_timeout_present() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .requestTimeout(180000)
                    .build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getRequestTimeout()).isPresent().hasValue(180000);
        }

        @Test
        void should_build_model_with_timeout_absent() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getRequestTimeout()).isEmpty();
        }

        @Test
        void should_build_model_with_custom_base_url() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://custom.deepseek.endpoint/v1/")
                    .build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getBaseUrl()).hasValue("https://custom.deepseek.endpoint/v1/");
        }

        @Test
        void should_build_model_with_all_options_present() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("deepseek-full-config")
                    .chatModelName("deepseek-chat")
                    .modelTemperature(0.8)
                    .requestTimeout(60000)
                    .build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);

            OpenAiChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
        }

        @Test
        void should_build_model_with_all_options_absent() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);

            OpenAiChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
        }

        @Test
        void should_build_model_with_model_name_and_temperature() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("deepseek-chat")
                    .modelTemperature(0.3)
                    .build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_build_model_with_temperature_and_timeout() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(0.7)
                    .requestTimeout(60000)
                    .build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_build_model_with_zero_temperature() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(0.0)
                    .build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getModelTemperature()).hasValue(0.0);
        }

        @Test
        void should_build_model_with_very_large_timeout() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .requestTimeout(300_000)
                    .build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getRequestTimeout()).hasValue(300_000);
        }

        @Test
        void should_build_model_with_minimum_timeout() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .requestTimeout(1)
                    .build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getRequestTimeout()).hasValue(1);
        }

        @Test
        void should_build_model_with_empty_string_model_name() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("")
                    .build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
        }
    }

    @Nested
    class AllChatTypes {

        @Test
        void should_create_ask_chat_model() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            DeepSeekAskAiChat chat = new DeepSeekAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_create_classify_chat_model() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            DeepSeekClassifyChat chat = new DeepSeekClassifyChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_create_extract_chat_model() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            DeepSeekExtractChat chat = new DeepSeekExtractChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_share_same_interface_across_chat_types() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();

            assertThat(new DeepSeekAskAiChat(config)).isInstanceOf(DeepSeekChat.class);
            assertThat(new DeepSeekClassifyChat(config)).isInstanceOf(DeepSeekChat.class);
            assertThat(new DeepSeekExtractChat(config)).isInstanceOf(DeepSeekChat.class);
        }

        @Test
        void should_build_independent_models_per_chat_instance() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();

            DeepSeekAskAiChat chat1 = new DeepSeekAskAiChat(config);
            DeepSeekAskAiChat chat2 = new DeepSeekAskAiChat(config);

            OpenAiChatModel model1 = chat1.getChatModel();
            OpenAiChatModel model2 = chat2.getChatModel();

            assertThat(model1).isNotSameAs(model2);
        }

        @Test
        void should_classify_chat_with_all_options() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("deepseek-chat")
                    .modelTemperature(0.0)
                    .requestTimeout(30000)
                    .build();
            DeepSeekClassifyChat chat = new DeepSeekClassifyChat(config);

            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_extract_chat_with_all_options() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("deepseek-chat")
                    .modelTemperature(0.2)
                    .requestTimeout(90000)
                    .build();
            DeepSeekExtractChat chat = new DeepSeekExtractChat(config);

            assertThat(chat.getChatModel()).isNotNull();
        }
    }
}
