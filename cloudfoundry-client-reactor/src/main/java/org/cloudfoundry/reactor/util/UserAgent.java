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

package org.cloudfoundry.reactor.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import reactor.netty.http.client.HttpClient;

import java.util.Optional;

/**
 * Utilities for working with the {@Code User-Agent}
 */
public final class UserAgent {

    /**
     * The {@code User-Agent}
     */
    public static final String USER_AGENT = String.format("CloudFoundryJavaClient/%s (Java; %s/%s) ReactorNetty/%s (Netty/%s)",
        javaClientVersion(), javaVendor(), javaVersion(), reactorNettyVersion(), nettyVersion());

    private UserAgent() {
    }

    /**
     * Add the {@code User-Agent} to a request.  Typically used with `.map`
     *
     * @param httpHeaders The headers to transform
     */
    public static void setUserAgent(HttpHeaders httpHeaders) {
        httpHeaders.set(HttpHeaderNames.USER_AGENT, USER_AGENT);
    }

    private static String javaClientVersion() {
        return Optional.ofNullable(UserAgent.class.getPackage().getImplementationVersion())
            .orElse("unknown");
    }

    private static String javaVendor() {
        return Optional.ofNullable(System.getProperty("java.vendor"))
            .orElse("unknown");
    }

    private static String javaVersion() {
        return Optional.ofNullable(System.getProperty("java.version"))
            .orElse("unknown");
    }

    private static String nettyVersion() {
        return Optional.ofNullable(Bootstrap.class.getPackage().getImplementationVersion())
            .orElse("unknown");
    }

    private static String reactorNettyVersion() {
        return Optional.ofNullable(HttpClient.class.getPackage().getImplementationVersion())
            .orElse("unknown");
    }

}
