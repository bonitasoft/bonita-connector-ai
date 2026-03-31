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
package org.bonitasoft.connectors.ai.azure;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.connectors.ai.ask.AskChat;
import org.bonitasoft.connectors.ai.ask.AskChatIT;
import org.bonitasoft.connectors.utils.IOs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Slf4j
@Tag("provider-api")
class AzureOpenAiAskChatIT extends AskChatIT {

    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    AskChat askChat;

    @BeforeEach
    void setUpAsk() {
        var configurationBuilder = AiConfiguration.builder().requestTimeout(3 * 60 * 1000);
        configurationBuilder.apiKey(System.getenv("AZURE_OPENAI_API_KEY"));
        configurationBuilder.baseUrl(System.getenv("AZURE_OPENAI_ENDPOINT"));
        String modelName = System.getenv("AZURE_OPENAI_MODEL");
        if (modelName != null && !modelName.isEmpty()) {
            configurationBuilder.chatModelName(modelName);
        }
        var configuration = configurationBuilder.build();
        askChat = new AzureOpenAiAskChat(configuration);
    }

    @Override
    protected void customize(AiConfiguration.AiConfigurationBuilder builder) {
        builder.apiKey(System.getenv("AZURE_OPENAI_API_KEY"));
        builder.baseUrl(System.getenv("AZURE_OPENAI_ENDPOINT"));
        String modelName = System.getenv("AZURE_OPENAI_MODEL");
        if (modelName != null && !modelName.isEmpty()) {
            builder.chatModelName(modelName);
        }
    }

    @Override
    protected AskChat getChat(AiConfiguration configuration) {
        return new AzureOpenAiAskChat(configuration);
    }

    @Test
    void should_ask_simple_question() {
        String systemPrompt = "You are a helpful assistant.";
        String userPrompt = "What is the capital of France? Answer with just the city name.";

        String response = askChat.ask(systemPrompt, userPrompt, null, (UserDocument) null);

        assertThat(response).isNotEmpty().containsIgnoringCase("Paris");
    }

    @Test
    void should_ask_with_json_output() throws Exception {
        String systemPrompt = "You are a helpful assistant that responds in JSON format.";
        String userPrompt = "List the 3 largest planets in our solar system by diameter.";
        String jsonSchema =
                """
                {
                    "$schema": "https://json-schema.org/draft/2020-12/schema",
                    "title": "Planets",
                    "type": "object",
                    "required": ["planets"],
                    "properties": {
                        "planets": {
                            "type": "array",
                            "items": {
                                "type": "object",
                                "required": ["name", "rank"],
                                "properties": {
                                    "name": { "type": "string" },
                                    "rank": { "type": "integer" }
                                }
                            }
                        }
                    }
                }
                """;

        String json = askChat.ask(systemPrompt, userPrompt, jsonSchema, (UserDocument) null);

        assertThat(json).isNotEmpty();
        log.debug("JSON response: {}", json);
        var result = mapper.readValue(json, PlanetsResponse.class);
        assertThat(result.planets()).hasSize(3);
        assertThat(result.planets().get(0).name()).containsIgnoringCase("Jupiter");
    }

    @Test
    void should_ask_with_pdf_document() throws Exception {
        var document = new UserDocument("application/pdf", IOs.readAllBytes("/data/AgileManifesto.pdf"));
        String systemPrompt = "You are a software methodology expert.";
        String userPrompt = "How many authors signed the Agile Manifesto? Answer with just a number in JSON format.";
        String jsonSchema =
                """
                {
                    "$schema": "https://json-schema.org/draft/2020-12/schema",
                    "type": "object",
                    "required": ["count"],
                    "properties": {
                        "count": { "type": "integer" }
                    }
                }
                """;

        String json = askChat.ask(systemPrompt, userPrompt, jsonSchema, document);

        assertThat(json).isNotEmpty();
        log.debug("JSON response: {}", json);
        var result = mapper.readValue(json, CountResponse.class);
        assertThat(result.count()).isEqualTo(17);
    }

    @Test
    void should_ask_without_system_prompt() {
        String userPrompt = "What is 2 + 2? Answer with just the number.";

        String response = askChat.ask("You are a helpful assistant.", userPrompt, null, (UserDocument) null);

        assertThat(response).isNotEmpty().contains("4");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record PlanetsResponse(java.util.List<Planet> planets) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Planet(String name, Integer rank) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record CountResponse(Integer count) {}
}
