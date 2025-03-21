package org.bonitasoft.connectors.openai;

import dev.langchain4j.model.openai.OpenAiChatModelName;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import org.bonitasoft.engine.connector.ConnectorValidationException;

@Data
@Builder
public class OpenAiConfiguration {

    public static final String URL = "url";
    public static final String TIMEOUT_MS = "requestTimeoutMs";
    public static final String API_KEY = "apiKey";
    // System environment variable name
    public static final String OPENAI_API_KEY = "OPENAI_API_KEY";
    public static final String CHAT_MODEL_NAME = "chatModelName";
    public static final String MODEL_TEMPERATURE = "modelTemperature";

    private String baseUrl;

    @Builder.Default
    private String apiKey = "changeMe";

    @Builder.Default
    private String chatModelName = OpenAiChatModelName.GPT_4_O_MINI.toString();

    private Double modelTemperature;
    private Integer requestTimeout;

    public Optional<String> getBaseUrl() {
        return Optional.ofNullable(baseUrl);
    }

    public Optional<Double> getModelTemperature() {
        return Optional.ofNullable(modelTemperature);
    }

    public Optional<Integer> getRequestTimeout() {
        return Optional.ofNullable(requestTimeout);
    }

    public static OpenAiConfiguration from(Map<String, Object> parameters) throws ConnectorValidationException {
        return OpenAiConfiguration.builder()
                .baseUrl(getInputValue(parameters, URL, String.class, null))
                .apiKey(getInputValue(parameters, API_KEY, String.class, "changeMe"))
                .chatModelName(getInputValue(
                        parameters, CHAT_MODEL_NAME, String.class, OpenAiChatModelName.GPT_4_O_MINI.toString()))
                .modelTemperature(getInputValue(parameters, MODEL_TEMPERATURE, Double.class, null))
                .requestTimeout(getInputValue(parameters, TIMEOUT_MS, Integer.class, null))
                .build();
    }

    public static <T> Optional<T> getInputValue(Map<String, Object> parameters, String name, Class<T> type)
            throws ConnectorValidationException {
        try {
            var value = type.cast(parameters.get(name));
            return Optional.ofNullable(value);
        } catch (ClassCastException e) {
            throw new ConnectorValidationException(
                    String.format("Parameter %s type must be a %s", name, type.getName()));
        }
    }

    public static <T> T getInputValue(Map<String, Object> parameters, String name, Class<T> type, T defaultValue)
            throws ConnectorValidationException {
        return getInputValue(parameters, name, type).orElse(defaultValue);
    }

    public static OpenAiConfiguration.OpenAiConfigurationBuilder builder() {
        return new OpenAiConfiguration.OpenAiConfigurationBuilder();
    }
}
