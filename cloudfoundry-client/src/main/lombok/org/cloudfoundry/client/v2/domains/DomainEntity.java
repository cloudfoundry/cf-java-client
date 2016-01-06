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

package org.cloudfoundry.client.v2.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * The entity response payload for the Domain resource
 */
@Data
public final class DomainEntity {

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The owning organization id
     *
     * @param owningOrganizationId the owning organization id
     * @return the owning organization id
     */
    private final String owningOrganizationId;

    /**
     * The owning organization url
     *
     * @param owningOrganizationUrl the owning organization url
     * @return the owning organization url
     */
    private final String owningOrganizationUrl;

    /**
     * The router group id
     *
     * @param routerGroupId the router group id
     * @return the router group id
     */
    private final String routerGroupId;

    /**
     * The shared organizations
     *
     * @param sharedOrganizations the shared organizations
     * @return the shared organizations
     */
    private final List<String> sharedOrganizations;

    /**
     * The spaces url
     *
     * @param spacesUrl the spaces url
     * @return the spaces url
     */
    private final String spacesUrl;

    @Builder
    DomainEntity(@JsonProperty("name") String name,
                 @JsonProperty("owning_organization_guid") String owningOrganizationId,
                 @JsonProperty("owning_organization_url") String owningOrganizationUrl,
                 @JsonProperty("router_group_guid") String routerGroupId,
                 @JsonProperty("shared_organizations") List<String> sharedOrganizations,
                 @JsonProperty("spaces_url") String spacesUrl) {
        this.name = name;
        this.owningOrganizationId = owningOrganizationId;
        this.owningOrganizationUrl = owningOrganizationUrl;
        this.routerGroupId = routerGroupId;
        this.sharedOrganizations = sharedOrganizations;
        this.spacesUrl = spacesUrl;
    }

}
