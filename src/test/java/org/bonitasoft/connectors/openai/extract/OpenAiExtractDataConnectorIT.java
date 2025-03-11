package org.bonitasoft.connectors.openai.extract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.document.DocumentSource;
import dev.langchain4j.data.document.Metadata;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.bonitasoft.connectors.openai.OpenAiConfiguration;
import org.bonitasoft.connectors.openai.ask.OpenAiConnector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpenAiExtractDataConnectorIT {

    OpenAiExtractDataConnector connector;

    @Mock
    DocumentSource bonitaDocumentSource;

    Map<String, Object> parameters;

    @BeforeEach
    void setUp() {
        lenient().when(bonitaDocumentSource.metadata()).thenReturn(new Metadata());

        connector = new OpenAiExtractDataConnector();
        connector.setBonitaDocumentSource(bonitaDocumentSource);

        parameters = new HashMap<>();
        parameters.putAll(Map.of(
                OpenAiConfiguration.URL,
                "http://localhost:8080/v1",
                OpenAiConfiguration.TIMEOUT_MS,
                3 * 60 * 1000,
                OpenAiConfiguration.CHAT_MODEL_NAME,
                "llama3.1:8b",
                OpenAiConfiguration.SOURCE_DOCUMENT_REF,
                "doc123456"));
    }

    @Test
    void should_extract_doc_content() throws ConnectorException, IOException, ConnectorValidationException {
        // Given
        when(bonitaDocumentSource.inputStream())
                .thenReturn(getClass().getResourceAsStream("/data/pdf_justificatifV1/justificatif_domicile_1.pdf"));

        parameters.put(
                OpenAiConfiguration.FIELDS_TO_EXTRACT,
                "FirstName,LastName,FullName,FullAddress,RecentDate,IssuerName,IdentificationNumber");

        // Lifecycle
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
        connector.connect();

        // When
        var result = connector.execute();

        // Then
        assertThat(result.get(OpenAiConnector.OUTPUT)).isInstanceOf(String.class);
        var json = (String) result.get(OpenAiConnector.OUTPUT);
        assertThat(json).isNotEmpty();
        assertJsonContent(json);
    }

    @Test
    void should_extract_doc_content_using_schema()
            throws ConnectorException, IOException, ConnectorValidationException {
        // Given
        when(bonitaDocumentSource.inputStream())
                .thenReturn(getClass().getResourceAsStream("/data/pdf_justificatifV1/justificatif_domicile_1.pdf"));

        connector.setInputParameters(Map.of(
                OpenAiConfiguration.FIELDS_TO_EXTRACT,
                "FirstName,LastName,FullName,FullAddress,RecentDate,IssuerName,IdentificationNumber",
                OpenAiConfiguration.OUTPUT_JSON_SCHEMA,
                Files.readString(Paths.get("src/test/resources/extract/schema.json"))));

        // Lifecycle
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
        connector.connect();

        // When
        var result = connector.execute();

        // Then
        assertThat(result.get(OpenAiConnector.OUTPUT)).isInstanceOf(String.class);
        var json = (String) result.get(OpenAiConnector.OUTPUT);
        assertThat(json).isNotEmpty();
        assertJsonContent(json);
    }

    private static void assertJsonContent(String json) throws JsonProcessingException {
        var user = new ObjectMapper().readValue(json, User.class);
        assertThat(user.FirstName).isEqualTo("Jean");
        assertThat(user.LastName).isEqualTo("Dupont");
        assertThat(user.FullName).isEqualTo("Jean Dupont");
        assertThat(user.FullAddress).isEqualTo("12 Rue de la Paix, 75002 Paris");
        assertThat(user.IssuerName).isEqualTo("Orange");
        assertThat(user.IdentificationNumber).isEqualTo("581325418");
    }

    record User(
            String FirstName,
            String LastName,
            String FullName,
            String FullAddress,
            String RecentDate,
            String IssuerName,
            String IdentificationNumber) {}
}
