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

package org.cloudfoundry.client.v2.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.List;

/**
 * The entity response payload for the Domain resource
 */
@JsonDeserialize
@Value.Immutable
abstract class _DomainEntity {

    /**
     * Whether this is an internal domain
     */
    @JsonProperty("internal")
    @Nullable
    abstract Boolean getInternal();

    /**
     * The name
     */
    @JsonProperty("name")
    @Nullable
    abstract String getName();

    /**
     * The owning organization id
     */
    @JsonProperty("owning_organization_guid")
    @Nullable
    abstract String getOwningOrganizationId();

    /**
     * The owning organization url
     */
    @JsonProperty("owning_organization_url")
    @Nullable
    abstract String getOwningOrganizationUrl();

    /**
     * The router group id
     */
    @JsonProperty("router_group_guid")
    @Nullable
    abstract String getRouterGroupId();

    /**
     * The router group type
     */
    @JsonProperty("router_group_type")
    @Nullable
    abstract String getRouterGroupType();

    /**
     * The shared organizations
     */
    @JsonProperty("shared_organizations")
    @Nullable
    abstract List<String> getSharedOrganizations();

    /**
     * The spaces url
     */
    @JsonProperty("spaces_url")
    @Nullable
    abstract String getSpacesUrl();

}
