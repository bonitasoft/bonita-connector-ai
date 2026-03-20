/**
 * Copyright (C) 2025 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.connectors.ai.anthropic;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.connectors.ai.extract.ExtractChat;
import org.bonitasoft.connectors.ai.extract.ExtractChatIT;
import org.bonitasoft.connectors.utils.IOs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Slf4j
@Tag("provider-api")
class AnthropicExtractChatIT extends ExtractChatIT {

    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    ExtractChat extractChat;

    @BeforeEach
    void setUpExtract() {
        var configurationBuilder = AiConfiguration.builder().requestTimeout(3 * 60 * 1000);
        configurationBuilder.apiKey(System.getenv("ANTHROPIC_API_KEY"));
        String modelName = System.getenv("ANTHROPIC_MODEL_NAME");
        if (modelName != null && !modelName.isEmpty()) {
            configurationBuilder.chatModelName(modelName);
        }
        var configuration = configurationBuilder.build();
        extractChat = new AnthropicExtractChat(configuration);
    }

    @Override
    protected void customize(AiConfiguration.AiConfigurationBuilder builder) {
        builder.apiKey(System.getenv("ANTHROPIC_API_KEY"));
        String modelName = System.getenv("ANTHROPIC_MODEL_NAME");
        if (modelName != null && !modelName.isEmpty()) {
            builder.chatModelName(modelName);
        }
    }

    @Override
    protected ExtractChat getChat(AiConfiguration configuration) {
        return new AnthropicExtractChat(configuration);
    }

    @Test
    void should_extract_data_from_pdf_with_fields() throws Exception {
        // Given
        var doc = new UserDocument(
                "application/pdf", IOs.readAllBytes("/data/justificatifs/justificatif_domicile_1.pdf"));
        var fields = List.of("firstName", "lastName", "fullName", "fullAddress", "issuerName");

        // When
        String json = extractChat.extract(doc, fields);

        // Then
        log.debug("JSON output: {}", json);
        assertThat(json).isNotEmpty();
        var user = mapper.readValue(json, ExtractedUser.class);
        assertThat(user.firstName()).isEqualTo("Jean");
        assertThat(user.lastName()).isEqualTo("Dupont");
        assertThat(user.fullName()).isEqualTo("Jean Dupont");
        assertThat(user.fullAddress()).isEqualTo("12 Rue de la Paix, 75002 Paris");
        assertThat(user.issuerName()).isEqualTo("Orange");
    }

    @Test
    void should_extract_data_from_png_with_vision() throws Exception {
        // Given - Claude supports native vision for images
        var doc = new UserDocument("image/png", IOs.readAllBytes("/data/justificatifs/justificatif_domicile_1.png"));
        var jsonSchema = IOs.readAsString("/extract/schema.json");

        // When
        String json = extractChat.extract(doc, jsonSchema);

        // Then
        log.debug("JSON output: {}", json);
        assertThat(json).isNotEmpty();
        var user = mapper.readValue(json, ExtractedUser.class);
        assertThat(user.firstName()).isEqualTo("Jean");
        assertThat(user.lastName()).isEqualTo("Dupont");
    }

    @Test
    void should_extract_identification_number() throws Exception {
        // Given
        var doc = new UserDocument(
                "application/pdf", IOs.readAllBytes("/data/justificatifs/justificatif_domicile_1.pdf"));
        var fields = List.of("identificationNumber", "issuerName");

        // When
        String json = extractChat.extract(doc, fields);

        // Then
        log.debug("JSON output: {}", json);
        assertThat(json).isNotEmpty();
        var result = mapper.readValue(json, IdResult.class);
        assertThat(result.identificationNumber()).isEqualTo("581325418");
        assertThat(result.issuerName()).isEqualTo("Orange");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ExtractedUser(String firstName, String lastName, String fullName, String fullAddress, String issuerName) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record IdResult(String identificationNumber, String issuerName) {}
}
