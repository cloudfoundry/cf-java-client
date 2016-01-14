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

package org.cloudfoundry.operations;

import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.GetDomainResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsResponse;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsResponse;
import org.cloudfoundry.client.v2.routes.ListRoutesResponse;
import org.cloudfoundry.client.v2.routes.RouteExistsRequest;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsResponse;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.Mono;

import static org.cloudfoundry.operations.v2.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultRoutesTest {

    public static final class CheckRouteInvalidDomain extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationPrivateDomainsRequest request1 = fill(ListOrganizationPrivateDomainsRequest.builder())
                    .id(TEST_ORGANIZATION_ID)
                    .name("test-invalid-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response1 = fill(ListOrganizationPrivateDomainsResponse.builder()).build();
            when(this.organizations.listPrivateDomains(request1)).thenReturn(Mono.just(response1));

            ListSharedDomainsRequest request2 = fill(ListSharedDomainsRequest.builder())
                    .name("test-invalid-domain")
                    .build();

            ListSharedDomainsResponse response2 = ListSharedDomainsResponse.builder()
                    .totalPages(1)
                    .build();
            when(this.sharedDomains.list(request2)).thenReturn(Mono.just(response2));
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(false);
        }

        @Override
        protected Mono<Boolean> invoke() {
            return this.routes.check(fill(CheckRouteRequest.builder(), "invalid-").build());
        }

    }

    public static final class CheckRouteInvalidHost extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationPrivateDomainsRequest request1 = fill(ListOrganizationPrivateDomainsRequest.builder())
                    .id(TEST_ORGANIZATION_ID)
                    .name("test-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response1 = fill(ListOrganizationPrivateDomainsResponse.builder())
                    .resource(fill(PrivateDomainResource.builder(), "privateDomain-").build())
                    .build();
            when(this.organizations.listPrivateDomains(request1)).thenReturn(Mono.just(response1));

            RouteExistsRequest request2 = RouteExistsRequest.builder()
                    .domainId("test-privateDomain-id")
                    .host("test-invalid-host")
                    .build();
            when(this.cloudFoundryClient.routes().exists(request2)).thenReturn(Mono.just(false));
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(false);
        }

        @Override
        protected Mono<Boolean> invoke() {
            return this.routes.check(fill(CheckRouteRequest.builder())
                    .host("test-invalid-host")
                    .build());
        }

    }

    public static final class CheckRouteNoOrganization extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, MISSING_ID, MISSING_ID);

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalStateException.class);
        }

        @Override
        protected Mono<Boolean> invoke() {
            return this.routes.check(fill(CheckRouteRequest.builder()).build());
        }

    }

    public static final class CheckRoutePrivateDomain extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationPrivateDomainsRequest request1 = fill(ListOrganizationPrivateDomainsRequest.builder())
                    .id(TEST_ORGANIZATION_ID)
                    .name("test-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response1 = fill(ListOrganizationPrivateDomainsResponse.builder(), "privateDomain-")
                    .resource(fill(PrivateDomainResource.builder(), "privateDomain-").build())
                    .build();
            when(this.organizations.listPrivateDomains(request1)).thenReturn(Mono.just(response1));

            RouteExistsRequest request2 = fill(RouteExistsRequest.builder())
                    .domainId("test-privateDomain-id")
                    .path(null)
                    .build();
            when(this.cloudFoundryClient.routes().exists(request2)).thenReturn(Mono.just(true));
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(true);
        }

        @Override
        protected Mono<Boolean> invoke() {
            return this.routes.check(fill(CheckRouteRequest.builder()).build());
        }

    }

    public static final class CheckRouteSharedDomain extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationPrivateDomainsRequest request1 = fill(ListOrganizationPrivateDomainsRequest.builder())
                    .id(TEST_ORGANIZATION_ID)
                    .name("test-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response1 = ListOrganizationPrivateDomainsResponse.builder()
                    .totalPages(1)
                    .build();
            when(this.organizations.listPrivateDomains(request1)).thenReturn(Mono.just(response1));

            ListSharedDomainsRequest request2 = fill(ListSharedDomainsRequest.builder())
                    .name("test-domain")
                    .build();
            ListSharedDomainsResponse response2 = fill(ListSharedDomainsResponse.builder(), "sharedDomains-")
                    .resource(fill(SharedDomainResource.builder(), "sharedDomain-").build())
                    .build();
            when(this.sharedDomains.list(request2)).thenReturn(Mono.just(response2));

            RouteExistsRequest request3 = fill(RouteExistsRequest.builder())
                    .domainId("test-sharedDomain-id")
                    .path(null)
                    .build();
            when(this.cloudFoundryClient.routes().exists(request3)).thenReturn(Mono.just(true));
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(true);
        }

        @Override
        protected Mono<Boolean> invoke() {
            return this.routes.check(fill(CheckRouteRequest.builder()).build());
        }

    }

    public static final class ListCurrentOrganization extends AbstractOperationsApiTest<Route> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            org.cloudfoundry.client.v2.routes.ListRoutesRequest request1 = org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder()
                    .organizationId(TEST_ORGANIZATION_ID)
                    .page(1)
                    .build();
            ListRoutesResponse response1 = fill(ListRoutesResponse.builder())
                    .resource(fill(RouteResource.builder(), "route-").build())
                    .build();
            when(this.cloudFoundryClient.routes().list(request1)).thenReturn(Mono.just(response1));

            GetDomainRequest request2 = GetDomainRequest.builder()
                    .id("test-route-domainId")
                    .build();
            GetDomainResponse response2 = fill(GetDomainResponse.builder(), "domain-").build();

            when(this.cloudFoundryClient.domains().get(request2)).thenReturn(Mono.just(response2));

            GetSpaceRequest request3 = GetSpaceRequest.builder()
                    .id("test-route-spaceId")
                    .build();
            GetSpaceResponse response3 = GetSpaceResponse.builder()
                    .entity(fill(SpaceEntity.builder(), "space-response-").build())
                    .build();
            when(this.cloudFoundryClient.spaces().get(request3)).thenReturn(Mono.just(response3));

            ListRouteApplicationsRequest request4 = fill(ListRouteApplicationsRequest.builder(), "route-")
                    .diego(null)
                    .build();
            ListRouteApplicationsResponse response4 = fill(ListRouteApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "application-").build())
                    .build();
            when(this.cloudFoundryClient.routes().listApplications(request4)).thenReturn(Mono.just(response4));
        }

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(Route.builder()
                            .application("test-application-name")
                            .domain("test-domain-name")
                            .host("test-route-host")
                            .routeId("test-route-id")
                            .space("test-space-response-name")
                            .build());
        }

        @Override
        protected Publisher<Route> invoke() {
            ListRoutesRequest request = ListRoutesRequest.builder()
                    .level(ListRoutesRequest.Level.ORGANIZATION)
                    .build();

            return this.routes.list(request);
        }
    }

    public static final class ListCurrentOrganizationNoOrganization extends AbstractOperationsApiTest<Route> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, MISSING_ID, MISSING_ID);

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalStateException.class);
        }

        @Override
        protected Publisher<Route> invoke() {
            ListRoutesRequest request = ListRoutesRequest.builder()
                    .level(ListRoutesRequest.Level.SPACE)
                    .build();

            return this.routes.list(request);
        }
    }

    public static final class ListCurrentSpace extends AbstractOperationsApiTest<Route> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            ListSpaceRoutesRequest request1 = fill(ListSpaceRoutesRequest.builder(), "space-")
                    .id(TEST_SPACE_ID)
                    .build();
            ListSpaceRoutesResponse response1 = fill(ListSpaceRoutesResponse.builder(), "spaceRoute-")
                    .resource(fill(RouteResource.builder(), "route-").build())
                    .build();
            when(this.cloudFoundryClient.spaces().listRoutes(request1)).thenReturn(Mono.just(response1));

            GetDomainRequest request2 = GetDomainRequest.builder()
                    .id("test-route-domainId")
                    .build();
            GetDomainResponse response2 = fill(GetDomainResponse.builder(), "domain-").build();
            when(this.cloudFoundryClient.domains().get(request2)).thenReturn(Mono.just(response2));

            GetSpaceRequest request3 = GetSpaceRequest.builder()
                    .id("test-route-spaceId")
                    .build();
            GetSpaceResponse response3 = fill(GetSpaceResponse.builder(), "space-").build();
            when(this.cloudFoundryClient.spaces().get(request3)).thenReturn(Mono.just(response3));

            ListRouteApplicationsRequest request4 = ListRouteApplicationsRequest.builder()
                    .id("test-route-id")
                    .page(1)
                    .build();
            ListRouteApplicationsResponse response4 = fill(ListRouteApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "routeApplication-").build())
                    .build();
            when(this.cloudFoundryClient.routes().listApplications(request4)).thenReturn(Mono.just(response4));
        }

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(Route.builder()
                            .routeId("test-route-id")
                            .application("test-routeApplication-name")
                            .domain("test-domain-name")
                            .host("test-route-host")
                            .space(TEST_SPACE_NAME)
                            .build());
        }

        @Override
        protected Publisher<Route> invoke() {
            ListRoutesRequest request = ListRoutesRequest.builder()
                    .level(ListRoutesRequest.Level.SPACE)
                    .build();

            return this.routes.list(request);
        }
    }

    public static final class ListCurrentSpaceNoOrganization extends AbstractOperationsApiTest<Route> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, MISSING_ID, MISSING_ID);

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalStateException.class);
        }

        @Override
        protected Publisher<Route> invoke() {
            ListRoutesRequest request = ListRoutesRequest.builder()
                    .level(ListRoutesRequest.Level.SPACE)
                    .build();

            return this.routes.list(request);
        }
    }

    public static final class ListCurrentSpaceNoSpace extends AbstractOperationsApiTest<Route> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalStateException.class);
        }

        @Override
        protected Publisher<Route> invoke() {
            ListRoutesRequest request = ListRoutesRequest.builder()
                    .level(ListRoutesRequest.Level.SPACE)
                    .build();

            return this.routes.list(request);
        }
    }

}
