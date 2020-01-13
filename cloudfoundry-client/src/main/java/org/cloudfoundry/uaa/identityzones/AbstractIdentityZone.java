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
import org.cloudfoundry.Nullable;

/**
 * The entity response payload for Identity Zone
 */
public abstract class AbstractIdentityZone {

    /**
     * Whether the identity zone is active
     */
    @JsonProperty("active")
    @Nullable
    abstract Boolean getActive();

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
    abstract Long getCreatedAt();

    /**
     * The description of the identity zone
     */
    @JsonProperty("description")
    @Nullable
    abstract String getDescription();

    /**
     * The id of the identity zone
     */
    @JsonProperty("id")
    abstract String getId();

    /**
     * The last modification date of the identity zone
     */
    @JsonProperty("last_modified")
    abstract Long getLastModified();

    /**
     * The name of the identity zone
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * The unique sub domain. It will be converted into lowercase upon creation.
     */
    @JsonProperty("subdomain")
    abstract String getSubdomain();

    /**
     * The version of the identity zone.
     */
    @JsonProperty("version")
    @Nullable
    abstract Integer getVersion();

}
