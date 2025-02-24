package org.bonitasoft.connectors;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel.OpenAiEmbeddingModelBuilder;
import org.bonitasoft.connectors.document.loader.BonitaDocumentLoader;
import org.bonitasoft.connectors.document.loader.DocumentLoader;
import org.bonitasoft.engine.connector.AbstractConnector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

public abstract class AbstractAiConnector extends AbstractConnector {

    static final String URL = "url";
    static final String API_KEY = "apiKey";
    static final String MODEL_NAME = "modelName";
    static final String USER_PROMPT = "userPrompt";
    static final String SYSTEM_PROMPT = "systemPrompt";
    static final String SOURCE_DOCUMENT_REF = "sourceDocumentRef";

    static final String OUTPUT = "output";

    private ChatLanguageModel chatModel;
    private EmbeddingModel embeddingModel;

    private DocumentLoader documentLoader;

    public ChatLanguageModel getChatModel() {
        return chatModel;
    }

    public EmbeddingModel getEmbeddingModel() {
        return embeddingModel;
    }

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
            T value = type.cast(getInputParameter(inputName));
            if (value == null) {
                throw new ConnectorValidationException(this,
                        String.format("Mandatory parameter '%s' is missing.", inputName));
            }
            if (value instanceof String sValue && sValue.isEmpty()) {
                throw new ConnectorValidationException(this,
                        String.format("Mandatory parameter '%s' is missing.", inputName));
            }
        } catch (ClassCastException e) {
            throw new ConnectorValidationException(this, String.format("'%s' parameter must be a String", inputName));
        }
    }

    protected <T> T getInputValue(String name) {
        var value = getInputParameter(name);
        return value == null ? null : (T) value;
    }

    protected <T> T getInputValue(String name, T defaultValue) {
        var value = getInputParameter(name);
        return value == null ? defaultValue : (T) value;
    }

    /**
     * Core method:
     * - Execute all the business logic of your connector using the inputs (connect to an external service, compute some values ...).
     * - Set the output of the connector execution. If outputs are not set, connector fails.
     */
    @Override
    protected void executeBusinessLogic() throws ConnectorException {
        String userPrompt = getInputValue(USER_PROMPT);
        String docRef = getInputValue(SOURCE_DOCUMENT_REF);
        var lastDocument = documentLoader.load(docRef);
        String aiResponse = doExecute(chatModel, userPrompt, lastDocument);
        setOutputParameter(OUTPUT, aiResponse);
    }

    protected abstract String doExecute(ChatLanguageModel chatModel, String userPrompt, byte[] lastDocument) throws ConnectorException;

    protected OpenAiChatModel.OpenAiChatModelBuilder customizeChatModelBuilder(OpenAiChatModel.OpenAiChatModelBuilder chatModelBuilder) {
        return chatModelBuilder;
    }

    @Override
    public void connect() throws ConnectorException {
        String apiKey = getInputValue(API_KEY, "changeMe");

        OpenAiChatModel.OpenAiChatModelBuilder chatModelBuilder = OpenAiChatModel.builder()
                .apiKey(apiKey);

        String url = getInputValue(URL);
        if (url != null && !url.isEmpty()) {
            chatModelBuilder.baseUrl(url);
        }
        String modelName = getInputValue(MODEL_NAME, OpenAiChatModelName.GPT_4_O.toString());
        chatModelBuilder.modelName(modelName);
        chatModelBuilder = customizeChatModelBuilder(chatModelBuilder);
        this.chatModel = chatModelBuilder.build();

        OpenAiEmbeddingModelBuilder embeddingModelBuilder = OpenAiEmbeddingModel.builder()
                .apiKey(apiKey);
        if (url != null && !url.isEmpty()) {
            embeddingModelBuilder.baseUrl(url);
        }
        this.embeddingModel = embeddingModelBuilder.build();

        if (this.documentLoader == null) {
            this.documentLoader = new BonitaDocumentLoader(getAPIAccessor().getProcessAPI(), getExecutionContext());
        }
    }

    public void setDocumentLoader(DocumentLoader documentLoader) {
        this.documentLoader = documentLoader;
    }
}
