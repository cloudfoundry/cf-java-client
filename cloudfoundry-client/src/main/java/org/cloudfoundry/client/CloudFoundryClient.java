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

package org.cloudfoundry.client;

import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.applicationusageevents.ApplicationUsageEvents;
import org.cloudfoundry.client.v2.buildpacks.Buildpacks;
import org.cloudfoundry.client.v2.domains.Domains;
import org.cloudfoundry.client.v2.environmentvariablegroups.EnvironmentVariableGroups;
import org.cloudfoundry.client.v2.events.Events;
import org.cloudfoundry.client.v2.featureflags.FeatureFlags;
import org.cloudfoundry.client.v2.info.Info;
import org.cloudfoundry.client.v2.jobs.Jobs;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitions;
import org.cloudfoundry.client.v2.organizations.Organizations;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomains;
import org.cloudfoundry.client.v2.routemappings.RouteMappings;
import org.cloudfoundry.client.v2.routes.Routes;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroups;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindings;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokers;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstances;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeys;
import org.cloudfoundry.client.v2.serviceplans.ServicePlans;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilities;
import org.cloudfoundry.client.v2.services.Services;
import org.cloudfoundry.client.v2.serviceusageevents.ServiceUsageEvents;
import org.cloudfoundry.client.v2.shareddomains.SharedDomains;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitions;
import org.cloudfoundry.client.v2.spaces.Spaces;
import org.cloudfoundry.client.v2.stacks.Stacks;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstances;
import org.cloudfoundry.client.v2.users.Users;
import org.cloudfoundry.client.v3.applications.ApplicationsV3;
import org.cloudfoundry.client.v3.droplets.Droplets;
import org.cloudfoundry.client.v3.packages.Packages;
import org.cloudfoundry.client.v3.processes.Processes;
import org.cloudfoundry.client.v3.tasks.Tasks;
import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Client API
 */
public interface CloudFoundryClient {

    /**
     * The currently supported Cloud Controller API version
     */
    String SUPPORTED_API_VERSION = "2.53.0";

    /**
     * Main entry point to the Cloud Foundry Application Usage Events Client API
     *
     * @return the Cloud Foundry Application Usage Events Client API
     */
    ApplicationUsageEvents applicationUsageEvents();

    /**
     * Main entry point to the Cloud Foundry Applications V2 Client API
     *
     * @return the Cloud Foundry Application V2 Client API
     */
    ApplicationsV2 applicationsV2();

    /**
     * Main entry point to the Cloud Foundry Applications V3 Client API
     *
     * @return the Cloud Foundry Application V3 Client API
     */
    ApplicationsV3 applicationsV3();

    /**
     * Main entry point to the Cloud Foundry Buildpacks V2 Client API
     *
     * @return the Cloud Foundry Buildpacks Client API
     */
    Buildpacks buildpacks();

    /**
     * Main entry point to the Cloud Foundry Domains Client API
     *
     * @return the Cloud Foundry Domains Client API
     */
    Domains domains();

    /**
     * Main entry point to the Cloud Foundry Droplets Client API
     *
     * @return the Cloud Foundry Application Droplets Client API
     */
    Droplets droplets();

    /***
     * Main entry point to the Cloud Foundry Environment Variable Groups Client API
     *
     * @return the Cloud Foundry Environment Variable Groups Client API
     */
    EnvironmentVariableGroups environmentVariableGroups();

    /**
     * Main entry point to the Cloud Foundry Events Client API
     *
     * @return the Cloud Foundry Application Events Client API
     */
    Events events();

    /**
     * Main entry point to the Cloud Foundry Feature Flags Client API
     *
     * @return the Cloud Foundry Application Feature Flags Client API
     */
    FeatureFlags featureFlags();

    /**
     * Returns the current OAuth2 access token
     *
     * @return the current OAuth2 access token
     */
    Mono<String> getAccessToken();

    /**
     * Main entry point to the Cloud Foundry Info Client API
     *
     * @return the Cloud Foundry Info Client API
     */
    Info info();

    /**
     * Main entry point to the Cloud Foundry Job Client API
     *
     * @return the Cloud Foundry Job Client API
     */
    Jobs jobs();

    /**
     * Main entry point to the Cloud Foundry Quota Definitions Client API
     *
     * @return the Cloud Foundry Quota Definitions Client API
     */
    OrganizationQuotaDefinitions organizationQuotaDefinitions();

    /**
     * Main entry point to the Cloud Foundry Organizations Client API
     *
     * @return the Cloud Foundry Organizations Client API
     */
    Organizations organizations();

    /**
     * Main entry point to the Cloud Foundry Packages Client API
     *
     * @return the Cloud Foundry Packages Client API
     */
    Packages packages();

    /**
     * Main entry point to the Cloud Foundry Private Domains Client API
     *
     * @return the Cloud Foundry Private Domains Client API
     */
    PrivateDomains privateDomains();

    /**
     * Main entry point to the Cloud Foundry Processes Client API
     *
     * @return the Cloud Foundry Processes Client API
     */
    Processes processes();

    /**
     * Main entry point to the Cloud Foundry Route Mappings Client API
     *
     * @return the Cloud Foundry Route Mappings Client API
     */
    RouteMappings routeMappings();

    /**
     * Main entry point to the Cloud Foundry Routes Client API
     *
     * @return the Cloud Foundry Routes Client API
     */
    Routes routes();

    /**
     * Main entry point to the Cloud Foundry Security Groups Client API
     *
     * @return the Cloud Foundry Security Groups Client API
     */
    SecurityGroups securityGroups();

    /**
     * Main entry point to the Cloud Foundry Service Bindings Client API
     *
     * @return the Cloud Foundry Service Bindings Client API
     */
    ServiceBindings serviceBindings();

    /**
     * Main entry point to the Cloud Foundry Service Brokers Client API
     *
     * @return the Cloud Foundry Service Brokers Client API
     */
    ServiceBrokers serviceBrokers();

    /**
     * Main entry point to the Cloud Foundry Service Instances Client API
     *
     * @return the Cloud Foundry Service Instances Client API
     */
    ServiceInstances serviceInstances();

    /**
     * Main entry point to the Cloud Foundry Service Keys Client API
     *
     * @return the Cloud Foundry Service Keys Client API
     */
    ServiceKeys serviceKeys();

    /**
     * Main entry point to the Cloud Foundry Service Plan Visibilities Client API
     *
     * @return the Cloud Foundry Service Plan Visibilities Client API
     */
    ServicePlanVisibilities servicePlanVisibilities();

    /**
     * Main entry point to the Cloud Foundry Service Plans Client API
     *
     * @return the Cloud Foundry Service Plans Client API
     */
    ServicePlans servicePlans();

    /**
     * Main entry point to the Cloud Foundry Service Usage Events Client API
     *
     * @return the Cloud Foundry Service Usage Events Client API
     */
    ServiceUsageEvents serviceUsageEvents();

    /**
     * Main entry point to the Cloud Foundry Services Client API
     *
     * @return the Cloud Foundry Services Client API
     */
    Services services();

    /**
     * Main entry point to the Cloud Foundry Shared Domains Client API
     *
     * @return the Cloud Foundry Shared Domains Client API
     */
    SharedDomains sharedDomains();

    /**
     * Main entry point to the Cloud Foundry Space Quota Definitions Client API
     *
     * @return the Cloud Foundry Space Quota Definitions Client API
     */
    SpaceQuotaDefinitions spaceQuotaDefinitions();

    /**
     * Main entry point to the Cloud Foundry Spaces Client API
     *
     * @return the Cloud Foundry Spaces Client API
     */
    Spaces spaces();

    /**
     * Main entry point to the Cloud Foundry Stacks Client API
     *
     * @return the Cloud Foundry Stacks Client API
     */
    Stacks stacks();

    /**
     * Main entry point to the Cloud Foundry Tasks Client API
     *
     * @return the Cloud Foundry Tasks Client API
     */
    Tasks tasks();

    /**
     * Main entry point to the Cloud Foundry User Provided Service Instances Client API
     *
     * @return the Cloud Foundry User Provided Service Instances Client API
     */
    UserProvidedServiceInstances userProvidedServiceInstances();

    /**
     * Main entry point to the Cloud Foundry Users Client API
     *
     * @return the Cloud Foundry Users Client API
     */
    Users users();

}
