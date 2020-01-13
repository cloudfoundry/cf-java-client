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
    @Nullable
    abstract Boolean getAllowSsh();

    /**
     * The application events url
     */
    @JsonProperty("app_events_url")
    @Nullable
    abstract String getApplicationEventsUrl();

    /**
     * The applications url
     */
    @JsonProperty("apps_url")
    @Nullable
    abstract String getApplicationsUrl();

    /**
     * The auditors url
     */
    @JsonProperty("auditors_url")
    @Nullable
    abstract String getAuditorsUrl();

    /**
     * The developers url
     */
    @JsonProperty("developers_url")
    @Nullable
    abstract String getDevelopersUrl();

    /**
     * The domains url
     */
    @JsonProperty("domains_url")
    @Nullable
    abstract String getDomainsUrl();

    /**
     * The events url
     */
    @JsonProperty("events_url")
    @Nullable
    abstract String getEventsUrl();

    /**
     * The isolation segment id
     */
    @JsonProperty("isolation_segment_guid")
    @Nullable
    abstract String getIsolationSegmentId();

    /**
     * The isolation segment url
     */
    @JsonProperty("isolation_segment_url")
    @Nullable
    abstract String getIsolationSegmentUrl();

    /**
     * The managers url
     */
    @JsonProperty("managers_url")
    @Nullable
    abstract String getManagersUrl();

    /**
     * The name
     */
    @JsonProperty("name")
    @Nullable
    abstract String getName();

    /**
     * The organization id
     */
    @JsonProperty("organization_guid")
    @Nullable
    abstract String getOrganizationId();

    /**
     * The organization url
     */
    @JsonProperty("organization_url")
    @Nullable
    abstract String getOrganizationUrl();

    /**
     * The routes url
     */
    @JsonProperty("routes_url")
    @Nullable
    abstract String getRoutesUrl();

    /**
     * The security groups url
     */
    @JsonProperty("security_groups_url")
    @Nullable
    abstract String getSecurityGroupsUrl();

    /**
     * The service instances url
     */
    @JsonProperty("service_instances_url")
    @Nullable
    abstract String getServiceInstancesUrl();

    /**
     * The space quota definition id
     */
    @JsonProperty("space_quota_definition_guid")
    @Nullable
    abstract String getSpaceQuotaDefinitionId();

    /**
     * The space quota definition url
     */
    @JsonProperty("space_quota_definition_url")
    @Nullable
    abstract String getSpaceQuotaDefinitionUrl();

    /**
     * The space quota definition url
     */
    @JsonProperty("staging_security_groups_url")
    @Nullable
    abstract String getStagingSecurityGroupsUrl();

}
