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

import reactor.core.publisher.Mono;

/**
 * Common, reusable, provider of root URIs
 */
public interface RootProvider {

    /**
     * The normalized API root
     *
     * @param connectionContext a {@link ConnectionContext} to be used if the root needs to be retrieved via a network request
     * @return the normalized API root
     */
    Mono<String> getRoot(ConnectionContext connectionContext);

    /**
     * The normalized root for a given key
     *
     * @param key               the key to look up root from
     * @param connectionContext a {@link ConnectionContext} to be used if the roo needs to be retrieved via a network request
     * @return the normalized API root
     */
    Mono<String> getRoot(String key, ConnectionContext connectionContext);

}
