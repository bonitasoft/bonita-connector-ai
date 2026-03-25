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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GeminiChatTest {

    @Nested
    class DefaultModel {

        @Test
        void should_have_correct_default_model_value() {
            assertThat(GeminiChat.DEFAULT_MODEL).isEqualTo("gemini-2.0-flash");
        }

        @Test
        void should_use_default_model_when_chat_model_name_not_set() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            assertThat(config.getChatModelName()).isEmpty();

            GeminiAskAiChat chat = new GeminiAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }
    }

    @Nested
    class GetChatModelBranches {

        @Test
        void should_build_model_with_only_api_key() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            GoogleAiGeminiChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
        }

        @Test
        void should_build_model_with_flash_model() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("gemini-2.0-flash")
                    .build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getChatModelName()).isPresent().hasValue("gemini-2.0-flash");
        }

        @Test
        void should_build_model_with_pro_model() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("gemini-1.5-pro")
                    .build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getChatModelName()).isPresent().hasValue("gemini-1.5-pro");
        }

        @Test
        void should_build_model_with_flash_15_model() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("gemini-1.5-flash")
                    .build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getChatModelName()).isPresent().hasValue("gemini-1.5-flash");
        }

        @Test
        void should_build_model_with_model_name_absent_uses_default() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();

            assertThat(config.getChatModelName()).isEmpty();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_build_model_with_temperature_present() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(0.5)
                    .build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getModelTemperature()).isPresent().hasValue(0.5);
        }

        @Test
        void should_build_model_with_temperature_absent() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getModelTemperature()).isEmpty();
        }

        @Test
        void should_build_model_with_timeout_present() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .requestTimeout(180000)
                    .build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getRequestTimeout()).isPresent().hasValue(180000);
        }

        @Test
        void should_build_model_with_timeout_absent() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            assertThat(chat.getChatModel()).isNotNull();
            assertThat(config.getRequestTimeout()).isEmpty();
        }

        @Test
        void should_build_model_with_all_options_present() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("AIza-full-config")
                    .chatModelName("gemini-1.5-pro")
                    .modelTemperature(0.8)
                    .requestTimeout(60000)
                    .build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            GoogleAiGeminiChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
        }

        @Test
        void should_build_model_with_all_options_absent() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);

            GoogleAiGeminiChatModel model = chat.getChatModel();

            assertThat(model).isNotNull();
        }

        @Test
        void should_build_model_with_model_name_and_temperature() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("gemini-1.5-pro")
                    .modelTemperature(0.3)
                    .build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_build_model_with_temperature_and_timeout() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .modelTemperature(0.7)
                    .requestTimeout(60000)
                    .build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_build_model_with_model_temperature_and_timeout() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("gemini-1.5-flash")
                    .modelTemperature(0.1)
                    .requestTimeout(120000)
                    .build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }
    }

    @Nested
    class AllChatTypes {

        @Test
        void should_create_ask_chat_model() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            GeminiAskAiChat chat = new GeminiAskAiChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_create_classify_chat_model() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            GeminiClassifyChat chat = new GeminiClassifyChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_create_extract_chat_model() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();
            GeminiExtractChat chat = new GeminiExtractChat(config);
            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_share_same_interface_across_chat_types() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();

            assertThat(new GeminiAskAiChat(config)).isInstanceOf(GeminiChat.class);
            assertThat(new GeminiClassifyChat(config)).isInstanceOf(GeminiChat.class);
            assertThat(new GeminiExtractChat(config)).isInstanceOf(GeminiChat.class);
        }

        @Test
        void should_build_independent_models_per_chat_instance() {
            AiConfiguration config =
                    AiConfiguration.builder().apiKey("test-key").build();

            GeminiAskAiChat chat1 = new GeminiAskAiChat(config);
            GeminiAskAiChat chat2 = new GeminiAskAiChat(config);

            GoogleAiGeminiChatModel model1 = chat1.getChatModel();
            GoogleAiGeminiChatModel model2 = chat2.getChatModel();

            assertThat(model1).isNotSameAs(model2);
        }

        @Test
        void should_classify_chat_with_all_options() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("gemini-1.5-flash")
                    .modelTemperature(0.0)
                    .requestTimeout(30000)
                    .build();
            GeminiClassifyChat chat = new GeminiClassifyChat(config);

            assertThat(chat.getChatModel()).isNotNull();
        }

        @Test
        void should_extract_chat_with_all_options() {
            AiConfiguration config = AiConfiguration.builder()
                    .apiKey("test-key")
                    .chatModelName("gemini-2.0-flash")
                    .modelTemperature(0.2)
                    .requestTimeout(90000)
                    .build();
            GeminiExtractChat chat = new GeminiExtractChat(config);

            assertThat(chat.getChatModel()).isNotNull();
        }
    }
}
