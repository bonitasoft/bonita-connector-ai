package org.bonitasoft.connectors.ai;

import java.util.Optional;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiConfiguration {

    public static final String URL = "url";
    public static final String TIMEOUT_MS = "requestTimeoutMs";
    public static final String API_KEY = "apiKey";
    // System environment variable name
    public static final String AI_API_KEY = "AI_API_KEY";
    public static final String CHAT_MODEL_NAME = "chatModelName";
    public static final String MODEL_TEMPERATURE = "modelTemperature";

    private String baseUrl;

    @Builder.Default
    private String apiKey = "changeMe";

    private String chatModelName;

    private Double modelTemperature;
    private Integer requestTimeout;

    public Optional<String> getBaseUrl() {
        return Optional.ofNullable(baseUrl);
    }

    public Optional<String> getChatModelName() {
        return Optional.ofNullable(chatModelName);
    }

    public Optional<Double> getModelTemperature() {
        return Optional.ofNullable(modelTemperature);
    }

    public Optional<Integer> getRequestTimeout() {
        return Optional.ofNullable(requestTimeout);
    }
}
