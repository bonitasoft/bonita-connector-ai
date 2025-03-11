package org.bonitasoft.connectors.openai.ask;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import org.bonitasoft.connectors.openai.OpenAiConfiguration;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OpenAiConnectorTest {

    OpenAiConnector connector;

    @BeforeEach
    void setUp() {
        connector = new OpenAiConnector();
    }

    @Test
    void should_throw_exception_if_mandatory_input_is_missing() {
        assertThrows(ConnectorValidationException.class, () -> connector.validateInputParameters());
    }

    @Test
    void should_throw_exception_if_mandatory_input_is_empty() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(OpenAiConfiguration.USER_PROMPT, "");
        connector.setInputParameters(parameters);
        assertThrows(ConnectorValidationException.class, () -> connector.validateInputParameters());
    }

    @Test
    void should_throw_exception_if_mandatory_input_is_not_a_string() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(OpenAiConfiguration.USER_PROMPT, 38);
        connector.setInputParameters(parameters);
        assertThrows(ConnectorValidationException.class, () -> connector.validateInputParameters());
    }
}
