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

import java.util.Map;

/**
 * The response payload for the Get Info operation
 */
@JsonDeserialize
@Value.Immutable
abstract class _GetInfoResponse {

    /**
     * The UAA application information
     */
    @JsonProperty("app")
    @Nullable
    abstract ApplicationInfo getApp();

    /**
     * The git sha for the UAA version
     */
    @JsonProperty("commit_id")
    @Nullable
    abstract String getCommitId();

    /**
     * The configured SAML entityId
     */
    @JsonProperty("entityID")
    @Nullable
    abstract String getEntityId();

    /**
     * A list of alias/url pairs of SAML IDP providers configured.
     */
    @JsonProperty("idpDefinitions")
    @Nullable
    abstract Map<String, String> getIdpDefinitions();

    /**
     * A list of alias/url pairs of configured action URLs for the UAA
     */
    @JsonProperty("links")
    @Nullable
    abstract Links getLinks();

    /**
     * Name/value pairs of configured prompts that the UAA will login a user.
     */
    @JsonProperty("prompts")
    @Nullable
    abstract Prompts getPrompts();

    /**
     * Whether login links are shown
     */
    @JsonProperty("showLoginLinks")
    @Nullable
    abstract Boolean getShowLoginLinks();

    /**
     * JSON timestamp for the commit of the UAA version
     */
    @JsonProperty("timestamp")
    @Nullable
    abstract String getTimestamp();

    /**
     * The name of the zone invoked
     */
    @JsonProperty("zone_name")
    @Nullable
    abstract String getZoneName();

}
