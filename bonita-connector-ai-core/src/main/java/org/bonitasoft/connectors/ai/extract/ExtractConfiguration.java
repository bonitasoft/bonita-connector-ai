package org.bonitasoft.connectors.ai.extract;

import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExtractConfiguration {

    public static final String SOURCE_DOCUMENT_REF = "sourceDocumentRef";
    public static final String OUTPUT_JSON_SCHEMA = "outputJsonSchema";
    public static final String FIELD_LIST = "fieldsToExtract";

    private String sourceDocumentRef;
    private String outputJsonSchema;
    private List<String> fieldsToExtract;

    public Optional<String> getOutputJsonSchema() {
        return Optional.ofNullable(outputJsonSchema);
    }

    public Optional<List<String>> getFieldsToExtract() {
        return Optional.ofNullable(fieldsToExtract);
    }
}
