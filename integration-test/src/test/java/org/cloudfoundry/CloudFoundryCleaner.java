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
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.domains.DeleteDomainRequest;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import org.cloudfoundry.client.v2.organizations.DeleteOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.spaces.DeleteSpaceRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.rx.Fluxion;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.cloudfoundry.util.OperationUtils.afterStreamComplete;
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

                Predicate<ApplicationResource> applicationPredicate = protectedOrganizationId
                    .map(id -> (Predicate<ApplicationResource>) r -> !protectedSpaceIds.contains(ResourceUtils.getEntity(r).getSpaceId()))
                    .orElse(r -> true);

                Predicate<OrganizationResource> organizationPredicate = protectedOrganizationId
                    .map(id -> (Predicate<OrganizationResource>) r -> !ResourceUtils.getId(r).equals(id))
                    .orElse(r -> true);

                Predicate<DomainResource> domainPredicate = protectedDomainId
                    .map(id -> (Predicate<DomainResource>) r -> !ResourceUtils.getId(r).equals(id))
                    .orElse(r -> true);

                Predicate<RouteResource> routePredicate = r -> true;

                Predicate<SpaceResource> spacePredicate = protectedOrganizationId
                    .map(id -> (Predicate<SpaceResource>) r -> !ResourceUtils.getEntity(r).getOrganizationId().equals(id))
                    .orElse(r -> true);

                return cleanApplications(this.cloudFoundryClient, applicationPredicate)
                    .as(afterStreamComplete(() -> cleanRoutes(this.cloudFoundryClient, routePredicate)))
                    .as(afterStreamComplete(() -> cleanDomains(this.cloudFoundryClient, domainPredicate)))
                    .as(afterStreamComplete(() -> cleanSpaces(this.cloudFoundryClient, spacePredicate)))
                    .as(afterStreamComplete(() -> cleanOrganizations(this.cloudFoundryClient, organizationPredicate)));
            }))
            .doOnSubscribe(s -> this.logger.debug(">> CLEANUP <<"))
            .doOnError(Throwable::printStackTrace)
            .doOnComplete(() -> this.logger.debug("<< CLEANUP >>"))
            .after()
            .get(Duration.ofMinutes(5));
    }

    private static Fluxion<Void> cleanApplications(CloudFoundryClient cloudFoundryClient, Predicate<ApplicationResource> predicate) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.applicationsV2()
                .list(ListApplicationsRequest.builder()
                    .page(page)
                    .build()))
            .filter(predicate)
            .map(ResourceUtils::getId)
            .flatMap(applicationId -> cloudFoundryClient.applicationsV2()
                .delete(DeleteApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build()));
    }

    private static Fluxion<Void> cleanDomains(CloudFoundryClient cloudFoundryClient, Predicate<DomainResource> predicate) {
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

    private static Fluxion<Void> cleanOrganizations(CloudFoundryClient cloudFoundryClient, Predicate<OrganizationResource> predicate) {
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

    private static Fluxion<Void> cleanRoutes(CloudFoundryClient cloudFoundryClient, Predicate<RouteResource> predicate) {
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

    private static Fluxion<Void> cleanSpaces(CloudFoundryClient cloudFoundryClient, Predicate<SpaceResource> predicate) {
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

}
