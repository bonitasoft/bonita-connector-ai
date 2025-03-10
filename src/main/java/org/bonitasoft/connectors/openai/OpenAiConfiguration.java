package org.bonitasoft.connectors.openai;

import dev.langchain4j.model.openai.OpenAiChatModelName;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import org.bonitasoft.engine.connector.ConnectorValidationException;

@Data
public class OpenAiConfiguration {

    public static final String URL = "url";
    public static final String API_KEY = "apiKey";
    public static final String CHAT_MODEL_NAME = "chatModelName";
    public static final String SYSTEM_PROMPT = "systemPrompt";
    public static final String MODEL_TEMPERATURE = "modelTemperature";
    public static final String USER_PROMPT = "userPrompt";
    public static final String SOURCE_DOCUMENT_REF = "sourceDocumentRef";

    private String endpointUrl;
    private String apiKey;
    private String chatModelName;
    private Double modelTemperature;
    private String systemPrompt;
    private String userPrompt;
    private String sourceDocumentRef;

    public OpenAiConfiguration(Map<String, Object> parameters) throws ConnectorValidationException {
        this.endpointUrl = getInputValue(parameters, URL, String.class, null);
        this.apiKey = getInputValue(parameters, API_KEY, String.class, "changeMe");

        this.chatModelName =
                getInputValue(parameters, CHAT_MODEL_NAME, String.class, OpenAiChatModelName.GPT_4_O.toString());
        this.modelTemperature = getInputValue(parameters, MODEL_TEMPERATURE, Double.class, null);

        this.systemPrompt = getInputValue(parameters, SYSTEM_PROMPT, String.class, "You are a polite Assistant");
        this.userPrompt = getInputValue(parameters, USER_PROMPT, String.class, "");

        this.sourceDocumentRef = getInputValue(parameters, SOURCE_DOCUMENT_REF, String.class, null);
    }

    public Optional<String> getEndpointUrl() {
        return Optional.ofNullable(endpointUrl);
    }

    public Optional<Double> getModelTemperature() {
        return Optional.ofNullable(modelTemperature);
    }

    public Optional<String> getSourceDocumentRef() {
        return Optional.ofNullable(sourceDocumentRef);
    }

    protected <T> Optional<T> getInputValue(Map<String, Object> parameters, String name, Class<T> type)
            throws ConnectorValidationException {
        try {
            var value = type.cast(parameters.get(name));
            return Optional.ofNullable(value);
        } catch (ClassCastException e) {
            throw new ConnectorValidationException(
                    String.format("Parameter %s type must be a %s", name, type.getName()));
        }
    }

    protected <T> T getInputValue(Map<String, Object> parameters, String name, Class<T> type, T defaultValue)
            throws ConnectorValidationException {
        return getInputValue(parameters, name, type).orElse(defaultValue);
    }

    public void validate() throws ConnectorValidationException {
        if (userPrompt == null || userPrompt.isBlank()) {
            throw new ConnectorValidationException(String.format("Mandatory parameter '%s' is missing.", USER_PROMPT));
        }
    }
}
