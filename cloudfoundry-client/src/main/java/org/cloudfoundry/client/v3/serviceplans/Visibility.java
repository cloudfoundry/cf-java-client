/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.client.v3.serviceplans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The visibility of a {@link ServicePlan}
 */
public enum Visibility {

    /**
     * Only Admin, Admin Read-Only and Global Auditor can see the service plan
     */
    ADMIN("admin"),

    /**
     * Restricted to members of a set of organizations
     */
    ORGANIZATION("organization"),

    /**
     * Everyone, including unauthenticated users can see the service plan
     */
    PUBLIC("public"),

    /**
     * Restricted to members of a space; only possible if the plan comes from a space-scoped service broker
     */
    SPACE("space");

    private final String value;

    Visibility(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Visibility from(String s) {
        switch (s.toLowerCase()) {
            case "admin":
                return ADMIN;
            case "organization":
                return ORGANIZATION;
            case "public":
                return PUBLIC;
            case "space":
                return SPACE;
            default:
                throw new IllegalArgumentException(String.format("Unknown visibility: %s", s));
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
