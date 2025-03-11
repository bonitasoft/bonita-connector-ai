package org.bonitasoft.connectors.openai.ask;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.document.DocumentSource;
import dev.langchain4j.data.document.Metadata;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bonitasoft.connectors.openai.OpenAiConfiguration;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpenAiAskConnectorIT {

    OpenAiAskConnector connector;

    @Mock
    DocumentSource bonitaDocumentSource;

    Map<String, Object> parameters;

    @BeforeEach
    void setUp() {
        lenient().when(bonitaDocumentSource.metadata()).thenReturn(new Metadata());
        connector = new OpenAiAskConnector();
        connector.setBonitaDocumentSource(bonitaDocumentSource);

        parameters = new HashMap<>();
        parameters.putAll(Map.of(
                OpenAiConfiguration.URL,
                "http://localhost:8080/v1",
                OpenAiConfiguration.TIMEOUT_MS,
                3 * 60 * 1000,
                OpenAiConfiguration.CHAT_MODEL_NAME,
                "llama3.1:8b"));
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connector != null) {
            connector.disconnect();
        }
    }

    @Test
    void should_create_output_for_valid_input() throws Exception {
        // Given
        parameters.put(OpenAiConfiguration.USER_PROMPT, "Can you tell me a joke making fun of Python developers ?");

        // Lifecycle
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
        connector.connect();

        // When
        Map<String, Object> outputs = connector.execute();

        // Then
        assertThat(outputs).containsKey(OpenAiAskConnector.OUTPUT);
    }

    @Test
    void should_use_doc() throws ConnectorException, IOException, ConnectorValidationException {
        // Given
        when(bonitaDocumentSource.inputStream()).thenReturn(getClass().getResourceAsStream("/data/AgileManifesto.pdf"));

        parameters.putAll(
                Map.of(
                        OpenAiConfiguration.SYSTEM_PROMPT,
                        "You are an expert in Agile software methodology and development.",
                        OpenAiConfiguration.USER_PROMPT,
                        "What year was created Manifesto for Agile Software Development ? \n manifesto : \n {{document}}",
                        OpenAiConfiguration.SOURCE_DOCUMENT_REF,
                        "123456abcdef",
                        OpenAiConfiguration.OUTPUT_JSON_SCHEMA,
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
                                """));

        // Lifecycle
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
        connector.connect();

        // When
        var result = connector.execute();

        // Then
        assertThat(result.get(OpenAiAskConnector.OUTPUT)).isInstanceOf(String.class);
        var json = (String) result.get(OpenAiAskConnector.OUTPUT);
        assertThat(json).isNotEmpty().contains("2001");

        YearOfDocument value = new ObjectMapper().readValue(json, YearOfDocument.class);
        assertThat(value.year()).isEqualTo("2001");
    }

    record YearOfDocument(String year) {}
}
