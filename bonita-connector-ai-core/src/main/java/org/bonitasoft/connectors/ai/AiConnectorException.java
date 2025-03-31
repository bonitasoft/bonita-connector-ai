package org.bonitasoft.connectors.ai;

public class AiConnectorException extends RuntimeException {
    public AiConnectorException(String message) {
        super(message);
    }

    public AiConnectorException(String message, Throwable cause) {
        super(message, cause);
    }
}
