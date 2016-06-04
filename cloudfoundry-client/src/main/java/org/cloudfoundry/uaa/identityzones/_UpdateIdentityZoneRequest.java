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

package org.cloudfoundry.uaa.identityzones;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The request payload for the update identity zone operation
 */
@Value.Immutable
abstract class _UpdateIdentityZoneRequest {

    /**
     * The configuration
     */
    @JsonProperty("config")
    @Nullable
    abstract IdentityZoneConfiguration getConfiguration();

    /**
     * The description of the identity zone
     */
    @JsonProperty("description")
    @Nullable
    abstract String getDescription();

    /**
     * The id of the identity zone. When not provided, an identifier will be generated.
     */
    @JsonIgnore
    abstract String getIdentityZoneId();

    /**
     * The name of the identity zone
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * The unique subdomain. It will be converted into lowercase upon creation.
     */
    @JsonProperty("subdomain")
    abstract String getSubdomain();

    /**
     * The version of the identity zone
     */
    @JsonProperty("version")
    @Nullable
    abstract Integer getVersion();

}
