package org.bonitasoft.connectors;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.bonitasoft.engine.connector.AbstractConnector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

import java.util.logging.Logger;

public class AiConnector extends AbstractConnector {

    private static final Logger LOGGER = Logger.getLogger(AiConnector.class.getName());

    static final String URL = "url";
    static final String API_KEY = "apiKey";
    static final String USER_PROMPT = "userPrompt";
    static final String MODEL_NAME = "modelName";

    static final String OUTPUT = "output";

    /**
     * Perform validation on the inputs defined on the connector definition (src/main/resources/bonita-connector-ai.def)
     * You should:
     * - validate that mandatory inputs are presents
     * - validate that the content of the inputs is coherent with your use case (e.g: validate that a date is / isn't in the past ...)
     */
    @Override
    public void validateInputParameters() throws ConnectorValidationException {
        checkMandatoryStringInput(USER_PROMPT);
    }

    protected void checkMandatoryStringInput(String inputName) throws ConnectorValidationException {
        try {
            String value = (String) getInputParameter(inputName);
            if (value == null || value.isEmpty()) {
                throw new ConnectorValidationException(this,
                        String.format("Mandatory parameter '%s' is missing.", inputName));
            }
        } catch (ClassCastException e) {
            throw new ConnectorValidationException(this, String.format("'%s' parameter must be a String", inputName));
        }
    }

    private <T> T getInputValue(String name) {
        var value = getInputParameter(name);
        return value == null ? null : (T) value;
    }

    private <T> T getInputValue(String name, T defaultValue) {
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
        OpenAiChatModel chatModel = getChatModel();

        String userPrompt = getInputValue(USER_PROMPT);
        String aiResponse = chatModel.generate(userPrompt);

        setOutputParameter(OUTPUT, aiResponse);
    }

    private OpenAiChatModel getChatModel() {

        String apiKey = getInputValue(API_KEY, "changeMe");
        String modelName = getInputValue(MODEL_NAME, "gpt-3.5-turbo");
        var openaiBuilder = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName);

        String url = getInputValue(URL);
        if (url != null && !url.isEmpty()) {
            openaiBuilder.baseUrl(url);
        }

        return openaiBuilder.build();
    }

    /**
     * [Optional] Open a connection to remote server
     */
    @Override
    public void connect() throws ConnectorException {

    }

    /**
     * [Optional] Close connection to remote server
     */
    @Override
    public void disconnect() throws ConnectorException {
    }
}
