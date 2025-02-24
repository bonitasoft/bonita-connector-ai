package org.bonitasoft.connectors;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel.OpenAiChatModelBuilder;
import org.bonitasoft.connectors.document.reader.AiDocumentReader;
import org.bonitasoft.connectors.document.reader.TikaAiDocumentReader;
import org.bonitasoft.engine.connector.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class AiConnector extends AbstractAiConnector {

    private static final Logger log = LoggerFactory.getLogger(AiConnector.class.getName());

    private AiDocumentReader aiDocumentReader = new TikaAiDocumentReader();

    /**
     * @param chatModel
     * @param userPrompt
     * @return
     * @throws ConnectorException
     */
    @Override
    protected String doExecute(ChatLanguageModel chatModel, String userPrompt, byte[] lastDocument) throws ConnectorException {

        if (lastDocument != null && lastDocument.length > 0) {
            var doc = aiDocumentReader.read(lastDocument);
            if (!doc.getContent().isBlank()) {
                var augmentedPrompt = userPrompt.replace("{document}", doc.getContent());
                return chatModel.generate(augmentedPrompt);
            }
        }

        log.debug("user prompt: {}", userPrompt);
        return chatModel.generate(userPrompt);
    }

    @Override
    protected OpenAiChatModelBuilder customizeChatModelBuilder(OpenAiChatModelBuilder chatModelBuilder) {
        if (log.isDebugEnabled()) {
            chatModelBuilder
                    .logRequests(true)
                    .logResponses(true)
            ;
        }
        return chatModelBuilder
                .timeout(Duration.ofMinutes(5))
                .temperature(0.0)
                ;
    }

    void setDocumentReader(AiDocumentReader aiDocumentReader) {
        this.aiDocumentReader = aiDocumentReader;
    }
}
