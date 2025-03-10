package org.bonitasoft.connectors.openai;

import static org.bonitasoft.connectors.openai.OpenAiConfiguration.*;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.Setter;
import org.bonitasoft.connectors.document.loader.BonitaDocumentLoader;
import org.bonitasoft.connectors.document.loader.DocumentLoader;
import org.bonitasoft.engine.connector.AbstractConnector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public abstract class AbstractOpenAiConnector extends AbstractConnector {

    public static final String OUTPUT = "output";

    private OpenAiConfiguration openAiConfiguration;

    private DocumentLoader documentLoader;

    private ChatLanguageModel chatModel;
    private OpenAiAssistant openAiAssistant;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    /**
     * Perform validation on the inputs defined on the connector definition
     * (src/main/resources/bonita-connector-ai.def) You should: - validate that mandatory inputs are
     * presents - validate that the content of the inputs is coherent with your use case (e.g:
     * validate that a date is / isn't in the past ...)
     */
    @Override
    public void validateInputParameters() throws ConnectorValidationException {
        if (this.openAiConfiguration == null) {
            this.openAiConfiguration = new OpenAiConfiguration(getInputParameters());
        }
        this.openAiConfiguration.validate();
    }

    /**
     * Core method: - Execute all the business logic of your connector using the inputs (connect to an
     * external service, compute some values ...). - Set the output of the connector execution. If
     * outputs are not set, connector fails.
     */
    @Override
    protected void executeBusinessLogic() throws ConnectorException {
        this.initialize();
        String aiResponse = doExecute();
        setOutputParameter(OUTPUT, aiResponse);
    }

    protected abstract String doExecute() throws ConnectorException;

    protected OpenAiChatModel.OpenAiChatModelBuilder customizeChatModelBuilder(
            OpenAiChatModel.OpenAiChatModelBuilder chatModelBuilder) {
        return chatModelBuilder;
    }

    public void initialize() {
        if (!initialized.get()) {

            if (this.documentLoader == null) {
                this.documentLoader = new BonitaDocumentLoader(getAPIAccessor().getProcessAPI(), getExecutionContext());
            }

            OpenAiChatModel.OpenAiChatModelBuilder chatModelBuilder =
                    OpenAiChatModel.builder().apiKey(openAiConfiguration.getApiKey());
            openAiConfiguration.getEndpointUrl().ifPresent(chatModelBuilder::baseUrl);
            chatModelBuilder.modelName(openAiConfiguration.getChatModelName());
            chatModelBuilder = customizeChatModelBuilder(chatModelBuilder);
            this.chatModel = chatModelBuilder.build();

            this.openAiAssistant = AiServices.builder(OpenAiAssistant.class)
                    .chatLanguageModel(this.chatModel)
                    .systemMessageProvider(chatMemoryId -> openAiConfiguration.getSystemPrompt())
                    .build();

            this.initialized.set(true);
        }
    }

    @NotNull
    private Map<String, Object> getInputParameters() {
        var parameters = new HashMap<String, Object>();
        parameters.put(URL, getInputParameter(URL));
        parameters.put(API_KEY, getInputParameter(API_KEY));
        parameters.put(CHAT_MODEL_NAME, getInputParameter(CHAT_MODEL_NAME));
        parameters.put(SYSTEM_PROMPT, getInputParameter(SYSTEM_PROMPT));
        parameters.put(MODEL_TEMPERATURE, getInputParameter(MODEL_TEMPERATURE));
        parameters.put(USER_PROMPT, getInputParameter(USER_PROMPT));
        parameters.put(SOURCE_DOCUMENT_REF, getInputParameter(SOURCE_DOCUMENT_REF));
        return parameters;
    }
}
