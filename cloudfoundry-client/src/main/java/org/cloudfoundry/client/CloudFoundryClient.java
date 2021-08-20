/*
 * Copyright 2013-2021 the original author or authors.
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
import org.cloudfoundry.client.v2.blobstores.Blobstores;
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
import org.cloudfoundry.client.v2.resourcematch.ResourceMatch;
import org.cloudfoundry.client.v2.routemappings.RouteMappings;
import org.cloudfoundry.client.v2.routes.Routes;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroups;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingsV2;
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
import org.cloudfoundry.client.v3.admin.AdminV3;
import org.cloudfoundry.client.v3.applications.ApplicationsV3;
import org.cloudfoundry.client.v3.auditevents.AuditEventsV3;
import org.cloudfoundry.client.v3.buildpacks.BuildpacksV3;
import org.cloudfoundry.client.v3.builds.Builds;
import org.cloudfoundry.client.v3.deployments.DeploymentsV3;
import org.cloudfoundry.client.v3.domains.DomainsV3;
import org.cloudfoundry.client.v3.droplets.Droplets;
import org.cloudfoundry.client.v3.isolationsegments.IsolationSegments;
import org.cloudfoundry.client.v3.jobs.JobsV3;
import org.cloudfoundry.client.v3.organizations.OrganizationsV3;
import org.cloudfoundry.client.v3.packages.Packages;
import org.cloudfoundry.client.v3.processes.Processes;
import org.cloudfoundry.client.v3.roles.RolesV3;
import org.cloudfoundry.client.v3.routes.RoutesV3;
import org.cloudfoundry.client.v3.serviceinstances.ServiceInstancesV3;
import org.cloudfoundry.client.v3.servicebindings.ServiceBindingsV3;
import org.cloudfoundry.client.v3.servicebrokers.ServiceBrokersV3;
import org.cloudfoundry.client.v3.serviceofferings.ServiceOfferingsV3;
import org.cloudfoundry.client.v3.serviceplans.ServicePlansV3;
import org.cloudfoundry.client.v3.spaces.SpacesV3;
import org.cloudfoundry.client.v3.stacks.StacksV3;
import org.cloudfoundry.client.v3.tasks.Tasks;

/**
 * Main entry point to the Cloud Foundry Client API
 */
public interface CloudFoundryClient {

    /**
     * The currently supported Cloud Controller API version
     */
    String SUPPORTED_API_VERSION = "2.150.0";

    /**
     * Main entry point to the Cloud Foundry Application Usage Events Client API
     */
    AdminV3 adminV3();

    /**
     * Main entry point to the Cloud Foundry Application Usage Events Client API
     */
    ApplicationUsageEvents applicationUsageEvents();

    /**
     * Main entry point to the Cloud Foundry Applications V2 Client API
     */
    ApplicationsV2 applicationsV2();

    /**
     * Main entry point to the Cloud Foundry Applications V3 Client API
     */
    ApplicationsV3 applicationsV3();

    /**
     * Main entry point to the Cloud Foundry Audit Events V3 Client API
     */
    AuditEventsV3 auditEventsV3();

    /**
     * Main entry point to the Cloud Foundry Blobstores Client API
     */
    Blobstores blobstores();

    /**
     * Main entry point to the Cloud Foundry Buildpacks Client API
     */
    Buildpacks buildpacks();

    /**
     * Main entry point to the Cloud Foundry Buildpacks V3 Client API
     */
    BuildpacksV3 buildpacksV3();

    /**
     * Main entry point to the Cloud Foundry Builds Client API
     */
    Builds builds();

    /**
     * Main entry point to the Cloud Foundry Deployments V3 Client API
     */
    DeploymentsV3 deploymentsV3();

    /**
     * Main entry point to the Cloud Foundry Domains Client API
     */
    Domains domains();

    /**
     * Main entry point to the Cloud Foundry Domains V3 Client API
     */
    DomainsV3 domainsV3();

    /**
     * Main entry point to the Cloud Foundry Droplets Client API
     */
    Droplets droplets();

    /***
     * Main entry point to the Cloud Foundry Environment Variable Groups Client API
     */
    EnvironmentVariableGroups environmentVariableGroups();

    /**
     * Main entry point to the Cloud Foundry Events Client API
     */
    Events events();

    /**
     * Main entry point to the Cloud Foundry Feature Flags Client API
     */
    FeatureFlags featureFlags();

    /**
     * Main entry point to the Cloud Foundry Info Client API
     */
    Info info();

    /**
     * Main entry point to the Cloud Foundry Isolation Segments API
     */
    IsolationSegments isolationSegments();

    /**
     * Main entry point to the Cloud Foundry Jobs Client API
     */
    Jobs jobs();

    /**
     * Main entry point to the Cloud Foundry Jobs V3 Client API
     */
    JobsV3 jobsV3();

    /**
     * Main entry point to the Cloud Foundry Quota Definitions Client API
     */
    OrganizationQuotaDefinitions organizationQuotaDefinitions();

    /**
     * Main entry point to the Cloud Foundry Organizations V2 Client API
     */
    Organizations organizations();

    /**
     * Main entry point to the Cloud Foundry Organizations V3 Client API
     */
    OrganizationsV3 organizationsV3();

    /**
     * Main entry point to the Cloud Foundry Packages Client API
     */
    Packages packages();

    /**
     * Main entry point to the Cloud Foundry Private Domains Client API
     */
    PrivateDomains privateDomains();

    /**
     * Main entry point to the Cloud Foundry Processes Client API
     */
    Processes processes();

    /**
     * Main entry point to the Cloud Foundry Resource Match Client API
     */
    ResourceMatch resourceMatch();

    /**
     * Main entry point to the Cloud Foundry Roles V3 Client API
     */
    RolesV3 rolesV3();

    /**
     * Main entry point to the Cloud Foundry Route Mappings Client API
     */
    RouteMappings routeMappings();

    /**
     * Main entry point to the Cloud Foundry Routes Client API
     */
    Routes routes();

    /**
     * Main entry point to the Cloud Foundry Routes V3 Client API
     */
    RoutesV3 routesV3();

    /**
     * Main entry point to the Cloud Foundry Security Groups Client API
     */
    SecurityGroups securityGroups();

    /**
     * Main entry point to the Cloud Foundry Service Bindings V2 Client API
     */
    ServiceBindingsV2 serviceBindingsV2();

    /**
     * Main entry point to the Cloud Foundry Service Bindings V3 Client API
     */
    ServiceBindingsV3 serviceBindingsV3();

    /**
     * Main entry point to the Cloud Foundry Service Brokers Client API
     */
    ServiceBrokers serviceBrokers();

    /**
     * Main entry point to the Cloud Foundry Service Brokers V3 Client API
     */
    ServiceBrokersV3 serviceBrokersV3();

    /**
     * Main entry point to the Cloud Foundry Service Instances Client API
     */
    ServiceInstances serviceInstances();

    /**
     * Main entry point to the Cloud Foundry Service Instances V3 Client API
     */
    ServiceInstancesV3 serviceInstancesV3();

    /**
     * Main entry point to the Cloud Foundry Service Keys Client API
     */
    ServiceKeys serviceKeys();

    /**
     * Main entry point to the Cloud Foundry Service Offerings V3 Client API
     */
    ServiceOfferingsV3 serviceOfferingsV3();

    /**
     * Main entry point to the Cloud Foundry Service Plan Visibilities Client API
     */
    ServicePlanVisibilities servicePlanVisibilities();

    /**
     * Main entry point to the Cloud Foundry Service Plans Client API
     */
    ServicePlans servicePlans();

    /**
     * Main entry point to the Cloud Foundry Service Plans V3 Client API
     */
    ServicePlansV3 servicePlansV3();

    /**
     * Main entry point to the Cloud Foundry Service Usage Events Client API
     */
    ServiceUsageEvents serviceUsageEvents();

    /**
     * Main entry point to the Cloud Foundry Services Client API
     */
    Services services();

    /**
     * Main entry point to the Cloud Foundry Shared Domains Client API
     */
    SharedDomains sharedDomains();

    /**
     * Main entry point to the Cloud Foundry Space Quota Definitions Client API
     */
    SpaceQuotaDefinitions spaceQuotaDefinitions();

    /**
     * Main entry point to the Cloud Foundry Spaces V2 Client API
     */
    Spaces spaces();

    /**
     * Main entry point to the Cloud Foundry Spaces V3 Client API
     */
    SpacesV3 spacesV3();

    /**
     * Main entry point to the Cloud Foundry Stacks Client API
     */
    Stacks stacks();

    /**
     * Main entry point to the Cloud Foundry Stacks V3 Client API
     */
    StacksV3 stacksV3();

    /**
     * Main entry point to the Cloud Foundry Tasks Client API
     */
    Tasks tasks();

    /**
     * Main entry point to the Cloud Foundry User Provided Service Instances Client API
     */
    UserProvidedServiceInstances userProvidedServiceInstances();

    /**
     * Main entry point to the Cloud Foundry Users Client API
     */
    Users users();

}
