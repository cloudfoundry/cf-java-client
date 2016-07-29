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

package org.cloudfoundry;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.applications.RemoveApplicationServiceBindingRequest;
import org.cloudfoundry.client.v2.buildpacks.DeleteBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.client.v2.featureflags.ListFeatureFlagsRequest;
import org.cloudfoundry.client.v2.featureflags.ListFeatureFlagsResponse;
import org.cloudfoundry.client.v2.featureflags.SetFeatureFlagRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.DeleteOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.DeleteOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.privatedomains.DeletePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.GetPrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsRequest;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v2.shareddomains.DeleteSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.GetSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.spaces.DeleteSpaceRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.DeleteUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstancesRequest;
import org.cloudfoundry.client.v3.applications.Application;
import org.cloudfoundry.client.v3.packages.DeletePackageRequest;
import org.cloudfoundry.client.v3.packages.ListPackagesRequest;
import org.cloudfoundry.client.v3.packages.Package;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.clients.Client;
import org.cloudfoundry.uaa.clients.DeleteClientRequest;
import org.cloudfoundry.uaa.clients.ListClientsRequest;
import org.cloudfoundry.uaa.groups.DeleteGroupRequest;
import org.cloudfoundry.uaa.groups.Group;
import org.cloudfoundry.uaa.groups.ListGroupsRequest;
import org.cloudfoundry.uaa.users.DeleteUserRequest;
import org.cloudfoundry.uaa.users.ListUsersRequest;
import org.cloudfoundry.uaa.users.User;
import org.cloudfoundry.util.FluentMap;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.Map;

import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static org.cloudfoundry.util.tuple.TupleUtils.predicate;

final class CloudFoundryCleaner {

    private static final Map<String, Boolean> STANDARD_FEATURE_FLAGS = FluentMap.<String, Boolean>builder()
        .entry("app_bits_upload", true)
        .entry("app_scaling", true)
        .entry("diego_docker", true)
        .entry("private_domain_creation", true)
        .entry("route_creation", true)
        .entry("service_instance_creation", true)
        .entry("set_roles_by_username", true)
        .entry("unset_roles_by_username", true)
        .entry("user_org_creation", false)
        .build();

    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.test");

    private final CloudFoundryClient cloudFoundryClient;

    private final NameFactory nameFactory;

    private final UaaClient uaaClient;

    CloudFoundryCleaner(CloudFoundryClient cloudFoundryClient, NameFactory nameFactory, UaaClient uaaClient) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.nameFactory = nameFactory;
        this.uaaClient = uaaClient;
    }

    void clean() {
        Flux.empty()
            .thenMany(cleanBuildpacks(this.cloudFoundryClient, this.nameFactory))
            .thenMany(cleanFeatureFlags(this.cloudFoundryClient))
            .thenMany(cleanRoutes(this.cloudFoundryClient, this.nameFactory))
            .thenMany(cleanApplicationsV2(this.cloudFoundryClient, this.nameFactory))
            .thenMany(cleanApplicationsV3(this.cloudFoundryClient, this.nameFactory))
            .thenMany(cleanPackages(this.cloudFoundryClient))
            .thenMany(cleanServiceInstances(this.cloudFoundryClient, this.nameFactory))
            .thenMany(cleanUserProvidedServiceInstances(this.cloudFoundryClient, this.nameFactory))
            .thenMany(cleanSharedDomains(this.cloudFoundryClient, this.nameFactory))
            .thenMany(cleanPrivateDomains(this.cloudFoundryClient, this.nameFactory))
            .thenMany(cleanGroups(this.uaaClient, this.nameFactory))
            .thenMany(cleanUsers(this.uaaClient, this.nameFactory))
            .thenMany(cleanClients(this.uaaClient, this.nameFactory))
            .thenMany(cleanSpaces(this.cloudFoundryClient, this.nameFactory))
            .thenMany(cleanOrganizations(this.cloudFoundryClient, this.nameFactory))
            .thenMany(cleanOrganizationQuotaDefinitions(this.cloudFoundryClient, this.nameFactory))
            .retry(5, t -> t instanceof SSLException)
            .doOnSubscribe(s -> this.logger.debug(">> CLEANUP <<"))
            .doOnError(Throwable::printStackTrace)
            .doOnComplete(() -> this.logger.debug("<< CLEANUP >>"))
            .then()
            .block(Duration.ofMinutes(30));
    }

    private static Flux<Void> cleanApplicationsV2(CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.applicationsV2()
                .list(ListApplicationsRequest.builder()
                    .page(page)
                    .build()))
            .filter(application -> nameFactory.isApplicationName(ResourceUtils.getEntity(application).getName()))
            .map(ResourceUtils::getId)
            .flatMap(applicationId -> removeServiceBindings(cloudFoundryClient, applicationId)
                .thenMany(Flux.just(applicationId)))
            .flatMap(applicationId -> cloudFoundryClient.applicationsV2()
                .delete(DeleteApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build()));
    }

    private static Flux<Void> cleanApplicationsV3(CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils
            .requestClientV3Resources(page -> cloudFoundryClient.applicationsV3()
                .list(org.cloudfoundry.client.v3.applications.ListApplicationsRequest.builder()
                    .page(page)
                    .build()))
            .filter(application -> nameFactory.isApplicationName(application.getName()))
            .map(Application::getId)
            .flatMap(applicationId -> cloudFoundryClient.applicationsV3()
                .delete(org.cloudfoundry.client.v3.applications.DeleteApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build()));
    }

    private static Flux<Void> cleanBuildpacks(CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.buildpacks()
                .list(ListBuildpacksRequest.builder()
                    .page(page)
                    .build()))
            .filter(buildpack -> nameFactory.isBuildpackName(ResourceUtils.getEntity(buildpack).getName()))
            .map(ResourceUtils::getId)
            .flatMap(buildpackId -> cloudFoundryClient.buildpacks()
                .delete(DeleteBuildpackRequest.builder()
                    .async(true)
                    .buildpackId(buildpackId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Flux<Void> cleanClients(UaaClient uaaClient, NameFactory nameFactory) {
        return PaginationUtils
            .requestUaaResources(startIndex -> uaaClient.clients()
                .list(ListClientsRequest.builder()
                    .startIndex(startIndex)
                    .build()))
            .filter(client -> nameFactory.isClientId(client.getClientId()))
            .map(Client::getClientId)
            .flatMap(clientId -> uaaClient.clients()
                .delete(DeleteClientRequest.builder()
                    .clientId(clientId)
                    .build())
                .then());
    }

    private static Flux<Void> cleanFeatureFlags(CloudFoundryClient cloudFoundryClient) {
        return cloudFoundryClient.featureFlags()
            .list(ListFeatureFlagsRequest.builder()
                .build())
            .flatMapIterable(ListFeatureFlagsResponse::getFeatureFlags)
            .filter(featureFlag -> STANDARD_FEATURE_FLAGS.containsKey(featureFlag.getName()))
            .filter(featureFlag -> STANDARD_FEATURE_FLAGS.get(featureFlag.getName()) != featureFlag.getEnabled())
            .flatMap(featureFlag -> cloudFoundryClient.featureFlags()
                .set(SetFeatureFlagRequest.builder()
                    .name(featureFlag.getName())
                    .enabled(STANDARD_FEATURE_FLAGS.get(featureFlag.getName()))
                    .build())
                .then());
    }

    private static Flux<Void> cleanGroups(UaaClient uaaClient, NameFactory nameFactory) {
        return PaginationUtils
            .requestUaaResources(startIndex -> uaaClient.groups()
                .list(ListGroupsRequest.builder()
                    .startIndex(startIndex)
                    .build()))
            .filter(group -> nameFactory.isGroupName(group.getDisplayName()))
            .map(Group::getId)
            .flatMap(groupId -> uaaClient.groups()
                .delete(DeleteGroupRequest.builder()
                    .groupId(groupId)
                    .version("*")
                    .build())
                .then());
    }

    private static Flux<Void> cleanOrganizationQuotaDefinitions(CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizationQuotaDefinitions()
                .list(ListOrganizationQuotaDefinitionsRequest.builder()
                    .page(page)
                    .build()))
            .filter(domain -> nameFactory.isQuotaDefinitionName(ResourceUtils.getEntity(domain).getName()))
            .map(ResourceUtils::getId)
            .flatMap(organizationQuotaDefinitionId -> cloudFoundryClient.organizationQuotaDefinitions()
                .delete(DeleteOrganizationQuotaDefinitionRequest.builder()
                    .async(true)
                    .organizationQuotaDefinitionId(organizationQuotaDefinitionId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Flux<Void> cleanOrganizations(CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .list(ListOrganizationsRequest.builder()
                    .page(page)
                    .build()))
            .filter(organization -> nameFactory.isOrganizationName(ResourceUtils.getEntity(organization).getName()))
            .map(ResourceUtils::getId)
            .flatMap(organizationId -> cloudFoundryClient.organizations()
                .delete(DeleteOrganizationRequest.builder()
                    .async(true)
                    .organizationId(organizationId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Flux<Void> cleanPackages(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV3Resources(page -> cloudFoundryClient.packages()
                .list(ListPackagesRequest.builder()
                    .page(page)
                    .build()))
            .filter(package1 -> true)
            .map(Package::getId)
            .flatMap(packageId -> cloudFoundryClient.packages()
                .delete(DeletePackageRequest.builder()
                    .packageId(packageId)
                    .build()));
    }

    private static Flux<Void> cleanPrivateDomains(CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.privateDomains()
                .list(ListPrivateDomainsRequest.builder()
                    .page(page)
                    .build()))
            .filter(domain -> nameFactory.isDomainName(ResourceUtils.getEntity(domain).getName()))
            .map(ResourceUtils::getId)
            .flatMap(privateDomainId -> cloudFoundryClient.privateDomains()
                .delete(DeletePrivateDomainRequest.builder()
                    .async(true)
                    .privateDomainId(privateDomainId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Flux<Void> cleanRoutes(CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.routes()
                .list(ListRoutesRequest.builder()
                    .page(page)
                    .build()))
            .flatMap(route -> Mono.when(
                Mono.just(route),
                getDomainName(cloudFoundryClient, ResourceUtils.getEntity(route).getDomainId())
            ))
            .filter(predicate((route, domainName) -> nameFactory.isDomainName(domainName) ||
                nameFactory.isApplicationName(ResourceUtils.getEntity(route).getHost()) ||
                nameFactory.isHostName(ResourceUtils.getEntity(route).getHost())))
            .map(function((route, domainName) -> ResourceUtils.getId(route)))
            .flatMap(routeId -> cloudFoundryClient.routes()
                .delete(DeleteRouteRequest.builder()
                    .async(true)
                    .routeId(routeId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Flux<Void> cleanServiceInstances(CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.serviceInstances()
                .list(ListServiceInstancesRequest.builder()
                    .page(page)
                    .build()))
            .filter(serviceInstance -> nameFactory.isServiceInstanceName(ResourceUtils.getEntity(serviceInstance).getName()))
            .map(ResourceUtils::getId)
            .flatMap(serviceInstanceId -> cloudFoundryClient.serviceInstances()
                .delete(DeleteServiceInstanceRequest.builder()
                    .async(true)
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Flux<Void> cleanSharedDomains(CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.sharedDomains()
                .list(ListSharedDomainsRequest.builder()
                    .page(page)
                    .build()))
            .filter(domain -> nameFactory.isDomainName(ResourceUtils.getEntity(domain).getName()))
            .map(ResourceUtils::getId)
            .flatMap(domainId -> cloudFoundryClient.sharedDomains()
                .delete(DeleteSharedDomainRequest.builder()
                    .async(true)
                    .sharedDomainId(domainId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Flux<Void> cleanSpaces(CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .list(ListSpacesRequest.builder()
                    .page(page)
                    .build()))
            .filter(space -> nameFactory.isSpaceName(ResourceUtils.getEntity(space).getName()))
            .map(ResourceUtils::getId)
            .flatMap(spaceId -> cloudFoundryClient.spaces()
                .delete(DeleteSpaceRequest.builder()
                    .async(true)
                    .spaceId(spaceId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Flux<Void> cleanUserProvidedServiceInstances(CloudFoundryClient cloudFoundryClient, NameFactory nameFactory) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.userProvidedServiceInstances()
                .list(ListUserProvidedServiceInstancesRequest.builder()
                    .page(page)
                    .build()))
            .filter(userProvidedServiceInstance -> nameFactory.isServiceInstanceName(ResourceUtils.getEntity(userProvidedServiceInstance).getName()))
            .map(ResourceUtils::getId)
            .flatMap(userProvidedServiceInstanceId -> cloudFoundryClient.userProvidedServiceInstances()
                .delete(DeleteUserProvidedServiceInstanceRequest.builder()
                    .userProvidedServiceInstanceId(userProvidedServiceInstanceId)
                    .build()));
    }

    private static Flux<Void> cleanUsers(UaaClient uaaClient, NameFactory nameFactory) {
        return PaginationUtils
            .requestUaaResources(startIndex -> uaaClient.users()
                .list(ListUsersRequest.builder()
                    .startIndex(startIndex)
                    .build()))
            .filter(user -> nameFactory.isUserName(user.getUserName()))
            .map(User::getId)
            .flatMap(userId -> uaaClient.users()
                .delete(DeleteUserRequest.builder()
                    .userId(userId)
                    .version("*")
                    .build())
                .then());
    }

    private static Mono<String> getDomainName(CloudFoundryClient cloudFoundryClient, String domainId) {
        return cloudFoundryClient.sharedDomains()
            .get(GetSharedDomainRequest.builder()
                .sharedDomainId(domainId)
                .build())
            .map(response -> response.getEntity().getName())
            .otherwise(e -> cloudFoundryClient.privateDomains()
                .get(GetPrivateDomainRequest.builder()
                    .privateDomainId(domainId)
                    .build())
                .map(response -> response.getEntity().getName()));
    }

    private static Flux<Void> removeServiceBindings(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.applicationsV2()
                .listServiceBindings(ListApplicationServiceBindingsRequest.builder()
                    .page(page)
                    .applicationId(applicationId)
                    .build()))
            .map(ResourceUtils::getId)
            .flatMap(serviceBindingId -> cloudFoundryClient.applicationsV2()
                .removeServiceBinding(RemoveApplicationServiceBindingRequest.builder()
                    .applicationId(applicationId)
                    .serviceBindingId(serviceBindingId)
                    .build()));
    }

}
