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

package org.cloudfoundry.reactor.client;

import org.cloudfoundry.client.CloudFoundryClient;
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
import org.cloudfoundry.client.v3.tasks.Tasks;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.applications.ReactorApplicationsV2;
import org.cloudfoundry.reactor.client.v2.applicationusageevents.ReactorApplicationUsageEvents;
import org.cloudfoundry.reactor.client.v2.blobstores.ReactorBlobstores;
import org.cloudfoundry.reactor.client.v2.buildpacks.ReactorBuildpacks;
import org.cloudfoundry.reactor.client.v2.domains.ReactorDomains;
import org.cloudfoundry.reactor.client.v2.environmentvariablegroups.ReactorEnvironmentVariableGroups;
import org.cloudfoundry.reactor.client.v2.events.ReactorEvents;
import org.cloudfoundry.reactor.client.v2.featureflags.ReactorFeatureFlags;
import org.cloudfoundry.reactor.client.v2.info.ReactorInfo;
import org.cloudfoundry.reactor.client.v2.jobs.ReactorJobs;
import org.cloudfoundry.reactor.client.v2.organizationquotadefinitions.ReactorOrganizationQuotaDefinitions;
import org.cloudfoundry.reactor.client.v2.organizations.ReactorOrganizations;
import org.cloudfoundry.reactor.client.v2.privatedomains.ReactorPrivateDomains;
import org.cloudfoundry.reactor.client.v2.resourcematch.ReactorResourceMatch;
import org.cloudfoundry.reactor.client.v2.routemappings.ReactorRouteMappings;
import org.cloudfoundry.reactor.client.v2.routes.ReactorRoutes;
import org.cloudfoundry.reactor.client.v2.securitygroups.ReactorSecurityGroups;
import org.cloudfoundry.reactor.client.v2.servicebindings.ReactorServiceBindingsV2;
import org.cloudfoundry.reactor.client.v2.servicebrokers.ReactorServiceBrokers;
import org.cloudfoundry.reactor.client.v2.serviceinstances.ReactorServiceInstances;
import org.cloudfoundry.reactor.client.v2.servicekeys.ReactorServiceKeys;
import org.cloudfoundry.reactor.client.v2.serviceplans.ReactorServicePlans;
import org.cloudfoundry.reactor.client.v2.serviceplanvisibilities.ReactorServicePlanVisibilities;
import org.cloudfoundry.reactor.client.v2.services.ReactorServices;
import org.cloudfoundry.reactor.client.v2.serviceusageevents.ReactorServiceUsageEvents;
import org.cloudfoundry.reactor.client.v2.shareddomains.ReactorSharedDomains;
import org.cloudfoundry.reactor.client.v2.spacequotadefinitions.ReactorSpaceQuotaDefinitions;
import org.cloudfoundry.reactor.client.v2.spaces.ReactorSpaces;
import org.cloudfoundry.reactor.client.v2.stacks.ReactorStacks;
import org.cloudfoundry.reactor.client.v2.userprovidedserviceinstances.ReactorUserProvidedServiceInstances;
import org.cloudfoundry.reactor.client.v2.users.ReactorUsers;
import org.cloudfoundry.reactor.client.v3.admin.ReactorAdminV3;
import org.cloudfoundry.reactor.client.v3.applications.ReactorApplicationsV3;
import org.cloudfoundry.reactor.client.v3.auditevents.ReactorAuditEventsV3;
import org.cloudfoundry.reactor.client.v3.builds.ReactorBuilds;
import org.cloudfoundry.reactor.client.v3.builpacks.ReactorBuildpacksV3;
import org.cloudfoundry.reactor.client.v3.deployments.ReactorDeploymentsV3;
import org.cloudfoundry.reactor.client.v3.domains.ReactorDomainsV3;
import org.cloudfoundry.reactor.client.v3.droplets.ReactorDroplets;
import org.cloudfoundry.reactor.client.v3.isolationsegments.ReactorIsolationSegments;
import org.cloudfoundry.reactor.client.v3.jobs.ReactorJobsV3;
import org.cloudfoundry.reactor.client.v3.organizations.ReactorOrganizationsV3;
import org.cloudfoundry.reactor.client.v3.packages.ReactorPackages;
import org.cloudfoundry.reactor.client.v3.processes.ReactorProcesses;
import org.cloudfoundry.reactor.client.v3.roles.ReactorRolesV3;
import org.cloudfoundry.reactor.client.v3.routes.ReactorRoutesV3;
import org.cloudfoundry.reactor.client.v3.servicebindings.ReactorServiceBindingsV3;
import org.cloudfoundry.reactor.client.v3.servicebrokers.ReactorServiceBrokersV3;
import org.cloudfoundry.reactor.client.v3.serviceinstances.ReactorServiceInstancesV3;
import org.cloudfoundry.reactor.client.v3.serviceofferings.ReactorServiceOfferingsV3;
import org.cloudfoundry.reactor.client.v3.serviceplans.ReactorServicePlansV3;
import org.cloudfoundry.reactor.client.v3.spaces.ReactorSpacesV3;
import org.cloudfoundry.reactor.client.v3.tasks.ReactorTasks;
import org.immutables.value.Value;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;

/**
 * The Reactor-based implementation of {@link CloudFoundryClient}
 */
@Value.Immutable
abstract class _ReactorCloudFoundryClient implements CloudFoundryClient {

    @Override
    @Value.Derived
    public AdminV3 adminV3() {
        return new ReactorAdminV3(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public ApplicationUsageEvents applicationUsageEvents() {
        return new ReactorApplicationUsageEvents(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public ApplicationsV2 applicationsV2() {
        return new ReactorApplicationsV2(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public ApplicationsV3 applicationsV3() {
        return new ReactorApplicationsV3(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public AuditEventsV3 auditEventsV3() {
        return new ReactorAuditEventsV3(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Blobstores blobstores() {
        return new ReactorBlobstores(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Buildpacks buildpacks() {
        return new ReactorBuildpacks(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public BuildpacksV3 buildpacksV3() {
        return new ReactorBuildpacksV3(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Builds builds() {
        return new ReactorBuilds(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @PostConstruct
    public void checkCompatibility() {
        new CloudFoundryClientCompatibilityChecker(info()).check();
    }

    @Override
    @Value.Derived
    public DeploymentsV3 deploymentsV3() {
        return new ReactorDeploymentsV3(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Domains domains() {
        return new ReactorDomains(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public DomainsV3 domainsV3() {
        return new ReactorDomainsV3(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Droplets droplets() {
        return new ReactorDroplets(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public EnvironmentVariableGroups environmentVariableGroups() {
        return new ReactorEnvironmentVariableGroups(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Events events() {
        return new ReactorEvents(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public FeatureFlags featureFlags() {
        return new ReactorFeatureFlags(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Info info() {
        return new ReactorInfo(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public IsolationSegments isolationSegments() {
        return new ReactorIsolationSegments(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Jobs jobs() {
        return new ReactorJobs(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public JobsV3 jobsV3() {
        return new ReactorJobsV3(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public OrganizationQuotaDefinitions organizationQuotaDefinitions() {
        return new ReactorOrganizationQuotaDefinitions(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Organizations organizations() {
        return new ReactorOrganizations(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public OrganizationsV3 organizationsV3() {
        return new ReactorOrganizationsV3(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Packages packages() {
        return new ReactorPackages(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public PrivateDomains privateDomains() {
        return new ReactorPrivateDomains(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Processes processes() {
        return new ReactorProcesses(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public ResourceMatch resourceMatch() {
        return new ReactorResourceMatch(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public RolesV3 rolesV3() {
        return new ReactorRolesV3(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public RouteMappings routeMappings() {
        return new ReactorRouteMappings(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Routes routes() {
        return new ReactorRoutes(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public RoutesV3 routesV3() {
        return new ReactorRoutesV3(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public SecurityGroups securityGroups() {
        return new ReactorSecurityGroups(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public ServiceBindingsV2 serviceBindingsV2() {
        return new ReactorServiceBindingsV2(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public ServiceBindingsV3 serviceBindingsV3() {
        return new ReactorServiceBindingsV3(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public ServiceBrokers serviceBrokers() {
        return new ReactorServiceBrokers(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public ServiceBrokersV3 serviceBrokersV3() {
	return new ReactorServiceBrokersV3(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public ServiceInstances serviceInstances() {
        return new ReactorServiceInstances(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public ServiceInstancesV3 serviceInstancesV3() {
        return new ReactorServiceInstancesV3(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public ServiceKeys serviceKeys() {
        return new ReactorServiceKeys(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public ServiceOfferingsV3 serviceOfferingsV3() {
        return new ReactorServiceOfferingsV3(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public ServicePlanVisibilities servicePlanVisibilities() {
        return new ReactorServicePlanVisibilities(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public ServicePlans servicePlans() {
        return new ReactorServicePlans(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public ServicePlansV3 servicePlansV3() {
        return new ReactorServicePlansV3(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public ServiceUsageEvents serviceUsageEvents() {
        return new ReactorServiceUsageEvents(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Services services() {
        return new ReactorServices(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public SharedDomains sharedDomains() {
        return new ReactorSharedDomains(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public SpaceQuotaDefinitions spaceQuotaDefinitions() {
        return new ReactorSpaceQuotaDefinitions(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Spaces spaces() {
        return new ReactorSpaces(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public SpacesV3 spacesV3() {
        return new ReactorSpacesV3(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Stacks stacks() {
        return new ReactorStacks(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Tasks tasks() {
        return new ReactorTasks(getConnectionContext(), getRootV3(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public UserProvidedServiceInstances userProvidedServiceInstances() {
        return new ReactorUserProvidedServiceInstances(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Users users() {
        return new ReactorUsers(getConnectionContext(), getRootV2(), getTokenProvider(), getRequestTags());
    }

    /**
     * The connection context
     */
    abstract ConnectionContext getConnectionContext();

    /**
     * Map of http header name and value which will be added to every request to the controller
     */
    @Value.Default
    Map<String, String> getRequestTags() {
        return Collections.emptyMap();
    }

    @Value.Default
    Mono<String> getRootV2() {
        return getConnectionContext().getRootProvider().getRoot("cloud_controller_v2", getConnectionContext());
    }

    @Value.Default
    Mono<String> getRootV3() {
        return getConnectionContext().getRootProvider().getRoot("cloud_controller_v3", getConnectionContext());
    }

    /**
     * The token provider
     */
    abstract TokenProvider getTokenProvider();

}
