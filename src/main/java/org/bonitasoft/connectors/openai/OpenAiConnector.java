package org.bonitasoft.connectors.openai;

import static org.bonitasoft.connectors.openai.OpenAiConfiguration.*;

import java.util.HashMap;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.openai.doc.BonitaDocumentSource;
import org.bonitasoft.connectors.openai.doc.UserDocument;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.document.Document;
import org.bonitasoft.engine.bpm.document.DocumentNotFoundException;
import org.bonitasoft.engine.connector.AbstractConnector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

/**
 * Connector lifecycle:
 * 1 - connector.validate();
 * 2 - connector.connect();
 * 3 - connector.execute();
 * 3 - connector.disconnect();
 */
@Slf4j
@Getter
@Setter
public abstract class OpenAiConnector extends AbstractConnector {

    public static final String OUTPUT = "output";

    protected OpenAiConfiguration configuration;

    /**
     * Perform validation on the inputs defined on the connector definition
     * (src/main/resources/bonita-connector-ai.def) You should: - validate that mandatory inputs are
     * presents - validate that the content of the inputs is coherent with your use case (e.g:
     * validate that a date is / isn't in the past ...)
     */
    @Override
    public void validateInputParameters() throws ConnectorValidationException {
        // Parse configuration from input parameters
        if (this.configuration == null) {
            try {
                var builder = OpenAiConfiguration.builder();
                Optional.ofNullable(System.getenv(OPENAI_API_KEY))
                        .or(() -> getInputValue(API_KEY, String.class))
                        .ifPresent(builder::apiKey);
                getInputValue(URL, String.class).ifPresent(builder::baseUrl);
                getInputValue(CHAT_MODEL_NAME, String.class).ifPresent(builder::chatModelName);
                getInputValue(TIMEOUT_MS, Integer.class).ifPresent(builder::requestTimeout);
                getInputValue(MODEL_TEMPERATURE, Double.class).ifPresent(builder::modelTemperature);
                this.configuration = builder.build();
            } catch (ClassCastException e) {
                throw new ConnectorValidationException(
                        "Provided input parameter is not of expected type : " + e.getMessage());
            }
        }
        // delegate validation to concrete classes
        validateConfiguration();
    }

    protected abstract void validateConfiguration() throws ConnectorValidationException;

    /**
     * Core method: - Execute all the business logic of your connector using the inputs (connect to an
     * external service, compute some values ...). - Set the output of the connector execution. If
     * outputs are not set, connector fails.
     */
    @Override
    protected void executeBusinessLogic() throws ConnectorException {
        var aiResponse = doExecute();
        log.debug("Open AI connector output: {}", aiResponse);
        setOutputParameter(OUTPUT, aiResponse);
    }

    protected abstract Object doExecute() throws ConnectorException;

    protected UserDocument getUserDocument(String docRef) {
        long processInstanceId = getExecutionContext().getProcessInstanceId();
        ProcessAPI processAPI = getAPIAccessor().getProcessAPI();
        try {
            Document document = processAPI.getLastDocument(processInstanceId, docRef);
            var metadata = new HashMap<String, Object>();
            metadata.put(BonitaDocumentSource.Metadatas.FILE_NAME.name(), document.getContentFileName());
            metadata.put(BonitaDocumentSource.Metadatas.AUTHOR.name(), document.getAuthor());
            metadata.put(BonitaDocumentSource.Metadatas.MIME_TYPE.name(), document.getContentMimeType());
            metadata.put(BonitaDocumentSource.Metadatas.DESCRIPTION.name(), document.getDescription());
            metadata.put(
                    BonitaDocumentSource.Metadatas.CREATION_DATE.name(),
                    document.getCreationDate().toString());
            var data = processAPI.getDocumentContent(document.getContentStorageId());
            return new UserDocument(document.getContentMimeType(), data, metadata);
        } catch (final DocumentNotFoundException e) {
            throw new OpenAiConnectorException("Document not found for ref: " + docRef, e);
        }
    }

    protected <T> Optional<T> getInputValue(String name, Class<T> type) {
        try {
            Object parameter = getInputParameter(name);
            var value = type.cast(parameter);
            return Optional.ofNullable(value);
        } catch (ClassCastException e) {
            throw new OpenAiConnectorException(String.format("Parameter %s type must be a %s", name, type.getName()));
        }
    }
}
