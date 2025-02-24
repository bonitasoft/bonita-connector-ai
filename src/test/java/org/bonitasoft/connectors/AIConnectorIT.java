package org.bonitasoft.connectors;

import org.bonitasoft.engine.connector.ConnectorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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
        connector.setInputParameters(Map.of(
                AiConnector.URL, "http://localhost:8080"
        ));
        connector.connect();
        byte[] docData = Files.readAllBytes(Path.of("src/test/resources/test.pdf"));

        // When
        String output = connector.doExecute(
                connector.getChatModel(),
                "Extract person names listed in the the following content : {document}",
                docData
        );

        // Then
        assertThat(output).isNotEmpty();
    }
}
