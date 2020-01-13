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

package org.cloudfoundry.client.v2.spaces;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the Creating a Space operation
 */
@JsonSerialize
@Value.Immutable
abstract class _CreateSpaceRequest {

    /**
     * Allow SSH
     */
    @JsonProperty("allow_ssh")
    @Nullable
    abstract Boolean getAllowSsh();

    /**
     * The auditor ids
     */
    @JsonProperty("auditor_guid")
    @Nullable
    abstract List<String> getAuditorsIds();

    /**
     * The developer ids
     */
    @JsonProperty("developer_guid")
    @Nullable
    abstract List<String> getDeveloperIds();

    /**
     * The domain ids
     */
    @JsonProperty("domain_guid")
    @Nullable
    abstract List<String> getDomainIds();

    /**
     * The manager ids
     */
    @JsonProperty("manager_guid")
    @Nullable
    abstract List<String> getManagerIds();

    /**
     * The name
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * The organization id
     */
    @JsonProperty("organization_guid")
    abstract String getOrganizationId();

    /**
     * The security group ids
     */
    @JsonProperty("security_group_guids")
    @Nullable
    abstract List<String> getSecurityGroupIds();

    /**
     * The space quota definition id
     */
    @JsonProperty("space_quota_definition_guid")
    @Nullable
    abstract String getSpaceQuotaDefinitionId();

}
