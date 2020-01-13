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

package org.cloudfoundry.uaa.authorizations;

import org.cloudfoundry.Nullable;
import org.cloudfoundry.QueryParameter;

import java.util.List;

/**
 * The request payload for OpenID requests
 */
abstract class AbstractAuthorizationRequest {

    /**
     * A unique string representing the registration information provided by the client
     */
    @QueryParameter("client_id")
    abstract String getClientId();

    /**
     * Redirection URI to which the authorization server will send the user-agent back once access is granted (or denied), optional if pre-registered by the client
     */
    @Nullable
    @QueryParameter("redirect_uri")
    abstract String getRedirectUri();

    /**
     * requested scopes, space-delimited
     */
    @Nullable
    @QueryParameter(value = "scope", delimiter = " ")
    abstract List<String> getScopes();

}
