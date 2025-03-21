package org.bonitasoft.connectors.openai.extract;

import static org.bonitasoft.connectors.openai.OpenAiConfiguration.getInputValue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import org.bonitasoft.engine.connector.ConnectorValidationException;

@Data
@Builder
public class ExtractConfiguration {

    public static final String SOURCE_DOCUMENT_REF = "sourceDocumentRef";
    public static final String OUTPUT_JSON_SCHEMA = "outputJsonSchema";
    public static final String FIELDS_TO_EXTRACT = "fieldsToExtract";

    private String sourceDocumentRef;
    private String outputJsonSchema;
    private List<String> fieldsToExtract;

    public Optional<String> getOutputJsonSchema() {
        return Optional.ofNullable(outputJsonSchema);
    }

    public Optional<List<String>> getFieldsToExtract() {
        return Optional.ofNullable(fieldsToExtract);
    }

    public Optional<String> getSourceDocumentRef() {
        return Optional.ofNullable(sourceDocumentRef);
    }

    public static ExtractConfiguration from(Map<String, Object> parameters) throws ConnectorValidationException {
        return ExtractConfiguration.builder()
                .sourceDocumentRef(getInputValue(parameters, SOURCE_DOCUMENT_REF, String.class, null))
                .outputJsonSchema(getInputValue(parameters, OUTPUT_JSON_SCHEMA, String.class, null))
                .fieldsToExtract(getInputValue(parameters, FIELDS_TO_EXTRACT, List.class, null))
                .build();
    }
}
