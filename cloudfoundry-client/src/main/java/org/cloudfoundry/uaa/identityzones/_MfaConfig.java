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

package org.cloudfoundry.uaa.identityzones;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.uaa.identityproviders.Type;
import org.immutables.value.Value;

import java.util.List;

/**
 * The payload for the Multi-factor Authentication configuration
 */
@JsonDeserialize
@Value.Immutable
abstract class _MfaConfig {

    @Value.Check
    void check() {
        if (getEnabled() && getProviderName().isEmpty()) {
            throw new IllegalStateException("Cannot build MfaConfig, providerName must be specified if MFA is enabled");
        }
    }

    /**
     * Whether Multi-factor Authentication is enabled
     */
    @JsonProperty("enabled")
    @Nullable
    abstract Boolean getEnabled();

    /**
     * The identity providers
     */
    @JsonProperty("identityProviders")
    @Nullable
    abstract List<Type> getIdentityProviders();

    /**
     * The XHR configuration
     */
    @JsonProperty("providerName")
    @Nullable
    abstract String getProviderName();

}
