package org.bonitasoft.connectors.openai.classify;

import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.openai.OpenAiChat;
import org.bonitasoft.connectors.openai.OpenAiConfiguration;
import org.bonitasoft.connectors.openai.doc.UserDocument;
import org.bonitasoft.connectors.openai.doc.UserDocumentSource;
import org.bonitasoft.connectors.utils.IOs;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
public class OpenAiClassifyChat extends OpenAiChat implements ClassifyChat {


    private String systemPrompt;
    private String userPrompt;
    private final OpenAiChatModel chatModel;

    public OpenAiClassifyChat(OpenAiConfiguration configuration) {
        super(configuration);
        var chatModelBuilder = getChatModelBuilder(configuration);
        // LLM req/res logs
        if (log.isDebugEnabled()) {
            chatModelBuilder.logRequests(true).logResponses(true);
        }
        this.chatModel = chatModelBuilder.build();

        systemPrompt = IOs.readAsString("/prompt/classify/system.txt");
        userPrompt = IOs.readAsString("/prompt/classify/user.txt");
    }

    @Override
    public String classify(List<String> categories, UserDocument document) {
        var contents = new ArrayList<Content>();
        // System prompt
        contents.add(TextContent.from(systemPrompt));
        // User prompt
        var categoriesForPrompt = String.join("\n   - ", categories);
        Prompt prompt = PromptTemplate.from(userPrompt).apply(Map.of("categories", categoriesForPrompt));
        contents.add(TextContent.from(prompt.text()));
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
        contents.add(content);
        var userMessage = UserMessage.from(contents);
        ChatResponse chatResponse = chatModel.chat(userMessage);
        return chatResponse.aiMessage().text();
    }
}
