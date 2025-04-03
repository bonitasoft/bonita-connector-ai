package org.bonitasoft.connectors.utils;

public final class Markdown {

    private Markdown() {
        // Utility class
    }

    public static String noJsonBlock(String text) {
        if (text != null && text.startsWith("```")) {
            return text.replace("```json", "").replace("```", "");
        }
        return text;
    }
}
