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
 * Property-based tests specific to the Extract operation of the Gemini connector.
 * Validates that GeminiExtractChat handles random valid inputs correctly.
 */
class GeminiExtractConnectorPropertyTest {

    @Property(tries = 30)
    @Label("Fields to extract is optional - config builds without it")
    void fieldsToExtractOptional(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey) {
        // fieldsToExtract is an input param, not part of AiConfiguration
        AiConfiguration config = AiConfiguration.builder().apiKey(apiKey).build();
        GeminiExtractChat chat = new GeminiExtractChat(config);

        assertThat(chat.getChatModel()).isNotNull();
    }

    @Property(tries = 30)
    @Label("JSON schema is optional - config builds without it for Extract")
    void jsonSchemaOptional(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey) {
        // outputJsonSchema is an input param, not in AiConfiguration
        AiConfiguration config = AiConfiguration.builder().apiKey(apiKey).build();
        GeminiExtractChat chat = new GeminiExtractChat(config);

        assertThat(chat.getChatModel()).isNotNull();
    }

    @Property(tries = 100)
    @Label("Any non-empty API key should be accepted for Extract chat")
    void apiKeyAlwaysAccepted(@ForAll @AlphaChars @StringLength(min = 1, max = 200) String apiKey) {
        AiConfiguration config = AiConfiguration.builder().apiKey(apiKey).build();
        GeminiExtractChat chat = new GeminiExtractChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(chat.getConfiguration().getApiKey()).isEqualTo(apiKey);
    }

    @Property(tries = 50)
    @Label("Model name is optional for Extract chat - builds with or without")
    void modelNameOptional(@ForAll @AlphaChars @StringLength(min = 1, max = 100) String modelName) {
        AiConfiguration configWith = AiConfiguration.builder()
                .apiKey("test-key")
                .chatModelName(modelName)
                .build();
        AiConfiguration configWithout =
                AiConfiguration.builder().apiKey("test-key").build();

        assertThat(new GeminiExtractChat(configWith).getChatModel()).isNotNull();
        assertThat(new GeminiExtractChat(configWithout).getChatModel()).isNotNull();
        assertThat(configWith.getChatModelName()).hasValue(modelName);
        assertThat(configWithout.getChatModelName()).isEmpty();
    }

    @Property(tries = 50)
    @Label("Temperature in range 0.0-1.0 should always be accepted for Extract chat")
    void temperatureInRange(@ForAll @DoubleRange(min = 0.0, max = 1.0) double temperature) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .modelTemperature(temperature)
                .build();
        GeminiExtractChat chat = new GeminiExtractChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(config.getModelTemperature()).hasValue(temperature);
    }

    @Property(tries = 50)
    @Label("Positive timeout should always be accepted for Extract chat")
    void timeoutPositive(@ForAll @IntRange(min = 1, max = 300_000) int timeout) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .requestTimeout(timeout)
                .build();
        GeminiExtractChat chat = new GeminiExtractChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(config.getRequestTimeout()).hasValue(timeout);
    }

    @Property(tries = 30)
    @Label("Document ref is optional - config builds without it for Extract")
    void documentRefOptional(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey) {
        // sourceDocumentRef is an input param, not in AiConfiguration
        AiConfiguration config = AiConfiguration.builder().apiKey(apiKey).build();
        GeminiExtractChat chat = new GeminiExtractChat(config);

        assertThat(chat.getChatModel()).isNotNull();
    }

    @Property(tries = 50)
    @Label("Debug logging both true and false should be accepted for Extract chat")
    void debugLoggingBothValues(@ForAll boolean debugLogging) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .enableDebugLogging(debugLogging)
                .build();
        GeminiExtractChat chat = new GeminiExtractChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(config.isEnableDebugLogging()).isEqualTo(debugLogging);
    }

    @Property(tries = 30)
    @Label("Full valid configuration should always build a chat model for Extract")
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
        GeminiExtractChat chat = new GeminiExtractChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(chat.getConfiguration().getApiKey()).isEqualTo(apiKey);
    }

    @Property(tries = 30)
    @Label("Default model should be used when model name not set for Extract")
    void defaultModelUsedWhenNotSet(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey) {
        AiConfiguration config = AiConfiguration.builder().apiKey(apiKey).build();

        assertThat(config.getChatModelName()).isEmpty();
        GeminiExtractChat chat = new GeminiExtractChat(config);
        assertThat(chat.getChatModel()).isNotNull();
    }
}
