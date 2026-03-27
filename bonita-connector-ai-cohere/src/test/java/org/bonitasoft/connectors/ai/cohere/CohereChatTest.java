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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CohereChatTest {

    @Nested
    class DefaultValues {

        @Test
        void should_have_correct_default_base_url() {
            assertThat(CohereChat.DEFAULT_BASE_URL).isEqualTo("https://api.cohere.com/compatibility/v1/");
        }

        @Test
        void should_have_correct_default_model_value() {
            assertThat(CohereChat.DEFAULT_MODEL).isEqualTo("command-r-plus");
        }

        @Test
        void should_use_default_model_when_chat_model_name_not_set() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            assertThat(config.getChatModelName()).isEmpty();

            CohereAskAiChat chat = new CohereAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_use_default_base_url_when_not_set() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            assertThat(config.getBaseUrl()).isEmpty();

            CohereAskAiChat chat = new CohereAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }
    }

    @Nested
    class GetChatModelBranches {

        @Test
        void should_build_model_with_only_api_key() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            OpenAiChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
        }

        @Test
        void should_build_model_with_command_r_plus() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("command-r-plus")
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getChatModelName()).isPresent().hasValue("command-r-plus");
        }

        @Test
        void should_build_model_with_command_r() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("command-r")
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getChatModelName()).isPresent().hasValue("command-r");
        }

        @Test
        void should_build_model_with_command_a() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("command-a-03-2025")
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getChatModelName()).isPresent().hasValue("command-a-03-2025");
        }

        @Test
        void should_build_model_with_model_name_absent_uses_default() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();

            assertThat(config.getChatModelName()).isEmpty();
            CohereAskAiChat chat = new CohereAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_build_model_with_temperature_present() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(0.5)
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getModelTemperature()).isPresent().hasValue(0.5);
        }

        @Test
        void should_build_model_with_temperature_absent() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getModelTemperature()).isEmpty();
        }

        @Test
        void should_build_model_with_timeout_present() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .requestTimeout(180000)
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getRequestTimeout()).isPresent().hasValue(180000);
        }

        @Test
        void should_build_model_with_timeout_absent() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getRequestTimeout()).isEmpty();
        }

        @Test
        void should_build_model_with_custom_base_url() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://custom.cohere.endpoint/v1/")
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getBaseUrl()).hasValue("https://custom.cohere.endpoint/v1/");
        }

        @Test
        void should_build_model_with_all_options_present() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("cohere-full-config")
                    .chatModelName("command-r-plus")
                    .modelTemperature(0.8)
                    .requestTimeout(60000)
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            OpenAiChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
        }

        @Test
        void should_build_model_with_all_options_absent() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            OpenAiChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
        }

        @Test
        void should_build_model_with_model_name_and_temperature() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("command-r-plus")
                    .modelTemperature(0.3)
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_build_model_with_temperature_and_timeout() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(0.7)
                    .requestTimeout(60000)
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_build_model_with_zero_temperature() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(0.0)
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getModelTemperature()).hasValue(0.0);
        }

        @Test
        void should_build_model_with_very_large_timeout() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .requestTimeout(300_000)
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getRequestTimeout()).hasValue(300_000);
        }

        @Test
        void should_build_model_with_minimum_timeout() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .requestTimeout(1)
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getRequestTimeout()).hasValue(1);
        }

        @Test
        void should_build_model_with_empty_string_model_name() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("")
                    .build();
            CohereAskAiChat chat = new CohereAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
        }
    }

    @Nested
    class AllChatTypes {

        @Test
        void should_create_ask_chat_model() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            CohereAskAiChat chat = new CohereAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_create_classify_chat_model() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            CohereClassifyChat chat = new CohereClassifyChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_create_extract_chat_model() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            CohereExtractChat chat = new CohereExtractChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_share_same_interface_across_chat_types() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();

            assertThat(new CohereAskAiChat(config)).isInstanceOf(CohereChat.class);
            assertThat(new CohereClassifyChat(config)).isInstanceOf(CohereChat.class);
            assertThat(new CohereExtractChat(config)).isInstanceOf(CohereChat.class);
        }

        @Test
        void should_build_independent_models_per_chat_instance() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();

            CohereAskAiChat chat1 = new CohereAskAiChat(config);
            CohereAskAiChat chat2 = new CohereAskAiChat(config);

            OpenAiChatModel model1 = chat1.getChatModel();
            OpenAiChatModel model2 = chat2.getChatModel();

            assertThat(model1).isNotSameAs(model2);
        }

        @Test
        void should_classify_chat_with_all_options() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("command-r")
                    .modelTemperature(0.0)
                    .requestTimeout(30000)
                    .build();
            CohereClassifyChat chat = new CohereClassifyChat(config);

            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_extract_chat_with_all_options() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("command-r-plus")
                    .modelTemperature(0.2)
                    .requestTimeout(90000)
                    .build();
            CohereExtractChat chat = new CohereExtractChat(config);

            assertThat(chat.getChatModel()).isNotNull();
        }
    }
}
