package org.bonitasoft.connectors.openai;

import static org.bonitasoft.connectors.openai.OpenAiConfiguration.*;

import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.DocumentSource;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.bonitasoft.connectors.document.BonitaDocumentSource;
import org.bonitasoft.engine.connector.AbstractConnector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.jetbrains.annotations.NotNull;

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

    protected OpenAiConfiguration openAiConfiguration;

    protected DocumentSource bonitaDocumentSource;

    protected ChatLanguageModel chatModel;

    /**
     * Perform validation on the inputs defined on the connector definition
     * (src/main/resources/bonita-connector-ai.def) You should: - validate that mandatory inputs are
     * presents - validate that the content of the inputs is coherent with your use case (e.g:
     * validate that a date is / isn't in the past ...)
     */
    @Override
    public void validateInputParameters() throws ConnectorValidationException {
        if (this.openAiConfiguration == null) {
            this.openAiConfiguration = OpenAiConfiguration.from(getInputParameters());
        }
        // delegate validation to concrete classes
    }

    /**
     * Core method: - Execute all the business logic of your connector using the inputs (connect to an
     * external service, compute some values ...). - Set the output of the connector execution. If
     * outputs are not set, connector fails.
     */
    @Override
    protected void executeBusinessLogic() throws ConnectorException {
        this.initialize();
        var aiResponse = doExecute();
        log.debug("connector output: {}", aiResponse);
        setOutputParameter(OUTPUT, aiResponse);
    }

    protected abstract Object doExecute() throws ConnectorException;

    protected OpenAiChatModel.OpenAiChatModelBuilder customizeChatModelBuilder(
            OpenAiChatModel.OpenAiChatModelBuilder chatModelBuilder) {
        return chatModelBuilder;
    }

    void initialize() {

        if (this.bonitaDocumentSource == null
                && openAiConfiguration.getSourceDocumentRef().isPresent()) {
            this.bonitaDocumentSource = new BonitaDocumentSource(
                    getAPIAccessor().getProcessAPI(),
                    getExecutionContext(),
                    openAiConfiguration.getSourceDocumentRef().get());
        }

        var chatModelBuilder = OpenAiChatModel.builder();
        // API Key
        chatModelBuilder.apiKey(openAiConfiguration.getApiKey());
        // Url override
        openAiConfiguration.getUrl().ifPresent(chatModelBuilder::baseUrl);
        // Chat model name
        chatModelBuilder.modelName(openAiConfiguration.getChatModelName());
        // LLM req/res logs
        if (log.isDebugEnabled()) {
            chatModelBuilder.logRequests(true).logResponses(true);
        }
        // Temperature
        openAiConfiguration.getModelTemperature().ifPresent(chatModelBuilder::temperature);
        // Req timeout
        if (openAiConfiguration.getRequestTimeout().isPresent()) {
            chatModelBuilder.timeout(
                    Duration.of(openAiConfiguration.getRequestTimeout().get(), ChronoUnit.MILLIS));
        }

        // Chat customizations
        chatModelBuilder = customizeChatModelBuilder(chatModelBuilder);

        this.chatModel = chatModelBuilder.build();
    }

    @NotNull
    private Map<String, Object> getInputParameters() {
        var parameters = new HashMap<String, Object>();
        parameters.put(URL, getInputParameter(URL));
        parameters.put(
                API_KEY,
                Optional.ofNullable(System.getenv(OPENAI_API_KEY)).orElse((String) getInputParameter(API_KEY)));
        parameters.put(CHAT_MODEL_NAME, getInputParameter(CHAT_MODEL_NAME));
        parameters.put(SYSTEM_PROMPT, getInputParameter(SYSTEM_PROMPT));
        parameters.put(USER_PROMPT, getInputParameter(USER_PROMPT));

        parameters.put(TIMEOUT_MS, getInputParameter(TIMEOUT_MS));
        parameters.put(MODEL_TEMPERATURE, getInputParameter(MODEL_TEMPERATURE));

        parameters.put(OUTPUT_JSON_SCHEMA, getInputParameter(OUTPUT_JSON_SCHEMA));

        parameters.put(FIELDS_TO_EXTRACT, getInputParameter(FIELDS_TO_EXTRACT));

        parameters.put(SOURCE_DOCUMENT_REF, getInputParameter(SOURCE_DOCUMENT_REF));
        return parameters;
    }

    @NotNull
    protected String getDocContent(String ref) {
        try {
            var doc = DocumentLoader.load(bonitaDocumentSource, new ApacheTikaDocumentParser());
            return doc.text();
        } catch (Exception e) {
            throw new OpenAiConnectorException("Failed to read document content for ref: " + ref, e);
        }
    }

    protected String monitor(Callable<String> callable) {
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            return callable.call();
        } catch (Exception e) {
            throw new OpenAiConnectorException("Failed to request LLM", e);
        } finally {
            stopWatch.stop();
            log.debug("OpenAI call duration: {}", stopWatch);
        }
    }
}
