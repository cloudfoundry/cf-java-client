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

package org.cloudfoundry.uaa.serverinformation;

import org.cloudfoundry.QueryParameter;
import org.immutables.value.Value;

/**
 * The request payload for the Perform Auto Login operation
 */
@Value.Immutable
abstract class _AutoLoginRequest {

    /**
     * The client_id that generated the autologin code
     */
    @QueryParameter("client_id")
    public abstract String getClientId();

    /**
     * The code generated from the POST /autologin
     */
    @QueryParameter("code")
    public abstract String getCode();

}
