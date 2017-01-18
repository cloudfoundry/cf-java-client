/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.reactor.util;

import io.netty.handler.codec.http.HttpHeaderNames;
import reactor.ipc.netty.http.client.HttpClientRequest;

import java.util.Optional;

/**
 * Utilities for working with the {@Code User-Agent}
 */
public final class UserAgent {

    /**
     * The {@code User-Agent}
     */
    public static final String USER_AGENT = String.format("Cloud Foundry Java Client/%s", version());

    private UserAgent() {
    }

    /**
     * Add the {@code User-Agent} to a request.  Typically used with `.map`
     *
     * @param request The request to transform
     * @return the transformed request
     */
    public static HttpClientRequest addUserAgent(HttpClientRequest request) {
        return request.header(HttpHeaderNames.USER_AGENT, USER_AGENT);
    }

    private static String version() {
        return Optional.ofNullable(UserAgent.class.getPackage().getImplementationVersion())
            .orElse("unknown");
    }

}
