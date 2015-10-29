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
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cloudfoundry.client.v2.Resource;

/**
 * Base class for resources that contain spaces
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class SpaceResource extends Resource<SpaceResource.SpaceEntity> {

    SpaceResource(@JsonProperty("entity") SpaceEntity entity,
                  @JsonProperty("metadata") Metadata metadata) {
        super(entity, metadata);
    }

    /**
     * The entity response payload for the Space resource
     */
    @Data
    public static final class SpaceEntity {

        /**
         * Allow SSH
         *
         * @param allowSsh allow SSH
         * @return allow SSH
         */
        private final Boolean allowSsh;

        /**
         * The application events url
         *
         * @param applicationEventsUrl the application events url
         * @return the application events url
         */
        private final String applicationEventsUrl;

        /**
         * The applications url
         *
         * @param applicationsUrl the applications url
         * @return the applications url
         */
        private final String applicationsUrl;

        /**
         * The auditors url
         *
         * @param auditorsUrl the auditors url
         * @return the auditors url
         */
        private final String auditorsUrl;

        /**
         * The developers url
         *
         * @param developersUrl the developers url
         * @return the developers url
         */
        private final String developersUrl;

        /**
         * The domains url
         *
         * @param domainsUrl the domains url
         * @return the domains url
         */
        private final String domainsUrl;

        /**
         * The events url
         *
         * @param eventsUrl the events url
         * @return the events url
         */
        private final String eventsUrl;

        /**
         * The managers url
         *
         * @param managersUrl the managers url
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
         * The organization id
         *
         * @param organizationId the organization id
         * @return the organization id
         */
        private final String organizationId;

        /**
         * The organization url
         *
         * @param organizationUrl the organization url
         * @return the organization url
         */
        private final String organizationUrl;

        /**
         * The routes url
         *
         * @param routesUrl the routes url
         * @return the routes url
         */
        private final String routesUrl;

        /**
         * The security groups url
         *
         * @param securityGroupsUrl the security groups url
         * @return the security groups url
         */
        private final String securityGroupsUrl;

        /**
         * The service instances url
         *
         * @param serviceInstancesUrl the service instances url
         * @return the service instances url
         */
        private final String serviceInstancesUrl;

        /**
         * The space quota definition id
         *
         * @param spaceQuotaDefinitionId the space quota definition id
         * @return the space quota definition id
         */
        private final String spaceQuotaDefinitionId;

        @Builder
        SpaceEntity(@JsonProperty("allow_ssh") Boolean allowSsh,
                    @JsonProperty("app_events_url") String applicationEventsUrl,
                    @JsonProperty("apps_url") String applicationsUrl,
                    @JsonProperty("auditors_url") String auditorsUrl,
                    @JsonProperty("developers_url") String developersUrl,
                    @JsonProperty("domains_url") String domainsUrl,
                    @JsonProperty("events_url") String eventsUrl,
                    @JsonProperty("managers_url") String managersUrl,
                    @JsonProperty("name") String name,
                    @JsonProperty("organization_guid") String organizationId,
                    @JsonProperty("organization_url") String organizationUrl,
                    @JsonProperty("routes_url") String routesUrl,
                    @JsonProperty("security_groups_url") String securityGroupsUrl,
                    @JsonProperty("service_instances_url") String serviceInstancesUrl,
                    @JsonProperty("space_quota_definition_guid") String spaceQuotaDefinitionId) {
            this.allowSsh = allowSsh;
            this.applicationEventsUrl = applicationEventsUrl;
            this.applicationsUrl = applicationsUrl;
            this.auditorsUrl = auditorsUrl;
            this.developersUrl = developersUrl;
            this.domainsUrl = domainsUrl;
            this.eventsUrl = eventsUrl;
            this.managersUrl = managersUrl;
            this.name = name;
            this.organizationId = organizationId;
            this.organizationUrl = organizationUrl;
            this.routesUrl = routesUrl;
            this.securityGroupsUrl = securityGroupsUrl;
            this.serviceInstancesUrl = serviceInstancesUrl;
            this.spaceQuotaDefinitionId = spaceQuotaDefinitionId;
        }

    }
}
