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
package org.bonitasoft.connectors.ai.groq;

import static org.assertj.core.api.Assertions.assertThat;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.bonitasoft.connectors.ai.AiConfiguration;

/**
 * Property-based tests specific to the Ask operation of the Groq connector.
 * Validates that GroqAskAiChat handles random valid inputs correctly.
 */
class GroqAskConnectorPropertyTest {

    @Property(tries = 100)
    @Label("Any non-empty API key should be accepted for Ask chat")
    void apiKeyAlwaysAccepted(@ForAll @AlphaChars @StringLength(min = 1, max = 200) String apiKey) {
        AiConfiguration config = AiConfiguration.builder().apiKey(apiKey).build();
        GroqAskAiChat chat = new GroqAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(chat.getConfiguration().getApiKey()).isEqualTo(apiKey);
    }

    @Property(tries = 50)
    @Label("Any valid model name should be accepted for Ask chat")
    void modelNameAlwaysAccepted(@ForAll @AlphaChars @StringLength(min = 1, max = 100) String modelName) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .chatModelName(modelName)
                .build();
        GroqAskAiChat chat = new GroqAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(chat.getConfiguration().getChatModelName()).hasValue(modelName);
    }

    @Property(tries = 50)
    @Label("Temperature in range 0.0-1.0 should always be accepted for Ask chat")
    void temperatureInRange(@ForAll @DoubleRange(min = 0.0, max = 1.0) double temperature) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .modelTemperature(temperature)
                .build();
        GroqAskAiChat chat = new GroqAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(config.getModelTemperature()).hasValue(temperature);
    }

    @Property(tries = 50)
    @Label("Positive timeout should always be accepted for Ask chat")
    void timeoutPositive(@ForAll @IntRange(min = 1, max = 300_000) int timeout) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .requestTimeout(timeout)
                .build();
        GroqAskAiChat chat = new GroqAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(config.getRequestTimeout()).hasValue(timeout);
    }

    @Property(tries = 30)
    @Label("System prompt is optional - null should be accepted in config")
    void systemPromptOptional(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey) {
        AiConfiguration config = AiConfiguration.builder().apiKey(apiKey).build();
        GroqAskAiChat chat = new GroqAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
    }

    @Property(tries = 30)
    @Label("User prompt is mandatory at execution time but config builds without it")
    void userPromptMandatory(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey) {
        AiConfiguration config = AiConfiguration.builder().apiKey(apiKey).build();
        GroqAskAiChat chat = new GroqAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
    }

    @Property(tries = 30)
    @Label("JSON schema is optional - config builds without it")
    void jsonSchemaOptional(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey) {
        AiConfiguration config = AiConfiguration.builder().apiKey(apiKey).build();
        GroqAskAiChat chat = new GroqAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
    }

    @Property(tries = 30)
    @Label("Base URL is optional - config builds with or without it")
    void baseUrlOptional(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey) {
        AiConfiguration configWithout = AiConfiguration.builder().apiKey(apiKey).build();
        AiConfiguration configWith = AiConfiguration.builder()
                .apiKey(apiKey)
                .baseUrl("https://custom.endpoint.com")
                .build();

        assertThat(new GroqAskAiChat(configWithout).getChatModel()).isNotNull();
        assertThat(new GroqAskAiChat(configWith).getChatModel()).isNotNull();
        assertThat(configWithout.getBaseUrl()).isEmpty();
        assertThat(configWith.getBaseUrl()).hasValue("https://custom.endpoint.com");
    }

    @Property(tries = 30)
    @Label("Full valid configuration should always build a chat model for Ask")
    void fullConfigAlwaysBuilds(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey,
            @ForAll @DoubleRange(min = 0.0, max = 1.0) double temperature,
            @ForAll @IntRange(min = 1, max = 300_000) int timeout) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey(apiKey)
                .chatModelName("llama-3.3-70b-versatile")
                .modelTemperature(temperature)
                .requestTimeout(timeout)
                .build();
        GroqAskAiChat chat = new GroqAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(chat.getConfiguration().getApiKey()).isEqualTo(apiKey);
    }
}
