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

package org.cloudfoundry.uaa.tokens;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The key type
 */
public enum KeyType {

    /**
     * The MAC key type
     */
    MAC("MAC"),

    /**
     * The RSA key type
     */
    RSA("RSA");

    private final String value;

    KeyType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static KeyType from(String s) {
        switch (s.toLowerCase()) {
            case "mac":
                return MAC;
            case "rsa":
                return RSA;
            default:
                throw new IllegalArgumentException(String.format("Unknown key type: %s", s));
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
