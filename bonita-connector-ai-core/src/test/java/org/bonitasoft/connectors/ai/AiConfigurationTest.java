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
package org.bonitasoft.connectors.ai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AiConfigurationTest {

    @Test
    void should_return_empty_api_version_when_not_set() {
        var config = AiConfiguration.builder().build();
        assertThat(config.getApiVersion()).isEmpty();
    }

    @Test
    void should_return_api_version_when_set() {
        var config = AiConfiguration.builder().apiVersion("2024-10-21").build();
        assertThat(config.getApiVersion()).contains("2024-10-21");
    }
}
