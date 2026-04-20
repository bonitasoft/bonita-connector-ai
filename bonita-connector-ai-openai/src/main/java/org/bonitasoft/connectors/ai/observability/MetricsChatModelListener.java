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
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.TokenUsage;
import io.micrometer.core.instrument.*;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetricsChatModelListener implements ChatModelListener {

    public static final String START_TIME_KEY_NAME = "startTime";

    private final Meter.MeterProvider<Counter> inputTokenUsage;
    private final Meter.MeterProvider<Counter> outputTokenUsage;
    private final Meter.MeterProvider<Timer> duration;

    public MetricsChatModelListener() {

        this.inputTokenUsage = Counter.builder("gen_ai.client.token.usage")
                .description("Measures number of input tokens used")
                .tag("gen_ai.operation.name", "completion")
                .tag("gen_ai.token.type", "input")
                .withRegistry(Metrics.globalRegistry);
        this.outputTokenUsage = Counter.builder("gen_ai.client.token.usage")
                .description("Measures number of output tokens used")
                .tag("gen_ai.operation.name", "completion")
                .tag("gen_ai.token.type", "output")
                .withRegistry(Metrics.globalRegistry);
        this.duration = Timer.builder("gen_ai.client.operation.duration")
                .description("GenAI operation duration")
                .tag("gen_ai.operation.name", "completion")
                .withRegistry(Metrics.globalRegistry);
    }

    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        final long startTime = Clock.SYSTEM.monotonicTime();
        requestContext.attributes().put(START_TIME_KEY_NAME, startTime);
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        final long endTime = Clock.SYSTEM.monotonicTime();

        ChatRequest request = responseContext.chatRequest();
        ChatResponse response = responseContext.chatResponse();
        Tags tags = Tags.of("gen_ai.request.model", request.parameters().modelName());
        if (response.metadata().modelName() != null) {
            tags = tags.and("gen_ai.response.model", response.metadata().modelName());
        }

        recordTokenUsage(responseContext, tags);
        recordDuration(responseContext, endTime, tags);
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {
        final long endTime = Clock.SYSTEM.monotonicTime();

        Long startTime = (Long) errorContext.attributes().get(START_TIME_KEY_NAME);
        if (startTime == null) {
            // should never happen
            log.warn("No start time found in response");
            return;
        }

        Tags tags = Tags.of(
                "gen_ai.request.model", errorContext.chatRequest().parameters().modelName());

        if (errorContext.error() != null) {
            tags = tags.and("error.type", errorContext.error().getMessage());
        }
        duration.withTags(tags).record(endTime - startTime, TimeUnit.NANOSECONDS);
    }

    private void recordTokenUsage(ChatModelResponseContext responseContext, Tags tags) {
        TokenUsage tokenUsage = responseContext.chatResponse().tokenUsage();
        if (tokenUsage == null) {
            return;
        }

        Integer inputTokenCount = tokenUsage.inputTokenCount();
        if (inputTokenCount != null) {
            inputTokenUsage.withTags(tags).increment(inputTokenCount);
        }
        Integer outputTokenCount = tokenUsage.outputTokenCount();
        if (outputTokenCount != null) {
            outputTokenUsage.withTags(tags).increment(outputTokenCount);
        }
    }

    private void recordDuration(ChatModelResponseContext responseContext, long endTime, Tags tags) {
        Long startTime = (Long) responseContext.attributes().get(START_TIME_KEY_NAME);
        if (startTime == null) {
            // should never happen
            log.warn("No start time found in response");
            return;
        }
        duration.withTags(tags).record(endTime - startTime, TimeUnit.NANOSECONDS);
    }
}
