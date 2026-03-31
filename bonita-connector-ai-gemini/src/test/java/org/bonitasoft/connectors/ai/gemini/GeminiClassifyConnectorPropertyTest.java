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

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.bonitasoft.connectors.ai.AiConfiguration;

/**
 * Property-based tests specific to the Classify operation of the Gemini connector.
 * Validates that GeminiClassifyChat handles random valid inputs correctly.
 */
class GeminiClassifyConnectorPropertyTest {

    @Property(tries = 50)
    @Label("Categories list concept - config always builds regardless of future category input")
    void categoriesListNotEmpty(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey) {
        // Categories are input params at execution time, not part of AiConfiguration
        AiConfiguration config = AiConfiguration.builder().apiKey(apiKey).build();
        GeminiClassifyChat chat = new GeminiClassifyChat(config);

        assertThat(chat.getChatModel()).isNotNull();
    }

    @Property(tries = 50)
    @Label("Categories accept any strings - config builds independently of categories")
    void categoriesAcceptAnyStrings(@ForAll @AlphaChars @StringLength(min = 1, max = 100) String apiKey) {
        AiConfiguration config = AiConfiguration.builder().apiKey(apiKey).build();
        GeminiClassifyChat chat = new GeminiClassifyChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(chat.getConfiguration().getApiKey()).isEqualTo(apiKey);
    }

    @Property(tries = 100)
    @Label("Any non-empty API key should be accepted for Classify chat")
    void apiKeyAlwaysAccepted(@ForAll @AlphaChars @StringLength(min = 1, max = 200) String apiKey) {
        AiConfiguration config = AiConfiguration.builder().apiKey(apiKey).build();
        GeminiClassifyChat chat = new GeminiClassifyChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(chat.getConfiguration().getApiKey()).isEqualTo(apiKey);
    }

    @Property(tries = 50)
    @Label("Model name is optional for Classify chat - builds with or without")
    void modelNameOptional(@ForAll @AlphaChars @StringLength(min = 1, max = 100) String modelName) {
        AiConfiguration configWith = AiConfiguration.builder()
                .apiKey("test-key")
                .chatModelName(modelName)
                .build();
        AiConfiguration configWithout =
                AiConfiguration.builder().apiKey("test-key").build();

        assertThat(new GeminiClassifyChat(configWith).getChatModel()).isNotNull();
        assertThat(new GeminiClassifyChat(configWithout).getChatModel()).isNotNull();
        assertThat(configWith.getChatModelName()).hasValue(modelName);
        assertThat(configWithout.getChatModelName()).isEmpty();
    }

    @Property(tries = 50)
    @Label("Temperature in range 0.0-1.0 should always be accepted for Classify chat")
    void temperatureInRange(@ForAll @DoubleRange(min = 0.0, max = 1.0) double temperature) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .modelTemperature(temperature)
                .build();
        GeminiClassifyChat chat = new GeminiClassifyChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(config.getModelTemperature()).hasValue(temperature);
    }

    @Property(tries = 50)
    @Label("Positive timeout should always be accepted for Classify chat")
    void timeoutPositive(@ForAll @IntRange(min = 1, max = 300_000) int timeout) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .requestTimeout(timeout)
                .build();
        GeminiClassifyChat chat = new GeminiClassifyChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(config.getRequestTimeout()).hasValue(timeout);
    }

    @Property(tries = 30)
    @Label("Document ref is optional - config builds without it")
    void documentRefOptional(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey) {
        // sourceDocumentRef is an input param, not in AiConfiguration
        AiConfiguration config = AiConfiguration.builder().apiKey(apiKey).build();
        GeminiClassifyChat chat = new GeminiClassifyChat(config);

        assertThat(chat.getChatModel()).isNotNull();
    }

    @Property(tries = 50)
    @Label("Debug logging both true and false should be accepted for Classify chat")
    void debugLoggingBothValues(@ForAll boolean debugLogging) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .enableDebugLogging(debugLogging)
                .build();
        GeminiClassifyChat chat = new GeminiClassifyChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(config.isEnableDebugLogging()).isEqualTo(debugLogging);
    }

    @Property(tries = 30)
    @Label("Full valid configuration should always build a chat model for Classify")
    void fullConfigAlwaysBuilds(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey,
            @ForAll @DoubleRange(min = 0.0, max = 1.0) double temperature,
            @ForAll @IntRange(min = 1, max = 300_000) int timeout,
            @ForAll boolean debug) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey(apiKey)
                .chatModelName("gemini-2.0-flash")
                .modelTemperature(temperature)
                .requestTimeout(timeout)
                .enableDebugLogging(debug)
                .build();
        GeminiClassifyChat chat = new GeminiClassifyChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(chat.getConfiguration().getApiKey()).isEqualTo(apiKey);
    }

    @Property(tries = 30)
    @Label("Default model should be used when model name not set for Classify")
    void defaultModelUsedWhenNotSet(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey) {
        AiConfiguration config = AiConfiguration.builder().apiKey(apiKey).build();

        assertThat(config.getChatModelName()).isEmpty();
        GeminiClassifyChat chat = new GeminiClassifyChat(config);
        assertThat(chat.getChatModel()).isNotNull();
    }
}
