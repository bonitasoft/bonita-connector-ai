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
package org.bonitasoft.connectors.ai.ask;

import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import java.util.ArrayList;
import org.bonitasoft.connectors.ai.AbstractAiChat;
import org.bonitasoft.connectors.ai.AiChat;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.connectors.utils.AiResponse;

public abstract class AskAiChat<T extends ChatModel> extends AbstractAiChat<T> implements AiChat<T>, AskChat {

    protected final AiConfiguration configuration;

    protected AskAiChat(AiConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public AiConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public String ask(String systemPrompt, String userPrompt, String jsonSchema, UserDocument document) {

        var messages = new ArrayList<ChatMessage>();

        var systemMessage = SystemMessage.from(systemPrompt);
        messages.add(systemMessage);

        var userPromptText = new StringBuilder(userPrompt);
        if (jsonSchema != null && !jsonSchema.isEmpty()) {
            userPromptText.append(
                    """
                            **Instructions:**
                            1. If the information is not explicitly stated, use context and reasoning to infer the answer.
                            2. If a requested piece of information is not present in the text, clearly indicate that it is "Absent" without any additional explanation or commentary.
                            5. Follow the format instructions precisely.
                            6. Format the answer information clearly and structured in JSON format and formatted according to this JSON schema:
                            """);
            userPromptText.append("\n   ").append(jsonSchema);
            userPromptText.append(
                    """
                            7. Do not include markdown in the output.
                            8. Do not include the JSON schema in the JSON output.
                            9. Answer must only contain the JSON output.
                            10.Do not include ```json ``` in the output.
                            """);
        }
        var userMessage = UserMessage.from(userPromptText.toString());
        messages.add(userMessage);

        if (document != null) {
            var docMessage = newDocMessage(document);
            messages.add(docMessage);
        }

        var chatRequest = ChatRequest.builder().messages(messages).build();
        ChatResponse chatResponse = getChatModel().chat(chatRequest);

        if (jsonSchema != null && !jsonSchema.isEmpty()) {
            return AiResponse.ensureJson(chatResponse.aiMessage().text());
        }
        return chatResponse.aiMessage().text();
    }
}
