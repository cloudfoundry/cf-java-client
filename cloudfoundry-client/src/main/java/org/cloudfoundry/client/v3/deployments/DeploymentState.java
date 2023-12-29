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

package org.cloudfoundry.client.v3.deployments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The state of a {@link Deployment}
 */
public enum DeploymentState {

    /**
     * The deploying state
     */
    DEPLOYING("DEPLOYING"),

    /**
     * The deployed state
     */
    DEPLOYED("DEPLOYED"),

    /**
     * The canceling state
     */
    CANCELING("CANCELING"),

    /**
     * The canceled state
     */
    CANCELED("CANCELED");

    private final String value;

    DeploymentState(String value) {
        this.value = value;
    }

    @JsonCreator
    public static DeploymentState from(String s) {
        switch (s.toLowerCase()) {
            case "deploying":
                return DEPLOYING;
            case "deployed":
                return DEPLOYED;
            case "canceling":
                return CANCELING;
            case "canceled":
                return CANCELED;
            default:
                throw new IllegalArgumentException(
                        String.format("Unknown deployment state: %s", s));
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
