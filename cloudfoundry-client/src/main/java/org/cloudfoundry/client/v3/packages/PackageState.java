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

package org.cloudfoundry.client.v3.packages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The state of a {@link Package}
 */
public enum PackageState {

    /**
     * The awaiting upload state
     */
    AWAITING_UPLOAD("AWAITING_UPLOAD"),

    /**
     * The copying state
     */
    COPYING("COPYING"),

    /**
     * The expired state
     */
    EXPIRED("EXPIRED"),

    /**
     * The failed state
     */
    FAILED("FAILED"),

    /**
     * The processing upload state
     */
    PROCESSING_UPLOAD("PROCESSING_UPLOAD"),

    /**
     * The ready state
     */
    READY("READY");

    private final String value;

    PackageState(String value) {
        this.value = value;
    }

    @JsonCreator
    public static PackageState from(String s) {
        switch (s.toLowerCase()) {
            case "awaiting_upload":
                return AWAITING_UPLOAD;
            case "copying":
                return COPYING;
            case "expired":
                return EXPIRED;
            case "failed":
                return FAILED;
            case "processing_upload":
                return PROCESSING_UPLOAD;
            case "ready":
                return READY;
            default:
                throw new IllegalArgumentException(String.format("Unknown package state: %s", s));
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
