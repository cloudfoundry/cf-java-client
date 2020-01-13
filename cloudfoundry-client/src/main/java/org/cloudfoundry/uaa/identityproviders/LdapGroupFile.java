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
 * The file to be used for group integration.
 */
public enum LdapGroupFile {

    GROUPS_AS_SCOPES("ldap/ldap-groups-as-scopes.xml"),

    GROUPS_MAP_TO_SCOPES("ldap/ldap-groups-map-to-scopes.xml"),

    NO_GROUP("ldap/ldap-groups-null.xml");

    private final String value;

    LdapGroupFile(String value) {
        this.value = value;
    }

    @JsonCreator
    public static LdapGroupFile from(String s) {
        switch (s.toLowerCase()) {
            case "ldap/ldap-groups-as-scopes.xml":
                return GROUPS_AS_SCOPES;
            case "ldap/ldap-groups-map-to-scopes.xml":
                return GROUPS_MAP_TO_SCOPES;
            case "ldap/ldap-groups-null.xml":
                return NO_GROUP;
            default:
                throw new IllegalArgumentException(String.format("Unknown ldap group file: %s", s));
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
