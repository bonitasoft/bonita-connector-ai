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
package org.bonitasoft.connectors.ai.mistral;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.AiConnectorException;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.connectors.ai.classify.ClassifyAiChat;

@Slf4j
public class MistralAiClassifyChat extends ClassifyAiChat<MistralAiChatModel> implements MistralAiChat {
    public MistralAiClassifyChat(AiConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected ChatMessage newDocMessage(UserDocument document) {
        if (List.of("image/png", "image/jpg", "image/jpeg").contains(document.mimeType())) {
            throw new AiConnectorException("Image types not supported yet by Mistral AI chat/completion API");
        }
        return super.newDocMessage(document);
    }
}
