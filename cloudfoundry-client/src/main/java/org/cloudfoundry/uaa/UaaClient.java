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

package org.cloudfoundry.uaa;

import org.cloudfoundry.uaa.authorizations.Authorizations;
import org.cloudfoundry.uaa.clients.Clients;
import org.cloudfoundry.uaa.groups.Groups;
import org.cloudfoundry.uaa.identityproviders.IdentityProviders;
import org.cloudfoundry.uaa.identityzones.IdentityZones;
import org.cloudfoundry.uaa.serverinformation.ServerInformation;
import org.cloudfoundry.uaa.tokens.Tokens;
import org.cloudfoundry.uaa.users.Users;
import reactor.core.publisher.Mono;

/**
 * Main entry point to the UAA Client API
 */
public interface UaaClient {

    /**
     * The currently supported UAA API version
     */
    String SUPPORTED_API_VERSION = "74.5.2";

    /**
     * Main entry point to the UAA Authorizations Client API
     */
    Authorizations authorizations();

    /**
     * Main entry point to the UAA Clients API
     */
    Clients clients();

    /**
     * Returns the username of the current user
     */
    Mono<String> getUsername();

    /**
     * Main entry point to the UAA Group Client API
     */
    Groups groups();

    /**
     * Main entry point to the UAA Identity Provider Client API
     */
    IdentityProviders identityProviders();

    /**
     * Main entry point to the UAA Identity Zone Client API
     */
    IdentityZones identityZones();

    /**
     * Main entry point to the UAA Server Information API
     */
    ServerInformation serverInformation();

    /**
     * Main entry point to the UAA Token Client API
     */
    Tokens tokens();

    /**
     * Main entry point to the UAA User Client API
     */
    Users users();

}
