package org.bonitasoft.connectors;

import org.bonitasoft.connectors.document.loader.DocumentLoader;
import org.bonitasoft.engine.connector.ConnectorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AIConnectorIT {

    AiConnector connector;
    @Mock
    DocumentLoader documentLoader;

    @BeforeEach
    void setUp() {
        connector = new AiConnector();
        connector.setDocumentLoader(documentLoader);
    }

    @Test
    void should_create_output_for_valid_input() throws ConnectorException {
        // Given
        connector.setInputParameters(Map.of(
                AiConnector.URL, "http://localhost:8080/v1",
                AiConnector.CHAT_MODEL_NAME, "llama3.1:8b",
                AiConnector.USER_PROMPT, "Can you tell me a joke ?"
        ));

        // When
        Map<String, Object> outputs = connector.execute();

        // Then
        assertThat(outputs).containsKey(AiConnector.OUTPUT);
    }

    @Test
    void should_use_doc() throws ConnectorException, IOException {
        // Given
        String docRef = "doc123456";
        byte[] docData = Files.readAllBytes(Path.of("src/test/resources/test.pdf"));
        when(documentLoader.load(docRef)).thenReturn(docData);

        connector.setInputParameters(Map.of(
                AiConnector.URL, "http://localhost:8080/v1",
                AiConnector.CHAT_MODEL_NAME, "llama3.1:8b",
                AiConnector.SYSTEM_PROMPT, "You are an expert in Agile software methodology and development.",
                AiConnector.USER_PROMPT, "Extract person names listed in the following Manifesto for Agile Software Development",
                AiConnector.SOURCE_DOCUMENT_REF, docRef
        ));

        // When
        var result = connector.execute();

        // Then
        assertThat(result.get(AiConnector.OUTPUT)).isInstanceOf(String.class);
        var aiResponse = (String) result.get(AiConnector.OUTPUT);
        assertThat(aiResponse).isNotEmpty();
    }
}
