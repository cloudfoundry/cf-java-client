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
 * The file to be used for group integration.
 */
public enum LdapGroupFile {
    NoGroup("ldap/ldap-no-groups.xml"),

    GroupsAsScopes("ldap/ldap-groups-as-scopes.xml"),

    GroupsMapToScopes("ldap/ldap-groups-map-to-scopes.xml");

    private final String value;

    LdapGroupFile(String value) {
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
    static LdapGroupFile from(String s) {
        switch (s.toLowerCase()) {
            case "ldap/ldap-no-groups.xml":
                return NoGroup;
            case "ldap/ldap-groups-as-scopes.xml":
                return GroupsAsScopes;
            case "ldap/ldap-groups-map-to-scopes.xml":
                return GroupsMapToScopes;
            default:
                throw new IllegalArgumentException(String.format("Unknown ldap group file: %s", s));
        }
    }

}
