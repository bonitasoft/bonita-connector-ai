package org.bonitasoft.connectors.openai.extract;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.doc.UserDocument;
import org.bonitasoft.connectors.ai.mistral.MistralAiExtractChat;
import org.bonitasoft.connectors.utils.IOs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class MistralAiExtractChatIT {

    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    MistralAiExtractChat chat;

    @BeforeEach
    void setUp() {

        var configuration = AiConfiguration.builder()
                //                .baseUrl("http://localhost:11434/v1")
                //                .chatModelName("llama3.1:8b")
                //                .apiKey("changeMe")
                .requestTimeout(3 * 60 * 1000)
                .build();

        chat = new MistralAiExtractChat(configuration);
    }

    @Test
    void should_extract_data_from_pdf() throws Exception {

        // Given
        var doc = new UserDocument(
                "application/pdf", IOs.readAllBytes("/data/justificatifs/justificatif_domicile_1.pdf"));
        var fields = List.of(
                "firstName",
                "lastName",
                "fullName",
                "fullAddress",
                "recentDate",
                "issuerName",
                "identificationNumber",
                "motherBirthday");

        // When
        String json = chat.extract(doc, fields);

        // Then
        log.debug("Json output: {}", json);
        assertThat(json).isNotEmpty();
        assertJsonContent(json);
    }

    @Test
    void should_extract_data_from_pdf_with_json_schema() throws Exception {

        // Given
        var doc = new UserDocument(
                "application/pdf", IOs.readAllBytes("/data/justificatifs/justificatif_domicile_1.pdf"));
        var jsonSchema = Files.readString(Paths.get("src/test/resources/extract/schema.json"));

        // When
        String json = chat.extract(doc, jsonSchema);

        // Then
        log.debug("Json output: {}", json);
        assertThat(json).isNotEmpty();
        assertJsonContent(json);
    }

    // @Disabled("Does not work with ollama")
    @Test
    void should_extract_data_from_png() throws Exception {
        // Given
        var doc = new UserDocument("image/png", IOs.readAllBytes("/data/justificatifs/justificatif_domicile_1.png"));
        var jsonSchema = Files.readString(Paths.get("src/test/resources/extract/schema.json"));

        // When
        String json = chat.extract(doc, jsonSchema);

        // Then
        log.debug("Json output: {}", json);
        assertThat(json).isNotEmpty();
        assertJsonContent(json);
    }

    private void assertJsonContent(String json) throws JsonProcessingException {
        var user = objectMapper.readValue(json, User.class);
        assertThat(user.firstName).isEqualTo("Jean");
        assertThat(user.lastName).isEqualTo("Dupont");
        assertThat(user.fullName).isEqualTo("Jean Dupont");
        assertThat(user.fullAddress).isEqualTo("12 Rue de la Paix, 75002 Paris");
        assertThat(user.issuerName).isEqualTo("Orange");
        assertThat(user.identificationNumber).isEqualTo("581325418");
        assertThat(user.motherBirthday).isEqualTo("Absent");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record User(
            String firstName,
            String lastName,
            String fullName,
            String fullAddress,
            String recentDate,
            String issuerName,
            String identificationNumber,
            String motherBirthday) {}
}
