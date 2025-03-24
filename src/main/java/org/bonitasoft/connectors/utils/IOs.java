package org.bonitasoft.connectors.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.bonitasoft.connectors.openai.OpenAiConnectorException;

public final class IOs {

    private IOs() {
        // Utility
    }

    public static String readAsString(String classpathResource) {
        return new String(readAllBytes(classpathResource), StandardCharsets.UTF_8);
    }

    public static byte[] readAllBytes(String classpathResource) {
        try (InputStream stream = IOs.class.getResourceAsStream(classpathResource); ) {
            assert stream != null;
            return stream.readAllBytes();
        } catch (IOException e) {
            throw new OpenAiConnectorException("Failed to read classpath resource: " + classpathResource, e);
        }
    }
}
