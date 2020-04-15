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
 * The status reason of a {@link Deployment}
 */
public enum DeploymentStatusReason {

    /**
     * The canceled status reason
     */
    CANCELED("CANCELED"),

    /**
     * The canceling status reason
     */
    CANCELING("CANCELING"),

    /**
     * The degenerate status reason
     */
    DEGENERATE("DEGENERATE"),

    /**
     * The deployed status reason
     */
    DEPLOYED("DEPLOYED"),

    /**
     * The deploying status reason
     */
    DEPLOYING("DEPLOYING"),

    /**
     * The superseded status reason
     */
    SUPERSEDED("SUPERSEDED");

    private final String value;

    DeploymentStatusReason(String value) {
        this.value = value;
    }

    @JsonCreator
    public static DeploymentStatusReason from(String s) {
        switch (s.toLowerCase()) {
            case "canceled":
                return CANCELED;
            case "canceling":
                return CANCELING;
            case "degenerate":
                return DEGENERATE;
            case "deployed":
                return DEPLOYED;
            case "deploying":
                return DEPLOYING;
            case "superseded":
                return SUPERSEDED;
            default:
                throw new IllegalArgumentException(String.format("Unknown deployment state: %s", s));
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
