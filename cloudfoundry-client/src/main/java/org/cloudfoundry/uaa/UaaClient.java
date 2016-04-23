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

package org.cloudfoundry.uaa;

import org.cloudfoundry.uaa.accesstokenadministration.AccessTokenAdministration;
import org.cloudfoundry.uaa.identityproviders.IdentityProviders;
import org.cloudfoundry.uaa.identityzonemanagement.IdentityZoneManagement;

/**
 * Main entry point to the UAA Client API
 */
public interface UaaClient {

    /**
     * Main entry point to the UAA Access Token Administration Client API
     *
     * @return the UAA Access Token Administration Client API
     */
    AccessTokenAdministration accessTokenAdministration();

    /**
     * Main entry point to the UAA Identity Providers Client API
     *
     * @return the UAA Identity Providers Client API
     */
    IdentityProviders identityProviders();

    /**
     * Main entry point to the UAA Identity Zone Management Client API
     *
     * @return the UAA Identity Zone Management Client API
     */
    IdentityZoneManagement identityZoneManagement();

}
