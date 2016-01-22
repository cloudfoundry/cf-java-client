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
import org.cloudfoundry.operations.util.v2.Paginated;
import org.cloudfoundry.operations.util.v2.Resources;
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
        return Paginated
                .requestResources(page -> {
                    ListApplicationsRequest request = ListApplicationsRequest.builder()
                            .page(page)
                            .build();

                    return cloudFoundryClient.applicationsV2().list(request);
                })
                .filter(predicate)
                .map(Resources::getId)
                .flatMap(applicationId -> {
                    DeleteApplicationRequest request = DeleteApplicationRequest.builder()
                            .applicationId(applicationId)
                            .build();

                    return cloudFoundryClient.applicationsV2().delete(request);
                });
    }

    private static Stream<Void> cleanDomains(CloudFoundryClient cloudFoundryClient, Predicate<DomainResource> predicate) {
        return Paginated
                .requestResources(page -> {
                    ListDomainsRequest request = ListDomainsRequest.builder()
                            .page(page)
                            .build();

                    return cloudFoundryClient.domains().list(request);
                })
                .filter(predicate)
                .map(Resources::getId)
                .flatMap(domainId -> {
                    DeleteDomainRequest request = DeleteDomainRequest.builder()
                            .domainId(domainId)
                            .build();

                    return cloudFoundryClient.domains().delete(request);
                });
    }

    private static Stream<Void> cleanOrganizations(CloudFoundryClient cloudFoundryClient, Predicate<OrganizationResource> predicate) {
        return Paginated
                .requestResources(page -> {
                    ListOrganizationsRequest request = ListOrganizationsRequest.builder()
                            .page(page)
                            .build();

                    return cloudFoundryClient.organizations().list(request);
                })
                .filter(predicate)
                .map(Resources::getId)
                .flatMap(organizationId -> {
                    DeleteOrganizationRequest request = DeleteOrganizationRequest.builder()
                            .id(organizationId)
                            .build();

                    return cloudFoundryClient.organizations().delete(request);
                });
    }

    private static Stream<Void> cleanRoutes(CloudFoundryClient cloudFoundryClient, Predicate<RouteResource> predicate) {
        return Paginated
                .requestResources(page -> {
                    ListRoutesRequest request = ListRoutesRequest.builder()
                            .page(page)
                            .build();

                    return cloudFoundryClient.routes().list(request);
                })
                .filter(predicate)
                .map(Resources::getId)
                .flatMap(routeId -> {
                    DeleteRouteRequest request = DeleteRouteRequest.builder()
                            .id(routeId)
                            .build();

                    return cloudFoundryClient.routes().delete(request);
                });
    }

    private static Stream<Void> cleanSpaces(CloudFoundryClient cloudFoundryClient, Predicate<SpaceResource> predicate) {
        return Paginated
                .requestResources(page -> {
                    ListSpacesRequest request = ListSpacesRequest.builder()
                            .page(page)
                            .build();

                    return cloudFoundryClient.spaces().list(request);
                })
                .filter(predicate)
                .map(Resources::getId)
                .flatMap(spaceId -> {
                    DeleteSpaceRequest request = DeleteSpaceRequest.builder()
                            .id(spaceId)
                            .build();

                    return cloudFoundryClient.spaces().delete(request);
                });
    }

}
