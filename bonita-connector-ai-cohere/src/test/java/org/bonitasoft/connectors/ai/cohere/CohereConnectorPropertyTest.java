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

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.bonitasoft.connectors.ai.AiConfiguration;

/**
 * Property-based tests for Cohere connector using jqwik.
 * Tests configuration building and chat model creation with randomly generated data.
 */
class CohereConnectorPropertyTest {

    @Property(tries = 100)
    @Label("Valid API key should always create a chat model")
    void should_create_model_when_api_key_valid(@ForAll @AlphaChars @StringLength(min = 1, max = 100) String apiKey) {
        AiConfiguration config = AiConfiguration.builder().apiKey(apiKey).build();
        CohereAskAiChat chat = new CohereAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
    }

    @Property(tries = 50)
    @Label("Valid temperature range should always create a chat model")
    void should_create_model_when_temperature_in_range(@ForAll @DoubleRange(min = 0.0, max = 1.0) double temperature) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .modelTemperature(temperature)
                .build();
        CohereAskAiChat chat = new CohereAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(config.getModelTemperature()).hasValue(temperature);
    }

    @Property(tries = 50)
    @Label("Positive timeout should always create a chat model")
    void should_create_model_when_timeout_positive(@ForAll @IntRange(min = 1, max = 300_000) int timeout) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .requestTimeout(timeout)
                .build();
        CohereAskAiChat chat = new CohereAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(config.getRequestTimeout()).hasValue(timeout);
    }

    @Property(tries = 50)
    @Label("Valid model name should always create a chat model")
    void should_create_model_when_model_name_valid(
            @ForAll @AlphaChars @StringLength(min = 1, max = 100) String modelName) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .chatModelName(modelName)
                .build();
        CohereAskAiChat chat = new CohereAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(config.getChatModelName()).hasValue(modelName);
    }

    @Property(tries = 50)
    @Label("All chat types should create models with same configuration")
    void should_create_all_chat_types_with_same_config(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey) {
        AiConfiguration config = AiConfiguration.builder().apiKey(apiKey).build();

        assertThat(new CohereAskAiChat(config).getChatModel()).isNotNull();
        assertThat(new CohereClassifyChat(config).getChatModel()).isNotNull();
        assertThat(new CohereExtractChat(config).getChatModel()).isNotNull();
    }

    @Property(tries = 30)
    @Label("Full configuration should always create a chat model")
    void should_create_model_when_all_options_present(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey,
            @ForAll @DoubleRange(min = 0.0, max = 1.0) double temperature,
            @ForAll @IntRange(min = 1, max = 300_000) int timeout) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey(apiKey)
                .chatModelName("command-r-plus")
                .modelTemperature(temperature)
                .requestTimeout(timeout)
                .build();
        CohereAskAiChat chat = new CohereAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
    }

    @Property(tries = 30)
    @Label("Default API key value should be 'changeMe'")
    void should_use_default_api_key_when_not_set(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String modelName) {
        AiConfiguration config =
                AiConfiguration.builder().chatModelName(modelName).build();

        assertThat(config.getApiKey()).isEqualTo("changeMe");
    }

    @Property(tries = 50)
    @Label("Custom base URL should be preserved")
    void should_preserve_custom_base_url(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey(apiKey)
                .baseUrl("https://custom.cohere.endpoint/v1/")
                .build();
        CohereAskAiChat chat = new CohereAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(config.getBaseUrl()).hasValue("https://custom.cohere.endpoint/v1/");
    }
}
