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

package org.cloudfoundry;

import static org.cloudfoundry.CloudFoundryVersion.PCF_1_12;
import static org.cloudfoundry.CloudFoundryVersion.PCF_2_1;
import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static org.cloudfoundry.util.tuple.TupleUtils.predicate;

import com.github.zafarkhaja.semver.Version;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.net.ssl.SSLException;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.applications.RemoveApplicationServiceBindingRequest;
import org.cloudfoundry.client.v2.buildpacks.DeleteBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.client.v2.featureflags.ListFeatureFlagsRequest;
import org.cloudfoundry.client.v2.featureflags.ListFeatureFlagsResponse;
import org.cloudfoundry.client.v2.featureflags.SetFeatureFlagRequest;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.organizationquotadefinitions.DeleteOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.DeleteOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.privatedomains.DeletePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsRequest;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.securitygroups.DeleteSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupsRequest;
import org.cloudfoundry.client.v2.servicebrokers.DeleteServiceBrokerRequest;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceRoutesRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceBindingsRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceKeysRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceinstances.UnbindServiceInstanceRouteRequest;
import org.cloudfoundry.client.v2.servicekeys.DeleteServiceKeyRequest;
import org.cloudfoundry.client.v2.shareddomains.DeleteSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.DeleteSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.ListSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.spaces.DeleteSpaceRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.stacks.DeleteStackRequest;
import org.cloudfoundry.client.v2.stacks.ListStacksRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.DeleteUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstanceRoutesRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstanceServiceBindingsRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstancesRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.RemoveUserProvidedServiceInstanceRouteRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstanceResource;
import org.cloudfoundry.client.v2.users.UserResource;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.applications.Application;
import org.cloudfoundry.client.v3.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.serviceinstances.ListSharedSpacesRelationshipRequest;
import org.cloudfoundry.client.v3.serviceinstances.ListSharedSpacesRelationshipResponse;
import org.cloudfoundry.client.v3.serviceinstances.UnshareServiceInstanceRequest;
import org.cloudfoundry.client.v3.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v3.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v3.spaces.UpdateSpaceRequest;
import org.cloudfoundry.client.v3.spaces.UpdateSpaceResponse;
import org.cloudfoundry.networking.NetworkingClient;
import org.cloudfoundry.networking.v1.policies.DeletePoliciesRequest;
import org.cloudfoundry.networking.v1.policies.Destination;
import org.cloudfoundry.networking.v1.policies.ListPoliciesRequest;
import org.cloudfoundry.networking.v1.policies.ListPoliciesResponse;
import org.cloudfoundry.networking.v1.policies.Policy;
import org.cloudfoundry.networking.v1.policies.Ports;
import org.cloudfoundry.networking.v1.policies.Source;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.clients.DeleteClientRequest;
import org.cloudfoundry.uaa.clients.ListClientsRequest;
import org.cloudfoundry.uaa.groups.DeleteGroupRequest;
import org.cloudfoundry.uaa.groups.Group;
import org.cloudfoundry.uaa.groups.ListGroupsRequest;
import org.cloudfoundry.uaa.groups.MemberSummary;
import org.cloudfoundry.uaa.identityproviders.DeleteIdentityProviderRequest;
import org.cloudfoundry.uaa.identityproviders.ListIdentityProvidersRequest;
import org.cloudfoundry.uaa.identityproviders.ListIdentityProvidersResponse;
import org.cloudfoundry.uaa.identityzones.DeleteIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.ListIdentityZonesRequest;
import org.cloudfoundry.uaa.identityzones.ListIdentityZonesResponse;
import org.cloudfoundry.uaa.users.DeleteUserRequest;
import org.cloudfoundry.uaa.users.ListUsersRequest;
import org.cloudfoundry.util.FluentMap;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.LastOperationUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;
import reactor.util.retry.Retry;

final class CloudFoundryCleaner implements InitializingBean, DisposableBean {

    private static boolean cleanSlateEnvironment = false;

    private static final Logger LOGGER = LoggerFactory.getLogger("cloudfoundry-client.test");

    private static final Map<String, Boolean> STANDARD_FEATURE_FLAGS =
            FluentMap.<String, Boolean>builder()
                    .entry("app_bits_upload", true)
                    .entry("app_scaling", true)
                    .entry("diego_docker", true)
                    .entry("private_domain_creation", true)
                    .entry("route_creation", true)
                    .entry("service_instance_creation", true)
                    .entry("service_instance_sharing", true)
                    .entry("set_roles_by_username", true)
                    .entry("unset_roles_by_username", true)
                    .entry("user_org_creation", false)
                    .build();

    private final CloudFoundryClient cloudFoundryClient;

    private final NameFactory nameFactory;

    private final NetworkingClient networkingClient;

    private final Version serverVersion;

    private final UaaClient uaaClient;

    CloudFoundryCleaner(
            CloudFoundryClient cloudFoundryClient,
            NameFactory nameFactory,
            NetworkingClient networkingClient,
            Version serverVersion,
            UaaClient uaaClient) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.nameFactory = nameFactory;
        this.networkingClient = networkingClient;
        this.serverVersion = serverVersion;
        this.uaaClient = uaaClient;
    }

    /**
     * Once at the beginning of the whole test suite, clean up the environment. It should only ever happen
     * once, hence the static init variable.
     */
    @Override
    public void afterPropertiesSet() {
        if (!cleanSlateEnvironment) {
            LOGGER.info(
                    "Performing clean slate cleanup. Should happen once per integration test run.");
            this.clean();
            cleanSlateEnvironment = true;
        }
    }

    @Override
    public void destroy() {
        this.clean();
    }

    void clean() {
        Flux.empty()
                .thenMany(
                        Mono.when( // Before Routes
                                cleanServiceInstances(
                                        this.cloudFoundryClient,
                                        this.nameFactory,
                                        this.serverVersion),
                                cleanUserProvidedServiceInstances(
                                        this.cloudFoundryClient, this.nameFactory)))
                .thenMany(
                        Mono.when( // No prerequisites
                                cleanBuildpacks(this.cloudFoundryClient, this.nameFactory),
                                cleanClients(this.uaaClient, this.nameFactory),
                                cleanFeatureFlags(this.cloudFoundryClient),
                                cleanGroups(this.uaaClient, this.nameFactory),
                                cleanIdentityProviders(this.uaaClient, this.nameFactory),
                                cleanIdentityZones(this.uaaClient, this.nameFactory),
                                cleanNetworkingPolicies(
                                        this.networkingClient,
                                        this.nameFactory,
                                        this.serverVersion),
                                cleanRoutes(this.cloudFoundryClient, this.nameFactory),
                                cleanSecurityGroups(this.cloudFoundryClient, this.nameFactory),
                                cleanServiceBrokers(this.cloudFoundryClient, this.nameFactory),
                                cleanSpaceQuotaDefinitions(
                                        this.cloudFoundryClient, this.nameFactory),
                                cleanStacks(this.cloudFoundryClient, this.nameFactory),
                                cleanUsers(this.cloudFoundryClient, this.nameFactory)))
                .thenMany(
                        Mono.when(
                                cleanApplicationsV3(
                                        this.cloudFoundryClient,
                                        this.nameFactory), // After Routes, cannot run with
                                // other cleanApps
                                cleanUsers(this.uaaClient, this.nameFactory) // After CF Users
                                ))
                .thenMany(
                        Mono.when( // After Routes/Applications
                                cleanPrivateDomains(this.cloudFoundryClient, this.nameFactory),
                                cleanSharedDomains(this.cloudFoundryClient, this.nameFactory),
                                cleanSpaces(this.cloudFoundryClient, this.nameFactory)))
                .thenMany(
                        cleanOrganizations(
                                this.cloudFoundryClient, this.nameFactory)) // After Spaces
                .thenMany(
                        cleanOrganizationQuotaDefinitions(
                                this.cloudFoundryClient, this.nameFactory)) // After Organizations
                .retryWhen(Retry.max(5).filter(SSLException.class::isInstance))
                .doOnSubscribe(s -> LOGGER.debug(">> CLEANUP <<"))
                .doOnComplete(() -> LOGGER.debug("<< CLEANUP >>"))
                .then()
                .block(Duration.ofMinutes(30));
    }

    private static Flux<Void> cleanApplicationsV3(
            CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils.requestClientV3Resources(
                        page ->
                                cloudFoundryClient
                                        .applicationsV3()
                                        .list(ListApplicationsRequest.builder().page(page).build()))
                .filter(application -> nameFactory.isApplicationName(application.getName()))
                .delayUntil(
                        application ->
                                removeApplicationServiceBindings(cloudFoundryClient, application))
                .flatMap(
                        application ->
                                cloudFoundryClient
                                        .applicationsV3()
                                        .delete(
                                                DeleteApplicationRequest.builder()
                                                        .applicationId(application.getId())
                                                        .build())
                                        .then()
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete V3 application"
                                                                        + " {}",
                                                                application.getName(),
                                                                t)));
    }

    private static Flux<Void> cleanBuildpacks(
            CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .buildpacks()
                                        .list(ListBuildpacksRequest.builder().page(page).build()))
                .filter(
                        buildpack ->
                                nameFactory.isBuildpackName(
                                        ResourceUtils.getEntity(buildpack).getName()))
                .flatMap(
                        buildpack ->
                                cloudFoundryClient
                                        .buildpacks()
                                        .delete(
                                                DeleteBuildpackRequest.builder()
                                                        .async(true)
                                                        .buildpackId(ResourceUtils.getId(buildpack))
                                                        .build())
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete buildpack {}",
                                                                ResourceUtils.getEntity(buildpack)
                                                                        .getName(),
                                                                t)))
                .flatMap(
                        job ->
                                JobUtils.waitForCompletion(
                                        cloudFoundryClient, Duration.ofMinutes(5), job));
    }

    private static Flux<Void> cleanClients(UaaClient uaaClient, NameFactory nameFactory) {
        return PaginationUtils.requestUaaResources(
                        startIndex ->
                                uaaClient
                                        .clients()
                                        .list(
                                                ListClientsRequest.builder()
                                                        .startIndex(startIndex)
                                                        .build()))
                .filter(client -> nameFactory.isClientId(client.getClientId()))
                .flatMap(
                        client ->
                                uaaClient
                                        .clients()
                                        .delete(
                                                DeleteClientRequest.builder()
                                                        .clientId(client.getClientId())
                                                        .build())
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete client {}",
                                                                client.getName(),
                                                                t))
                                        .then());
    }

    private static Flux<Void> cleanFeatureFlags(CloudFoundryClient cloudFoundryClient) {
        return cloudFoundryClient
                .featureFlags()
                .list(ListFeatureFlagsRequest.builder().build())
                .flatMapIterable(ListFeatureFlagsResponse::getFeatureFlags)
                .filter(featureFlag -> STANDARD_FEATURE_FLAGS.containsKey(featureFlag.getName()))
                .filter(
                        featureFlag ->
                                STANDARD_FEATURE_FLAGS.get(featureFlag.getName())
                                        != featureFlag.getEnabled())
                .flatMap(
                        featureFlag ->
                                cloudFoundryClient
                                        .featureFlags()
                                        .set(
                                                SetFeatureFlagRequest.builder()
                                                        .name(featureFlag.getName())
                                                        .enabled(
                                                                STANDARD_FEATURE_FLAGS.get(
                                                                        featureFlag.getName()))
                                                        .build())
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to set feature flag {} to"
                                                                        + " {}",
                                                                featureFlag.getName(),
                                                                STANDARD_FEATURE_FLAGS.get(
                                                                        featureFlag.getName()),
                                                                t))
                                        .then());
    }

    private static Flux<Void> cleanGroups(UaaClient uaaClient, NameFactory nameFactory) {
        return PaginationUtils.requestUaaResources(
                        startIndex ->
                                uaaClient
                                        .groups()
                                        .list(
                                                ListGroupsRequest.builder()
                                                        .startIndex(startIndex)
                                                        .build()))
                .filter(group -> nameFactory.isGroupName(group.getDisplayName()))
                .sort(
                        (group1, group2) -> {
                            if (containsMember(group1, group2)) {
                                return -1;
                            } else if (containsMember(group2, group1)) {
                                return 1;
                            } else {
                                return 0;
                            }
                        })
                .concatMap(
                        group ->
                                uaaClient
                                        .groups()
                                        .delete(
                                                DeleteGroupRequest.builder()
                                                        .groupId(group.getId())
                                                        .version("*")
                                                        .build())
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete group {}",
                                                                group.getDisplayName(),
                                                                t))
                                        .then());
    }

    private static Flux<Void> cleanIdentityProviders(UaaClient uaaClient, NameFactory nameFactory) {
        return uaaClient
                .identityProviders()
                .list(ListIdentityProvidersRequest.builder().build())
                .flatMapIterable(ListIdentityProvidersResponse::getIdentityProviders)
                .filter(provider -> nameFactory.isIdentityProviderName(provider.getName()))
                .flatMap(
                        provider ->
                                uaaClient
                                        .identityProviders()
                                        .delete(
                                                DeleteIdentityProviderRequest.builder()
                                                        .identityProviderId(provider.getId())
                                                        .build())
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete identity provider"
                                                                        + " {}",
                                                                provider.getName(),
                                                                t))
                                        .then());
    }

    private static Flux<Void> cleanIdentityZones(UaaClient uaaClient, NameFactory nameFactory) {
        return uaaClient
                .identityZones()
                .list(ListIdentityZonesRequest.builder().build())
                .flatMapIterable(ListIdentityZonesResponse::getIdentityZones)
                .filter(zone -> nameFactory.isIdentityZoneName(zone.getName()))
                .flatMap(
                        zone ->
                                uaaClient
                                        .identityZones()
                                        .delete(
                                                DeleteIdentityZoneRequest.builder()
                                                        .identityZoneId(zone.getId())
                                                        .build())
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete identity zone {}",
                                                                zone.getName(),
                                                                t))
                                        .then());
    }

    private static Flux<Void> cleanNetworkingPolicies(
            NetworkingClient networkingClient, NameFactory nameFactory, Version serverVersion) {
        return ifCfVersion(
                PCF_1_12,
                serverVersion,
                () ->
                        networkingClient
                                .policies()
                                .list(ListPoliciesRequest.builder().build())
                                .flatMapIterable(ListPoliciesResponse::getPolicies)
                                .filter(
                                        policy ->
                                                nameFactory.isPort(
                                                        policy.getDestination()
                                                                .getPorts()
                                                                .getStart()))
                                .filter(
                                        policy ->
                                                nameFactory.isPort(
                                                        policy.getDestination()
                                                                .getPorts()
                                                                .getEnd()))
                                .flatMap(
                                        policy ->
                                                networkingClient
                                                        .policies()
                                                        .delete(
                                                                DeletePoliciesRequest.builder()
                                                                        .policy(
                                                                                Policy.builder()
                                                                                        .destination(
                                                                                                Destination
                                                                                                        .builder()
                                                                                                        .id(
                                                                                                                policy.getDestination()
                                                                                                                        .getId())
                                                                                                        .ports(
                                                                                                                Ports
                                                                                                                        .builder()
                                                                                                                        .end(
                                                                                                                                policy.getDestination()
                                                                                                                                        .getPorts()
                                                                                                                                        .getEnd())
                                                                                                                        .start(
                                                                                                                                policy.getDestination()
                                                                                                                                        .getPorts()
                                                                                                                                        .getStart())
                                                                                                                        .build())
                                                                                                        .protocol(
                                                                                                                policy.getDestination()
                                                                                                                        .getProtocol())
                                                                                                        .build())
                                                                                        .source(
                                                                                                Source
                                                                                                        .builder()
                                                                                                        .id(
                                                                                                                policy.getSource()
                                                                                                                        .getId())
                                                                                                        .build())
                                                                                        .build())
                                                                        .build())
                                                        .doOnError(
                                                                t ->
                                                                        LOGGER.error(
                                                                                "Unable to delete"
                                                                                    + " networking"
                                                                                    + " policy"
                                                                                    + " between {}"
                                                                                    + " and {}",
                                                                                policy.getSource()
                                                                                        .getId(),
                                                                                policy.getDestination()
                                                                                        .getId(),
                                                                                t))
                                                        .then()));
    }

    private static Flux<Void> cleanOrganizationQuotaDefinitions(
            CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .organizationQuotaDefinitions()
                                        .list(
                                                ListOrganizationQuotaDefinitionsRequest.builder()
                                                        .page(page)
                                                        .build()))
                .filter(
                        domain ->
                                nameFactory.isQuotaDefinitionName(
                                        ResourceUtils.getEntity(domain).getName()))
                .flatMap(
                        organizationQuotaDefinition ->
                                cloudFoundryClient
                                        .organizationQuotaDefinitions()
                                        .delete(
                                                DeleteOrganizationQuotaDefinitionRequest.builder()
                                                        .async(true)
                                                        .organizationQuotaDefinitionId(
                                                                ResourceUtils.getId(
                                                                        organizationQuotaDefinition))
                                                        .build())
                                        .flatMapMany(
                                                job ->
                                                        JobUtils.waitForCompletion(
                                                                cloudFoundryClient,
                                                                Duration.ofMinutes(5),
                                                                job))
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete organization"
                                                                        + " quota definition {}",
                                                                ResourceUtils.getEntity(
                                                                                organizationQuotaDefinition)
                                                                        .getName(),
                                                                t)));
    }

    private static Flux<Void> cleanOrganizations(
            CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .organizations()
                                        .list(
                                                ListOrganizationsRequest.builder()
                                                        .page(page)
                                                        .build()))
                .filter(
                        organization ->
                                nameFactory.isOrganizationName(
                                        ResourceUtils.getEntity(organization).getName()))
                .flatMap(
                        organization ->
                                cloudFoundryClient
                                        .organizations()
                                        .delete(
                                                DeleteOrganizationRequest.builder()
                                                        .async(true)
                                                        .organizationId(
                                                                ResourceUtils.getId(organization))
                                                        .build())
                                        .flatMapMany(
                                                job ->
                                                        JobUtils.waitForCompletion(
                                                                cloudFoundryClient,
                                                                Duration.ofMinutes(5),
                                                                job))
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete organization {}",
                                                                ResourceUtils.getEntity(
                                                                                organization)
                                                                        .getName(),
                                                                t)));
    }

    private static Flux<Void> cleanPrivateDomains(
            CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .privateDomains()
                                        .list(
                                                ListPrivateDomainsRequest.builder()
                                                        .page(page)
                                                        .build()))
                .filter(
                        domain ->
                                nameFactory.isDomainName(ResourceUtils.getEntity(domain).getName()))
                .flatMap(
                        privateDomain ->
                                cloudFoundryClient
                                        .privateDomains()
                                        .delete(
                                                DeletePrivateDomainRequest.builder()
                                                        .async(true)
                                                        .privateDomainId(
                                                                ResourceUtils.getId(privateDomain))
                                                        .build())
                                        .flatMapMany(
                                                job ->
                                                        JobUtils.waitForCompletion(
                                                                cloudFoundryClient,
                                                                Duration.ofMinutes(5),
                                                                job))
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete private domain"
                                                                        + " {}",
                                                                ResourceUtils.getEntity(
                                                                                privateDomain)
                                                                        .getName(),
                                                                t)));
    }

    private static Flux<Void> cleanRoutes(
            CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return getAllDomains(cloudFoundryClient)
                .flatMapMany(
                        domains ->
                                PaginationUtils.requestClientV2Resources(
                                                page ->
                                                        cloudFoundryClient
                                                                .routes()
                                                                .list(
                                                                        ListRoutesRequest.builder()
                                                                                .page(page)
                                                                                .build()))
                                        .map(resource -> Tuples.of(domains, resource)))
                .filter(
                        predicate(
                                (domains, route) ->
                                        nameFactory.isDomainName(
                                                        domains.get(
                                                                ResourceUtils.getEntity(route)
                                                                        .getDomainId()))
                                                || nameFactory.isApplicationName(
                                                        ResourceUtils.getEntity(route).getHost())
                                                || nameFactory.isHostName(
                                                        ResourceUtils.getEntity(route).getHost())))
                .flatMap(
                        function(
                                (domains, route) ->
                                        cloudFoundryClient
                                                .routes()
                                                .delete(
                                                        DeleteRouteRequest.builder()
                                                                .async(true)
                                                                .routeId(ResourceUtils.getId(route))
                                                                .build())
                                                .flatMapMany(
                                                        job ->
                                                                JobUtils.waitForCompletion(
                                                                        cloudFoundryClient,
                                                                        Duration.ofMinutes(5),
                                                                        job))
                                                .doOnError(
                                                        t -> {
                                                            RouteEntity entity =
                                                                    ResourceUtils.getEntity(route);
                                                            LOGGER.error(
                                                                    "Unable to delete route"
                                                                            + " {}.{}:{}{}",
                                                                    entity.getHost(),
                                                                    domains.get(
                                                                            entity.getDomainId()),
                                                                    entity.getPort(),
                                                                    entity.getPath(),
                                                                    t);
                                                        })));
    }

    private static Flux<Void> cleanSecurityGroups(
            CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .securityGroups()
                                        .list(
                                                ListSecurityGroupsRequest.builder()
                                                        .page(page)
                                                        .build()))
                .filter(
                        securityGroup ->
                                nameFactory.isSecurityGroupName(
                                        ResourceUtils.getEntity(securityGroup).getName()))
                .flatMap(
                        securityGroup ->
                                cloudFoundryClient
                                        .securityGroups()
                                        .delete(
                                                DeleteSecurityGroupRequest.builder()
                                                        .securityGroupId(
                                                                ResourceUtils.getId(securityGroup))
                                                        .build())
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete security group"
                                                                        + " {}",
                                                                ResourceUtils.getEntity(
                                                                                securityGroup)
                                                                        .getName(),
                                                                t))
                                        .then());
    }

    private static Flux<Void> cleanServiceBrokers(
            CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .serviceBrokers()
                                        .list(
                                                ListServiceBrokersRequest.builder()
                                                        .page(page)
                                                        .build()))
                .filter(
                        serviceBroker ->
                                nameFactory.isServiceBrokerName(
                                        ResourceUtils.getEntity(serviceBroker).getName()))
                .flatMap(
                        serviceBroker ->
                                cloudFoundryClient
                                        .serviceBrokers()
                                        .delete(
                                                DeleteServiceBrokerRequest.builder()
                                                        .serviceBrokerId(
                                                                ResourceUtils.getId(serviceBroker))
                                                        .build())
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete service broker"
                                                                        + " {}",
                                                                ResourceUtils.getEntity(
                                                                                serviceBroker)
                                                                        .getName(),
                                                                t)));
    }

    private static Flux<Void> cleanServiceInstances(
            CloudFoundryClient cloudFoundryClient, NameFactory nameFactory, Version serverVersion) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .serviceInstances()
                                        .list(
                                                ListServiceInstancesRequest.builder()
                                                        .page(page)
                                                        .build()))
                .filter(
                        serviceInstance ->
                                nameFactory.isServiceInstanceName(
                                        ResourceUtils.getEntity(serviceInstance).getName()))
                .delayUntil(
                        serviceInstance ->
                                removeRouteAssociations(cloudFoundryClient, serviceInstance))
                .delayUntil(
                        serviceInstance ->
                                removeServiceInstanceServiceBindings(
                                        cloudFoundryClient, serviceInstance))
                .delayUntil(
                        serviceInstance -> removeServiceKeys(cloudFoundryClient, serviceInstance))
                .delayUntil(
                        serviceInstance ->
                                removeServiceShares(
                                        cloudFoundryClient, serviceInstance, serverVersion))
                .flatMap(
                        serviceInstance ->
                                cloudFoundryClient
                                        .serviceInstances()
                                        .delete(
                                                DeleteServiceInstanceRequest.builder()
                                                        .async(true)
                                                        .serviceInstanceId(
                                                                ResourceUtils.getId(
                                                                        serviceInstance))
                                                        .build())
                                        .flatMap(
                                                response -> {
                                                    Object entity = response.getEntity();
                                                    if (entity instanceof JobEntity) {
                                                        return JobUtils.waitForCompletion(
                                                                cloudFoundryClient,
                                                                Duration.ofMinutes(5),
                                                                (JobEntity) response.getEntity());
                                                    } else {
                                                        return LastOperationUtils.waitForCompletion(
                                                                Duration.ofMinutes(5),
                                                                () ->
                                                                        cloudFoundryClient
                                                                                .serviceInstances()
                                                                                .get(
                                                                                        GetServiceInstanceRequest
                                                                                                .builder()
                                                                                                .serviceInstanceId(
                                                                                                        ResourceUtils
                                                                                                                .getId(
                                                                                                                        serviceInstance))
                                                                                                .build())
                                                                                .map(
                                                                                        r ->
                                                                                                ResourceUtils
                                                                                                        .getEntity(
                                                                                                                r)
                                                                                                        .getLastOperation()));
                                                    }
                                                })
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete service instance"
                                                                        + " {}",
                                                                ResourceUtils.getEntity(
                                                                                serviceInstance)
                                                                        .getName(),
                                                                t)));
    }

    private static Flux<Void> cleanSharedDomains(
            CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .sharedDomains()
                                        .list(
                                                ListSharedDomainsRequest.builder()
                                                        .page(page)
                                                        .build()))
                .filter(
                        domain ->
                                nameFactory.isDomainName(ResourceUtils.getEntity(domain).getName()))
                .flatMap(
                        domain ->
                                cloudFoundryClient
                                        .sharedDomains()
                                        .delete(
                                                DeleteSharedDomainRequest.builder()
                                                        .async(true)
                                                        .sharedDomainId(ResourceUtils.getId(domain))
                                                        .build())
                                        .flatMapMany(
                                                job ->
                                                        JobUtils.waitForCompletion(
                                                                cloudFoundryClient,
                                                                Duration.ofMinutes(5),
                                                                job))
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete domain {}",
                                                                ResourceUtils.getEntity(domain)
                                                                        .getName(),
                                                                t)));
    }

    private static Flux<Void> cleanSpaceQuotaDefinitions(
            CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .spaceQuotaDefinitions()
                                        .list(
                                                ListSpaceQuotaDefinitionsRequest.builder()
                                                        .page(page)
                                                        .build()))
                .filter(
                        quota ->
                                nameFactory.isQuotaDefinitionName(
                                        ResourceUtils.getEntity(quota).getName()))
                .flatMap(
                        quota ->
                                cloudFoundryClient
                                        .spaceQuotaDefinitions()
                                        .delete(
                                                (DeleteSpaceQuotaDefinitionRequest.builder()
                                                        .async(true)
                                                        .spaceQuotaDefinitionId(
                                                                ResourceUtils.getId(quota))
                                                        .build()))
                                        .flatMapMany(
                                                job ->
                                                        JobUtils.waitForCompletion(
                                                                cloudFoundryClient,
                                                                Duration.ofMinutes(5),
                                                                job))
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete space quota"
                                                                        + " definition {}",
                                                                ResourceUtils.getEntity(quota)
                                                                        .getName(),
                                                                t)));
    }

    private static Flux<Void> cleanSpaces(
            CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .spaces()
                                        .list(ListSpacesRequest.builder().page(page).build()))
                .filter(space -> nameFactory.isSpaceName(ResourceUtils.getEntity(space).getName()))
                .delayUntil(
                        space ->
                                removeSpaceMetadata(
                                        cloudFoundryClient,
                                        space)) // TODO: Remove once the delete spaces endpoint
                // handles spaces with metadata
                .flatMap(
                        space ->
                                cloudFoundryClient
                                        .spaces()
                                        .delete(
                                                DeleteSpaceRequest.builder()
                                                        .async(true)
                                                        .recursive(true)
                                                        .spaceId(ResourceUtils.getId(space))
                                                        .build())
                                        .flatMapMany(
                                                job ->
                                                        JobUtils.waitForCompletion(
                                                                cloudFoundryClient,
                                                                Duration.ofMinutes(5),
                                                                job))
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete space {}",
                                                                ResourceUtils.getEntity(space)
                                                                        .getName(),
                                                                t)));
    }

    private static Flux<Void> cleanStacks(
            CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .stacks()
                                        .list(ListStacksRequest.builder().page(page).build()))
                .filter(stack -> nameFactory.isStackName(ResourceUtils.getEntity(stack).getName()))
                .flatMap(
                        stack ->
                                cloudFoundryClient
                                        .stacks()
                                        .delete(
                                                (DeleteStackRequest.builder()
                                                        .async(true)
                                                        .stackId(ResourceUtils.getId(stack))
                                                        .build()))
                                        .flatMapMany(
                                                job ->
                                                        JobUtils.waitForCompletion(
                                                                cloudFoundryClient,
                                                                Duration.ofMinutes(5),
                                                                job))
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete stack {}",
                                                                ResourceUtils.getEntity(stack)
                                                                        .getName(),
                                                                t)));
    }

    private static Flux<Void> cleanUserProvidedServiceInstances(
            CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .userProvidedServiceInstances()
                                        .list(
                                                ListUserProvidedServiceInstancesRequest.builder()
                                                        .page(page)
                                                        .build()))
                .filter(
                        userProvidedServiceInstance ->
                                nameFactory.isServiceInstanceName(
                                        ResourceUtils.getEntity(userProvidedServiceInstance)
                                                .getName()))
                .delayUntil(
                        userProvidedServiceInstance ->
                                removeRouteAssociations(
                                        cloudFoundryClient, userProvidedServiceInstance))
                .delayUntil(
                        userProvidedServiceInstance ->
                                removeUserProvidedServiceInstanceServiceBindings(
                                        cloudFoundryClient, userProvidedServiceInstance))
                .flatMap(
                        userProvidedServiceInstance ->
                                cloudFoundryClient
                                        .userProvidedServiceInstances()
                                        .delete(
                                                DeleteUserProvidedServiceInstanceRequest.builder()
                                                        .userProvidedServiceInstanceId(
                                                                ResourceUtils.getId(
                                                                        userProvidedServiceInstance))
                                                        .build())
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete user provided"
                                                                        + " service instance {}",
                                                                ResourceUtils.getEntity(
                                                                                userProvidedServiceInstance)
                                                                        .getName(),
                                                                t)));
    }

    private static Flux<Void> cleanUsers(
            CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .users()
                                        .list(
                                                org.cloudfoundry.client.v2.users.ListUsersRequest
                                                        .builder()
                                                        .page(page)
                                                        .build()))
                .filter(resource -> isCleanable(nameFactory, resource))
                .map(resource -> resource.getMetadata().getId())
                .flatMap(
                        userId ->
                                cloudFoundryClient
                                        .users()
                                        .delete(
                                                org.cloudfoundry.client.v2.users.DeleteUserRequest
                                                        .builder()
                                                        .async(true)
                                                        .userId(userId)
                                                        .build())
                                        .flatMapMany(
                                                job ->
                                                        JobUtils.waitForCompletion(
                                                                cloudFoundryClient,
                                                                Duration.ofMinutes(5),
                                                                job))
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete user {}",
                                                                userId,
                                                                t)));
    }

    private static Flux<Void> cleanUsers(UaaClient uaaClient, NameFactory nameFactory) {
        return PaginationUtils.requestUaaResources(
                        startIndex ->
                                uaaClient
                                        .users()
                                        .list(
                                                ListUsersRequest.builder()
                                                        .startIndex(startIndex)
                                                        .build()))
                .filter(user -> nameFactory.isUserName(user.getUserName()))
                .flatMap(
                        user ->
                                uaaClient
                                        .users()
                                        .delete(
                                                DeleteUserRequest.builder()
                                                        .userId(user.getId())
                                                        .version("*")
                                                        .build())
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to delete user {}",
                                                                user.getName(),
                                                                t))
                                        .then());
    }

    private static boolean containsMember(Group group, Group candidate) {
        return group.getMembers().stream()
                .map(MemberSummary::getMemberId)
                .anyMatch(id -> candidate.getId().equals(id));
    }

    private static Mono<Map<String, String>> getAllDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .privateDomains()
                                        .list(
                                                ListPrivateDomainsRequest.builder()
                                                        .page(page)
                                                        .build()))
                .map(
                        response ->
                                Tuples.of(
                                        ResourceUtils.getId(response),
                                        ResourceUtils.getEntity(response).getName()))
                .mergeWith(
                        PaginationUtils.requestClientV2Resources(
                                        page ->
                                                cloudFoundryClient
                                                        .sharedDomains()
                                                        .list(
                                                                ListSharedDomainsRequest.builder()
                                                                        .page(page)
                                                                        .build()))
                                .map(
                                        response ->
                                                Tuples.of(
                                                        ResourceUtils.getId(response),
                                                        ResourceUtils.getEntity(response)
                                                                .getName())))
                .collectMap(function((id, name) -> id), function((id, name) -> name));
    }

    private static Flux<Void> ifCfVersion(
            CloudFoundryVersion expectedVersion,
            Version serverVersion,
            Supplier<Flux<Void>> supplier) {
        return serverVersion.lessThan(expectedVersion.getVersion()) ? Flux.empty() : supplier.get();
    }

    private static boolean isCleanable(NameFactory nameFactory, UserResource resource) {
        return nameFactory.isUserId(ResourceUtils.getId(resource))
                || nameFactory.isUserName(ResourceUtils.getEntity(resource).getUsername());
    }

    private static Flux<Void> removeApplicationServiceBindings(
            CloudFoundryClient cloudFoundryClient, Application application) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .applicationsV2()
                                        .listServiceBindings(
                                                ListApplicationServiceBindingsRequest.builder()
                                                        .page(page)
                                                        .applicationId(application.getId())
                                                        .build()))
                .flatMap(
                        serviceBinding ->
                                requestRemoveServiceBinding(
                                                cloudFoundryClient,
                                                application.getId(),
                                                ResourceUtils.getId(serviceBinding))
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to remove service binding"
                                                                        + " from {}",
                                                                application.getName(),
                                                                t)));
    }

    private static Flux<Void> removeRouteAssociations(
            CloudFoundryClient cloudFoundryClient, ServiceInstanceResource serviceInstance) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .serviceInstances()
                                        .listRoutes(
                                                ListServiceInstanceRoutesRequest.builder()
                                                        .page(page)
                                                        .serviceInstanceId(
                                                                ResourceUtils.getId(
                                                                        serviceInstance))
                                                        .build()))
                .flatMap(
                        route ->
                                cloudFoundryClient
                                        .serviceInstances()
                                        .unbindRoute(
                                                UnbindServiceInstanceRouteRequest.builder()
                                                        .routeId(ResourceUtils.getId(route))
                                                        .serviceInstanceId(
                                                                ResourceUtils.getId(
                                                                        serviceInstance))
                                                        .build())
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to remove route binding"
                                                                        + " from {}",
                                                                ResourceUtils.getEntity(
                                                                                serviceInstance)
                                                                        .getName(),
                                                                t)));
    }

    private static Flux<Void> removeRouteAssociations(
            CloudFoundryClient cloudFoundryClient,
            UserProvidedServiceInstanceResource serviceInstance) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .userProvidedServiceInstances()
                                        .listRoutes(
                                                ListUserProvidedServiceInstanceRoutesRequest
                                                        .builder()
                                                        .page(page)
                                                        .userProvidedServiceInstanceId(
                                                                ResourceUtils.getId(
                                                                        serviceInstance))
                                                        .build()))
                .flatMap(
                        route ->
                                cloudFoundryClient
                                        .userProvidedServiceInstances()
                                        .removeRoute(
                                                RemoveUserProvidedServiceInstanceRouteRequest
                                                        .builder()
                                                        .routeId(ResourceUtils.getId(route))
                                                        .userProvidedServiceInstanceId(
                                                                ResourceUtils.getId(
                                                                        serviceInstance))
                                                        .build())
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to remove route binding"
                                                                        + " from {}",
                                                                ResourceUtils.getEntity(
                                                                                serviceInstance)
                                                                        .getName(),
                                                                t)));
    }

    private static Flux<Void> removeServiceInstanceServiceBindings(
            CloudFoundryClient cloudFoundryClient, ServiceInstanceResource serviceInstance) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .serviceInstances()
                                        .listServiceBindings(
                                                ListServiceInstanceServiceBindingsRequest.builder()
                                                        .page(page)
                                                        .serviceInstanceId(
                                                                ResourceUtils.getId(
                                                                        serviceInstance))
                                                        .build()))
                .flatMap(
                        serviceBinding ->
                                requestRemoveServiceBinding(
                                                cloudFoundryClient,
                                                ResourceUtils.getEntity(serviceBinding)
                                                        .getApplicationId(),
                                                ResourceUtils.getId(serviceBinding))
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to remove service binding"
                                                                        + " from {}",
                                                                ResourceUtils.getEntity(
                                                                                serviceInstance)
                                                                        .getName(),
                                                                t)));
    }

    private static Flux<Void> removeServiceKeys(
            CloudFoundryClient cloudFoundryClient, ServiceInstanceResource serviceInstance) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .serviceInstances()
                                        .listServiceKeys(
                                                ListServiceInstanceServiceKeysRequest.builder()
                                                        .page(page)
                                                        .serviceInstanceId(
                                                                ResourceUtils.getId(
                                                                        serviceInstance))
                                                        .build()))
                .flatMap(
                        serviceKey ->
                                cloudFoundryClient
                                        .serviceKeys()
                                        .delete(
                                                DeleteServiceKeyRequest.builder()
                                                        .serviceKeyId(
                                                                ResourceUtils.getId(serviceKey))
                                                        .build())
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to remove service binding"
                                                                        + " from {}",
                                                                ResourceUtils.getEntity(serviceKey)
                                                                        .getName(),
                                                                t)));
    }

    private static Flux<Void> removeServiceShares(
            CloudFoundryClient cloudFoundryClient,
            ServiceInstanceResource serviceInstance,
            Version serverVersion) {
        return ifCfVersion(
                PCF_2_1,
                serverVersion,
                () ->
                        cloudFoundryClient
                                .serviceInstancesV3()
                                .listSharedSpacesRelationship(
                                        ListSharedSpacesRelationshipRequest.builder()
                                                .serviceInstanceId(
                                                        ResourceUtils.getId(serviceInstance))
                                                .build())
                                .flatMapIterable(ListSharedSpacesRelationshipResponse::getData)
                                .map(Relationship::getId)
                                .flatMap(
                                        spaceId ->
                                                requestUnshareServiceInstance(
                                                                cloudFoundryClient,
                                                                serviceInstance,
                                                                spaceId)
                                                        .doOnError(
                                                                t ->
                                                                        LOGGER.error(
                                                                                "Unable to remove"
                                                                                    + " service"
                                                                                    + " share from"
                                                                                    + " {}",
                                                                                ResourceUtils
                                                                                        .getEntity(
                                                                                                serviceInstance)
                                                                                        .getName(),
                                                                                t))));
    }

    private static Mono<UpdateSpaceResponse> removeSpaceMetadata(
            CloudFoundryClient cloudFoundryClient, SpaceResource space) {
        return cloudFoundryClient
                .spacesV3()
                .get(GetSpaceRequest.builder().spaceId(ResourceUtils.getId(space)).build())
                .map(GetSpaceResponse::getMetadata)
                .flatMap(
                        metadata -> {
                            if (metadata.getAnnotations().isEmpty()
                                    && metadata.getLabels().isEmpty()) {
                                return Mono.empty();
                            }

                            Map<String, String> annotations =
                                    new HashMap<>(metadata.getAnnotations());
                            Map<String, String> labels = new HashMap<>(metadata.getLabels());

                            annotations.replaceAll((k, v) -> null);
                            labels.replaceAll((k, v) -> null);

                            return requestUpdateSpace(
                                            cloudFoundryClient,
                                            annotations,
                                            labels,
                                            ResourceUtils.getId(space))
                                    .doOnError(
                                            t ->
                                                    LOGGER.error(
                                                            "Unable to remove metadata from {}",
                                                            ResourceUtils.getEntity(space)
                                                                    .getName()));
                        });
    }

    private static Flux<Void> removeUserProvidedServiceInstanceServiceBindings(
            CloudFoundryClient cloudFoundryClient,
            UserProvidedServiceInstanceResource serviceInstance) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                cloudFoundryClient
                                        .userProvidedServiceInstances()
                                        .listServiceBindings(
                                                ListUserProvidedServiceInstanceServiceBindingsRequest
                                                        .builder()
                                                        .page(page)
                                                        .userProvidedServiceInstanceId(
                                                                ResourceUtils.getId(
                                                                        serviceInstance))
                                                        .build()))
                .flatMap(
                        serviceBinding ->
                                requestRemoveServiceBinding(
                                                cloudFoundryClient,
                                                ResourceUtils.getEntity(serviceBinding)
                                                        .getApplicationId(),
                                                ResourceUtils.getId(serviceBinding))
                                        .doOnError(
                                                t ->
                                                        LOGGER.error(
                                                                "Unable to remove service binding"
                                                                        + " from {}",
                                                                ResourceUtils.getEntity(
                                                                                serviceInstance)
                                                                        .getName(),
                                                                t)));
    }

    private static Mono<Void> requestRemoveServiceBinding(
            CloudFoundryClient cloudFoundryClient, String applicationId, String serviceBindingId) {
        return cloudFoundryClient
                .applicationsV2()
                .removeServiceBinding(
                        RemoveApplicationServiceBindingRequest.builder()
                                .applicationId(applicationId)
                                .serviceBindingId(serviceBindingId)
                                .build());
    }

    private static Mono<Void> requestUnshareServiceInstance(
            CloudFoundryClient cloudFoundryClient,
            ServiceInstanceResource serviceInstance,
            String spaceId) {
        return cloudFoundryClient
                .serviceInstancesV3()
                .unshare(
                        UnshareServiceInstanceRequest.builder()
                                .serviceInstanceId(ResourceUtils.getId(serviceInstance))
                                .spaceId(spaceId)
                                .build());
    }

    private static Mono<UpdateSpaceResponse> requestUpdateSpace(
            CloudFoundryClient cloudFoundryClient,
            Map<String, String> annotations,
            Map<String, String> labels,
            String spaceId) {
        return cloudFoundryClient
                .spacesV3()
                .update(
                        UpdateSpaceRequest.builder()
                                .metadata(
                                        Metadata.builder()
                                                .annotations(annotations)
                                                .labels(labels)
                                                .build())
                                .spaceId(spaceId)
                                .build());
    }
}
