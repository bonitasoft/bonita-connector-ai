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

    @Disabled
    @Test
    void document_content_should_be_added_to_user_prompt() throws ConnectorException {
        // Given
        ChatLanguageModel chatModel = mock(ChatLanguageModel.class);
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        String docContent = "This is the doc content.";
        // When
        connector.doExecute(chatModel, "Summarize the following information", docContent.getBytes(StandardCharsets.UTF_8));
        // Then
        verify(chatModel, times(1)).generate(promptCaptor.capture());
        Assertions.assertThat(promptCaptor.getValue()).endsWith(docContent + System.lineSeparator());
    }
}