package org.bonitasoft.connectors;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import lombok.Getter;
import lombok.Setter;
import org.bonitasoft.connectors.document.loader.BonitaDocumentLoader;
import org.bonitasoft.connectors.document.loader.DocumentLoader;
import org.bonitasoft.engine.connector.AbstractConnector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public abstract class AbstractAiConnector extends AbstractConnector {

    static final String URL = "url";
    static final String API_KEY = "apiKey";
    static final String CHAT_MODEL_NAME = "chatModelName";
    static final String USER_PROMPT = "userPrompt";
    static final String SYSTEM_PROMPT = "systemPrompt";
    static final String SOURCE_DOCUMENT_REF = "sourceDocumentRef";
    static final String OUTPUT = "output";

    private String endpointUrl;
    private String apiKey;
    private String chatModelName;
    private String userPrompt;
    private String systemPrompt;
    private String sourceDocumentRef;

    private DocumentLoader documentLoader;

    private ChatLanguageModel chatModel;
    private Assistant assistant;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    /**
     * Perform validation on the inputs defined on the connector definition (src/main/resources/bonita-connector-ai.def)
     * You should:
     * - validate that mandatory inputs are presents
     * - validate that the content of the inputs is coherent with your use case (e.g: validate that a date is / isn't in the past ...)
     */
    @Override
    public void validateInputParameters() throws ConnectorValidationException {
        checkMandatoryStringInput(USER_PROMPT, String.class);
    }

    protected <T> void checkMandatoryStringInput(String inputName, Class<T> type) throws ConnectorValidationException {
        try {
            T value = getInputValue(inputName, type).orElseThrow(() ->
                    new ConnectorValidationException(this, String.format("Mandatory parameter '%s' is missing.", inputName))
            );
            if (value instanceof String sValue && sValue.isEmpty()) {
                throw new ConnectorValidationException(this,
                        String.format("Mandatory parameter '%s' is missing.", inputName));
            }
        } catch (ClassCastException e) {
            throw new ConnectorValidationException(this, String.format("'%s' parameter must be a String", inputName));
        }
    }

    protected <T> Optional<T> getInputValue(String name, Class<T> type) {
        var value = type.cast(getInputParameter(name));
        return Optional.ofNullable(value);
    }

    protected <T> T getInputValue(String name, Class<T> type, T defaultValue) {
        return getInputValue(name, type).orElse(defaultValue);
    }

    /**
     * Core method:
     * - Execute all the business logic of your connector using the inputs (connect to an external service, compute some values ...).
     * - Set the output of the connector execution. If outputs are not set, connector fails.
     */
    @Override
    protected void executeBusinessLogic() throws ConnectorException {
        this.initialize();
        String aiResponse = doExecute();
        setOutputParameter(OUTPUT, aiResponse);
    }

    protected abstract String doExecute() throws ConnectorException;

    protected OpenAiChatModel.OpenAiChatModelBuilder customizeChatModelBuilder(OpenAiChatModel.OpenAiChatModelBuilder chatModelBuilder) {
        return chatModelBuilder;
    }


    public void initialize() {
        if (!initialized.get()) {

            this.endpointUrl = getInputValue(URL, String.class, null);
            this.apiKey = getInputValue(API_KEY, String.class, "changeMe");

            this.chatModelName = getInputValue(CHAT_MODEL_NAME, String.class, OpenAiChatModelName.GPT_4_O.toString());

            this.systemPrompt = getInputValue(SYSTEM_PROMPT, String.class, null);
            this.userPrompt = getInputValue(USER_PROMPT, String.class, "You are a polite assistant");

            this.sourceDocumentRef = getInputValue(SOURCE_DOCUMENT_REF, String.class, null);
            if (this.documentLoader == null) {
                this.documentLoader = new BonitaDocumentLoader(getAPIAccessor().getProcessAPI(), getExecutionContext());
            }

            OpenAiChatModel.OpenAiChatModelBuilder chatModelBuilder = OpenAiChatModel.builder().apiKey(apiKey);
            if (this.endpointUrl != null && !this.endpointUrl.isEmpty()) {
                chatModelBuilder.baseUrl(this.endpointUrl);
            }
            chatModelBuilder.modelName(this.chatModelName);
            chatModelBuilder = customizeChatModelBuilder(chatModelBuilder);
            this.chatModel = chatModelBuilder.build();

            this.assistant = AiServices.builder(Assistant.class)
                    .chatLanguageModel(this.chatModel)
                    .systemMessageProvider(chatMemoryId -> this.systemPrompt)
                    .build();

            this.initialized.set(true);
        }
    }

}
