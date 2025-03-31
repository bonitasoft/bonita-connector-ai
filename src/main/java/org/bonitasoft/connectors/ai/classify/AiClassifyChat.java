package org.bonitasoft.connectors.ai.classify;

import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiChat;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.doc.UserDocument;
import org.bonitasoft.connectors.ai.doc.UserDocumentSource;
import org.bonitasoft.connectors.utils.IOs;

@Slf4j
public abstract class AiClassifyChat<T extends ChatLanguageModel> implements AiChat<T>, ClassifyChat {

    protected String systemPrompt;
    protected String userPrompt;
    protected final AiConfiguration configuration;

    public AiClassifyChat(AiConfiguration configuration) {
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
        Content content =
                switch (document.mimeType()) {
                    case "image/png", "image/jpg", "image/jpeg" -> ImageContent.from(
                            Base64.getEncoder().encodeToString(document.data()), document.mimeType());
                    default -> {
                        // Default to Tika parser support and extracting text.
                        var doc = DocumentLoader.load(new UserDocumentSource(document), new ApacheTikaDocumentParser());
                        yield TextContent.from(doc.text());
                    }
                };
        var docMessage = UserMessage.from(content);
        messages.add(docMessage);
        var chatRequest = ChatRequest.builder().messages(messages).build();
        ChatResponse chatResponse = getChatModel().chat(chatRequest);
        return chatResponse.aiMessage().text();
    }
}
