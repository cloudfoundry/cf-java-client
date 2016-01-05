/*
 * Copyright 2013-2015 the original author or authors.
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

import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.domains.DomainEntity;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.GetDomainResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsResponse;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsResponse;
import org.cloudfoundry.client.v2.routes.ListRoutesResponse;
import org.cloudfoundry.client.v2.routes.RouteEntity;
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
import reactor.Publishers;
import reactor.rx.Streams;

import static org.mockito.Mockito.when;

public final class DefaultRoutesTest {

    public static final class CheckRouteInvalidDomain extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Streams.just(TEST_ORGANIZATION), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationPrivateDomainsRequest request1 = ListOrganizationPrivateDomainsRequest.builder()
                    .id(TEST_ORGANIZATION)
                    .name("test-domain")
                    .page(1)
                    .build();
            ListOrganizationPrivateDomainsResponse response1 = ListOrganizationPrivateDomainsResponse.builder()
                    .totalPages(1)
                    .build();
            when(this.organizations.listPrivateDomains(request1)).thenReturn(Publishers.just(response1));

            ListSharedDomainsRequest request2 = ListSharedDomainsRequest.builder()
                    .name("test-domain")
                    .page(1)
                    .build();
            ListSharedDomainsResponse response2 = ListSharedDomainsResponse.builder()
                    .totalPages(1)
                    .build();
            when(this.sharedDomains.list(request2)).thenReturn(Publishers.just(response2));
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(false);
        }

        @Override
        protected Publisher<Boolean> invoke() {
            CheckRouteRequest request = CheckRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .build();

            return this.routes.check(request);
        }

    }

    public static final class CheckRouteInvalidHost extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Streams.just(TEST_ORGANIZATION), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationPrivateDomainsRequest request1 = ListOrganizationPrivateDomainsRequest.builder()
                    .id(TEST_ORGANIZATION)
                    .name("test-domain")
                    .page(1)
                    .build();
            ListOrganizationPrivateDomainsResponse response1 = ListOrganizationPrivateDomainsResponse.builder()
                    .resource(PrivateDomainResource.builder()
                            .metadata(Resource.Metadata.builder()
                                    .id("test-domain-id")
                                    .build())
                            .build())
                    .totalPages(1)
                    .build();
            when(this.organizations.listPrivateDomains(request1)).thenReturn(Publishers.just(response1));

            RouteExistsRequest request2 = RouteExistsRequest.builder()
                    .domainId("test-domain-id")
                    .host("test-host")
                    .build();
            when(this.cloudFoundryClient.routes().exists(request2)).thenReturn(Publishers.just(false));
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(false);
        }

        @Override
        protected Publisher<Boolean> invoke() {
            CheckRouteRequest request = CheckRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .build();

            return this.routes.check(request);
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
        protected Publisher<Boolean> invoke() {
            CheckRouteRequest request = CheckRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .build();

            return this.routes.check(request);
        }

    }

    public static final class CheckRoutePrivateDomain extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Streams.just(TEST_ORGANIZATION), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationPrivateDomainsRequest request1 = ListOrganizationPrivateDomainsRequest.builder()
                    .id(TEST_ORGANIZATION)
                    .name("test-domain")
                    .page(1)
                    .build();
            ListOrganizationPrivateDomainsResponse response1 = ListOrganizationPrivateDomainsResponse.builder()
                    .resource(PrivateDomainResource.builder()
                            .metadata(Resource.Metadata.builder()
                                    .id("test-domain-id")
                                    .build())
                            .build())
                    .totalPages(1)
                    .build();
            when(this.organizations.listPrivateDomains(request1)).thenReturn(Publishers.just(response1));

            RouteExistsRequest request2 = RouteExistsRequest.builder()
                    .domainId("test-domain-id")
                    .host("test-host")
                    .build();
            when(this.cloudFoundryClient.routes().exists(request2)).thenReturn(Publishers.just(true));
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(true);
        }

        @Override
        protected Publisher<Boolean> invoke() {
            CheckRouteRequest request = CheckRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .build();

            return this.routes.check(request);
        }

    }

    public static final class CheckRouteSharedDomain extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Streams.just(TEST_ORGANIZATION), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationPrivateDomainsRequest request1 = ListOrganizationPrivateDomainsRequest.builder()
                    .id(TEST_ORGANIZATION)
                    .name("test-domain")
                    .page(1)
                    .build();
            ListOrganizationPrivateDomainsResponse response1 = ListOrganizationPrivateDomainsResponse.builder()
                    .totalPages(1)
                    .build();
            when(this.organizations.listPrivateDomains(request1)).thenReturn(Publishers.just(response1));

            ListSharedDomainsRequest request2 = ListSharedDomainsRequest.builder()
                    .name("test-domain")
                    .page(1)
                    .build();
            ListSharedDomainsResponse response2 = ListSharedDomainsResponse.builder()
                    .resource(SharedDomainResource.builder()
                            .metadata(Resource.Metadata.builder()
                                    .id("test-domain-id")
                                    .build())
                            .build())
                    .totalPages(1)
                    .build();
            when(this.sharedDomains.list(request2)).thenReturn(Publishers.just(response2));

            RouteExistsRequest request3 = RouteExistsRequest.builder()
                    .domainId("test-domain-id")
                    .host("test-host")
                    .build();
            when(this.cloudFoundryClient.routes().exists(request3)).thenReturn(Publishers.just(true));
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(true);
        }

        @Override
        protected Publisher<Boolean> invoke() {
            CheckRouteRequest request = CheckRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .build();

            return this.routes.check(request);
        }

    }

    public static final class ListCurrentOrganization extends AbstractOperationsApiTest<Route> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Streams.just(TEST_ORGANIZATION), Streams.just(TEST_SPACE));

        @Before
        public void setUp() throws Exception {
            org.cloudfoundry.client.v2.routes.ListRoutesRequest request1 = org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder()
                    .organizationId("test-organization-id")
                    .page(1)
                    .build();
            ListRoutesResponse response1 = ListRoutesResponse.builder()
                    .resource(RouteResource.builder()
                            .entity(RouteEntity.builder()
                                    .domainId("domain-id")
                                    .host("host")
                                    .spaceId("test-space-id")
                                    .build())
                            .metadata(Resource.Metadata.builder()
                                    .id("route-id")
                                    .build())
                            .build())
                    .totalPages(1)
                    .build();
            when(this.cloudFoundryClient.routes().list(request1)).thenReturn(Publishers.just(response1));

            GetDomainRequest request2 = GetDomainRequest.builder()
                    .id("domain-id")
                    .build();
            GetDomainResponse response2 = GetDomainResponse.builder()
                    .entity(DomainEntity.builder()
                            .name("domain")
                            .build())
                    .build();
            when(this.cloudFoundryClient.domains().get(request2)).thenReturn(Publishers.just(response2));

            GetSpaceRequest request3 = GetSpaceRequest.builder()
                    .id("test-space-id")
                    .build();
            GetSpaceResponse response3 = GetSpaceResponse.builder()
                    .entity(SpaceEntity.builder()
                            .name("test-space")
                            .build())
                    .build();
            when(this.cloudFoundryClient.spaces().get(request3)).thenReturn(Publishers.just(response3));

            ListRouteApplicationsRequest request4 = ListRouteApplicationsRequest.builder()
                    .id("route-id")
                    .page(1)
                    .build();
            ListRouteApplicationsResponse response4 = ListRouteApplicationsResponse.builder()
                    .resource(ApplicationResource.builder()
                            .entity(ApplicationEntity.builder()
                                    .name("application")
                                    .build())
                            .build())
                    .totalPages(1)
                    .build();
            when(this.cloudFoundryClient.routes().listApplications(request4)).thenReturn(Publishers.just(response4));
        }

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(Route.builder()
                            .routeId("route-id")
                            .application("application")
                            .domain("domain")
                            .host("host")
                            .space("test-space")
                            .build());
        }

        @Override
        protected Publisher<Route> invoke() {
            ListRoutesRequest request = ListRoutesRequest.builder()
                    .level(ListRoutesRequest.Level.Organization)
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
                    .level(ListRoutesRequest.Level.Space)
                    .build();

            return this.routes.list(request);
        }
    }

    public static final class ListCurrentSpace extends AbstractOperationsApiTest<Route> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Streams.just(TEST_ORGANIZATION), Streams.just(TEST_SPACE));

        @Before
        public void setUp() throws Exception {
            ListSpaceRoutesRequest request1 = ListSpaceRoutesRequest.builder()
                    .id("test-space-id")
                    .page(1)
                    .build();
            ListSpaceRoutesResponse response1 = ListSpaceRoutesResponse.builder()
                    .resource(RouteResource.builder()
                            .entity(RouteEntity.builder()
                                    .domainId("domain-id")
                                    .host("host")
                                    .spaceId("test-space-id")
                                    .build())
                            .metadata(Resource.Metadata.builder()
                                    .id("route-id")
                                    .build())
                            .build())
                    .totalPages(1)
                    .build();
            when(this.cloudFoundryClient.spaces().listRoutes(request1)).thenReturn(Publishers.just(response1));

            GetDomainRequest request2 = GetDomainRequest.builder()
                    .id("domain-id")
                    .build();
            GetDomainResponse response2 = GetDomainResponse.builder()
                    .entity(DomainEntity.builder()
                            .name("domain")
                            .build())
                    .build();
            when(this.cloudFoundryClient.domains().get(request2)).thenReturn(Publishers.just(response2));

            GetSpaceRequest request3 = GetSpaceRequest.builder()
                    .id("test-space-id")
                    .build();
            GetSpaceResponse response3 = GetSpaceResponse.builder()
                    .entity(SpaceEntity.builder()
                            .name("test-space")
                            .build())
                    .build();
            when(this.cloudFoundryClient.spaces().get(request3)).thenReturn(Publishers.just(response3));

            ListRouteApplicationsRequest request4 = ListRouteApplicationsRequest.builder()
                    .id("route-id")
                    .page(1)
                    .build();
            ListRouteApplicationsResponse response4 = ListRouteApplicationsResponse.builder()
                    .resource(ApplicationResource.builder()
                            .entity(ApplicationEntity.builder()
                                    .name("application")
                                    .build())
                            .build())
                    .totalPages(1)
                    .build();
            when(this.cloudFoundryClient.routes().listApplications(request4)).thenReturn(Publishers.just(response4));
        }

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(Route.builder()
                            .routeId("route-id")
                            .application("application")
                            .domain("domain")
                            .host("host")
                            .space("test-space")
                            .build());
        }

        @Override
        protected Publisher<Route> invoke() {
            ListRoutesRequest request = ListRoutesRequest.builder()
                    .level(ListRoutesRequest.Level.Space)
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
                    .level(ListRoutesRequest.Level.Space)
                    .build();

            return this.routes.list(request);
        }
    }

    public static final class ListCurrentSpaceNoSpace extends AbstractOperationsApiTest<Route> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Streams.just(TEST_ORGANIZATION), MISSING_ID);

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalStateException.class);
        }

        @Override
        protected Publisher<Route> invoke() {
            ListRoutesRequest request = ListRoutesRequest.builder()
                    .level(ListRoutesRequest.Level.Space)
                    .build();

            return this.routes.list(request);
        }
    }

}
