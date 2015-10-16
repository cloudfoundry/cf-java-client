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
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * The request payload for the Creating a Space operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class CreateSpaceRequest implements Validatable {

    private volatile Boolean allowSsh = null;

    private volatile List<String> auditorIds = new ArrayList<>();

    private volatile List<String> developerIds = new ArrayList<>();

    private volatile List<String> domainIds = new ArrayList<>();

    private volatile List<String> managerIds = new ArrayList<>();

    private volatile String name;

    private volatile String organizationId;

    private volatile List<String> securityGroupIds = new ArrayList<>();

    private volatile String spaceQuotaDefinitionId;

    /**
     * Returns allow ssh
     *
     * @return allow ssh
     */
    @JsonProperty("allow_ssh")
    public Boolean getAllowSsh() {
        return this.allowSsh;
    }

    /**
     * Configure allow ssh
     *
     * @param allowSsh the allow ssh
     * @return {@code this}
     */
    public CreateSpaceRequest withAllowSsh(Boolean allowSsh) {
        this.allowSsh = allowSsh;
        return this;
    }

    /**
     * Returns the auditor ids
     *
     * @return the auditor ids
     */
    @JsonProperty("auditor_guid")
    @JsonInclude(NON_EMPTY)
    public List<String> getAuditorIds() {
        return this.auditorIds;
    }

    /**
     * Configure the auditor id
     *
     * @param auditor the auditor id
     * @return {@code this}
     */
    public CreateSpaceRequest withAuditorId(String auditor) {
        this.auditorIds.add(auditor);
        return this;
    }

    /**
     * Configure the auditor ids
     *
     * @param auditorIds the auditor ids
     * @return {@code this}
     */
    public CreateSpaceRequest withAuditorIds(List<String> auditorIds) {
        this.auditorIds.addAll(auditorIds);
        return this;
    }

    /**
     * Returns the developer ids
     *
     * @return the developer ids
     */
    @JsonProperty("developer_guid")
    @JsonInclude(NON_EMPTY)
    public List<String> getDeveloperIds() {
        return this.developerIds;
    }

    /**
     * Configure the developer id
     *
     * @param developerId the developer id
     * @return {@code this}
     */
    public CreateSpaceRequest withDeveloperId(String developerId) {
        this.developerIds.add(developerId);
        return this;
    }

    /**
     * Configure the developer ids
     *
     * @param developerIds the developer ids
     * @return {@code this}
     */
    public CreateSpaceRequest withDeveloperIds(List<String> developerIds) {
        this.developerIds.addAll(developerIds);
        return this;
    }

    /**
     * Returns the domain ids
     *
     * @return the domain ids
     */
    @JsonProperty("domain_guid")
    @JsonInclude(NON_EMPTY)
    public List<String> getDomainIds() {
        return this.domainIds;
    }

    /**
     * Configure the domain id
     *
     * @param domainId the domain id
     * @return {@code this}
     */
    public CreateSpaceRequest withDomainId(String domainId) {
        this.domainIds.add(domainId);
        return this;
    }

    /**
     * Configure the domain ids
     *
     * @param domainIds the domain ids
     * @return {@code this}
     */
    public CreateSpaceRequest withDomainIds(List<String> domainIds) {
        this.domainIds.addAll(domainIds);
        return this;
    }

    /**
     * Returns the manager ids
     *
     * @return the manager ids
     */
    @JsonProperty("manager_guid")
    @JsonInclude(NON_EMPTY)
    public List<String> getManagerIds() {
        return this.managerIds;
    }

    /**
     * Configure the manager id
     *
     * @param managerId the manager id
     * @return {@code this}
     */
    public CreateSpaceRequest withManagerId(String managerId) {
        this.managerIds.add(managerId);
        return this;
    }

    /**
     * Configure the manager ids
     *
     * @param managerIds the manager ids
     * @return {@code this}
     */
    public CreateSpaceRequest withManagerIds(List<String> managerIds) {
        this.managerIds.addAll(managerIds);
        return this;
    }

    /**
     * Returns the name
     *
     * @return the name
     */
    @JsonProperty("name")
    public String getName() {
        return this.name;
    }

    /**
     * Configure the name
     *
     * @param name the name
     * @return {@code this}
     */
    public CreateSpaceRequest withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Returns the organization id
     *
     * @return the organization id
     */
    @JsonProperty("organization_guid")
    public String getOrganizationId() {
        return this.organizationId;
    }

    /**
     * Configure the organization id
     *
     * @param organizationId the organization id
     * @return {@code this}
     */
    public CreateSpaceRequest withOrganizationId(String organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    /**
     * Returns the security group ids
     *
     * @return the security group ids
     */
    @JsonProperty("security_group_guids")
    @JsonInclude(NON_EMPTY)
    public List<String> getSecurityGroups() {
        return this.securityGroupIds;
    }

    /**
     * Configure the security group id
     *
     * @param securityGroupId the security group id
     * @return {@code this}
     */
    public CreateSpaceRequest withSecurityGroupId(String securityGroupId) {
        this.securityGroupIds.add(securityGroupId);
        return this;
    }

    /**
     * Configure the security group ids
     *
     * @param securityGroupIds the security group ids
     * @return {@code this}
     */
    public CreateSpaceRequest withSecurityGroupIds(List<String> securityGroupIds) {
        this.securityGroupIds.addAll(securityGroupIds);
        return this;
    }

    /**
     * Returns the space quota definition guid
     *
     * @return the space quota definition guid
     */
    @JsonProperty("space quota definition guid")
    public String getSpaceQuotaDefinitionId() {
        return this.spaceQuotaDefinitionId;
    }

    /**
     * Configure the space quota definition id
     *
     * @param spaceQuotaDefinitionId the space quota definition id
     * @return {@code this}
     */
    public CreateSpaceRequest withSpaceQuotaDefinitionId(String spaceQuotaDefinitionId) {
        this.spaceQuotaDefinitionId = spaceQuotaDefinitionId;
        return this;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult result = new ValidationResult();

        if (this.name == null) {
            result.invalid("name must be specified");
        }

        if (this.organizationId == null) {
            result.invalid("organization id must be specified");
        }

        return result;
    }
}
