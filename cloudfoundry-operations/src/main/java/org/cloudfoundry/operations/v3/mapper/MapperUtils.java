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

package org.cloudfoundry.operations.v3.mapper;

import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.CloudFoundryClient;
import org.immutables.value.Value;
import org.cloudfoundry.client.v3.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v3.spaces.SpaceResource;
import org.cloudfoundry.client.v3.routes.RouteRelationships;
import org.cloudfoundry.client.v3.routes.RouteResource;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.organizations.ListOrganizationDomainsRequest;
import org.cloudfoundry.client.v3.domains.DomainResource;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ExceptionUtils;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import java.util.NoSuchElementException;

public final class MapperUtils {

        public static Mono<String> getRouteId(CloudFoundryClient cloudFoundryClient, String organizationId,
                        String domain,
                        String host, Integer port, String path) {

                return Mono.just(domain)
                                .flatMap(name -> getDomainIdByName(cloudFoundryClient, organizationId, name))
                                .flatMap(domainId -> listRoutes(cloudFoundryClient, null,
                                                new String[] { organizationId }, new String[] { path },
                                                new String[] { host }, new int[] { port },
                                                new String[] { domainId })
                                                .single()
                                                .switchIfEmpty(ExceptionUtils.illegalArgument(
                                                                "Route for %s does not exist",
                                                                domain)))
                                .flatMap(route -> Mono.just(route.getId()));
        }

        public static Mono<String> getSpaceIdByName(CloudFoundryClient cloudFoundryClient, String organizationId,
                        String spaceName) {
                return listSpaces(cloudFoundryClient, organizationId, new String[] { spaceName })
                                .flatMap(space -> Mono.just(space.getId()));
        }

        public static Mono<String> getDomainIdByName(CloudFoundryClient cloudFoundryClient, String organizationId,
                        String domain) {
                return getDomain(cloudFoundryClient, organizationId, domain)
                                .map(resource -> resource.getId());
        }

        public static Mono<String> getOptionalDomainIdByName(CloudFoundryClient cloudFoundryClient,
                        String organizationId,
                        String domain) {
                return listDomains(cloudFoundryClient, organizationId, new String[] { domain })
                                .singleOrEmpty()
                                .map(resource -> resource.getId());
        }

        public static Mono<SpaceResource> listSpaces(CloudFoundryClient cloudFoundryClient, String organizationId,
                        String[] spacesNameFilter) {
                return PaginationUtils.requestClientV3Resources(page -> cloudFoundryClient.spacesV3()
                                .list(ListSpacesRequest.builder()
                                                .organizationId(organizationId)
                                                .names(spacesNameFilter)
                                                .page(page)
                                                .build()))
                                .single()
                                .onErrorResume(NoSuchElementException.class,
                                                t -> ExceptionUtils.illegalArgument(
                                                                "Space %s does not exist",
                                                                String.join(",", spacesNameFilter)));

        }

        private static Mono<DomainResource> getDomain(CloudFoundryClient cloudFoundryClient,
                        String organizationId, String domainName) {
                return listDomains(cloudFoundryClient, organizationId, new String[] { domainName })
                                .single()
                                .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils
                                                .illegalArgument("Domain %s does not exist", domainName));

        }

        public static Flux<DomainResource> listDomains(CloudFoundryClient cloudFoundryClient,
                        String organizationId, String[] domainNamesFilter) {
                return PaginationUtils
                                .requestClientV3Resources(page -> cloudFoundryClient.organizationsV3().listDomains(
                                                ListOrganizationDomainsRequest.builder()
                                                                .names(domainNamesFilter)
                                                                .page(page)
                                                                .organizationId(organizationId).build()));
        }

        public static Flux<RouteResource> listRoutes(CloudFoundryClient cloudFoundryClient, String[] spaceIdFilter,
                        String[] organizationIdFilter, String[] pathsFilter, String[] hostsFilter,
                        int[] portsFilter, String[] domainIdFilter) {

                return PaginationUtils
                                .requestClientV3Resources(page -> cloudFoundryClient.routesV3()
                                                .list(org.cloudfoundry.client.v3.routes.ListRoutesRequest.builder()
                                                                .page(page)
                                                                .spaceIds(spaceIdFilter)
                                                                .organizationIds(organizationIdFilter)
                                                                .ports(portsFilter)
                                                                .hosts(hostsFilter)
                                                                .paths(pathsFilter)
                                                                .domainIds(domainIdFilter)
                                                                .build()));
        }
}
