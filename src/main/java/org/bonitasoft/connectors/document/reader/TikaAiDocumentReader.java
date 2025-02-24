package org.bonitasoft.connectors.document.reader;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.bonitasoft.connectors.AiConnectorException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class TikaAiDocumentReader implements AiDocumentReader {

    private final Parser parser = new AutoDetectParser();
    private final BodyContentHandler handler = new BodyContentHandler(-1);
    private final ParseContext context = new ParseContext();

    @Override
    public AiDocument read(byte[] docContent) {
        Metadata metadata = new Metadata();
        try (InputStream stream = new ByteArrayInputStream(docContent)) {
            parser.parse(stream, handler, metadata, context);
            return new TikaAiDocument(handler.toString(), metadata);
        } catch (Exception e) {
            throw new AiConnectorException("Failed to read doc content", e);
        }
    }
}
