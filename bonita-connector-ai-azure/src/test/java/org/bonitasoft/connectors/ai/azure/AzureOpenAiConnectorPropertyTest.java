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

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.bonitasoft.connectors.ai.AiConfiguration;

class AzureOpenAiConnectorPropertyTest {

    @Property(tries = 100)
    @Label("Valid configuration should always create a chat model")
    void should_create_model_when_config_valid(
            @ForAll @AlphaChars @StringLength(min = 1, max = 100) String apiKey,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String deploymentName) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey(apiKey)
                .baseUrl("https://test.openai.azure.com")
                .chatModelName(deploymentName)
                .build();
        AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);
        assertThat(chat.getChatModel()).isNotNull();
    }

    @Property(tries = 50)
    @Label("Valid temperature range should always create a chat model")
    void should_create_model_when_temperature_in_range(@ForAll @DoubleRange(min = 0.0, max = 1.0) double temperature) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .baseUrl("https://test.openai.azure.com")
                .chatModelName("gpt-4o")
                .modelTemperature(temperature)
                .build();
        AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);
        assertThat(chat.getChatModel()).isNotNull();
        assertThat(config.getModelTemperature()).hasValue(temperature);
    }

    @Property(tries = 50)
    @Label("Positive timeout should always create a chat model")
    void should_create_model_when_timeout_positive(@ForAll @IntRange(min = 1, max = 300_000) int timeout) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .baseUrl("https://test.openai.azure.com")
                .chatModelName("gpt-4o")
                .requestTimeout(timeout)
                .build();
        AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);
        assertThat(chat.getChatModel()).isNotNull();
        assertThat(config.getRequestTimeout()).hasValue(timeout);
    }

    @Property(tries = 50)
    @Label("Custom API version should always create a chat model")
    void should_create_model_when_api_version_custom(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String apiVersion) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .baseUrl("https://test.openai.azure.com")
                .chatModelName("gpt-4o")
                .apiVersion(apiVersion)
                .build();
        AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);
        assertThat(chat.getChatModel()).isNotNull();
        assertThat(config.getApiVersion()).hasValue(apiVersion);
    }

    @Property(tries = 50)
    @Label("All chat types should create models with same configuration")
    void should_create_all_chat_types_with_same_config(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey(apiKey)
                .baseUrl("https://test.openai.azure.com")
                .chatModelName("gpt-4o")
                .build();
        assertThat(new AzureOpenAiAskChat(config).getChatModel()).isNotNull();
        assertThat(new AzureOpenAiClassifyChat(config).getChatModel()).isNotNull();
        assertThat(new AzureOpenAiExtractChat(config).getChatModel()).isNotNull();
    }

    @Property(tries = 30)
    @Label("Full configuration should always create a chat model")
    void should_create_model_when_all_options_present(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey,
            @ForAll @DoubleRange(min = 0.0, max = 1.0) double temperature,
            @ForAll @IntRange(min = 1, max = 300_000) int timeout) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey(apiKey)
                .baseUrl("https://my-resource.openai.azure.com")
                .chatModelName("gpt-4o")
                .apiVersion("2024-10-21")
                .modelTemperature(temperature)
                .requestTimeout(timeout)
                .build();
        AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);
        assertThat(chat.getChatModel()).isNotNull();
    }

    @Property(tries = 30)
    @Label("Default API key value should be changeMe")
    void should_use_default_api_key_when_not_set(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String deploymentName) {
        AiConfiguration config = AiConfiguration.builder()
                .baseUrl("https://test.openai.azure.com")
                .chatModelName(deploymentName)
                .build();
        assertThat(config.getApiKey()).isEqualTo("changeMe");
    }

    @Property(tries = 30)
    @Label("Default API version should be used when not specified")
    void should_use_default_api_version_when_not_specified(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String apiKey) {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey(apiKey)
                .baseUrl("https://test.openai.azure.com")
                .chatModelName("gpt-4o")
                .build();
        assertThat(config.getApiVersion()).isEmpty();
        AzureOpenAiAskChat chat = new AzureOpenAiAskChat(config);
        assertThat(chat.getChatModel()).isNotNull();
    }
}
