/*
 * Copyright 2013-2019 the original author or authors.
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
 * Extends {@link TokenProvider} to add UAA-specific functionality.
 */
public interface UaaTokenProvider extends TokenProvider {

    /**
     * Returns a {@link Flux} of refresh tokens for a connection
     *
     * @param connectionContext A {@link ConnectionContext} to be used to identity which connection the refresh tokens be retrieved for
     * @return a {@link Flux} that emits the last token on subscribe and new refresh tokens as they are negotiated
     */
    Flux<String> getRefreshTokens(ConnectionContext connectionContext);

}
