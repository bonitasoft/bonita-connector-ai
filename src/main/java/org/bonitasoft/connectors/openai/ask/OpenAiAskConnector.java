package org.bonitasoft.connectors.openai.ask;

import static org.bonitasoft.connectors.openai.ask.AskConfiguration.*;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.openai.OpenAiConnector;
import org.bonitasoft.connectors.openai.doc.UserDocument;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

@Slf4j
@Getter
@Setter
public class OpenAiAskConnector extends OpenAiConnector {

    private AskConfiguration askConfiguration;
    private AskChat chat;

    @Override
    protected void validateConfiguration() throws ConnectorValidationException {
        try {
            this.askConfiguration = AskConfiguration.builder()
                    .systemPrompt((String) getInputParameter(SYSTEM_PROMPT))
                    .userPrompt((String) getInputParameter(USER_PROMPT))
                    .sourceDocumentRef((String) getInputParameter(SOURCE_DOCUMENT_REF))
                    .outputJsonSchema((String) getInputParameter(OUTPUT_JSON_SCHEMA))
                    .build();
        } catch (ClassCastException e) {
            throw new ConnectorValidationException("Some input parameter is not of expected type : " + e.getMessage());
        }
        // Specific validation
        if (askConfiguration.getUserPrompt() == null) {
            throw new ConnectorValidationException("UserPrompt is required");
        }
        if (askConfiguration.getUserPrompt().isBlank()) {
            throw new ConnectorValidationException("UserPrompt is blank or empty");
        }
    }

    @Override
    public void connect() throws ConnectorException {
        chat = new OpenAiAskChat(configuration);
    }

    /**
     * @return
     * @throws ConnectorException
     */
    @Override
    protected Object doExecute() throws ConnectorException {

        // Try to read doc if any
        UserDocument userDocument = askConfiguration
                .getSourceDocumentRef()
                .map(this::getUserDocument)
                .orElse(null);

        return chat.ask(
                askConfiguration.getSystemPrompt(),
                askConfiguration.getUserPrompt(),
                askConfiguration.getOutputJsonSchema().orElse(null),
                userDocument);
    }
}
