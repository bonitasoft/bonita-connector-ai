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

import java.util.*;

public record UserDocument(String mimeType, byte[] data, Map<String, Object> metadata) {

    public UserDocument(String mimeType, byte[] data) {
        this(mimeType, data, null);
    }

    public UserDocument(String mimeType, byte[] data, Map<String, Object> metadata) {
        this.mimeType = Optional.ofNullable(mimeType).orElse("application/octet-stream");
        this.data = Optional.ofNullable(data).orElse(new byte[0]);
        this.metadata = Optional.ofNullable(metadata).orElse(new HashMap<>());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserDocument that = (UserDocument) o;
        return Objects.deepEquals(data, that.data)
                && Objects.equals(mimeType, that.mimeType)
                && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mimeType, Arrays.hashCode(data), metadata);
    }

    @Override
    public String toString() {
        return "UserDocument{" + "mimeType='"
                + mimeType + '\'' + ", data="
                + Arrays.toString(data) + ", metadata="
                + metadata + '}';
    }
}
