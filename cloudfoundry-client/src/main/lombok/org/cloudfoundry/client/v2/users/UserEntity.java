/*
 * Copyright 2015 the original author or authors.
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
import lombok.Builder;
import lombok.Data;

/**
 * The entity response payload for the User resource
 */
@Data
public final class UserEntity {

    /**
     * The active property
     *
     * @param active the active boolean
     * @return active
     */
    private final Boolean active;

    /**
     * The admin property
     *
     * @param admin the admin boolean
     * @return admin
     */
    private final Boolean admin;

    /**
     * The audited organizations url
     *
     * @param auditedOrganizationsUrl the audited organizations url
     * @return the audited organizations url
     */
    private final String auditedOrganizationsUrl;

    /**
     * The audited spaces url
     *
     * @param auditedSpacesUrl the audited spaces url
     * @return the audited spaces url
     */
    private final String auditedSpacesUrl;

    /**
     * The billing managed organizations url
     *
     * @param billingManagedOrganizationsUrl the billing managed organizations url
     * @return the billing managed organizations url
     */
    private final String billingManagedOrganizationsUrl;

    /**
     * The Default Space Id
     *
     * @param defaultSpaceId default Space Id
     * @return default Space Id
     */
    private final String defaultSpaceId;

    /**
     * The managed organizations url
     *
     * @param managedOrganizationsUrl the managed organizations url
     * @return the managed organizations url
     */
    private final String managedOrganizationsUrl;

    /**
     * The managed spaces url
     *
     * @param managedSpacesUrl the managed spaces url
     * @return the managed spaces url
     */
    private final String managedSpacesUrl;

    /**
     * The organizations url
     *
     * @param organizationsUrl the organizations url
     * @return the organizations url
     */
    private final String organizationsUrl;

    /**
     * The spaces url
     *
     * @param spacesUrl the spaces url
     * @return the spaces url
     */
    private final String spacesUrl;

    /**
     * The username
     *
     * @param username the username
     * @return the username
     */
    private final String username;

    @Builder
    UserEntity(@JsonProperty("active") Boolean active,
               @JsonProperty("admin") Boolean admin,
               @JsonProperty("audited_organizations_url") String auditedOrganizationsUrl,
               @JsonProperty("audited_spaces_url") String auditedSpacesUrl,
               @JsonProperty("billing_managed_organizations_url") String
                       billingManagedOrganizationsUrl,
               @JsonProperty("default_space_guid") String defaultSpaceId,
               @JsonProperty("managed_organizations_url") String managedOrganizationsUrl,
               @JsonProperty("managed_spaces_url") String managedSpacesUrl,
               @JsonProperty("organizations_url") String organizationsUrl,
               @JsonProperty("spaces_url") String spacesUrl,
               @JsonProperty("username") String username) {
        this.active = active;
        this.admin = admin;
        this.auditedOrganizationsUrl = auditedOrganizationsUrl;
        this.auditedSpacesUrl = auditedSpacesUrl;
        this.billingManagedOrganizationsUrl = billingManagedOrganizationsUrl;
        this.defaultSpaceId = defaultSpaceId;
        this.managedOrganizationsUrl = managedOrganizationsUrl;
        this.managedSpacesUrl = managedSpacesUrl;
        this.organizationsUrl = organizationsUrl;
        this.spacesUrl = spacesUrl;
        this.username = username;
    }

}
