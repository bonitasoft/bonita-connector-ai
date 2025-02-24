package org.bonitasoft.connectors.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class TikaAiDocumentReaderTest {

    private static final Logger log = LoggerFactory.getLogger(TikaAiDocumentReaderTest.class);

    TikaAiDocumentReader reader;

    @BeforeEach
    void setUp() {
        reader = new TikaAiDocumentReader();
    }

    @Test
    void pdf_content_should_be_parsed() throws IOException {
        // Given
        byte[] docData = Files.readAllBytes(Path.of("src/test/resources/test.pdf"));
        // When
        AiDocument doc = reader.read(docData);
        // Then
        assertThat(doc.getContent()).isNotEmpty();
        assertThat(doc.getMetadataNames()).isNotEmpty();
    }
}