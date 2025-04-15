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
package org.bonitasoft.connectors.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AiResponseTest {

    @Test
    void noMarkdown() {
        var text = "```json\n" + "{\n" + "  \"category\": \"Unknown\",\n" + "  \"confidence\": 0.9\n" + "}\n" + "```";
        var response = AiResponse.noJsonBlock(text);
        assertThat(response).doesNotContain("``json").startsWith("{");
    }

    @Test
    void ensureJson() {
        var text = "```json\n" + "{\n" + "  \"category\": \"Unknown\",\n" + "  \"confidence\": 0.9\n" + "}\n" + "```";
        var response = AiResponse.ensureJson(text);
        assertThat(response).doesNotContain("``json").startsWith("{");
    }
}
