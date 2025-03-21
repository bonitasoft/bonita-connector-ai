package org.bonitasoft.connectors.openai.extract;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.openai.OpenAiConnector;
import org.bonitasoft.connectors.openai.OpenAiConnectorException;
import org.bonitasoft.connectors.openai.doc.UserDocument;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

import java.util.List;

import static org.bonitasoft.connectors.openai.extract.ExtractConfiguration.OUTPUT_JSON_SCHEMA;
import static org.bonitasoft.connectors.openai.extract.ExtractConfiguration.SOURCE_DOCUMENT_REF;

@Slf4j
@Getter
@Setter
public class OpenAiExtractDataConnector extends OpenAiConnector {

    private ExtractConfiguration extractConfiguration;
    private ExtractChat chat;

    @Override
    protected void validateConfiguration() throws ConnectorValidationException {
        try {
            var builder = ExtractConfiguration.builder();
            getInputValue(SOURCE_DOCUMENT_REF, String.class).ifPresent(builder::sourceDocumentRef);
            getInputValue(OUTPUT_JSON_SCHEMA, String.class).ifPresent(builder::outputJsonSchema);
            getInputValue(SOURCE_DOCUMENT_REF, List.class).ifPresent(builder::fieldsToExtract);
            extractConfiguration = builder.build();
        } catch (ClassCastException e) {
            throw new ConnectorValidationException("Some input parameter is not of expected type : " + e.getMessage());
        }

        if (extractConfiguration.getSourceDocumentRef().isEmpty()) {
            throw new ConnectorValidationException("Source document ref is empty");
        }
        if (extractConfiguration.getFieldsToExtract().isEmpty()
            && extractConfiguration.getOutputJsonSchema().isEmpty()) {
            throw new ConnectorValidationException("Either field list or a jsonschema must be provided");
        }
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
