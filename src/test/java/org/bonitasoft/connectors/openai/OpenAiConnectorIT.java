package org.bonitasoft.connectors.openai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.bonitasoft.connectors.document.loader.DocumentLoader;
import org.bonitasoft.engine.connector.ConnectorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpenAiConnectorIT {

    OpenAiConnector connector;

    @Mock
    DocumentLoader documentLoader;

    @BeforeEach
    void setUp() {
        connector = new OpenAiConnector();
        connector.setDocumentLoader(documentLoader);
    }

    @Test
    void should_create_output_for_valid_input() throws ConnectorException {
        // Given
        connector.setInputParameters(Map.of(
                OpenAiConfiguration.URL, "http://localhost:8080/v1",
                OpenAiConfiguration.CHAT_MODEL_NAME, "llama3.1:8b",
                OpenAiConfiguration.USER_PROMPT, "Can you tell me a joke ?"));

        // When
        Map<String, Object> outputs = connector.execute();

        // Then
        assertThat(outputs).containsKey(OpenAiConnector.OUTPUT);
    }

    @Test
    void should_use_doc() throws ConnectorException, IOException {
        // Given
        String docRef = "doc123456";
        byte[] docData = Files.readAllBytes(Path.of("src/test/resources/test.pdf"));
        when(documentLoader.load(docRef)).thenReturn(docData);

        connector.setInputParameters(Map.of(
                OpenAiConfiguration.URL, "http://localhost:8001/v1",
                OpenAiConfiguration.CHAT_MODEL_NAME, "llama3.1:8b",
                OpenAiConfiguration.SYSTEM_PROMPT, "You are an expert in Agile software methodology and development.",
                OpenAiConfiguration.USER_PROMPT,
                        "Extract person names listed in the following Manifesto for Agile Software Development",
                OpenAiConfiguration.SOURCE_DOCUMENT_REF, docRef));

        // When
        var result = connector.execute();

        // Then
        assertThat(result.get(OpenAiConnector.OUTPUT)).isInstanceOf(String.class);
        var aiResponse = (String) result.get(OpenAiConnector.OUTPUT);
        assertThat(aiResponse).isNotEmpty();
    }
}
