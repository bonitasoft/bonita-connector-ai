package org.bonitasoft.connectors.openai.ask;

import static org.bonitasoft.connectors.openai.OpenAiConfiguration.getInputValue;

import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import org.bonitasoft.engine.connector.ConnectorValidationException;

@Data
@Builder
public class AskConfiguration {

    public static final String SYSTEM_PROMPT = "systemPrompt";
    public static final String USER_PROMPT = "userPrompt";
    public static final String SOURCE_DOCUMENT_REF = "sourceDocumentRef";
    public static final String OUTPUT_JSON_SCHEMA = "outputJsonSchema";

    private String systemPrompt;
    private String userPrompt;
    private String sourceDocumentRef;
    private String outputJsonSchema;

    public Optional<String> getOutputJsonSchema() {
        return Optional.ofNullable(outputJsonSchema);
    }

    public Optional<String> getSourceDocumentRef() {
        return Optional.ofNullable(sourceDocumentRef);
    }

    public static AskConfiguration from(Map<String, Object> parameters) throws ConnectorValidationException {
        return AskConfiguration.builder()
                .systemPrompt(getInputValue(parameters, SYSTEM_PROMPT, String.class, "You are a polite Assistant"))
                .userPrompt(getInputValue(parameters, USER_PROMPT, String.class, ""))
                .sourceDocumentRef(getInputValue(parameters, SOURCE_DOCUMENT_REF, String.class, null))
                .outputJsonSchema(getInputValue(parameters, OUTPUT_JSON_SCHEMA, String.class, null))
                .build();
    }
}
