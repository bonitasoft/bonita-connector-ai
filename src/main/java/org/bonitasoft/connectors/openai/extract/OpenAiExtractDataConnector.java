package org.bonitasoft.connectors.openai.extract;

import static org.bonitasoft.connectors.openai.extract.ExtractConfiguration.*;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.openai.OpenAiConnector;
import org.bonitasoft.connectors.openai.OpenAiConnectorException;
import org.bonitasoft.connectors.openai.doc.UserDocument;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

@Slf4j
@Getter
@Setter
public class OpenAiExtractDataConnector extends OpenAiConnector {

    private ExtractConfiguration extractConfiguration;
    private ExtractChat chat;

    @Override
    protected void validateConfiguration() throws ConnectorValidationException {
        try {
            extractConfiguration = ExtractConfiguration.builder()
                    .sourceDocumentRef((String) getInputParameter(SOURCE_DOCUMENT_REF))
                    .outputJsonSchema((String) getInputParameter(OUTPUT_JSON_SCHEMA))
                    .fieldsToExtract((List<String>) getInputParameter(FIELDS_TO_EXTRACT))
                    .build();
        } catch (ClassCastException e) {
            throw new ConnectorValidationException("Some input parameter is not of expected type : " + e.getMessage());
        }
        // TODO: validation
    }

    @Override
    public void connect() throws ConnectorException {
        chat = new OpenAiExtractChat(configuration);
    }

    /**
     * @return
     * @throws ConnectorException
     */
    @Override
    protected Object doExecute() throws ConnectorException {
        // Try to read doc
        UserDocument userDocument = extractConfiguration
                .getSourceDocumentRef()
                .map(this::getUserDocument)
                .orElseThrow(() -> new OpenAiConnectorException("Failed to load document to analyze."));

        var output = extractConfiguration
                .getOutputJsonSchema()
                .map(jsonSchema -> chat.extract(userDocument, jsonSchema))
                .or(() -> extractConfiguration.getFieldsToExtract().map(fields -> chat.extract(userDocument, fields)));

        return output.orElseThrow(() -> new OpenAiConnectorException("Fields to extract or JSON schema is missing."));
    }
}
