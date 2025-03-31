package org.bonitasoft.connectors.openai.ask;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.connectors.ai.ask.AskConfiguration;
import org.bonitasoft.connectors.ai.openai.OpenAskAiChat;
import org.bonitasoft.connectors.utils.IOs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class OpenAskAiChatIT {

    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    OpenAskAiChat chat;

    @BeforeEach
    void setUp() {
        var configuration = AiConfiguration.builder()
                //                .baseUrl("http://localhost:11434/v1")
                //                .chatModelName("llama3.1:8b")
                //                .apiKey("changeMe")
                .requestTimeout(3 * 60 * 1000)
                .build();

        chat = new OpenAskAiChat(configuration);
    }

    @Test
    void should_use_doc() throws IOException {
        // Given
        var document = new UserDocument("application/pdf", IOs.readAllBytes("/data/AgileManifesto.pdf"));
        var config = AskConfiguration.builder()
                .systemPrompt("You are an expert in Agile software methodology and development.")
                .userPrompt(
                        "What year was created Manifesto for Agile Software Development ? \n The manifesto is the document provided.")
                .sourceDocumentRef("123456abcdef")
                .outputJsonSchema(
                        """
                                {
                                    "$schema": "https://json-schema.org/draft/2020-12/schema",
                                    "title": "YearOfDocument",
                                    "type": "object",
                                    "required":["year"]
                                    "properties": {
                                      "year": {
                                        "description": "The document creation year",
                                        "type": "integer"
                                      }
                                    }
                                }
                                """)
                .build();

        // When
        var json = chat.ask(
                config.getSystemPrompt(),
                config.getUserPrompt(),
                config.getOutputJsonSchema().orElse(null),
                document);

        // Then
        assertThat(json).isNotEmpty().contains("2001");

        YearOfDocument value = objectMapper.readValue(json, YearOfDocument.class);
        assertThat(value.year()).isEqualTo("2001");
    }

    record YearOfDocument(String year) {}
}
