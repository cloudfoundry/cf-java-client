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

package org.cloudfoundry.uaa.identityproviders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.uaa.IdentityZoned;
import org.immutables.value.Value;

/**
 * The request payload for the update identity provider
 */
@JsonSerialize
@Value.Immutable
abstract class _UpdateIdentityProviderRequest implements IdentityZoned {

    /**
     * Whether the identity provider is active
     */
    @JsonProperty("active")
    @Nullable
    abstract Boolean getActive();

    /**
     * The configuration of this identity provider according to its type.
     */
    @JsonProperty("config")
    abstract IdentityProviderConfiguration getConfiguration();

    /**
     * The identity provider id
     */
    @JsonIgnore
    abstract String getIdentityProviderId();

    /**
     * Human-readable name for this provider
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * A unique identifier for the IDP. Cannot be updated.
     */
    @JsonProperty("originKey")
    abstract String getOriginKey();

    /**
     * The type of the identity provider. Cannot be updated.
     */
    @JsonProperty("type")
    abstract Type getType();

    /**
     * Version of the identity provider data. Clients can use this to protect against conflicting updates.
     */
    @JsonProperty("version")
    abstract Integer getVersion();

}
