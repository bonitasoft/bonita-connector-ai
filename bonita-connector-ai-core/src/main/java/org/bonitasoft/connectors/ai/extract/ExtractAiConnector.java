package org.bonitasoft.connectors.ai.extract;

import static org.bonitasoft.connectors.ai.extract.ExtractConfiguration.OUTPUT_JSON_SCHEMA;
import static org.bonitasoft.connectors.ai.extract.ExtractConfiguration.SOURCE_DOCUMENT_REF;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConnector;
import org.bonitasoft.connectors.ai.AiConnectorException;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

@Slf4j
public abstract class ExtractAiConnector<T extends ExtractChat> extends AiConnector {

    protected ExtractConfiguration extractConfiguration;
    protected T chat;

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

    /**
     * @return
     * @throws ConnectorException
     */
    @Override
    protected Object doExecute() throws ConnectorException {
        // Read doc
        UserDocument userDocument = getUserDocument(extractConfiguration.getSourceDocumentRef());

        var output = extractConfiguration
                .getOutputJsonSchema()
                .map(jsonSchema -> chat.extract(userDocument, jsonSchema))
                .or(() -> extractConfiguration.getFieldsToExtract().map(fields -> chat.extract(userDocument, fields)));

        return output.orElseThrow(() -> new AiConnectorException("Fields to extract or JSON schema is missing."));
    }
}
