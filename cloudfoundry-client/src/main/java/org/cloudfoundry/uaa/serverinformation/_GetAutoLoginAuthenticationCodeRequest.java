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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * The request payload for the Get Auto Login Authentication Code operation
 */
@JsonSerialize
@Value.Immutable
abstract class _GetAutoLoginAuthenticationCodeRequest {

    /**
     * The client id
     */
    @JsonIgnore
    public abstract String getClientId();

    /**
     * The client secret
     */
    @JsonIgnore
    public abstract String getClientSecret();

    /**
     * The password for the autologin request
     */
    @JsonProperty("password")
    abstract String getPassword();

    /**
     * The username for the autologin request
     */
    @JsonProperty("username")
    abstract String getUsername();

}
