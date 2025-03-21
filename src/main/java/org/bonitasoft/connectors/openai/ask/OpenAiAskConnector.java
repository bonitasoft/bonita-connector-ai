package org.bonitasoft.connectors.openai.ask;

import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.openai.AbstractOpenAiConnector;
import org.bonitasoft.connectors.openai.doc.UserDocument;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

@Slf4j
public class OpenAiAskConnector extends AbstractOpenAiConnector {

    private AskConfiguration askConfiguration;

    @Override
    protected void validateConfiguration() throws ConnectorValidationException {
        this.askConfiguration = AskConfiguration.from(getInputParameters());

        // Specific validation
        if (askConfiguration.getUserPrompt() == null) {
            throw new ConnectorValidationException("UserPrompt is required");
        }
        if (askConfiguration.getUserPrompt().isBlank()) {
            throw new ConnectorValidationException("UserPrompt is blank or empty");
        }
    }

    /**
     * @return
     * @throws ConnectorException
     */
    @Override
    protected Object doExecute() throws ConnectorException {

        // Try to read doc
        UserDocument userDocument = askConfiguration
                .getSourceDocumentRef()
                .map(this::getUserDocument)
                .orElse(null);

        AskChat askChat = new OpenAiAskChat(configuration);
        return askChat.ask(
                askConfiguration.getSystemPrompt(),
                askConfiguration.getUserPrompt(),
                askConfiguration.getOutputJsonSchema().orElse(null),
                userDocument);
    }
}
