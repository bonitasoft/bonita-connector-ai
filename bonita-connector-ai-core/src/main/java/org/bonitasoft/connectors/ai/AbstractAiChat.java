package org.bonitasoft.connectors.ai;

import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.ChatLanguageModel;
import java.util.Base64;
import org.bonitasoft.connectors.ai.langchain4j.UserDocumentSource;

public abstract class AbstractAiChat<T extends ChatLanguageModel> implements AiChat<T> {

    protected ChatMessage newDocMessage(UserDocument document) {
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
        return UserMessage.from(content);
    }
}
