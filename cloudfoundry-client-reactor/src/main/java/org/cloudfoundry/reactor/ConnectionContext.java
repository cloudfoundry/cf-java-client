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

package org.cloudfoundry.reactor;

import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClient;

/**
 * Common, reusable, connection context
 */
public interface ConnectionContext {

    /**
     * The {@link HttpClient} to use
     */
    HttpClient getHttpClient();

    /**
     * The {@link ObjectMapper} to use
     */
    ObjectMapper getObjectMapper();

    /**
     * The normalized API root
     */
    Mono<String> getRoot();

    /**
     * The normalized root for a given key
     *
     * @param key the key to look up root from
     */
    Mono<String> getRoot(String key);

}
