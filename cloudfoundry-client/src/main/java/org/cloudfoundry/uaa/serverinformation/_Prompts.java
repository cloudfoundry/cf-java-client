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

import java.util.List;

/**
 * The payload for the server information prompts
 */
@JsonDeserialize
@Value.Immutable
abstract class _Prompts {

    /**
     * If a SAML identity provider is configured, this prompt contains a URL to where the user can initiate the SAML authentication flow
     */
    @JsonProperty("passcode")
    @Nullable
    abstract List<String> getPasscode();

    /**
     * Information about the password prompt
     */
    @JsonProperty("password")
    @Nullable
    abstract List<String> getPassword();

    /**
     * Information about the username prompt
     */
    @JsonProperty("username")
    @Nullable
    abstract List<String> getUsername();

}
