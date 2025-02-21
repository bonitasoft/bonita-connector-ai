package org.bonitasoft.connectors;

import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AiConnectorTest {

    AiConnector connector;

    @BeforeEach
    void setUp() {
        connector = new AiConnector();
    }

    @Test
    void should_throw_exception_if_mandatory_input_is_missing() {
        assertThrows(ConnectorValidationException.class, () ->
                connector.validateInputParameters()
        );
    }

    @Test
    void should_throw_exception_if_mandatory_input_is_empty() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(AiConnector.USER_PROMPT, "");
        connector.setInputParameters(parameters);
        assertThrows(ConnectorValidationException.class, () ->
                connector.validateInputParameters()
        );
    }

    @Test
    void should_throw_exception_if_mandatory_input_is_not_a_string() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(AiConnector.USER_PROMPT, 38);
        connector.setInputParameters(parameters);
        assertThrows(ConnectorValidationException.class, () ->
                connector.validateInputParameters()
        );
    }

    @Disabled
    @Test
    void should_create_output_for_valid_input() throws ConnectorException {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(AiConnector.USER_PROMPT, "valid");
        parameters.put(AiConnector.URL, "http://localhost:8080/bidon");
        connector.setInputParameters(parameters);

        // When
        Map<String, Object> outputs = connector.execute();

        // Then
        assertThat(outputs).containsKey("output");
    }

}