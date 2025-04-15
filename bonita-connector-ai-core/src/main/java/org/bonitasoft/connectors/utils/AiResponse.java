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
package org.bonitasoft.connectors.utils;

import org.bonitasoft.connectors.ai.AiConnectorException;

public final class AiResponse {

    private AiResponse() {
        // Utility class
    }

    public static String ensureJson(String text) {
        if (text == null) return null;

        var json = noJsonBlock(text);
        if (!json.startsWith("{") && !json.startsWith("[")) {
            throw new AiConnectorException("Response contains invalid JSON: " + json);
        }
        return json;
    }

    public static String noJsonBlock(String text) {
        if (text == null) return null;

        if (text.startsWith("```")) {
            return text.replace("```json", "").replace("```", "").trim();
        }
        return text;
    }
}
