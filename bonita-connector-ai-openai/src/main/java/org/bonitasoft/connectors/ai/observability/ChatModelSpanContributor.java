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

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import io.opentelemetry.api.trace.Span;

/**
 * Contributes custom attributes, events or other data to the spans created by {@link SpanChatModelListener}.
 */
public interface ChatModelSpanContributor {
    /**
     * Allows for custom data to be added to the span.
     *
     * @param requestContext The request context. It contains the {@link ChatRequest} and attributes.
     *        The attributes can be used to pass data between methods of this listener
     *        or between multiple listeners.
     * @param currentSpan Span opened by {@link SpanChatModelListener}.
     */
    default void onRequest(ChatModelRequestContext requestContext, Span currentSpan) {}

    /**
     * Allows for custom data to be added to the span.
     *
     * @param responseContext The response context.
     *        It contains {@link ChatResponse}, corresponding {@link ChatRequest} and attributes.
     *        The attributes can be used to pass data between methods of this listener
     *        or between multiple listeners.
     * @param currentSpan Span opened by {@link SpanChatModelListener}.
     */
    default void onResponse(ChatModelResponseContext responseContext, Span currentSpan) {}

    /**
     * Allows for custom data to be added to the span.
     *
     * @param errorContext The error context.
     *        It contains the error, corresponding {@link ChatRequest},
     *        partial {@link ChatResponse} (if available) and attributes.
     *        The attributes can be used to pass data between methods of this listener
     *        or between multiple listeners.
     * @param currentSpan Span opened by {@link SpanChatModelListener}.
     */
    default void onError(ChatModelErrorContext errorContext, Span currentSpan) {}
}
