package org.bonitasoft.connectors.openai;

import static org.bonitasoft.connectors.openai.OpenAiConfiguration.*;
import static org.bonitasoft.connectors.openai.ask.AskConfiguration.SYSTEM_PROMPT;
import static org.bonitasoft.connectors.openai.ask.AskConfiguration.USER_PROMPT;
import static org.bonitasoft.connectors.openai.extract.ExtractConfiguration.*;

import dev.langchain4j.model.openai.OpenAiChatModel;
import java.util.HashMap;
import java.util.Map;
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
public abstract class AbstractOpenAiConnector extends AbstractConnector {

    public static final String OUTPUT = "output";

    protected OpenAiConfiguration configuration;
    protected OpenAiChatModel chatModel;

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
            this.configuration = OpenAiConfiguration.from(getInputParameters());
        }
        // delegate validation to concrete classes
        validateConfiguration();
    }

    protected abstract void validateConfiguration() throws ConnectorValidationException;

    @Override
    public void connect() throws ConnectorException {
        var chatModelBuilder = configuration.getChatModelBuilder();
        // LLM req/res logs
        if (log.isDebugEnabled()) {
            chatModelBuilder.logRequests(true).logResponses(true);
        }
        this.chatModel = chatModelBuilder.build();
    }

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

    protected Map<String, Object> getInputParameters() {
        var parameters = new HashMap<String, Object>();
        parameters.put(URL, getInputParameter(URL));
        parameters.put(
                API_KEY,
                Optional.ofNullable(System.getenv(OPENAI_API_KEY)).orElse((String) getInputParameter(API_KEY)));
        parameters.put(CHAT_MODEL_NAME, getInputParameter(CHAT_MODEL_NAME));
        parameters.put(SYSTEM_PROMPT, getInputParameter(SYSTEM_PROMPT));
        parameters.put(USER_PROMPT, getInputParameter(USER_PROMPT));
        parameters.put(SOURCE_DOCUMENT_REF, getInputParameter(SOURCE_DOCUMENT_REF));

        parameters.put(TIMEOUT_MS, getInputParameter(TIMEOUT_MS));
        parameters.put(MODEL_TEMPERATURE, getInputParameter(MODEL_TEMPERATURE));

        parameters.put(OUTPUT_JSON_SCHEMA, getInputParameter(OUTPUT_JSON_SCHEMA));
        parameters.put(FIELDS_TO_EXTRACT, getInputParameter(FIELDS_TO_EXTRACT));

        return parameters;
    }

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
}
