package org.bonitasoft.connectors.openai;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.model.openai.OpenAiChatModel.OpenAiChatModelBuilder;
import java.io.ByteArrayInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
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

        String prompt = getUserPrompt();

        String ref = getSourceDocumentRef();
        if (ref != null && !ref.isEmpty()) {
            prompt = appendDocToPrompt(ref, prompt);
        }

        return getOpenAiAssistant().chat(prompt);
    }

    @NotNull
    private String appendDocToPrompt(String ref, String prompt) throws ConnectorException {
        byte[] docData = getDocumentLoader().load(ref);
        var parser = new ApacheTikaDocumentParser(AutoDetectParser::new, null, Metadata::new, null);
        try (var docStream = new ByteArrayInputStream(docData)) {
            Document doc = parser.parse(docStream);
            prompt += "\n----\n" + doc.text();
        } catch (Exception e) {
            throw new ConnectorException(e);
        }
        return prompt;
    }

    @Override
    protected OpenAiChatModelBuilder customizeChatModelBuilder(OpenAiChatModelBuilder chatModelBuilder) {
        if (log.isDebugEnabled()) {
            chatModelBuilder.logRequests(true).logResponses(true);
        }
        getInputValue(MODEL_TEMPERATURE, Double.class).ifPresent(chatModelBuilder::temperature);
        return chatModelBuilder;
    }
}
