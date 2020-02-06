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

package org.cloudfoundry.reactor;

import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Optional;

/**
 * Common, reusable, connection context
 */
public interface ConnectionContext {

    /**
     * The duration that stable responses like the payload of the API root should be cached
     */
    Optional<Duration> getCacheDuration();

    /**
     * The {@link HttpClient} to use
     */
    HttpClient getHttpClient();

    /**
     * The number of retries after an unsuccessful request
     */
    Long getInvalidTokenRetries();

    /**
     * The {@link ObjectMapper} to use
     */
    ObjectMapper getObjectMapper();

    /**
     * The {@link RootProvider} to use
     */
    RootProvider getRootProvider();

    /**
     * Attempt to explicitly trust the TLS certificate of an endpoint.  Implementations can choose whether any actual trusting will happen.
     *
     * @param host the host of the endpoint to trust
     * @param port the port of the endpoint to trust
     */
    Mono<Void> trust(String host, int port);

}
