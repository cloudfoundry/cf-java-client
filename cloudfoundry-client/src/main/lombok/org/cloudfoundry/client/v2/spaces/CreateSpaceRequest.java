/*
 * Copyright 2013-2015 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Singular;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * The request payload for the Creating a Space operation
 */
@Data
public final class CreateSpaceRequest implements Validatable {

    /**
     * Allow SSH
     *
     * @param allowSsh allow ssh
     * @return allow ssh
     */
    @Getter(onMethod = @__(@JsonProperty("allow_ssh")))
    private final Boolean allowSsh;

    /**
     * The auditor ids
     *
     * @param auditorIds the auditor ids
     * @return the auditor ids
     */
    @Getter(onMethod = @__({@JsonProperty("auditor_guid"), @JsonInclude(NON_EMPTY)}))
    private final List<String> auditorIds;

    /**
     * The developer ids
     *
     * @param developerIds the developer ids
     * @return the developer ids
     */
    @Getter(onMethod = @__({@JsonProperty("developer_guid"), @JsonInclude(NON_EMPTY)}))
    private final List<String> developerIds;

    /**
     * The domain ids
     *
     * @param domainIds the domain ids
     * @return the domain ids
     */
    @Getter(onMethod = @__({@JsonProperty("domain_guid"), @JsonInclude(NON_EMPTY)}))
    private final List<String> domainIds;

    /**
     * The manager ids
     *
     * @param managerIds the manager ids
     * @return the manager ids
     */
    @Getter(onMethod = @__({@JsonProperty("manager_guid"), @JsonInclude(NON_EMPTY)}))
    private final List<String> managerIds;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    @Getter(onMethod = @__(@JsonProperty("name")))
    private final String name;

    /**
     * The organization is
     *
     * @param organizationId the organization id
     * @return the organization id
     */
    @Getter(onMethod = @__(@JsonProperty("organization_guid")))
    private final String organizationId;

    /**
     * The security group ids
     *
     * @param securityGroupIds the security group ids
     * @return the security group ids
     */
    @Getter(onMethod = @__({@JsonProperty("security_group_guids"), @JsonInclude(NON_EMPTY)}))
    private final List<String> securityGroupIds;

    /**
     * The space quota definition id
     *
     * @param spaceQuotaDefinitionId the space quota definition id
     * @return the space quota definition id
     */
    @Getter(onMethod = @__(@JsonProperty("space quota definition guid")))
    private final String spaceQuotaDefinitionId;

    @Builder
    CreateSpaceRequest(Boolean allowSsh,
                       @Singular List<String> auditorIds,
                       @Singular List<String> developerIds,
                       @Singular List<String> domainIds,
                       @Singular List<String> managerIds,
                       String name,
                       String organizationId,
                       @Singular List<String> securityGroupIds,
                       String spaceQuotaDefinitionId) {
        this.allowSsh = allowSsh;
        this.auditorIds = auditorIds;
        this.developerIds = developerIds;
        this.domainIds = domainIds;
        this.managerIds = managerIds;
        this.name = name;
        this.organizationId = organizationId;
        this.securityGroupIds = securityGroupIds;
        this.spaceQuotaDefinitionId = spaceQuotaDefinitionId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("name must be specified");
        }

        if (this.organizationId == null) {
            builder.message("organization id must be specified");
        }

        return builder.build();
    }

}
