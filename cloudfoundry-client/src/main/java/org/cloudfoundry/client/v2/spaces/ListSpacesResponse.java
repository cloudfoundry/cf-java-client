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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.client.v2.PaginatedResponse;
import org.cloudfoundry.client.v2.Resource;

/**
 * The response payload for the List Spaces operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class ListSpacesResponse extends PaginatedResponse<ListSpacesResponse,
        ListSpacesResponse.ListSpacesResponseResource> {

    /**
     * The resource response payload for the List Spaces operation
     *
     * <p><b>This class is NOT threadsafe.</b>
     */
    public static final class ListSpacesResponseResource
            extends Resource<ListSpacesResponseResource, ListSpacesResponseEntity> {
    }

    /**
     * The entity response payload for the List Spaces operation
     *
     * <p><b>This class is NOT threadsafe.</b>
     */
    public static final class ListSpacesResponseEntity {

        private volatile Boolean allowSsh;

        private volatile String applicationEventsUrl;

        private volatile String applicationsUrl;

        private volatile String auditorsUrl;

        private volatile String developersUrl;

        private volatile String domainsUrl;

        private volatile String eventsUrl;

        private volatile String managersUrl;

        private volatile String name;

        private volatile String organizationId;

        private volatile String organizationUrl;

        private volatile String routesUrl;

        private volatile String securityGroupsUrl;

        private volatile String serviceInstancesUrl;

        private volatile String spaceQuotaDefinitionId;

        /**
         * Returns whether to allow SSH
         *
         * @return whether to allow SSH
         */
        public Boolean getAllowSsh() {
            return this.allowSsh;
        }

        /**
         * Configure whether to allow SSH
         *
         * @param allowSsh whether to allow SSH
         * @return {@code this}
         */
        @JsonProperty("allow_ssh")
        public ListSpacesResponseEntity allowSsh(Boolean allowSsh) {
            this.allowSsh = allowSsh;
            return this;
        }

        /**
         * Returns the application events url
         *
         * @return the application events url
         */
        public String getApplicationEventsUrl() {
            return this.applicationEventsUrl;
        }

        /**
         * Configure the application events url
         *
         * @param applicationEventsUrl the application events url
         * @return {@code this}
         */
        @JsonProperty("app_events_url")
        public ListSpacesResponseEntity withApplicationEventsUrl(String applicationEventsUrl) {
            this.applicationEventsUrl = applicationEventsUrl;
            return this;
        }

        /**
         * Returns the applications url
         *
         * @return the applications url
         */
        public String getApplicationsUrl() {
            return this.applicationsUrl;
        }

        /**
         * Configure the applications url
         *
         * @param applicationsUrl the applications url
         * @return {@code this}
         */
        @JsonProperty("apps_url")
        public ListSpacesResponseEntity withApplicationsUrl(String applicationsUrl) {
            this.applicationsUrl = applicationsUrl;
            return this;
        }

        /**
         * Returns the auditors url
         *
         * @return the auditors url
         */
        public String getAuditorsUrl() {
            return this.auditorsUrl;
        }

        /**
         * Configure the auditors url
         *
         * @param auditorsUrl the auditors url
         * @return {@code this}
         */
        @JsonProperty("auditors_url")
        public ListSpacesResponseEntity withAuditorsUrl(String auditorsUrl) {
            this.auditorsUrl = auditorsUrl;
            return this;
        }

        /**
         * Returns the developers url
         *
         * @return the developers url
         */
        public String getDevelopersUrl() {
            return this.developersUrl;
        }

        /**
         * Configure the developers url
         *
         * @param developersUrl the developers url
         * @return {@code this}
         */
        @JsonProperty("developers_url")
        public ListSpacesResponseEntity withDevelopersUrl(String developersUrl) {
            this.developersUrl = developersUrl;
            return this;
        }

        /**
         * Returns the domains url
         *
         * @return the domains url
         */
        public String getDomainsUrl() {
            return this.domainsUrl;
        }

        /**
         * Configure the domains url
         *
         * @param domainsUrl the domains url
         * @return {@code this}
         */
        @JsonProperty("domains_url")
        public ListSpacesResponseEntity withDomainsUrl(String domainsUrl) {
            this.domainsUrl = domainsUrl;
            return this;
        }

        /**
         * Return the events url
         *
         * @return the events url
         */
        public String getEventsUrl() {
            return this.eventsUrl;
        }

        /**
         * Configure the events url
         *
         * @param eventsUrl the events url
         * @return {@code this}
         */
        @JsonProperty("events_url")
        public ListSpacesResponseEntity withEventsUrl(String eventsUrl) {
            this.eventsUrl = eventsUrl;
            return this;
        }

        /**
         * Returns the managers url
         *
         * @return the managers url
         */
        public String getManagersUrl() {
            return this.managersUrl;
        }

        /**
         * Configure the managers url
         *
         * @param managersUrl the managers url
         * @return {@code this}
         */
        @JsonProperty("managers_url")
        public ListSpacesResponseEntity withManagersUrl(String managersUrl) {
            this.managersUrl = managersUrl;
            return this;
        }

        /**
         * Returns the name
         *
         * @return the name
         */
        public String getName() {
            return this.name;
        }

        /**
         * Configure the name
         *
         * @param name the name
         * @return {@code this}
         */
        @JsonProperty("name")
        public ListSpacesResponseEntity withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Returns the organization id
         *
         * @return the organization id
         */
        public String getOrganizationId() {
            return this.organizationId;
        }

        /**
         * Configure the organization id
         *
         * @param organizationId the organization id
         * @return {@code this}
         */
        @JsonProperty("organization_guid")
        public ListSpacesResponseEntity withOrganizationId(String organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        /**
         * Returns the organization url
         *
         * @return the organization url
         */
        public String getOrganizationUrl() {
            return this.organizationUrl;
        }

        /**
         * Configure the organization url
         *
         * @param organizationUrl the organization url
         * @return {@code this}
         */
        @JsonProperty("organization_url")
        public ListSpacesResponseEntity withOrganizationUrl(String organizationUrl) {
            this.organizationUrl = organizationUrl;
            return this;
        }

        /**
         * Returns the routes url
         *
         * @return the routes url
         */
        public String getRoutesUrl() {
            return this.routesUrl;
        }

        /**
         * Configure the routes url
         *
         * @param routesUrl the routes url
         * @return {@code this}
         */
        @JsonProperty("routes_url")
        public ListSpacesResponseEntity withRoutesUrl(String routesUrl) {
            this.routesUrl = routesUrl;
            return this;
        }

        /**
         * Returns the security groups url
         *
         * @return the security groups url
         */
        public String getSecurityGroupsUrl() {
            return this.securityGroupsUrl;
        }

        /**
         * Configure the security groups url
         *
         * @param securityGroupsUrl the security groups url
         * @return {@code this}
         */
        @JsonProperty("security_groups_url")
        public ListSpacesResponseEntity withSecurityGroupsUrl(String securityGroupsUrl) {
            this.securityGroupsUrl = securityGroupsUrl;
            return this;
        }

        /**
         * Returns the service instances url
         *
         * @return the service instances url
         */
        public String getServiceInstancesUrl() {
            return this.serviceInstancesUrl;
        }

        /**
         * Configure the service instances url
         *
         * @param serviceInstancesUrl the service instances url
         * @return {@code this}
         */
        @JsonProperty("service_instances_url")
        public ListSpacesResponseEntity withServiceInstancesUrl(String serviceInstancesUrl) {
            this.serviceInstancesUrl = serviceInstancesUrl;
            return this;
        }

        /**
         * Returns the space quota definition id
         *
         * @return the space quota definition id
         */
        public String getSpaceQuotaDefinitionId() {
            return this.spaceQuotaDefinitionId;
        }

        /**
         * Configure the space quota definition id
         *
         * @param spaceQuotaDefinitionId the space quota definition id
         * @return {@code this}
         */
        @JsonProperty("space_quota_definition_guid")
        public ListSpacesResponseEntity withSpaceQuotaDefinitionId(String spaceQuotaDefinitionId) {
            this.spaceQuotaDefinitionId = spaceQuotaDefinitionId;
            return this;
        }

    }

}
