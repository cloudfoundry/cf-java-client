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
 * The file to be used for configuring the LDAP authentication.
 */
public enum LdapProfileFile {
    SimpleBind("ldap/ldap-simple-bind.xml"),

    SearchAndBind("ldap/ldap-search-and-bind.xml"),

    SearchAndCompare("ldap/ldap-search-and-compare.xml");

    private final String value;

    LdapProfileFile(String value) {
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
    static LdapProfileFile from(String s) {
        switch (s.toLowerCase()) {
            case "ldap/ldap-simple-bind.xml":
                return SimpleBind;
            case "ldap/ldap-search-and-bind.xml":
                return SearchAndBind;
            case "ldap/ldap-search-and-compare.xml":
                return SearchAndCompare;
            default:
                throw new IllegalArgumentException(String.format("Unknown ldap profile file: %s", s));
        }
    }

}
