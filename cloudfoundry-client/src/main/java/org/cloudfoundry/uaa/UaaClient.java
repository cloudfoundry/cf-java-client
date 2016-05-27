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

import org.cloudfoundry.uaa.identityzones.IdentityZones;
import org.cloudfoundry.uaa.tokens.Tokens;
import org.cloudfoundry.uaa.users.Users;

/**
 * Main entry point to the UAA Client API
 */
public interface UaaClient {

    /**
     * The currently supported UAA API version
     */
    String SUPPORTED_API_VERSION = "3.3.0";

    /**
     * Main entry point to the UAA Identity Zone Client API
     *
     * @return the UAA Identity Zone Client API
     */
    IdentityZones identityZones();

    /**
     * Main entry point to the UAA Token Client API
     *
     * @return the UAA Token Client API
     */
    Tokens tokens();

    /**
     * Main entry point to the UAA User Client API
     *
     * @return the UAA User Client API
     */
    Users users();

}
