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

package org.cloudfoundry.client.v2.spaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request payload for the Update a Space operation
 */
@Data
public final class UpdateSpaceRequest implements Validatable {

    /**
     * The allow ssh
     *
     * @param allowSsh the allow ssh
     * @return the allow ssh
     */
    @Getter(onMethod = @__(@JsonProperty("allow_ssh")))
    private volatile String allowSsh;

    /**
     * The auditor ids
     *
     * @param auditorIds the auditor ids
     * @return the auditor ids
     */
    @Getter(onMethod = @__(@JsonProperty("auditor_guids")))
    private volatile String auditorIds;

    /**
     * The developer ids
     *
     * @param developerIds the developer ids
     * @return the developer ids
     */
    @Getter(onMethod = @__(@JsonProperty("developer_guids")))
    private volatile String developerIds;

    /**
     * The domain ids
     *
     * @param domainIds the domain ids
     * @return the domain ids
     */
    @Getter(onMethod = @__(@JsonProperty("domain_guids")))
    private volatile String domainIds;

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private volatile String id;

    /**
     * The manager ids
     *
     * @param managerIds the manager ids
     * @return the manager ids
     */
    @Getter(onMethod = @__(@JsonProperty("manager_guids")))
    private volatile String managerIds;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    @Getter(onMethod = @__(@JsonProperty("name")))
    private volatile String name;

    /**
     * The organization id
     *
     * @param organizationId the organization id
     * @return the organization id
     */
    @Getter(onMethod = @__(@JsonProperty("organization_guid")))
    private volatile String organizationId;

    /**
     * The security group ids
     *
     * @param securityGroupIds the security group ids
     * @return the security group ids
     */
    @Getter(onMethod = @__(@JsonProperty("security_group_guids")))
    private volatile String securityGroupIds;

    @Builder
    UpdateSpaceRequest(String allowSsh, String auditorIds, String developerIds, String domainIds, String id, String
            managerIds, String name, String organizationId, String securityGroupIds) {
        this.allowSsh = allowSsh;
        this.auditorIds = auditorIds;
        this.developerIds = developerIds;
        this.domainIds = domainIds;
        this.id = id;
        this.managerIds = managerIds;
        this.name = name;
        this.organizationId = organizationId;
        this.securityGroupIds = securityGroupIds;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.id == null) {
            builder.message("id must be specified");
        }

        return builder.build();
    }

}
