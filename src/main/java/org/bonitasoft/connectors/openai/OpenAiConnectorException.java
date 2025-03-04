package org.bonitasoft.connectors.openai;

public class OpenAiConnectorException extends RuntimeException {
    public OpenAiConnectorException(String message) {
        super(message);
    }

    public OpenAiConnectorException(String message, Throwable cause) {
        super(message, cause);
    }
}
