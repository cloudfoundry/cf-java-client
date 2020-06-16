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

import org.cloudfoundry.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.netty.http.client.HttpClientRequest;
import reactor.netty.http.client.HttpClientResponse;

import java.util.List;

public class RequestLogger {

    static final Logger REQUEST_LOGGER = LoggerFactory.getLogger("cloudfoundry-client.request");

    static final Logger RESPONSE_LOGGER = LoggerFactory.getLogger("cloudfoundry-client.response");

    private static final String CF_WARNINGS = "X-Cf-Warnings";

    private long requestSentTime;

    public void request(HttpClientRequest request) {
        request(String.format("%-6s {}", request.method()), request.uri());
    }

    public void response(HttpClientResponse response) {
        if (!RESPONSE_LOGGER.isDebugEnabled()) {
            return;
        }

        String elapsed = TimeUtils.asTime(System.currentTimeMillis() - this.requestSentTime);
        List<String> warnings = response.responseHeaders().getAll(CF_WARNINGS);

        if (warnings.isEmpty()) {
            if (RESPONSE_LOGGER.isTraceEnabled()) {
                RESPONSE_LOGGER.debug("{}    {} ({}, {})", response.status().code(), response.uri(), elapsed, response.responseHeaders().get("X-Vcap-Request-Id"));
            } else {
                RESPONSE_LOGGER.debug("{}    {} ({})", response.status().code(), response.uri(), elapsed);
            }
        } else {
            if (RESPONSE_LOGGER.isTraceEnabled()) {
                RESPONSE_LOGGER.warn("{}    {} ({}, {}) [{}]", response.status().code(), response.uri(), elapsed, response.responseHeaders().get("X-Vcap-Request-Id"),
                    String.join(", ", warnings));
            } else {
                RESPONSE_LOGGER.warn("{}    {} ({}) [{}]", response.status().code(), response.uri(), elapsed, String.join(", ", warnings));

            }
        }
    }

    public void websocketRequest(String uri) {
        request("WS     {}", uri);
    }

    private void request(String message, String uri) {
        REQUEST_LOGGER.debug(message, uri);
        this.requestSentTime = System.currentTimeMillis();
    }

}
