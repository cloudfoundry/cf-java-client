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
import org.cloudfoundry.utils.JobUtils;
import org.cloudfoundry.utils.PaginationUtils;
import org.cloudfoundry.utils.ResourceUtils;
import reactor.fn.Predicate;
import reactor.rx.Stream;

final class CloudFoundryCleaner {

    private CloudFoundryCleaner() {
    }

    static Stream<Void> clean(CloudFoundryClient cloudFoundryClient,
                              Predicate<ApplicationResource> applicationPredicate,
                              Predicate<DomainResource> domainPredicate,
                              Predicate<OrganizationResource> organizationPredicate,
                              Predicate<RouteResource> routePredicate,
                              Predicate<SpaceResource> spacePredicate) {

        return cleanApplications(cloudFoundryClient, applicationPredicate)
            .after(() -> cleanRoutes(cloudFoundryClient, routePredicate))
            .after(() -> cleanDomains(cloudFoundryClient, domainPredicate))
            .after(() -> cleanSpaces(cloudFoundryClient, spacePredicate))
            .after(() -> cleanOrganizations(cloudFoundryClient, organizationPredicate));
    }

    private static Stream<Void> cleanApplications(CloudFoundryClient cloudFoundryClient, Predicate<ApplicationResource> predicate) {
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

    private static Stream<Void> cleanDomains(CloudFoundryClient cloudFoundryClient, Predicate<DomainResource> predicate) {
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

    private static Stream<Void> cleanOrganizations(CloudFoundryClient cloudFoundryClient, Predicate<OrganizationResource> predicate) {
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
                    .build()));
    }

    private static Stream<Void> cleanRoutes(CloudFoundryClient cloudFoundryClient, Predicate<RouteResource> predicate) {
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

    private static Stream<Void> cleanSpaces(CloudFoundryClient cloudFoundryClient, Predicate<SpaceResource> predicate) {
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
