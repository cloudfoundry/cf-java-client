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

package org.cloudfoundry.client.v3.serviceinstances;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The protocol of a {@link ServiceInstance}
 */
public enum ServiceInstanceType {

    /**
     * Managed service instance
     */
    MANAGED("managed"),

    /**
     * User provided service instance
     */
    USER_PROVIDED("user-provided");

    private final String value;

    ServiceInstanceType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ServiceInstanceType from(String s) {
        switch (s.toLowerCase()) {
            case "managed":
                return MANAGED;
            case "user-provided":
                return USER_PROVIDED;
            default:
                throw new IllegalArgumentException(
                        String.format("Unknown service instance type: %s", s));
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
