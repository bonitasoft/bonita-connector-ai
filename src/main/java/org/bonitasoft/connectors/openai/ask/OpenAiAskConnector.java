package org.bonitasoft.connectors.openai.ask;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.openai.OpenAiConnector;
import org.bonitasoft.connectors.openai.doc.UserDocument;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

import static org.bonitasoft.connectors.openai.ask.AskConfiguration.*;

@Slf4j
@Getter
@Setter
public class OpenAiAskConnector extends OpenAiConnector {

    private AskConfiguration askConfiguration;
    private AskChat chat;

    @Override
    protected void validateConfiguration() throws ConnectorValidationException {
        try {
            var builder = AskConfiguration.builder();
            getInputValue(SYSTEM_PROMPT, String.class).ifPresent(builder::systemPrompt);
            getInputValue(USER_PROMPT, String.class).ifPresent(builder::userPrompt);
            getInputValue(SOURCE_DOCUMENT_REF, String.class).ifPresent(builder::sourceDocumentRef);
            getInputValue(OUTPUT_JSON_SCHEMA, String.class).ifPresent(builder::outputJsonSchema);
            this.askConfiguration = builder.build();
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
