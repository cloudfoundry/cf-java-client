/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.uaa.identityproviders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The mapping mode for a {@link SamlConfiguration}
 */
public enum ExternalGroupMappingMode {

    /**
     * The explicitly mapped type
     */
    EXPLICITLY_MAPPED("EXPLICITLY_MAPPED"),

    /**
     * The as scopes type
     */
    AS_SCOPES("AS_SCOPES");

    private final String value;

    ExternalGroupMappingMode(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ExternalGroupMappingMode from(String s) {
        switch (s.toLowerCase()) {
            case "explicitly_mapped":
                return EXPLICITLY_MAPPED;
            case "as_scopes":
                return AS_SCOPES;
            default:
                throw new IllegalArgumentException(String.format("Unknown external group mapping mode: %s", s));
        }
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return getValue();
    }

}
