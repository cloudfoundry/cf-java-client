/*
 * Copyright 2013-2016 the original author or authors.
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
 * A provider that adds the {@code Authorization} header to requests
 */
public interface TokenProvider {

    /**
     * Provides an OAuth token to be used by requests
     *
     * @param connectionContext A {@link ConnectionContext} to be used if a token needs to be retrieved via a network request
     * @return an OAuth token
     */
    Mono<String> getToken(ConnectionContext connectionContext);

}
