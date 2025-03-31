package org.bonitasoft.connectors.ai.ask;

import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import java.util.ArrayList;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiChat;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.connectors.ai.langchain4j.UserDocumentSource;

@Slf4j
public abstract class AskAiChat<T extends ChatLanguageModel> implements AiChat<T>, AskChat {

    protected final AiConfiguration configuration;

    public AskAiChat(AiConfiguration configuration) {
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
                            """);
        }
        var userMessage = UserMessage.from(userPromptText.toString());
        messages.add(userMessage);

        if (document != null) {
            Content content =
                    switch (document.mimeType()) {
                        case "image/png", "image/jpg", "image/jpeg" -> ImageContent.from(
                                Base64.getEncoder().encodeToString(document.data()), document.mimeType());
                        default -> {
                            // Default to Tika parser support and extracting text.
                            var doc = DocumentLoader.load(
                                    new UserDocumentSource(document), new ApacheTikaDocumentParser());
                            yield TextContent.from(doc.text());
                        }
                    };
            var docMessage = UserMessage.from(content);
            messages.add(docMessage);
        }

        var chatRequest = ChatRequest.builder().messages(messages).build();
        ChatResponse chatResponse = getChatModel().chat(chatRequest);
        return chatResponse.aiMessage().text();
    }
}
