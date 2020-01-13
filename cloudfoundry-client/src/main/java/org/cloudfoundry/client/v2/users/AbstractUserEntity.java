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
import org.cloudfoundry.Nullable;

/**
 * The entity response payload for the User resource
 */
public abstract class AbstractUserEntity {

    /**
     * The active property
     */
    @JsonProperty("active")
    @Nullable
    public abstract Boolean getActive();

    /**
     * The admin property
     */
    @JsonProperty("admin")
    @Nullable
    public abstract Boolean getAdmin();

    /**
     * The audited organizations url
     */
    @JsonProperty("audited_organizations_url")
    @Nullable
    public abstract String getAuditedOrganizationsUrl();

    /**
     * The audited spaces url
     */
    @JsonProperty("audited_spaces_url")
    @Nullable
    public abstract String getAuditedSpacesUrl();

    /**
     * The billing managed organizations url
     */
    @JsonProperty("billing_managed_organizations_url")
    @Nullable
    public abstract String getBillingManagedOrganizationsUrl();

    /**
     * The default space id
     */
    @JsonProperty("default_space_guid")
    @Nullable
    public abstract String getDefaultSpaceId();

    /**
     * The default space url
     */
    @JsonProperty("default_space_url")
    @Nullable
    public abstract String getDefaultSpaceUrl();

    /**
     * The managed organizations url
     */
    @JsonProperty("managed_organizations_url")
    @Nullable
    public abstract String getManagedOrganizationsUrl();

    /**
     * The managed spaces url
     */
    @JsonProperty("managed_spaces_url")
    @Nullable
    public abstract String getManagedSpacesUrl();

    /**
     * The organizations url
     */
    @JsonProperty("organizations_url")
    @Nullable
    public abstract String getOrganizationsUrl();

    /**
     * The spaces url
     */
    @JsonProperty("spaces_url")
    @Nullable
    public abstract String getSpacesUrl();

    /**
     * The username
     */
    @JsonProperty("username")
    @Nullable
    public abstract String getUsername();

}
