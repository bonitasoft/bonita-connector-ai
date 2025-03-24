package org.bonitasoft.connectors.openai.classify;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bonitasoft.connectors.openai.OpenAiConfiguration;
import org.bonitasoft.connectors.openai.doc.UserDocument;
import org.bonitasoft.connectors.utils.IOs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAiClassifyChatIT {

    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    OpenAiClassifyChat chat;

    @BeforeEach
    void setUp() {

        var configuration = OpenAiConfiguration.builder()
                .baseUrl("http://localhost:11434/v1")
                .chatModelName("llama3.1:8b")
                .apiKey("changeMe")
                .requestTimeout(3 * 60 * 1000)
                .build();

        chat = new OpenAiClassifyChat(configuration);
    }


    @Test
    void should_classify_user_doc() throws Exception {
        // Given
        var doc = new UserDocument("application/pdf", IOs.readAllBytes("/data/classify/rib-sample.pdf"));
        var categories = List.of("RIB", "Carte d'identité", "Justificatif de domicile", "Passeport");

        // When
        String category = chat.classify(categories, doc);

        // Then
        assertThat(category).isNotEmpty();
        Classification classification = mapper.readValue(category, Classification.class);
        assertThat(classification.category()).isEqualTo("RIB");
        assertThat(classification.confidence()).isGreaterThan(0.5);
    }

    record Classification(String category, Double confidence) {
    }
}