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

import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import io.opentelemetry.api.trace.Span;
import java.util.stream.Collectors;

public class PromptTextSpanContributor implements ChatModelSpanContributor {

    private final boolean includePrompt;

    public PromptTextSpanContributor(boolean includePrompt) {
        this.includePrompt = includePrompt;
    }

    @Override
    public void onRequest(ChatModelRequestContext requestContext, Span currentSpan) {
        if (!includePrompt) {
            return;
        }
        currentSpan.setAttribute(
                "gen_ai.prompt",
                requestContext.chatRequest().messages().stream()
                        .map(LangChain4jUtil::chatMessageToText)
                        .collect(Collectors.joining("\n")));
    }
}
