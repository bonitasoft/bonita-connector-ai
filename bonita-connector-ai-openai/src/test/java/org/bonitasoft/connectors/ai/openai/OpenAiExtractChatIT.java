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
package org.bonitasoft.connectors.ai.openai;

import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.extract.ExtractChat;
import org.bonitasoft.connectors.ai.extract.ExtractChatIT;

@Slf4j
class OpenAiExtractChatIT extends ExtractChatIT {

    @Override
    protected void customize(AiConfiguration.AiConfigurationBuilder builder) {
        builder.apiKey(System.getenv("OPENAI_API_KEY"));
    }

    @Override
    protected ExtractChat getChat(AiConfiguration configuration) {
        return new OpenAiExtractChat(configuration);
    }
}
