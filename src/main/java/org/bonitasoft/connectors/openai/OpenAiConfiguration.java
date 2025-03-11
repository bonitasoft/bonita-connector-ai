package org.bonitasoft.connectors.openai;

import dev.langchain4j.model.openai.OpenAiChatModelName;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import org.bonitasoft.engine.connector.ConnectorValidationException;

@Data
public class OpenAiConfiguration {

    public static final String URL = "url";
    public static final String TIMEOUT_MS = "requestTimeoutMs";

    public static final String API_KEY = "apiKey";
    /**
     * System environment variable name
     */
    public static final String OPENAI_API_KEY = "OPENAI_API_KEY";

    public static final String CHAT_MODEL_NAME = "chatModelName";
    public static final String SYSTEM_PROMPT = "systemPrompt";
    public static final String MODEL_TEMPERATURE = "modelTemperature";
    public static final String USER_PROMPT = "userPrompt";

    public static final String OUTPUT_JSON_SCHEMA = "outputJsonSchema";
    public static final String FIELDS_TO_EXTRACT = "fieldsToExtract";

    public static final String SOURCE_DOCUMENT_REF = "sourceDocumentRef";

    private String endpointUrl;
    private String apiKey;
    private String chatModelName;
    private Double modelTemperature;
    private Integer requestTimeout;
    private String systemPrompt;
    private String userPrompt;
    private String outputJsonSchema;
    private String fieldsToExtract;

    private String sourceDocumentRef;

    public Optional<String> getEndpointUrl() {
        return Optional.ofNullable(endpointUrl);
    }

    public Optional<Double> getModelTemperature() {
        return Optional.ofNullable(modelTemperature);
    }

    public Optional<Integer> getRequestTimeout() {
        return Optional.ofNullable(requestTimeout);
    }

    public Optional<String> getOutputJsonSchema() {
        return Optional.ofNullable(outputJsonSchema);
    }

    public Optional<String> getFieldsToExtract() {
        return Optional.ofNullable(fieldsToExtract);
    }

    public Optional<String> getSourceDocumentRef() {
        return Optional.ofNullable(sourceDocumentRef);
    }

    protected static <T> Optional<T> getInputValue(Map<String, Object> parameters, String name, Class<T> type)
            throws ConnectorValidationException {
        try {
            var value = type.cast(parameters.get(name));
            return Optional.ofNullable(value);
        } catch (ClassCastException e) {
            throw new ConnectorValidationException(
                    String.format("Parameter %s type must be a %s", name, type.getName()));
        }
    }

    protected static <T> T getInputValue(Map<String, Object> parameters, String name, Class<T> type, T defaultValue)
            throws ConnectorValidationException {
        return getInputValue(parameters, name, type).orElse(defaultValue);
    }

    public static OpenAiConfiguration from(Map<String, Object> parameters) throws ConnectorValidationException {
        var config = new OpenAiConfiguration();
        config.endpointUrl = getInputValue(parameters, URL, String.class, null);
        config.apiKey = getInputValue(parameters, API_KEY, String.class, "changeMe");

        config.chatModelName =
                getInputValue(parameters, CHAT_MODEL_NAME, String.class, OpenAiChatModelName.GPT_4_O.toString());
        config.systemPrompt = getInputValue(parameters, SYSTEM_PROMPT, String.class, "You are a polite Assistant");
        config.userPrompt = getInputValue(parameters, USER_PROMPT, String.class, "");

        config.modelTemperature = getInputValue(parameters, MODEL_TEMPERATURE, Double.class, null);
        config.requestTimeout = getInputValue(parameters, TIMEOUT_MS, Integer.class, null);

        config.outputJsonSchema = getInputValue(parameters, OUTPUT_JSON_SCHEMA, String.class, null);
        config.fieldsToExtract = getInputValue(parameters, FIELDS_TO_EXTRACT, String.class, null);

        config.sourceDocumentRef = getInputValue(parameters, SOURCE_DOCUMENT_REF, String.class, null);
        return config;
    }
}
