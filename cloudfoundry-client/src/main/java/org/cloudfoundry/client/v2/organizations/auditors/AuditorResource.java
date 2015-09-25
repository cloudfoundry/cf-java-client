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

package org.cloudfoundry.client.v2.organizations.auditors;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.client.v2.Resource;

/**
 * Base class for resources that contain auditors
 *
 * <p><b>This class is NOT threadsafe.</b>
 *
 * @param <T> the "self" type.  Used to ensure the appropriate type is returned from builder APIs.
 */
public abstract class AuditorResource<T extends AuditorResource<T>> extends Resource<T, AuditorResource.AuditorEntity> {

    /**
     * The entity response payload for the Auditor resource
     *
     * <p><b>This class is NOT threadsafe.</b>
     */
    public static final class AuditorEntity {

        private volatile String applicationEventsUrl;

        private volatile String auditorsUrl;

        private volatile Boolean billingEnabled;

        private volatile String billingManagersUrl;

        private volatile String domainsUrl;

        private volatile String managersUrl;

        private volatile String name;

        private volatile String privateDomainsUrl;

        private volatile String quotaDefinitionId;

        private volatile String quotaDefinitionUrl;

        private volatile String spaceQuotaDefinitionsUrl;

        private volatile String spacesUrl;

        private volatile String status;

        private volatile String usersUrl;

        /**
         * Returns the application events url
         *
         * @return the application events url
         */
        public String getApplicationEventsUrl() {
            return applicationEventsUrl;
        }

        /**
         * Configure the application events url
         *
         * @param applicationEventsUrl the application events url
         * @return {@code this}
         */
        @JsonProperty("app_events_url")
        public AuditorEntity withApplicationEventsUrl(String applicationEventsUrl) {
            this.applicationEventsUrl = applicationEventsUrl;
            return this;
        }

        /**
         * Returns the auditors url
         *
         * @return the auditors url
         */
        public String getAuditorsUrl() {
            return auditorsUrl;
        }

        /**
         * Configure the auditors url
         *
         * @param auditorsUrl the auditors url
         * @return {@code this}
         */
        @JsonProperty("auditors_url")
        public AuditorEntity withAuditorsUrl(String auditorsUrl) {
            this.auditorsUrl = auditorsUrl;
            return this;
        }

        /**
         * Returns whether billing is enabled
         *
         * @return whether billing is enabled
         */
        public Boolean getBillingEnabled() {
            return billingEnabled;
        }

        /**
         * Configure whether billing is enabled
         *
         * @param billingEnabled whether billing is enabled
         * @return {@code this}
         */
        @JsonProperty("billing_enabled")
        public AuditorEntity withBillingEnabled(Boolean billingEnabled) {
            this.billingEnabled = billingEnabled;
            return this;
        }

        /**
         * Returns the billing managers url
         *
         * @return the billing managers url
         */
        public String getBillingManagersUrl() {
            return billingManagersUrl;
        }

        /**
         * Configure the billing managers url
         *
         * @param billingManagersUrl the billing managers url
         * @return {@code this}
         */
        @JsonProperty("billing_managers_url")
        public AuditorEntity withBillingManagersUrl(String billingManagersUrl) {
            this.billingManagersUrl = billingManagersUrl;
            return this;
        }

        /**
         * Returns the domains url
         *
         * @return the domains url
         */
        public String getDomainsUrl() {
            return domainsUrl;
        }

        /**
         * Configure the domains url
         *
         * @param domainsUrl the domains url
         * @return {@code this}
         */
        @JsonProperty("domains_url")
        public AuditorEntity withDomainsUrl(String domainsUrl) {
            this.domainsUrl = domainsUrl;
            return this;
        }

        /**
         * Returns the managers url
         *
         * @return the managers url
         */
        public String getManagersUrl() {
            return managersUrl;
        }

        /**
         * Configure the managers url
         *
         * @param managersUrl the managers url
         * @return {@code this}
         */
        @JsonProperty("managers_url")
        public AuditorEntity withManagersUrl(String managersUrl) {
            this.managersUrl = managersUrl;
            return this;
        }

        /**
         * Returns the name
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Configure the name
         *
         * @param name the name
         * @return {@code this}
         */
        @JsonProperty("name")
        public AuditorEntity withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Returns the private domains url
         *
         * @return the private domains url
         */
        public String getPrivateDomainsUrl() {
            return privateDomainsUrl;
        }

        /**
         * Configure the private domains url
         *
         * @param privateDomainsUrl the private domains url
         * @return {@code this}
         */
        @JsonProperty("private_domains_url")
        public AuditorEntity withPrivateDomainsUrl(String privateDomainsUrl) {
            this.privateDomainsUrl = privateDomainsUrl;
            return this;
        }

        /**
         * Returns the quota definition id
         *
         * @return the quota definition id
         */
        public String getQuotaDefinitionId() {
            return quotaDefinitionId;
        }

        /**
         * Configure the quota definition id
         *
         * @param quotaDefinitionId the quota definition id
         * @return {@code this}
         */
        @JsonProperty("quota_definition_guid")
        public AuditorEntity withQuotaDefinitionId(String quotaDefinitionId) {
            this.quotaDefinitionId = quotaDefinitionId;
            return this;
        }

        /**
         * Returns the quota definition url
         *
         * @return the quota definition url
         */
        public String getQuotaDefinitionUrl() {
            return quotaDefinitionUrl;
        }

        /**
         * Configure the quota definition url
         *
         * @param quotaDefinitionUrl the quota definition url
         * @return {@code this}
         */
        @JsonProperty("quota_definition_url")
        public AuditorEntity withQuotaDefinitionUrl(String quotaDefinitionUrl) {
            this.quotaDefinitionUrl = quotaDefinitionUrl;
            return this;
        }

        /**
         * Returns the space quota definitions url
         *
         * @return the space quota definitions url
         */
        public String getSpaceQuotaDefinitionsUrl() {
            return spaceQuotaDefinitionsUrl;
        }

        /**
         * Configure the space quota definitions url
         *
         * @param spaceQuotaDefinitionsUrl the space quota definitions url
         * @return {@code this}
         */
        @JsonProperty("space_quota_definitions_url")
        public AuditorEntity withSpaceQuotaDefinitionsUrl(String spaceQuotaDefinitionsUrl) {
            this.spaceQuotaDefinitionsUrl = spaceQuotaDefinitionsUrl;
            return this;
        }

        /**
         * Returns the spaces url
         *
         * @return the spaces url
         */
        public String getSpacesUrl() {
            return this.spacesUrl;
        }

        /**
         * Configure the spaces url
         *
         * @param spacesUrl the spaces url
         * @return {@code this}
         */
        @JsonProperty("spaces_url")
        public AuditorEntity withSpacesUrl(String spacesUrl) {
            this.spacesUrl = spacesUrl;
            return this;
        }

        /**
         * Returns the status
         *
         * @return the status
         */
        public String getStatus() {
            return status;
        }

        /**
         * Configure the status
         *
         * @param status the status
         * @return {@code this}
         */
        @JsonProperty("status")
        public AuditorEntity withStatus(String status) {
            this.status = status;
            return this;
        }

        /**
         * Returns the users url
         *
         * @return the users url
         */
        public String getUsersUrl() {
            return usersUrl;
        }

        /**
         * Configure the users url
         *
         * @param usersUrl the users url
         * @return {@code this}
         */
        @JsonProperty("users_url")
        public AuditorEntity withUsersUrl(String usersUrl) {
            this.usersUrl = usersUrl;
            return this;
        }

    }

}
