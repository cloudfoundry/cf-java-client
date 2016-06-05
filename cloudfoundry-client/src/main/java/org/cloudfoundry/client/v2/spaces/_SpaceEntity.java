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

package org.cloudfoundry.client.v2.spaces;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The entity response payload for the Space resource
 */
@JsonDeserialize
@Value.Immutable
abstract class _SpaceEntity {

    /**
     * Allow SSH
     */
    @JsonProperty("allow_ssh")
    abstract Boolean getAllowSsh();

    /**
     * The application events url
     */
    @JsonProperty("app_events_url")
    abstract String getApplicationEventsUrl();

    /**
     * The applications url
     */
    @JsonProperty("apps_url")
    abstract String getApplicationsUrl();

    /**
     * The auditors url
     */
    @JsonProperty("auditors_url")
    abstract String getAuditorsUrl();

    /**
     * The developers url
     */
    @JsonProperty("developers_url")
    abstract String getDevelopersUrl();

    /**
     * The domains url
     */
    @JsonProperty("domains_url")
    abstract String getDomainsUrl();

    /**
     * The events url
     */
    @JsonProperty("events_url")
    abstract String getEventsUrl();

    /**
     * The managers url
     */
    @JsonProperty("managers_url")
    abstract String getManagersUrl();

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
     * The organization url
     */
    @JsonProperty("organization_url")
    abstract String getOrganizationUrl();

    /**
     * The routes url
     */
    @JsonProperty("routes_url")
    abstract String getRoutesUrl();

    /**
     * The security groups url
     */
    @JsonProperty("security_groups_url")
    abstract String getSecurityGroupsUrl();

    /**
     * The service instances url
     */
    @JsonProperty("service_instances_url")
    abstract String getServiceInstancesUrl();

    /**
     * The space quota definition id
     */
    @JsonProperty("space_quota_definition_guid")
    @Nullable
    abstract String getSpaceQuotaDefinitionId();

}
