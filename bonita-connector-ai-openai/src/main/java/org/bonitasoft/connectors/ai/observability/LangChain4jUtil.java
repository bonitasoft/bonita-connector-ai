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
package org.bonitasoft.connectors.ai.observability;

import dev.langchain4j.data.message.*;

public class LangChain4jUtil {
    private LangChain4jUtil() {}

    public static String chatMessageToText(ChatMessage chatMessage) {
        if (chatMessage instanceof AiMessage aiMessage) {
            return aiMessage.text();
        }
        if (chatMessage instanceof UserMessage userMessage) {
            return userMessage.hasSingleText() ? userMessage.singleText() : null;
        }
        if (chatMessage instanceof SystemMessage systemMessage) {
            return systemMessage.text();
        }
        if (chatMessage instanceof ToolExecutionResultMessage toolExecutionResultMessage) {
            return toolExecutionResultMessage.text();
        }
        return null;
    }
}
