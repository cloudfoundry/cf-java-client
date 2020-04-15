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

package org.cloudfoundry.client.v3.deployments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The status value of a {@link Deployment}
 */
public enum DeploymentStatusValue {

    /**
     * The active status value
     */
    ACTIVE("ACTIVE"),

    /**
     * The canceling status value
     */
    CANCELING("CANCELING"),

    /**
     * The deploying status value
     */
    DEPLOYING("DEPLOYING"),

    /**
     * The finalized status value
     */
    FINALIZED("FINALIZED");

    private final String value;

    DeploymentStatusValue(String value) {
        this.value = value;
    }

    @JsonCreator
    public static DeploymentStatusValue from(String s) {
        switch (s.toLowerCase()) {
            case "active":
                return ACTIVE;
            case "deploying":
                return DEPLOYING;
            case "canceling":
                return CANCELING;
            case "finalized":
                return FINALIZED;
            default:
                throw new IllegalArgumentException(String.format("Unknown deployment status value: %s", s));
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
