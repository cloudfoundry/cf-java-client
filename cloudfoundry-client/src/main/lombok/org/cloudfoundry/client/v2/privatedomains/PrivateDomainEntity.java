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

package org.cloudfoundry.client.v2.privatedomains;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * The entity response payload for Service Instances
 */
@Data
public final class PrivateDomainEntity {

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
     * The shared organizations url
     *
     * @param sharedOrganizationsUrl the shared organizations url
     * @return the shared organizations url
     */
    private final String sharedOrganizationsUrl;

    @Builder
    PrivateDomainEntity(@JsonProperty("name") String name,
                        @JsonProperty("owning_organization_guid") String owningOrganizationId,
                        @JsonProperty("owning_organization_url") String owningOrganizationUrl,
                        @JsonProperty("shared_organizations_url") String sharedOrganizationsUrl) {
        this.name = name;
        this.owningOrganizationId = owningOrganizationId;
        this.owningOrganizationUrl = owningOrganizationUrl;
        this.sharedOrganizationsUrl = sharedOrganizationsUrl;
    }

}
