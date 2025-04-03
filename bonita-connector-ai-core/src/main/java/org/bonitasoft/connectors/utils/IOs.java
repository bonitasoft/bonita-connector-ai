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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.bonitasoft.connectors.ai.AiConnectorException;

public final class IOs {

    private IOs() {
        // Utility class
    }

    public static String readAsString(String classpathResource) {
        return new String(readAllBytes(classpathResource), StandardCharsets.UTF_8);
    }

    public static byte[] readAllBytes(String classpathResource) {
        try (InputStream stream = IOs.class.getResourceAsStream(classpathResource); ) {
            assert stream != null;
            return stream.readAllBytes();
        } catch (IOException e) {
            throw new AiConnectorException("Failed to read classpath resource: " + classpathResource, e);
        }
    }
}
