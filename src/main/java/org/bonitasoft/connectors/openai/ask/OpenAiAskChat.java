package org.bonitasoft.connectors.openai.ask;

import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.openai.OpenAiConfiguration;
import org.bonitasoft.connectors.openai.doc.UserDocument;
import org.bonitasoft.connectors.openai.doc.UserDocumentSource;

@Slf4j
public class OpenAiAskChat implements AskChat {

    private final OpenAiChatModel chatModel;

    public OpenAiAskChat(OpenAiConfiguration configuration) {
        var chatModelBuilder = OpenAiChatModel.builder();
        // API Key
        chatModelBuilder.apiKey(configuration.getApiKey());
        // Url override
        configuration.getBaseUrl().ifPresent(chatModelBuilder::baseUrl);
        // Chat model name
        chatModelBuilder.modelName(configuration.getChatModelName());
        // LLM req/res logs
        if (log.isDebugEnabled()) {
            chatModelBuilder.logRequests(true).logResponses(true);
        }
        // Temperature
        configuration.getModelTemperature().ifPresent(chatModelBuilder::temperature);
        // Req timeout
        if (configuration.getRequestTimeout().isPresent()) {
            chatModelBuilder.timeout(
                    Duration.of(configuration.getRequestTimeout().get(), ChronoUnit.MILLIS));
        }
        this.chatModel = chatModelBuilder.build();
    }

    @Override
    public String ask(String systemPrompt, String userPrompt, String jsonSchema, UserDocument document) {

        var contents = new ArrayList<Content>();
        contents.add(TextContent.from(systemPrompt));

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
        contents.add(TextContent.from(userPromptText.toString()));

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
            contents.add(content);
        }

        ChatResponse chatResponse = chatModel.chat(UserMessage.from(contents));
        return chatResponse.aiMessage().text();
    }
}
