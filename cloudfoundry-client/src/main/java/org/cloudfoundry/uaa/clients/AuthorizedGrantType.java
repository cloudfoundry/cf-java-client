/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.uaa.clients;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The grant types that can be used to obtain a token with this client.
 */
public enum AuthorizedGrantType {

    /**
     * The authorization code grant type
     */
    AUTHORIZATION_CODE("authorization_code"),

    /**
     * The client_credentials grant type
     */
    CLIENT_CREDENTIALS("client_credentials"),

    /**
     * The implicit grant type
     */
    IMPLICIT("implicit"),

    /**
     * The password grant type
     */
    PASSWORD("password");

    private final String value;

    AuthorizedGrantType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return getValue();
    }

    @JsonCreator
    static AuthorizedGrantType from(String s) {
        switch (s.toLowerCase()) {
            case "authorization_code":
                return AUTHORIZATION_CODE;
            case "client_credentials":
                return CLIENT_CREDENTIALS;
            case "implicit":
                return IMPLICIT;
            case "password":
                return PASSWORD;
            default:
                throw new IllegalArgumentException(String.format("Unknown authorized grant type: %s", s));
        }
    }
}
