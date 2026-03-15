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

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AzureOpenAiAskConnectorTest {

    @Nested
    class ConnectorInstantiation {
        @Test
        void should_create_connector_instance() {
            var connector = new AzureOpenAiAskConnector();
            assertThat(connector).isNotNull();
        }
    }

    @Nested
    class ChatModelCreation {
        @Test
        void should_create_chat_model_with_configuration() {
            var configuration = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .apiVersion("2024-10-21")
                    .build();
            var chat = new AzureOpenAiAskChat(configuration);
            AzureOpenAiChatModel model = chat.getChatModel();
            assertThat(model).isNotNull();
        }

        @Test
        void should_use_default_api_version_when_not_specified() {
            var configuration = AiConfiguration.builder()
                    .apiKey("test-key")
                    .baseUrl("https://test.openai.azure.com")
                    .chatModelName("gpt-4o")
                    .build();
            var chat = new AzureOpenAiAskChat(configuration);
            AzureOpenAiChatModel model = chat.getChatModel();
            assertThat(model).isNotNull();
        }
    }
}
