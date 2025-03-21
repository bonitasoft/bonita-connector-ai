package org.bonitasoft.connectors.openai.ask;

import java.util.Optional;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AskConfiguration {

    public static final String SYSTEM_PROMPT = "systemPrompt";
    public static final String USER_PROMPT = "userPrompt";
    public static final String SOURCE_DOCUMENT_REF = "sourceDocumentRef";
    public static final String OUTPUT_JSON_SCHEMA = "outputJsonSchema";

    @Builder.Default
    private String systemPrompt = "You are a polite Assistant";

    private String userPrompt;
    private String sourceDocumentRef;
    private String outputJsonSchema;

    public Optional<String> getOutputJsonSchema() {
        return Optional.ofNullable(outputJsonSchema);
    }

    public Optional<String> getSourceDocumentRef() {
        return Optional.ofNullable(sourceDocumentRef);
    }
}
