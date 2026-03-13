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

import org.bonitasoft.connectors.ai.AiConfiguration;
import org.junit.jupiter.api.Test;

class AnthropicAskConnectorTest {

    @Test
    void should_create_connector() throws Exception {
        AnthropicAskConnector connector = new AnthropicAskConnector();
        assertThat(connector).isNotNull();
    }

    @Test
    void should_create_chat_model_with_default_model() {
        AiConfiguration config = AiConfiguration.builder().apiKey("test-key").build();
        AnthropicAskAiChat chat = new AnthropicAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
    }

    @Test
    void should_use_default_model_name() {
        assertThat(AnthropicChat.DEFAULT_MODEL).isEqualTo("claude-sonnet-4-6");
    }

    @Test
    void should_create_chat_with_custom_model() {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .chatModelName("claude-haiku-4-5-20251001")
                .build();
        AnthropicAskAiChat chat = new AnthropicAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(chat.getConfiguration().getChatModelName()).hasValue("claude-haiku-4-5-20251001");
    }

    @Test
    void should_create_chat_with_custom_temperature() {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .modelTemperature(0.3)
                .build();
        AnthropicAskAiChat chat = new AnthropicAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(chat.getConfiguration().getModelTemperature()).hasValue(0.3);
    }

    @Test
    void should_create_chat_with_custom_timeout() {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .requestTimeout(60000)
                .build();
        AnthropicAskAiChat chat = new AnthropicAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(chat.getConfiguration().getRequestTimeout()).hasValue(60000);
    }

    @Test
    void should_create_chat_with_custom_base_url() {
        AiConfiguration config = AiConfiguration.builder()
                .apiKey("test-key")
                .baseUrl("https://custom-proxy.example.com")
                .build();
        AnthropicAskAiChat chat = new AnthropicAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
        assertThat(chat.getConfiguration().getBaseUrl()).hasValue("https://custom-proxy.example.com");
    }
}
