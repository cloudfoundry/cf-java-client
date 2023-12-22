/*
 * Copyright 2013-2022 the original author or authors.
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

package org.cloudfoundry.operations.applications;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The protocol of a {@link ManifestV3Route}
 */
public enum ManifestV3RouteProtocol {

    /**
     * TCP protocol
     */
    TCP("tcp"),

    /**
     * HTTP1 protocol
     */
    HTTP1("http1"),

    /**
     * HTTP2 protocol
     */
    HTTP2("http2");

    private final String value;

    ManifestV3RouteProtocol(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ManifestV3RouteProtocol from(String s) {
        switch (s.toLowerCase()) {
            case "http1":
                return HTTP1;
            case "http2":
                return HTTP2;
            case "tcp":
                return TCP;
            default:
                throw new IllegalArgumentException(String.format("Unknown protocol: %s", s));
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
