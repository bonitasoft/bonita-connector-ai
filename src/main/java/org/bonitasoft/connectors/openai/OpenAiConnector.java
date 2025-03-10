package org.bonitasoft.connectors.openai;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.model.openai.OpenAiChatModel.OpenAiChatModelBuilder;
import java.io.ByteArrayInputStream;
import org.bonitasoft.engine.connector.ConnectorException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAiConnector extends AbstractOpenAiConnector {

    private static final Logger log = LoggerFactory.getLogger(OpenAiConnector.class.getName());

    /**
     * @return
     * @throws ConnectorException
     */
    @Override
    protected String doExecute() throws ConnectorException {
        String prompt = getOpenAiConfiguration()
                .getSourceDocumentRef()
                .map(this::getDocContent)
                .map(docContent -> getOpenAiConfiguration().getUserPrompt() + "\n----\n" + docContent)
                .orElse(getOpenAiConfiguration().getUserPrompt());
        return getOpenAiAssistant().chat(prompt);
    }

    @NotNull
    private String getDocContent(String ref) {
        byte[] docData = getDocumentLoader().load(ref);
        var parser = new ApacheTikaDocumentParser();
        try (var docStream = new ByteArrayInputStream(docData)) {
            Document doc = parser.parse(docStream);
            return doc.text();
        } catch (Exception e) {
            throw new OpenAiConnectorException("Failed to read document content for ref: " + ref, e);
        }
    }

    @Override
    protected OpenAiChatModelBuilder customizeChatModelBuilder(OpenAiChatModelBuilder chatModelBuilder) {
        if (log.isDebugEnabled()) {
            chatModelBuilder.logRequests(true).logResponses(true);
        }
        getOpenAiConfiguration().getModelTemperature().ifPresent(chatModelBuilder::temperature);
        return chatModelBuilder;
    }
}
