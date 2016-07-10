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
import org.cloudfoundry.client.v2.domains.DeleteDomainRequest;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import org.cloudfoundry.client.v2.featureflags.ListFeatureFlagsRequest;
import org.cloudfoundry.client.v2.featureflags.ListFeatureFlagsResponse;
import org.cloudfoundry.client.v2.featureflags.SetFeatureFlagRequest;
import org.cloudfoundry.client.v2.organizations.DeleteOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.privatedomains.DeletePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsRequest;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v2.spaces.DeleteSpaceRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.DeleteUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstancesRequest;
import org.cloudfoundry.client.v3.applications.Application;
import org.cloudfoundry.client.v3.packages.DeletePackageRequest;
import org.cloudfoundry.client.v3.packages.ListPackagesRequest;
import org.cloudfoundry.client.v3.packages.Package;
import org.cloudfoundry.uaa.UaaClient;
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

    private final UaaClient uaaClient;

    CloudFoundryCleaner(CloudFoundryClient cloudFoundryClient, UaaClient uaaClient) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.uaaClient = uaaClient;
    }

    void clean() {
        Flux.empty()
            .thenMany(cleanBuildpacks(this.cloudFoundryClient))
            .thenMany(cleanFeatureFlags(this.cloudFoundryClient))
            .thenMany(cleanRoutes(this.cloudFoundryClient))
            .thenMany(cleanApplicationsV2(this.cloudFoundryClient))
            .thenMany(cleanApplicationsV3(this.cloudFoundryClient))
            .thenMany(cleanPackages(this.cloudFoundryClient))
            .thenMany(cleanServiceInstances(this.cloudFoundryClient))
            .thenMany(cleanUserProvidedServiceInstances(this.cloudFoundryClient))
            .thenMany(cleanDomains(this.cloudFoundryClient))
            .thenMany(cleanPrivateDomains(this.cloudFoundryClient))
            .thenMany(cleanUsers(this.uaaClient))
            .thenMany(cleanSpaces(this.cloudFoundryClient))
            .thenMany(cleanOrganizations(this.cloudFoundryClient))
            .retry(5, t -> t instanceof SSLException)
            .doOnSubscribe(s -> this.logger.debug(">> CLEANUP <<"))
            .doOnError(Throwable::printStackTrace)
            .doOnComplete(() -> this.logger.debug("<< CLEANUP >>"))
            .then()
            .block(Duration.ofMinutes(10));
    }

    private static Flux<Void> cleanApplicationsV2(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.applicationsV2()
                .list(ListApplicationsRequest.builder()
                    .page(page)
                    .build()))
            .filter(application -> ResourceUtils.getEntity(application).getName().startsWith("test-application-"))
            .map(ResourceUtils::getId)
            .flatMap(applicationId -> removeServiceBindings(cloudFoundryClient, applicationId)
                .thenMany(Flux.just(applicationId)))
            .flatMap(applicationId -> cloudFoundryClient.applicationsV2()
                .delete(DeleteApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build()));
    }

    private static Flux<Void> cleanApplicationsV3(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV3Resources(page -> cloudFoundryClient.applicationsV3()
                .list(org.cloudfoundry.client.v3.applications.ListApplicationsRequest.builder()
                    .page(page)
                    .build()))
            .filter(application -> application.getName().startsWith("test-application-"))
            .map(Application::getId)
            .flatMap(applicationId -> cloudFoundryClient.applicationsV3()
                .delete(org.cloudfoundry.client.v3.applications.DeleteApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build()));
    }

    private static Flux<Void> cleanBuildpacks(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.buildpacks()
                .list(ListBuildpacksRequest.builder()
                    .page(page)
                    .build()))
            .filter(buildpack -> ResourceUtils.getEntity(buildpack).getName().startsWith("test-buildpack-"))
            .map(ResourceUtils::getId)
            .flatMap(buildpackId -> cloudFoundryClient.buildpacks()
                .delete(DeleteBuildpackRequest.builder()
                    .async(true)
                    .buildpackId(buildpackId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Flux<Void> cleanDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.domains()
                .list(ListDomainsRequest.builder()
                    .page(page)
                    .build()))
            .filter(domain -> ResourceUtils.getEntity(domain).getName().startsWith("test.domain."))
            .map(ResourceUtils::getId)
            .flatMap(domainId -> cloudFoundryClient.domains()
                .delete(DeleteDomainRequest.builder()
                    .async(true)
                    .domainId(domainId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
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

    private static Flux<Void> cleanOrganizations(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .list(ListOrganizationsRequest.builder()
                    .page(page)
                    .build()))
            .filter(organization -> ResourceUtils.getEntity(organization).getName().startsWith("test-organization-"))
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

    private static Flux<Void> cleanPrivateDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.privateDomains()
                .list(ListPrivateDomainsRequest.builder()
                    .page(page)
                    .build()))
            .filter(domain -> ResourceUtils.getEntity(domain).getName().startsWith("test.domain."))
            .map(ResourceUtils::getId)
            .flatMap(privateDomainId -> cloudFoundryClient.privateDomains()
                .delete(DeletePrivateDomainRequest.builder()
                    .async(true)
                    .privateDomainId(privateDomainId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Flux<Void> cleanRoutes(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.routes()
                .list(ListRoutesRequest.builder()
                    .page(page)
                    .build()))
            .flatMap(route -> Mono.when(
                Mono.just(route),
                cloudFoundryClient.domains()
                    .get(GetDomainRequest.builder()
                        .domainId(ResourceUtils.getEntity(route).getDomainId())
                        .build())
            ))
            .filter(predicate((route, domain) -> ResourceUtils.getEntity(domain).getName().startsWith("test.domain.")))
            .map(function((route, domain) -> ResourceUtils.getId(route)))
            .flatMap(routeId -> cloudFoundryClient.routes()
                .delete(DeleteRouteRequest.builder()
                    .async(true)
                    .routeId(routeId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Flux<Void> cleanServiceInstances(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.serviceInstances()
                .list(ListServiceInstancesRequest.builder()
                    .page(page)
                    .build()))
            .filter(serviceInstance -> ResourceUtils.getEntity(serviceInstance).getName().startsWith("test-service-instance-"))
            .map(ResourceUtils::getId)
            .flatMap(serviceInstanceId -> cloudFoundryClient.serviceInstances()
                .delete(DeleteServiceInstanceRequest.builder()
                    .async(true)
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Flux<Void> cleanSpaces(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .list(ListSpacesRequest.builder()
                    .page(page)
                    .build()))
            .filter(space -> ResourceUtils.getEntity(space).getName().startsWith("test-space-"))
            .map(ResourceUtils::getId)
            .flatMap(spaceId -> cloudFoundryClient.spaces()
                .delete(DeleteSpaceRequest.builder()
                    .async(true)
                    .spaceId(spaceId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Flux<Void> cleanUserProvidedServiceInstances(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.userProvidedServiceInstances()
                .list(ListUserProvidedServiceInstancesRequest.builder()
                    .page(page)
                    .build()))
            .filter(userProvidedServiceInstance -> ResourceUtils.getEntity(userProvidedServiceInstance).getName().startsWith("test-service-instance-"))
            .map(ResourceUtils::getId)
            .flatMap(userProvidedServiceInstanceId -> cloudFoundryClient.userProvidedServiceInstances()
                .delete(DeleteUserProvidedServiceInstanceRequest.builder()
                    .userProvidedServiceInstanceId(userProvidedServiceInstanceId)
                    .build()));
    }

    private static Flux<Void> cleanUsers(UaaClient uaaClient) {
        return PaginationUtils
            .requestUaaResources(startIndex -> uaaClient.users()
                .list(ListUsersRequest.builder()
                    .startIndex(startIndex)
                    .build()))
            .filter(user -> user.getUserName().startsWith("test-user-"))
            .map(User::getId)
            .flatMap(userId -> uaaClient.users()
                .delete(DeleteUserRequest.builder()
                    .userId(userId)
                    .version("*")
                    .build())
                .then());
    }

    private static Flux<Void> removeServiceBindings(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.applicationsV2()
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
