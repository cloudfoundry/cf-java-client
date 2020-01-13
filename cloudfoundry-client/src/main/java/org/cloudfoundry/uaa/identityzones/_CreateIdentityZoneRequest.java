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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The request payload for the create identity zone operation
 */
@JsonSerialize
@Value.Immutable
abstract class _CreateIdentityZoneRequest {

    /**
     * The configuration
     */
    @JsonProperty("config")
    @Nullable
    abstract IdentityZoneConfiguration getConfiguration();

    /**
     * The creation date of the identity zone
     */
    @JsonProperty("created")
    @Nullable
    abstract Long getCreatedAt();

    /**
     * The description of the identity zone
     */
    @JsonProperty("description")
    @Nullable
    abstract String getDescription();

    /**
     * The id of the identity zone. When not provided, an identifier will be generated
     */
    @JsonProperty("id")
    @Nullable
    abstract String getIdentityZoneId();

    /**
     * The last modification date of the identity zone
     */
    @JsonProperty("last_modified")
    @Nullable
    abstract Long getLastModified();

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
