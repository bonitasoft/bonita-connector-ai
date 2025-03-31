package org.bonitasoft.connectors.openai.classify;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.connectors.ai.mistral.MistralAiClassifyChat;
import org.bonitasoft.connectors.utils.IOs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MistralAiClassifyChatIT {

    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    MistralAiClassifyChat chat;

    @BeforeEach
    void setUp() {

        var configuration = AiConfiguration.builder()
                //                .baseUrl("http://localhost:11434/v1")
                //                .chatModelName("llama3.1:8b")
                //                .apiKey("changeMe")
                .requestTimeout(3 * 60 * 1000)
                .build();

        chat = new MistralAiClassifyChat(configuration);
    }

    @Test
    void should_classify_user_doc() throws Exception {
        // Given
        var doc = new UserDocument("application/pdf", IOs.readAllBytes("/data/classify/rib-sample.pdf"));
        // "/data/classify/justificatif-anon.jpg"
        // "/data/AgileManifesto.pdf"

        var categories = List.of("RIB", "Carte d'identité", "Justificatif de domicile", "Passeport", "Unknown");

        // When
        String category = chat.classify(categories, doc);

        // Then
        assertThat(category).isNotEmpty();
        Classification classification = mapper.readValue(category, Classification.class);
        assertThat(classification.category()).isEqualTo("RIB");
        assertThat(classification.confidence()).isGreaterThan(0.5);
    }

    record Classification(String category, Double confidence) {}
}
