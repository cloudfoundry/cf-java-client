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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The payload for the server information links
 */
@JsonDeserialize
@Value.Immutable
abstract class _Links {

    /**
     * The link to the login host alias of the UAA
     */
    @JsonProperty("login")
    @Nullable
    abstract String getLogin();

    /**
     * The link to the 'Forgot Password' functionality
     */
    @JsonProperty("passwd")
    @Nullable
    abstract String getPassword();

    /**
     * The link to the 'Create Account' functionality
     */
    @JsonProperty("register")
    @Nullable
    abstract String getRegister();

    /**
     * The link to the uaa alias host of the UAA
     */
    @JsonProperty("uaa")
    @Nullable
    abstract String getUaa();

}
