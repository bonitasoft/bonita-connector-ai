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
package org.bonitasoft.connectors.ai.classify;

import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AbstractAiChat;
import org.bonitasoft.connectors.ai.AiChat;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.connectors.utils.AiResponse;
import org.bonitasoft.connectors.utils.IOs;

@Slf4j
public abstract class ClassifyAiChat<T extends ChatLanguageModel> extends AbstractAiChat<T>
        implements AiChat<T>, ClassifyChat {

    protected String systemPrompt;
    protected String userPrompt;
    protected final AiConfiguration configuration;

    protected ClassifyAiChat(AiConfiguration configuration) {
        this.configuration = configuration;
        systemPrompt = IOs.readAsString("/prompt/classify/system.txt");
        userPrompt = IOs.readAsString("/prompt/classify/user.txt");
    }

    @Override
    public AiConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public String classify(List<String> categories, UserDocument document) {

        var messages = new ArrayList<ChatMessage>();
        // System prompt
        var systemMessage = SystemMessage.from(systemPrompt);
        messages.add(systemMessage);
        // User prompt
        var categoriesForPrompt = String.join("\n   - ", categories);
        Prompt prompt = PromptTemplate.from(userPrompt).apply(Map.of("categories", categoriesForPrompt));
        var userMessage = prompt.toUserMessage();
        messages.add(userMessage);
        // Doc
        var docMessage = newDocMessage(document);
        messages.add(docMessage);

        var chatRequest = ChatRequest.builder().messages(messages).build();
        ChatResponse chatResponse = getChatModel().chat(chatRequest);
        return AiResponse.ensureJson(chatResponse.aiMessage().text());
    }
}
