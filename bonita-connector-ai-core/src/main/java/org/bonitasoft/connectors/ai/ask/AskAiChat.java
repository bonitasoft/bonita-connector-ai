package org.bonitasoft.connectors.ai.ask;

import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AbstractAiChat;
import org.bonitasoft.connectors.ai.AiChat;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.connectors.utils.Markdown;

@Slf4j
public abstract class AskAiChat<T extends ChatLanguageModel> extends AbstractAiChat<T> implements AiChat<T>, AskChat {

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
            return Markdown.noJsonBlock(chatResponse.aiMessage().text());
        }
        return chatResponse.aiMessage().text();
    }
}
