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

package org.cloudfoundry.uaa.identityproviders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The type of Identity provider in paylaod
 */
public enum Type {
    INTERNAL("uaa"),

    SAML("saml"),

    LDAP("ldap"),

    OAUTH2("oauth2.0"),

    OIDC("oidc1.0");

    private final String value;

    Type(String value) {
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
    static Type from(String s) {
        switch (s.toLowerCase()) {
            case "uaa":
                return INTERNAL;
            case "saml":
                return SAML;
            case "ldap":
                return LDAP;
            case "oauth2.0":
                return OAUTH2;
            case "oidc1.0":
                return OIDC;
            default:
                throw new IllegalArgumentException(String.format("Unknown type: %s", s));
        }
    }

}
