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
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.applications.RemoveApplicationServiceBindingRequest;
import org.cloudfoundry.client.v2.domains.DeleteDomainRequest;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import org.cloudfoundry.client.v2.organizations.DeleteOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.privatedomains.DeletePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsRequest;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.spaces.DeleteSpaceRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.DeleteUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstancesRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstanceResource;
import org.cloudfoundry.client.v3.applications.Application;
import org.cloudfoundry.client.v3.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v3.packages.DeletePackageRequest;
import org.cloudfoundry.client.v3.packages.ListPackagesRequest;
import org.cloudfoundry.client.v3.packages.ListPackagesResponse;
import org.cloudfoundry.client.v3.packages.Package;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

final class CloudFoundryCleaner {

    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.test");

    private final CloudFoundryClient cloudFoundryClient;

    private final Mono<Optional<String>> protectedDomainId;

    private final Mono<Optional<String>> protectedOrganizationId;

    private final Mono<List<String>> protectedSpaceIds;

    CloudFoundryCleaner(CloudFoundryClient cloudFoundryClient, Mono<Optional<String>> protectedDomainId, Mono<Optional<String>> protectedOrganizationId, Mono<List<String>> protectedSpaceIds) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.protectedDomainId = protectedDomainId;
        this.protectedOrganizationId = protectedOrganizationId;
        this.protectedSpaceIds = protectedSpaceIds;
    }

    void clean() {
        Mono
            .when(this.protectedDomainId, this.protectedOrganizationId, this.protectedSpaceIds)
            .flatMap(function((protectedDomainId, protectedOrganizationId, protectedSpaceIds) -> {

                Predicate<ApplicationResource> applicationV2Predicate = protectedOrganizationId
                    .map(id -> (Predicate<ApplicationResource>) r -> !protectedSpaceIds.contains(ResourceUtils.getEntity(r).getSpaceId()))
                    .orElse(r -> true);

                Predicate<ListApplicationsResponse.Resource> applicationsV3Predicate = r -> true;  // TODO: Filter out interesting organizations

                Predicate<ListPackagesResponse.Resource> packagePredicate = r -> true;  // TODO: Filter out interesting organizations

                Predicate<ServiceInstanceResource> serviceInstancePredicate = r -> !protectedSpaceIds.contains(ResourceUtils.getEntity(r).getSpaceId());

                Predicate<UserProvidedServiceInstanceResource> userProvidedServiceInstancePredicate = r -> !protectedSpaceIds.contains(ResourceUtils.getEntity(r).getSpaceId());

                Predicate<RouteResource> routePredicate = r -> true;

                Predicate<DomainResource> domainPredicate = protectedDomainId
                    .map(id -> (Predicate<DomainResource>) r -> !ResourceUtils.getId(r).equals(id))
                    .orElse(r -> true);

                Predicate<PrivateDomainResource> privateDomainPredicate = r -> true;

                Predicate<SpaceResource> spacePredicate = protectedOrganizationId
                    .map(id -> (Predicate<SpaceResource>) r -> !ResourceUtils.getEntity(r).getOrganizationId().equals(id))
                    .orElse(r -> true);

                Predicate<OrganizationResource> organizationPredicate = protectedOrganizationId
                    .map(id -> (Predicate<OrganizationResource>) r -> !ResourceUtils.getId(r).equals(id))
                    .orElse(r -> true);

                return Flux.empty()
                    .after(() -> cleanRoutes(this.cloudFoundryClient, routePredicate))
                    .after(() -> cleanApplicationsV2(this.cloudFoundryClient, applicationV2Predicate))
                    .after(() -> cleanApplicationsV3(this.cloudFoundryClient, applicationsV3Predicate))
                    .after(() -> cleanPackages(this.cloudFoundryClient, packagePredicate))
                    .after(() -> cleanServiceInstances(this.cloudFoundryClient, serviceInstancePredicate))
                    .after(() -> cleanUserProvidedServiceInstances(this.cloudFoundryClient, userProvidedServiceInstancePredicate))
                    .after(() -> cleanDomains(this.cloudFoundryClient, domainPredicate))
                    .after(() -> cleanPrivateDomains(this.cloudFoundryClient, privateDomainPredicate))
                    .after(() -> cleanSpaces(this.cloudFoundryClient, spacePredicate))
                    .after(() -> cleanOrganizations(this.cloudFoundryClient, organizationPredicate));
            }))
            .doOnSubscribe(s -> this.logger.debug(">> CLEANUP <<"))
            .doOnError(Throwable::printStackTrace)
            .doOnComplete(() -> this.logger.debug("<< CLEANUP >>"))
            .after()
            .get(Duration.ofMinutes(10));
    }

    private static Flux<Void> cleanApplicationsV2(CloudFoundryClient cloudFoundryClient, Predicate<ApplicationResource> predicate) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.applicationsV2()
                .list(ListApplicationsRequest.builder()
                    .page(page)
                    .build()))
            .filter(predicate)
            .map(ResourceUtils::getId)
            .flatMap(applicationId -> removeServiceBindings(cloudFoundryClient, applicationId)
                .after(() -> Flux.just(applicationId)))
            .flatMap(applicationId -> cloudFoundryClient.applicationsV2()
                .delete(DeleteApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build()));
    }

    private static Flux<Void> cleanApplicationsV3(CloudFoundryClient cloudFoundryClient, Predicate<ListApplicationsResponse.Resource> predicate) {
        return cloudFoundryClient.applicationsV3()  // TODO: Handle pagination properly
            .list(org.cloudfoundry.client.v3.applications.ListApplicationsRequest.builder()
                .page(1)
                .perPage(5_000)
                .build())
            .flatMap(response -> Flux.fromIterable(response.getResources()))
            .filter(predicate)
            .map(Application::getId)
            .flatMap(applicationId -> cloudFoundryClient.applicationsV3()
                .delete(org.cloudfoundry.client.v3.applications.DeleteApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build()));
    }

    private static Flux<Void> cleanDomains(CloudFoundryClient cloudFoundryClient, Predicate<DomainResource> predicate) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.domains()
                .list(ListDomainsRequest.builder()
                    .page(page)
                    .build()))
            .filter(predicate)
            .map(ResourceUtils::getId)
            .flatMap(domainId -> cloudFoundryClient.domains()
                .delete(DeleteDomainRequest.builder()
                    .async(true)
                    .domainId(domainId)
                    .build()))
            .map(ResourceUtils::getId)
            .flatMap(jobId -> JobUtils.waitForCompletion(cloudFoundryClient, jobId));
    }

    private static Flux<Void> cleanOrganizations(CloudFoundryClient cloudFoundryClient, Predicate<OrganizationResource> predicate) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.organizations()
                .list(ListOrganizationsRequest.builder()
                    .page(page)
                    .build()))
            .filter(predicate)
            .map(ResourceUtils::getId)
            .flatMap(organizationId -> cloudFoundryClient.organizations()
                .delete(DeleteOrganizationRequest.builder()
                    .async(true)
                    .organizationId(organizationId)
                    .build()))
            .map(ResourceUtils::getId)
            .flatMap(jobId -> JobUtils.waitForCompletion(cloudFoundryClient, jobId));
    }

    private static Flux<Void> cleanPackages(CloudFoundryClient cloudFoundryClient, Predicate<ListPackagesResponse.Resource> predicate) {
        return cloudFoundryClient.packages()  // TODO: Handle pagination properly
            .list(ListPackagesRequest.builder()
                .page(1)
                .perPage(5_000)
                .build())
            .flatMap(response -> Flux.fromIterable(response.getResources()))
            .filter(predicate)
            .map(Package::getId)
            .flatMap(packageId -> cloudFoundryClient.packages()
                .delete(DeletePackageRequest.builder()
                    .packageId(packageId)
                    .build()));
    }

    private static Flux<Void> cleanPrivateDomains(CloudFoundryClient cloudFoundryClient, Predicate<PrivateDomainResource> predicate) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.privateDomains()
                .list(ListPrivateDomainsRequest.builder()
                    .page(page)
                    .build()))
            .filter(predicate)
            .map(ResourceUtils::getId)
            .flatMap(privateDomainId -> cloudFoundryClient.privateDomains()
                .delete(DeletePrivateDomainRequest.builder()
                    .async(true)
                    .privateDomainId(privateDomainId)
                    .build()))
            .map(ResourceUtils::getId)
            .flatMap(jobId -> JobUtils.waitForCompletion(cloudFoundryClient, jobId));
    }

    private static Flux<Void> cleanRoutes(CloudFoundryClient cloudFoundryClient, Predicate<RouteResource> predicate) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.routes()
                .list(ListRoutesRequest.builder()
                    .page(page)
                    .build()))
            .filter(predicate)
            .map(ResourceUtils::getId)
            .flatMap(routeId -> cloudFoundryClient.routes()
                .delete(DeleteRouteRequest.builder()
                    .async(true)
                    .routeId(routeId)
                    .build()))
            .map(ResourceUtils::getId)
            .flatMap(jobId -> JobUtils.waitForCompletion(cloudFoundryClient, jobId));
    }

    private static Flux<Void> cleanServiceInstances(CloudFoundryClient cloudFoundryClient, Predicate<ServiceInstanceResource> predicate) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.serviceInstances()
                .list(ListServiceInstancesRequest.builder()
                    .page(page)
                    .build()))
            .filter(predicate)
            .map(ResourceUtils::getId)
            .flatMap(serviceInstanceId -> cloudFoundryClient.serviceInstances()
                .delete(DeleteServiceInstanceRequest.builder()
                    .serviceInstanceId(serviceInstanceId)
                    .build()));
    }

    private static Flux<Void> cleanSpaces(CloudFoundryClient cloudFoundryClient, Predicate<SpaceResource> predicate) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.spaces()
                .list(ListSpacesRequest.builder()
                    .page(page)
                    .build()))
            .filter(predicate)
            .map(ResourceUtils::getId)
            .flatMap(spaceId -> cloudFoundryClient.spaces()
                .delete(DeleteSpaceRequest.builder()
                    .async(true)
                    .spaceId(spaceId)
                    .build()))
            .map(ResourceUtils::getId)
            .flatMap(jobId -> JobUtils.waitForCompletion(cloudFoundryClient, jobId));
    }

    private static Flux<Void> cleanUserProvidedServiceInstances(CloudFoundryClient cloudFoundryClient, Predicate<UserProvidedServiceInstanceResource> predicate) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.userProvidedServiceInstances()
                .list(ListUserProvidedServiceInstancesRequest.builder()
                    .page(page)
                    .build()))
            .filter(predicate)
            .map(ResourceUtils::getId)
            .flatMap(userProvidedServiceInstanceId -> cloudFoundryClient.userProvidedServiceInstances()
                .delete(DeleteUserProvidedServiceInstanceRequest.builder()
                    .userProvidedServiceInstanceId(userProvidedServiceInstanceId)
                    .build()));
    }

    private static Flux<Void> removeServiceBindings(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.applicationsV2()
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
