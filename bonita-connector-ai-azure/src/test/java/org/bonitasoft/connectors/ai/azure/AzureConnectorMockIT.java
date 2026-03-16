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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import java.util.List;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.UserDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("integration")
@DisplayName("Azure AI Foundry Connector Mock Integration Tests")
class AzureConnectorMockIT {

    AiConfiguration config = AiConfiguration.builder().apiKey("test-key").build();

    private AzureOpenAiChatModel mockModel(String text) {
        AzureOpenAiChatModel m = mock(AzureOpenAiChatModel.class);
        ChatResponse r = ChatResponse.builder().aiMessage(AiMessage.from(text)).build();
        when(m.chat(any(ChatRequest.class))).thenReturn(r);
        return m;
    }

    @Nested
    @DisplayName("Ask Chat")
    class AskMock {
        @Test
        void should_return_response_for_simple_prompt() {
            var chat = new AzureOpenAiAskChat(config) {
                @Override
                public AzureOpenAiChatModel getChatModel() {
                    return mockModel("Paris is the capital of France.");
                }
            };
            String result = chat.ask("Assistant.", "Capital of France?", null, (UserDocument) null);
            assertThat(result).contains("Paris");
        }

        @Test
        void should_return_json_response() {
            var chat = new AzureOpenAiAskChat(config) {
                @Override
                public AzureOpenAiChatModel getChatModel() {
                    return mockModel("{\"answer\": \"42\"}");
                }
            };
            String result = chat.ask("JSON.", "Answer?", "{\"type\":\"object\"}", (UserDocument) null);
            assertThat(result).contains("42");
        }

        @Test
        void should_handle_multiple_documents() {
            var chat = new AzureOpenAiAskChat(config) {
                @Override
                public AzureOpenAiChatModel getChatModel() {
                    return mockModel("Both docs analyzed.");
                }
            };
            var doc1 = new UserDocument("text/plain", "Doc 1".getBytes());
            var doc2 = new UserDocument("text/plain", "Doc 2".getBytes());
            String result = chat.ask("Analyze.", "Compare.", null, List.of(doc1, doc2));
            assertThat(result).contains("analyzed");
        }

        @Test
        void should_handle_empty_document_list() {
            var chat = new AzureOpenAiAskChat(config) {
                @Override
                public AzureOpenAiChatModel getChatModel() {
                    return mockModel("No docs.");
                }
            };
            assertThat(chat.ask("S.", "U.", null, List.of())).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Classify Chat")
    class ClassifyMock {
        @Test
        void should_classify_document() {
            var chat = new AzureOpenAiClassifyChat(config) {
                @Override
                public AzureOpenAiChatModel getChatModel() {
                    return mockModel("{\"category\":\"INVOICE\",\"confidence\":0.95}");
                }
            };
            var doc = new UserDocument("text/plain", "Invoice #123".getBytes());
            assertThat(chat.classify(List.of("INVOICE", "CONTRACT"), doc)).contains("INVOICE");
        }
    }

    @Nested
    @DisplayName("Extract Chat")
    class ExtractMock {
        @Test
        void should_extract_fields() {
            var chat = new AzureOpenAiExtractChat(config) {
                @Override
                public AzureOpenAiChatModel getChatModel() {
                    return mockModel("{\"firstName\":\"Jean\",\"lastName\":\"Dupont\"}");
                }
            };
            var doc = new UserDocument("text/plain", "Name: Jean Dupont".getBytes());
            assertThat(chat.extract(doc, List.of("firstName", "lastName")))
                    .contains("Jean")
                    .contains("Dupont");
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class Errors {
        @Test
        void should_propagate_api_error() {
            var chat = new AzureOpenAiAskChat(config) {
                @Override
                public AzureOpenAiChatModel getChatModel() {
                    AzureOpenAiChatModel m = mock(AzureOpenAiChatModel.class);
                    when(m.chat(any(ChatRequest.class))).thenThrow(new RuntimeException("API Error 500"));
                    return m;
                }
            };
            assertThatThrownBy(() -> chat.ask("S.", "U.", null, (UserDocument) null))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("API Error");
        }

        @Test
        void should_handle_empty_response() {
            var chat = new AzureOpenAiAskChat(config) {
                @Override
                public AzureOpenAiChatModel getChatModel() {
                    return mockModel("");
                }
            };
            assertThat(chat.ask("S.", "U.", null, (UserDocument) null)).isEmpty();
        }
    }
}
