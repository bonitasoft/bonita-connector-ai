package org.bonitasoft.connectors.openai.ask;

import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.service.AiServices;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.openai.AbstractOpenAiConnector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

@Slf4j
public class OpenAiAskConnector extends AbstractOpenAiConnector {

    @Override
    public void validateInputParameters() throws ConnectorValidationException {
        super.validateInputParameters();

        // Specific validation
        if (getOpenAiConfiguration().getUserPrompt() == null) {
            throw new ConnectorValidationException("UserPrompt is required");
        }
        if (getOpenAiConfiguration().getUserPrompt().isBlank()) {
            throw new ConnectorValidationException("UserPrompt is blank or empty");
        }
    }

    /**
     * @return
     * @throws ConnectorException
     */
    @Override
    protected Object doExecute() throws ConnectorException {

        var openAiAssistant = AiServices.builder(OpenAiAssistant.class)
                .chatLanguageModel(chatModel)
                .systemMessageProvider(o -> openAiConfiguration.getSystemPrompt())
                .build();

        var userPrompt = openAiConfiguration.getUserPrompt();
        if (openAiConfiguration.getOutputJsonSchema().isPresent()) {
            userPrompt +=
                    """
                    **Instructions:**
                    1. If the information is not explicitly stated, use context and reasoning to infer the answer.
                    2. If a requested piece of information is not present in the text, clearly indicate that it is "Absent" without any additional explanation or commentary.
                    5. Follow the format instructions precisely.
                    6. Format the answer information clearly and structured in JSON format and formatted according to this JSON schema:
                    """;
            userPrompt += "\n   " + openAiConfiguration.getOutputJsonSchema().get();
            userPrompt +=
                    """
                    7. Do not include markdown in the output.
                    8. Do not include the JSON schema in the JSON output.
                    9. Answer must only contain the JSON output.
                    """;
        }

        PromptTemplate template = PromptTemplate.from(userPrompt);
        var prompt = getOpenAiConfiguration()
                .getSourceDocumentRef()
                .map(this::getDocContent)
                .map(docContent -> template.apply(Map.of("document", docContent)))
                .orElse(Prompt.from(openAiConfiguration.getUserPrompt()));

        var chatRequest = ChatRequest.builder().messages(prompt.toUserMessage()).build();

        return monitor(() -> openAiAssistant.chat(chatRequest));
    }

    public interface OpenAiAssistant {
        String chat(ChatRequest chatRequest);
    }
}
