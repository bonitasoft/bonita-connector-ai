/**
 * Copyright (C) 2025 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.connectors.ai;

import java.util.Optional;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiConfiguration {

    // System environment variable name
    public static final String AI_API_KEY = "AI_API_KEY";

    public static final String URL = "url";
    public static final String TIMEOUT_MS = "requestTimeoutMs";
    public static final String API_KEY = "apiKey";
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
