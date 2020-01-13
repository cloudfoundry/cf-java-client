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

package org.cloudfoundry.client.v2.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.List;

/**
 * The entity response payload for the User Summary resource
 */
@JsonDeserialize
@Value.Immutable
abstract class _UserSummaryEntity {

    /**
     * The audited organizations
     */
    @JsonProperty("audited_organizations")
    @Nullable
    public abstract List<UserOrganizationResource> getAuditedOrganizations();

    /**
     * The audited spaces
     */
    @JsonProperty("audited_spaces")
    @Nullable
    public abstract List<UserSpaceResource> getAuditedSpaces();

    /**
     * The billing managed organizations
     */
    @JsonProperty("billing_managed_organizations")
    @Nullable
    public abstract List<UserOrganizationResource> getBillingManagedOrganizations();

    /**
     * The managed organizations
     */
    @JsonProperty("managed_organizations")
    @Nullable
    public abstract List<UserOrganizationResource> getManagedOrganizations();

    /**
     * The managed spaces
     */
    @JsonProperty("managed_spaces")
    @Nullable
    public abstract List<UserSpaceResource> getManagedSpaces();

    /**
     * The developer organizations
     */
    @JsonProperty("organizations")
    @Nullable
    public abstract List<UserOrganizationResource> getOrganizations();

    /**
     * The developer spaces
     */
    @JsonProperty("spaces")
    @Nullable
    public abstract List<UserSpaceResource> getSpaces();

}
