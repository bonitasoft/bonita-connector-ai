package org.bonitasoft.connectors.openai.extract;

import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import java.util.Collections;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.bonitasoft.connectors.openai.AbstractOpenAiConnector;
import org.bonitasoft.connectors.openai.OpenAiConnectorException;
import org.bonitasoft.engine.connector.ConnectorException;

@Slf4j
public class OpenAiExtractDataConnector extends AbstractOpenAiConnector {

    /**
     * @return
     * @throws ConnectorException
     */
    @Override
    protected Object doExecute() throws ConnectorException {

        // Doc content
        var docContent = getOpenAiConfiguration()
                .getSourceDocumentRef()
                .map(this::getDocContent)
                .orElse("");

        // JSON fields
        var fieldsToExtract = openAiConfiguration.getFieldsToExtract().orElse(Collections.emptyList());
        var fieldsToExtractForPrompt = String.join("\n   - ", fieldsToExtract);

        OpenAiExtractor openAiExtractor = AiServices.create(OpenAiExtractor.class, chatModel);
        if (openAiConfiguration.getOutputJsonSchema().isPresent()) {
            return monitor(() -> openAiExtractor.extractWithJsonSchema(
                    docContent, openAiConfiguration.getOutputJsonSchema().get()));
        }
        return monitor(() -> openAiExtractor.extract(docContent, fieldsToExtractForPrompt));
    }

    protected String monitor(Callable<String> callable) {
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            return callable.call();
        } catch (Exception e) {
            throw new OpenAiConnectorException("Failed to request LLM", e);
        } finally {
            stopWatch.stop();
            log.debug("OpenAI call duration: {}", stopWatch);
        }
    }

    public interface OpenAiExtractor {
        @SystemMessage(fromResource = "prompt/extract/system.txt")
        @UserMessage(fromResource = "prompt/extract/user.txt")
        String extract(@V("document") String document, @V("fieldsToExtract") String fieldsToExtract);

        @SystemMessage(fromResource = "prompt/extract/system.txt")
        @UserMessage(fromResource = "prompt/extract/user_with_json_schema.txt")
        String extractWithJsonSchema(@V("document") String document, @V("jsonSchema") String jsonSchema);
    }
}
