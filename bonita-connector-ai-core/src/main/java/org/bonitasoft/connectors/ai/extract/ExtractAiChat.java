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
package org.bonitasoft.connectors.ai.extract;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bonitasoft.connectors.ai.AbstractAiChat;
import org.bonitasoft.connectors.ai.AiChat;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.connectors.utils.AiResponse;
import org.bonitasoft.connectors.utils.IOs;

public abstract class ExtractAiChat<T extends ChatLanguageModel> extends AbstractAiChat<T>
        implements AiChat<T>, ExtractChat {

    protected final String systemPrompt;
    protected final String userPrompt;
    protected final String userPromptWthJsonSchema;
    protected final AiConfiguration configuration;

    protected ExtractAiChat(AiConfiguration configuration) {
        this.configuration = configuration;
        systemPrompt = IOs.readAsString("/prompt/extract/system.txt");
        userPrompt = IOs.readAsString("/prompt/extract/user.txt");
        userPromptWthJsonSchema = IOs.readAsString("/prompt/extract/user_with_json_schema.txt");
    }

    @Override
    public AiConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public String extract(UserDocument document, List<String> fields) {
        var fieldsToExtractForPrompt = String.join("\n   - ", fields);
        Prompt prompt = PromptTemplate.from(userPrompt).apply(Map.of("fieldsToExtract", fieldsToExtractForPrompt));
        return doExtract(document, prompt.text());
    }

    @Override
    public String extract(UserDocument document, String jsonSchema) {
        Prompt prompt = PromptTemplate.from(userPromptWthJsonSchema).apply(Map.of("jsonSchema", jsonSchema));
        return doExtract(document, prompt.text());
    }

    String doExtract(UserDocument document, String userText) {
        var messages = new ArrayList<ChatMessage>();
        // System prompt
        var systemMessage = SystemMessage.from(systemPrompt);
        messages.add(systemMessage);
        // User message
        var userMessage = UserMessage.from(userText);
        messages.add(userMessage);
        // Doc
        var docMessage = newDocMessage(document);
        messages.add(docMessage);
        var chatRequest = ChatRequest.builder().messages(messages).build();
        ChatResponse chatResponse = getChatModel().chat(chatRequest);
        return AiResponse.ensureJson(chatResponse.aiMessage().text());
    }
}
