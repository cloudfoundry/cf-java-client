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

package org.cloudfoundry.client.v2.organizations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.v2.PaginatedResponse;
import org.cloudfoundry.client.v2.Resource;

import java.util.List;

/**
 * The response payload for the List Operations operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListOrganizationsResponse
        extends PaginatedResponse<ListOrganizationsResponse.ListOrganizationsResponseResource> {

    @Builder
    ListOrganizationsResponse(@JsonProperty("next_url") String nextUrl,
                              @JsonProperty("prev_url") String previousUrl,
                              @JsonProperty("resources") @Singular List<ListOrganizationsResponseResource> resources,
                              @JsonProperty("total_pages") Integer totalPages,
                              @JsonProperty("total_results") Integer totalResults) {
        super(nextUrl, previousUrl, resources, totalPages, totalResults);
    }

    /**
     * The resource response payload for the List Organizations operation
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static final class ListOrganizationsResponseResource extends Resource<ListOrganizationsResponseEntity> {

        @Builder
        ListOrganizationsResponseResource(@JsonProperty("entity") ListOrganizationsResponseEntity entity,
                                          @JsonProperty("metadata") Metadata metadata) {
            super(entity, metadata);
        }
    }

    /**
     * The entity response payload for the List Organizations operation
     */
    @Data
    public static final class ListOrganizationsResponseEntity {

        /**
         * The application events url
         *
         * @param applicationEventsUrl the application events url
         * @return the application events url
         */
        private final String applicationEventsUrl;

        /**
         * The auditors url
         *
         * @param auditorisUrl the auditors url
         * @return the auditors url
         */
        private final String auditorsUrl;

        /**
         * Billing enabled
         *
         * @param billingEnabled billing enabled
         * @return billing enabled
         */
        private final Boolean billingEnabled;

        /**
         * The billing managers url
         *
         * @param billingManagersUrl the billing managers url
         * @return the billing managers url
         */
        private final String billingManagersUrl;

        /**
         * The domains url
         *
         * @param domainsUrl the domains url
         * @return the domains url
         */
        private final String domainsUrl;

        /**
         * The managers url
         *
         * @param managersUrl the managers the url
         * @return the managers url
         */
        private final String managersUrl;

        /**
         * The name
         *
         * @param name the name
         * @return the name
         */
        private final String name;

        /**
         * The private domains url
         *
         * @param privateDomainsUrl the private domains url
         * @return the private domains url
         */
        private final String privateDomainsUrl;

        /**
         * The quota definition id
         *
         * @param quotaDefinitionId the quota definition id
         * @return the quota definition id
         */
        private final String quotaDefinitionId;

        /**
         * The quota definition url
         *
         * @param quotaDefinitionUrl the quota definition url
         * @return the quota definition url
         */
        private final String quotaDefinitionUrl;

        /**
         * The space quota definitions url
         *
         * @param spaceQuotaDefinitionsUrl the space quota definitions url
         * @return the space quota definitions url
         */
        private final String spaceQuotaDefinitionsUrl;

        /**
         * The spaces url
         *
         * @param spacesUrl the spaces url
         * @return the spaces url
         */
        private final String spacesUrl;

        /**
         * The status
         *
         * @param status the status
         * @return the status
         */
        private final String status;

        /**
         * The users url
         *
         * @param usersUrl the users url
         * @return the users url
         */
        private final String usersUrl;

        @Builder
        ListOrganizationsResponseEntity(@JsonProperty("app_events_url") String applicationEventsUrl,
                                        @JsonProperty("auditors_url") String auditorsUrl,
                                        @JsonProperty("billing_enabled") Boolean billingEnabled,
                                        @JsonProperty("billing_managers_url") String billingManagersUrl,
                                        @JsonProperty("domains_url") String domainsUrl,
                                        @JsonProperty("managers_url") String managersUrl,
                                        @JsonProperty("name") String name,
                                        @JsonProperty("private_domains_url") String privateDomainsUrl,
                                        @JsonProperty("quota_definition_guid") String quotaDefinitionId,
                                        @JsonProperty("quota_definition_url") String quotaDefinitionUrl,
                                        @JsonProperty("space_quota_definitions_url") String spaceQuotaDefinitionsUrl,
                                        @JsonProperty("spaces_url") String spacesUrl,
                                        @JsonProperty("status") String status,
                                        @JsonProperty("users_url") String usersUrl) {
            this.applicationEventsUrl = applicationEventsUrl;
            this.auditorsUrl = auditorsUrl;
            this.billingEnabled = billingEnabled;
            this.billingManagersUrl = billingManagersUrl;
            this.domainsUrl = domainsUrl;
            this.managersUrl = managersUrl;
            this.name = name;
            this.privateDomainsUrl = privateDomainsUrl;
            this.quotaDefinitionId = quotaDefinitionId;
            this.quotaDefinitionUrl = quotaDefinitionUrl;
            this.spaceQuotaDefinitionsUrl = spaceQuotaDefinitionsUrl;
            this.spacesUrl = spacesUrl;
            this.status = status;
            this.usersUrl = usersUrl;
        }

    }

}
