package org.bonitasoft.connectors;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.assertj.core.api.Assertions;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

}