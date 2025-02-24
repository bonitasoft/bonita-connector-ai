package org.bonitasoft.connectors;

import org.bonitasoft.connectors.document.loader.DocumentLoader;
import org.bonitasoft.engine.connector.ConnectorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AIConnectorIT {

    AiConnector connector;

    @BeforeEach
    void setUp() {
        connector = new AiConnector();
    }

    @Test
    void should_create_output_for_valid_input() throws ConnectorException {
        // Given
        connector.setInputParameters(Map.of(
                AiConnector.USER_PROMPT, "Can you tell me a joke ?",
                AiConnector.URL, "http://localhost:8080"
        ));

        // When
        Map<String, Object> outputs = connector.execute();

        // Then
        assertThat(outputs).containsKey(AiConnector.OUTPUT);
    }

    @Test
    void should_use_doc_as_embedding() throws ConnectorException, IOException {
        // Given
        String docRef = "doc123456";

        DocumentLoader documentLoader = mock(DocumentLoader.class);
        byte[] docData = Files.readAllBytes(Path.of("src/test/resources/test.pdf"));
        when(documentLoader.load(docRef)).thenReturn(docData);
        connector.setDocumentLoader(documentLoader);

        connector.setInputParameters(Map.of(
                AiConnector.URL, "http://localhost:8080",
                AiConnector.USER_PROMPT, "Extract person names listed in the the provided content",
                AiConnector.SOURCE_DOCUMENT_REF, docRef
        ));
        connector.connect();

        // When
        var result = connector.execute();

        // Then
        assertThat(result.get(AiConnector.OUTPUT)).isInstanceOf(String.class);
        var aiResponse = (String) result.get(AiConnector.OUTPUT);
        assertThat(aiResponse).isNotEmpty();
    }
}
