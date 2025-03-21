package org.bonitasoft.connectors.openai.extract;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.openai.AbstractOpenAiConnector;
import org.bonitasoft.connectors.openai.OpenAiConnectorException;
import org.bonitasoft.connectors.openai.doc.UserDocument;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

@Slf4j
public class OpenAiExtractDataConnector extends AbstractOpenAiConnector {

    private ExtractConfiguration extractConfiguration;

    @Override
    protected void validateConfiguration() throws ConnectorValidationException {
        extractConfiguration = ExtractConfiguration.from(getOutputParameters());
    }

    /**
     * @return
     * @throws ConnectorException
     */
    @Override
    protected Object doExecute() throws ConnectorException {
        // Try to read doc
        if (extractConfiguration.getSourceDocumentRef().isEmpty()) {
            throw new OpenAiConnectorException("Failed to load document to analyze.");
        }
        var docRef = extractConfiguration.getSourceDocumentRef().get();
        UserDocument userDocument = getUserDocument(docRef);

        ExtractChat chat = new OpenAiExtractChat(chatModel);

        if (extractConfiguration.getOutputJsonSchema().isPresent()) {
            String jsonSchema = extractConfiguration.getOutputJsonSchema().get();
            return chat.extract(userDocument, jsonSchema);
        }
        if (extractConfiguration.getFieldsToExtract().isPresent()) {
            List<String> fields = extractConfiguration.getFieldsToExtract().get();
            return chat.extract(userDocument, fields);
        }
        throw new OpenAiConnectorException("Fields to extract or JSON schema is missing.");
    }
}
