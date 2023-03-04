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

    /**
     * Called when a {@code 401 UNAUTHORIZED} is received as part of a request.  Since not all {@link TokenProvider}s care about this possibility, the default implementation does nothing.
     * Implementations are free to manage internal state with this call if they choose to.
     *
     * @param connectionContext A {@link ConnectionContext} to be used to identity which connection the tokens should be invalidated for
     */
    default void invalidate(ConnectionContext connectionContext) {
    }

    /**
     * Provides the name of the property in jwt which is effective for user identity (i.e. client_id for ClientCredentialsTokenProvider and user_name for PasswordGrantTokenProvider)
     * 
     * @return
     */
    default String getUserIdentityProperty(){
    	return "user_name";
    }
}
