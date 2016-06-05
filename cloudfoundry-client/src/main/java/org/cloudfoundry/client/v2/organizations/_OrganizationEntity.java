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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

/**
 * The entity response payload for the Organization resource
 */
@JsonDeserialize
@Value.Immutable
abstract class _OrganizationEntity {

    /**
     * The application events url
     */
    @JsonProperty("app_events_url")
    abstract String getApplicationEventsUrl();

    /**
     * The auditors url
     */
    @JsonProperty("auditors_url")
    abstract String getAuditorsUrl();

    /**
     * Billing enabled
     */
    @JsonProperty("billing_enabled")
    abstract Boolean getBillingEnabled();

    /**
     * The billing managers url
     */
    @JsonProperty("billing_managers_url")
    abstract String getBillingManagersUrl();

    /**
     * The domains url
     */
    @JsonProperty("domains_url")
    abstract String getDomainsUrl();

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
     * The private domains url
     */
    @JsonProperty("private_domains_url")
    abstract String getPrivateDomainsUrl();

    /**
     * The quota definition id
     */
    @JsonProperty("quota_definition_guid")
    abstract String getQuotaDefinitionId();

    /**
     * The quota definition url
     */
    @JsonProperty("quota_definition_url")
    abstract String getQuotaDefinitionUrl();

    /**
     * The space quota definition url
     */
    @JsonProperty("space_quota_definitions_url")
    abstract String getSpaceQuotaDefinitionsUrl();

    /**
     * The spaces url
     */
    @JsonProperty("spaces_url")
    abstract String getSpacesUrl();

    /**
     * The status
     */
    @JsonProperty("status")
    abstract String getStatus();

    /**
     * The users url
     */
    @JsonProperty("users_url")
    abstract String getUsersUrl();

}
