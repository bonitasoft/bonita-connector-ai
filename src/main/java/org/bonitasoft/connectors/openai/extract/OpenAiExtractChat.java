package org.bonitasoft.connectors.openai.extract;

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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.openai.doc.UserDocument;
import org.bonitasoft.connectors.openai.doc.UserDocumentSource;
import org.bonitasoft.connectors.utils.IOs;
import org.bonitasoft.engine.connector.ConnectorException;

@Slf4j
public class OpenAiExtractChat implements ExtractChat {

    private final String systemPrompt;
    private final String userPrompt;
    private final String userPromptWthJsonSchema;

    private final OpenAiChatModel chatModel;

    public OpenAiExtractChat(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
        systemPrompt = IOs.readAsString("/prompt/extract/system.txt");
        userPrompt = IOs.readAsString("/prompt/extract/user.txt");
        userPromptWthJsonSchema = IOs.readAsString("/prompt/extract/user_with_json_schema.txt");
    }

    @Override
    public String extract(UserDocument document, List<String> fields) throws ConnectorException {
        var fieldsToExtractForPrompt = String.join("\n   - ", fields);
        Prompt prompt = PromptTemplate.from(userPrompt).apply(Map.of("fieldsToExtract", fieldsToExtractForPrompt));
        return doExtract(document, prompt.text());
    }

    @Override
    public String extract(UserDocument document, String jsonSchema) throws ConnectorException {
        Prompt prompt = PromptTemplate.from(userPromptWthJsonSchema).apply(Map.of("jsonSchema", jsonSchema));
        return doExtract(document, prompt.text());
    }

    String doExtract(UserDocument document, String userText) {
        var contents = new ArrayList<Content>();
        // System prompt
        contents.add(TextContent.from(systemPrompt));
        // User prompt
        contents.add(TextContent.from(userText));
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
