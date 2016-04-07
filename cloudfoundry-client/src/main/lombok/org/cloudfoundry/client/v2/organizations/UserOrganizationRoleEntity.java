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

package org.cloudfoundry.client.v2.organizations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.v2.users.AbstractUserEntity;

import java.util.List;

/**
 * The entity response payload for the Route resource
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class UserOrganizationRoleEntity extends AbstractUserEntity {

    /**
     * The organization roles
     *
     * @param organizationRoles the organization roles
     * @return the organization roles
     */
    private final List<String> organizationRoles;

    @Builder
    UserOrganizationRoleEntity(@JsonProperty("active") Boolean active,
                               @JsonProperty("admin") Boolean admin,
                               @JsonProperty("audited_organizations_url") String auditedOrganizationsUrl,
                               @JsonProperty("audited_spaces_url") String auditedSpacesUrl,
                               @JsonProperty("billing_managed_organizations_url") String billingManagedOrganizationsUrl,
                               @JsonProperty("default_space_guid") String defaultSpaceId,
                               @JsonProperty("default_space_url") String defaultSpaceUrl,
                               @JsonProperty("managed_organizations_url") String managedOrganizationsUrl,
                               @JsonProperty("managed_spaces_url") String managedSpacesUrl,
                               @JsonProperty("organizations_url") String organizationsUrl,
                               @JsonProperty("organization_roles") @Singular List<String> organizationRoles,
                               @JsonProperty("spaces_url") String spacesUrl,
                               @JsonProperty("username") String username) {
        super(active, admin, auditedOrganizationsUrl, auditedSpacesUrl, billingManagedOrganizationsUrl, defaultSpaceId, defaultSpaceUrl, managedOrganizationsUrl, managedSpacesUrl, organizationsUrl,
            spacesUrl, username);

        this.organizationRoles = organizationRoles;
    }

}
