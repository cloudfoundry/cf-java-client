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

package org.cloudfoundry.operations.routes;

import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteResponse;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.GetDomainResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsResponse;
import org.cloudfoundry.client.v2.routes.ListRoutesResponse;
import org.cloudfoundry.client.v2.routes.RemoveRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteExistsRequest;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsResponse;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Before;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.operations.util.v2.TestObjects.fill;
import static org.cloudfoundry.operations.util.v2.TestObjects.fillPage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class DefaultRoutesTest {

    public static final class CheckRouteInvalidDomain extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationPrivateDomainsRequest request1 = fillPage(ListOrganizationPrivateDomainsRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-invalid-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response1 = fillPage(ListOrganizationPrivateDomainsResponse.builder()).build();
            when(this.organizations.listPrivateDomains(request1)).thenReturn(Mono.just(response1));

            ListSharedDomainsRequest request2 = fillPage(ListSharedDomainsRequest.builder())
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
            CheckRouteRequest request = fill(CheckRouteRequest.builder(), "invalid-")
                    .build();

            return this.routes.check(request);
        }

    }

    public static final class CheckRouteInvalidHost extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationPrivateDomainsRequest request1 = fillPage(ListOrganizationPrivateDomainsRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response1 = fillPage(ListOrganizationPrivateDomainsResponse.builder())
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
                    .path(null)
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

    public static final class CheckRoutePath extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationPrivateDomainsRequest request1 = fillPage(ListOrganizationPrivateDomainsRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response1 = fillPage(ListOrganizationPrivateDomainsResponse.builder(), "privateDomain-")
                    .resource(fill(PrivateDomainResource.builder(), "privateDomain-").build())
                    .build();
            when(this.organizations.listPrivateDomains(request1)).thenReturn(Mono.just(response1));

            RouteExistsRequest request2 = fill(RouteExistsRequest.builder())
                    .domainId("test-privateDomain-id")
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
            return this.routes.check(fill(CheckRouteRequest.builder())
                    .path("test-path")
                    .build());
        }

    }

    public static final class CheckRoutePrivateDomain extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationPrivateDomainsRequest request1 = fillPage(ListOrganizationPrivateDomainsRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response1 = fillPage(ListOrganizationPrivateDomainsResponse.builder(), "privateDomain-")
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
            return this.routes.check(fill(CheckRouteRequest.builder())
                    .path(null)
                    .build());
        }

    }

    public static final class CheckRouteSharedDomain extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationPrivateDomainsRequest request1 = fillPage(ListOrganizationPrivateDomainsRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response1 = ListOrganizationPrivateDomainsResponse.builder()
                    .totalPages(1)
                    .build();
            when(this.organizations.listPrivateDomains(request1)).thenReturn(Mono.just(response1));

            ListSharedDomainsRequest request2 = fillPage(ListSharedDomainsRequest.builder())
                    .name("test-domain")
                    .build();
            ListSharedDomainsResponse response2 = fillPage(ListSharedDomainsResponse.builder(), "sharedDomains-")
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
            return this.routes.check(fill(CheckRouteRequest.builder())
                    .path(null)
                    .build());
        }

    }

    public static final class CreateRouteInvalidDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationSpacesRequest request0 = fillPage(ListOrganizationSpacesRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name(TEST_SPACE_NAME)
                    .build();
            ListOrganizationSpacesResponse response0 = fillPage(ListOrganizationSpacesResponse.builder())
                    .resource(fill(SpaceResource.builder(), "orgSpace-").build())
                    .build();
            when(this.organizations.listSpaces(request0)).thenReturn(Mono.just(response0));

            ListOrganizationPrivateDomainsRequest request1 = fillPage(ListOrganizationPrivateDomainsRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-invalid-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response1 = fillPage(ListOrganizationPrivateDomainsResponse.builder()).build();
            when(this.organizations.listPrivateDomains(request1)).thenReturn(Mono.just(response1));

            ListSharedDomainsRequest request2 = fillPage(ListSharedDomainsRequest.builder())
                    .name("test-invalid-domain")
                    .build();
            ListSharedDomainsResponse response2 = fillPage(ListSharedDomainsResponse.builder()).build();
            when(this.sharedDomains.list(request2)).thenReturn(Mono.just(response2));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            CreateRouteRequest request = CreateRouteRequest.builder()
                    .domain("test-invalid-domain")
                    .host("test-any-host")
                    .space(TEST_SPACE_NAME)
                    .build();

            return this.routes.create(request);
        }

    }

    public static final class CreateRouteInvalidHost extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationSpacesRequest request0 = fillPage(ListOrganizationSpacesRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-routeSpace-name")
                    .build();
            ListOrganizationSpacesResponse response0 = fillPage(ListOrganizationSpacesResponse.builder())
                    .resource(fill(SpaceResource.builder(), "orgSpace-").build())
                    .build();
            when(this.organizations.listSpaces(request0)).thenReturn(Mono.just(response0));

            ListOrganizationPrivateDomainsRequest request1 = fillPage(ListOrganizationPrivateDomainsRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response1 = fillPage(ListOrganizationPrivateDomainsResponse.builder())
                    .resource(fill(PrivateDomainResource.builder(), "privateDomain-").build())
                    .build();
            when(this.organizations.listPrivateDomains(request1)).thenReturn(Mono.just(response1));

            org.cloudfoundry.client.v2.routes.CreateRouteRequest request2 = org.cloudfoundry.client.v2.routes.CreateRouteRequest.builder()
                    .domainId("test-privateDomain-id")
                    .host("test-invalid-host")
                    .spaceId("test-orgSpace-id")
                    .build();
            when(this.cloudFoundryClient.routes().create(request2)).thenThrow(new CloudFoundryException(-1, "", ""));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(CloudFoundryException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            CreateRouteRequest request = CreateRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-invalid-host")
                    .space("test-routeSpace-name")
                    .build();

            return this.routes.create(request);
        }

    }

    public static final class CreateRouteInvalidSpace extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationSpacesRequest request0 = fillPage(ListOrganizationSpacesRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-routeSpace-name")
                    .build();
            ListOrganizationSpacesResponse response0 = fillPage(ListOrganizationSpacesResponse.builder()).build();
            when(this.organizations.listSpaces(request0)).thenReturn(Mono.just(response0));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            CreateRouteRequest request = CreateRouteRequest.builder()
                    .domain("test-any-domain")
                    .host("test-any-host")
                    .space("test-routeSpace-name")
                    .build();

            return this.routes.create(request);
        }

    }

    public static final class CreateRouteNoOrganization extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, MISSING_ID, MISSING_ID);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalStateException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            CreateRouteRequest request = fill(CreateRouteRequest.builder(), "any-").build();

            return this.routes.create(request);
        }

    }

    public static final class CreateRoutePath extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationSpacesRequest request0 = fillPage(ListOrganizationSpacesRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-specific-space")
                    .build();
            ListOrganizationSpacesResponse response0 = fillPage(ListOrganizationSpacesResponse.builder())
                    .resource(fill(SpaceResource.builder(), "ourSpace-").build())
                    .build();
            when(this.organizations.listSpaces(request0)).thenReturn(Mono.just(response0));

            ListOrganizationPrivateDomainsRequest request1 = fillPage(ListOrganizationPrivateDomainsRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-specific-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response1 = fillPage(ListOrganizationPrivateDomainsResponse.builder()).build();
            when(this.organizations.listPrivateDomains(request1)).thenReturn(Mono.just(response1));

            ListSharedDomainsRequest request2 = fillPage(ListSharedDomainsRequest.builder())
                    .name("test-specific-domain")
                    .build();
            ListSharedDomainsResponse response2 = fillPage(ListSharedDomainsResponse.builder())
                    .resource(fill(SharedDomainResource.builder(), "specificDomain-").build())
                    .build();
            when(this.sharedDomains.list(request2)).thenReturn(Mono.just(response2));

            org.cloudfoundry.client.v2.routes.CreateRouteRequest request3 = org.cloudfoundry.client.v2.routes.CreateRouteRequest.builder()
                    .domainId("test-specificDomain-id")
                    .host("test-specific-host")
                    .path("test-specific-path")
                    .spaceId("test-ourSpace-id")
                    .build();
            CreateRouteResponse response3 = CreateRouteResponse.builder().build();
            when(this.cloudFoundryClient.routes().create(request3)).thenReturn(Mono.just(response3));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            CreateRouteRequest request = fill(CreateRouteRequest.builder(), "specific-")
                    .path("test-specific-path")
                    .build();

            return this.routes.create(request);
        }

    }

    public static final class CreateRoutePrivateDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            ListOrganizationSpacesRequest request0 = fillPage(ListOrganizationSpacesRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-specific-space")
                    .build();
            ListOrganizationSpacesResponse response0 = fillPage(ListOrganizationSpacesResponse.builder())
                    .resource(fill(SpaceResource.builder(), "specific-space-").build())
                    .build();
            when(this.organizations.listSpaces(request0)).thenReturn(Mono.just(response0));

            ListOrganizationPrivateDomainsRequest request1 = fillPage(ListOrganizationPrivateDomainsRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-specific-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response1 = fillPage(ListOrganizationPrivateDomainsResponse.builder())
                    .resource(fill(PrivateDomainResource.builder(), "private-").build())
                    .build();
            when(this.organizations.listPrivateDomains(request1)).thenReturn(Mono.just(response1));

            org.cloudfoundry.client.v2.routes.CreateRouteRequest request2 = org.cloudfoundry.client.v2.routes.CreateRouteRequest.builder()
                    .domainId("test-private-id")
                    .host("test-specific-host")
                    .spaceId("test-specific-space-id")
                    .build();
            CreateRouteResponse response2 = CreateRouteResponse.builder().build();
            when(this.cloudFoundryClient.routes().create(request2)).thenReturn(Mono.just(response2));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            CreateRouteRequest request = fill(CreateRouteRequest.builder(), "specific-")
                    .path(null)
                    .build();

            return this.routes.create(request);
        }

    }

    public static final class DeleteOrphanedRoutesAbortsOnError extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            ListSpaceRoutesRequest request1 = fillPage(ListSpaceRoutesRequest.builder())
                    .spaceId("test-space-id")
                    .build();
            ListSpaceRoutesResponse response1 = fillPage(ListSpaceRoutesResponse.builder())
                    .resource(fill(RouteResource.builder(), "route1-").build())
                    .resource(fill(RouteResource.builder(), "route2-").build())
                    .build();
            when(this.cloudFoundryClient.spaces().listRoutes(request1)).thenReturn(Mono.just(response1));

            ListRouteApplicationsRequest request2 = fillPage(ListRouteApplicationsRequest.builder())
                    .diego(null)
                    .routeId("test-route1-id")
                    .spaceId("test-route1-spaceId")
                    .build();
            ListRouteApplicationsResponse response2 = fillPage(ListRouteApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "application1-")
                            .build())
                    .build();
            when(this.cloudFoundryClient.routes().listApplications(request2)).thenReturn(Mono.just(response2));

            ListRouteApplicationsRequest request3 = fillPage(ListRouteApplicationsRequest.builder())
                    .diego(null)
                    .routeId("test-route2-id")
                    .spaceId("test-route2-spaceId")
                    .build();
            ListRouteApplicationsResponse response3 = fillPage(ListRouteApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "application2-")
                            .build())
                    .build();
            when(this.cloudFoundryClient.routes().listApplications(request3)).thenReturn(Mono.just(response3));

            DeleteRouteRequest request4 = fill(DeleteRouteRequest.builder())
                    .async(null)
                    .routeId("test-route1-id")
                    .build();
            when(this.cloudFoundryClient.routes().delete(request4)).thenReturn(Mono.<Void>error(new IllegalStateException("failure")));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber.assertError(IllegalStateException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.routes.deleteOrphanedRoutes();
        }

        @Override
        protected void extraVerifications() throws Exception {
            verify(this.cloudFoundryClient.routes(), Mockito.times(1)).delete(Mockito.any(DeleteRouteRequest.class));
        }

    }

    public static final class DeleteOrphanedRoutesAssociatedApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            ListSpaceRoutesRequest request1 = fillPage(ListSpaceRoutesRequest.builder())
                    .spaceId("test-space-id")
                    .build();
            ListSpaceRoutesResponse response1 = fillPage(ListSpaceRoutesResponse.builder())
                    .resource(fill(RouteResource.builder(), "route-").build())
                    .build();
            when(this.cloudFoundryClient.spaces().listRoutes(request1)).thenReturn(Mono.just(response1));

            ListRouteApplicationsRequest request2 = fillPage(ListRouteApplicationsRequest.builder())
                    .diego(null)
                    .routeId("test-route-id")
                    .spaceId("test-route-spaceId")
                    .build();
            ListRouteApplicationsResponse response2 = fillPage(ListRouteApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder())
                            .build())
                    .build();
            when(this.cloudFoundryClient.routes().listApplications(request2)).thenReturn(Mono.just(response2));

            DeleteRouteRequest request3 = fill(DeleteRouteRequest.builder())
                    .async(null)
                    .routeId("test-route-id")
                    .build();
            when(this.cloudFoundryClient.routes().delete(request3)).thenReturn(Mono.<Void>empty());
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.routes.deleteOrphanedRoutes();
        }

    }

    public static final class DeleteOrphanedRoutesNoAssociatedApplications extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            ListSpaceRoutesRequest request1 = fillPage(ListSpaceRoutesRequest.builder())
                    .spaceId("test-space-id")
                    .build();
            ListSpaceRoutesResponse response1 = fillPage(ListSpaceRoutesResponse.builder())
                    .resource(fill(RouteResource.builder(), "route-").build())
                    .build();
            when(this.cloudFoundryClient.spaces().listRoutes(request1)).thenReturn(Mono.just(response1));

            ListRouteApplicationsRequest request2 = fillPage(ListRouteApplicationsRequest.builder())
                    .diego(null)
                    .routeId("test-route-id")
                    .spaceId("test-route-spaceId")
                    .build();
            ListRouteApplicationsResponse response2 = fillPage(ListRouteApplicationsResponse.builder())
                    .build();
            when(this.cloudFoundryClient.routes().listApplications(request2)).thenReturn(Mono.just(response2));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.routes.deleteOrphanedRoutes();
        }

    }

    public static final class DeleteOrphanedRoutesNoRoutes extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            ListSpaceRoutesRequest request1 = fillPage(ListSpaceRoutesRequest.builder())
                    .spaceId("test-space-id")
                    .build();
            ListSpaceRoutesResponse response1 = fillPage(ListSpaceRoutesResponse.builder())
                    .build();
            when(this.cloudFoundryClient.spaces().listRoutes(request1)).thenReturn(Mono.just(response1));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.routes.deleteOrphanedRoutes();
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
            ListRoutesResponse response1 = fillPage(ListRoutesResponse.builder())
                    .resource(fill(RouteResource.builder(), "route-")
                            .entity(fill(RouteEntity.builder(), "routeEntity-").build())
                            .build())
                    .build();
            when(this.cloudFoundryClient.routes().list(request1)).thenReturn(Mono.just(response1));

            GetDomainRequest request2 = GetDomainRequest.builder()
                    .domainId("test-routeEntity-domainId")
                    .build();
            GetDomainResponse response2 = fill(GetDomainResponse.builder(), "domain-").build();

            when(this.cloudFoundryClient.domains().get(request2)).thenReturn(Mono.just(response2));

            GetSpaceRequest request3 = GetSpaceRequest.builder()
                    .spaceId("test-routeEntity-spaceId")
                    .build();
            GetSpaceResponse response3 = GetSpaceResponse.builder()
                    .entity(fill(SpaceEntity.builder(), "space-response-").build())
                    .build();
            when(this.cloudFoundryClient.spaces().get(request3)).thenReturn(Mono.just(response3));

            ListRouteApplicationsRequest request4 = fillPage(ListRouteApplicationsRequest.builder(), "route-")
                    .routeId("test-route-id")
                    .diego(null)
                    .build();
            ListRouteApplicationsResponse response4 = fillPage(ListRouteApplicationsResponse.builder())
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
                            .host("test-routeEntity-host")
                            .path("test-routeEntity-path")
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
            ListSpaceRoutesRequest request1 = fillPage(ListSpaceRoutesRequest.builder(), "space-")
                    .spaceId(TEST_SPACE_ID)
                    .build();
            ListSpaceRoutesResponse response1 = fillPage(ListSpaceRoutesResponse.builder(), "spaceRoute-")
                    .resource(fill(RouteResource.builder(), "route-").build())
                    .build();
            when(this.cloudFoundryClient.spaces().listRoutes(request1)).thenReturn(Mono.just(response1));

            GetDomainRequest request2 = GetDomainRequest.builder()
                    .domainId("test-route-domainId")
                    .build();
            GetDomainResponse response2 = fill(GetDomainResponse.builder(), "domain-").build();
            when(this.cloudFoundryClient.domains().get(request2)).thenReturn(Mono.just(response2));

            GetSpaceRequest request3 = GetSpaceRequest.builder()
                    .spaceId("test-route-spaceId")
                    .build();
            GetSpaceResponse response3 = fill(GetSpaceResponse.builder(), "space-").build();
            when(this.cloudFoundryClient.spaces().get(request3)).thenReturn(Mono.just(response3));

            ListRouteApplicationsRequest request4 = ListRouteApplicationsRequest.builder()
                    .routeId("test-route-id")
                    .page(1)
                    .build();
            ListRouteApplicationsResponse response4 = fillPage(ListRouteApplicationsResponse.builder())
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
                            .path("test-route-path")
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

    public static final class MapRouteInvalidApplicationName extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            ListSpaceApplicationsRequest request1 = fillPage(ListSpaceApplicationsRequest.builder())
                    .diego(null)
                    .name("test-applicationName")
                    .spaceId("test-space-id")
                    .build();
            ListSpaceApplicationsResponse response1 = fillPage(ListSpaceApplicationsResponse.builder())
                    .build();
            when(this.spaces.listApplications(request1)).thenReturn(Mono.just(response1));

            ListOrganizationPrivateDomainsRequest request2 = fillPage(ListOrganizationPrivateDomainsRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response2 = fillPage(ListOrganizationPrivateDomainsResponse.builder(), "privateDomain-")
                    .resource(fill(PrivateDomainResource.builder(), "privateDomain-").build())
                    .build();
            when(this.organizations.listPrivateDomains(request2)).thenReturn(Mono.just(response2));

            org.cloudfoundry.client.v2.routes.ListRoutesRequest request3 = fillPage(org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder())
                    .domainId("test-privateDomain-id")
                    .host("test-host")
                    .organizationId(null)
                    .build();
            ListRoutesResponse response3 = fillPage(ListRoutesResponse.builder())
                    .resource(fill(RouteResource.builder(), "route-").build())
                    .build();
            when(super.routes.list(request3)).thenReturn(Mono.just(response3));

            org.cloudfoundry.client.v2.routes.CreateRouteRequest request4 = org.cloudfoundry.client.v2.routes.CreateRouteRequest.builder()
                    .domainId("test-privateDomain-id")
                    .host("test-host")
                    .path("test-path")
                    .spaceId("test-space-id")
                    .build();
            CreateRouteResponse response4 = fill(CreateRouteResponse.builder()).build();
            when(this.cloudFoundryClient.routes().create(request4)).thenReturn(Mono.just(response4));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            MapRouteRequest request = fill(MapRouteRequest.builder())
                    .build();

            return this.routes.map(request);
        }
    }

    public static final class MapRouteInvalidDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            ListSpaceApplicationsRequest request1 = fillPage(ListSpaceApplicationsRequest.builder())
                    .diego(null)
                    .name("test-applicationName")
                    .spaceId("test-space-id")
                    .build();
            ListSpaceApplicationsResponse response1 = fillPage(ListSpaceApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "application-")
                            .build())
                    .build();
            when(this.spaces.listApplications(request1)).thenReturn(Mono.just(response1));

            ListOrganizationPrivateDomainsRequest request2 = fillPage(ListOrganizationPrivateDomainsRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response2 = fillPage(ListOrganizationPrivateDomainsResponse.builder(), "privateDomain-")
                    .build();
            when(this.organizations.listPrivateDomains(request2)).thenReturn(Mono.just(response2));

            ListSharedDomainsRequest request3 = fillPage(ListSharedDomainsRequest.builder())
                    .name("test-domain")
                    .build();
            ListSharedDomainsResponse response3 = fillPage(ListSharedDomainsResponse.builder(), "sharedDomains-")
                    .build();
            when(this.sharedDomains.list(request3)).thenReturn(Mono.just(response3));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            MapRouteRequest request = fill(MapRouteRequest.builder())
                    .build();

            return this.routes.map(request);
        }
    }

    public static final class MapRouteNoOrganization extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, MISSING_ID, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            ListSpaceApplicationsRequest request1 = fillPage(ListSpaceApplicationsRequest.builder())
                    .diego(null)
                    .name("test-applicationName")
                    .spaceId("test-space-id")
                    .build();
            ListSpaceApplicationsResponse response1 = fillPage(ListSpaceApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder())
                            .build())
                    .build();
            when(this.spaces.listApplications(request1)).thenReturn(Mono.just(response1));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalStateException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            MapRouteRequest request = fill(MapRouteRequest.builder())
                    .build();

            return this.routes.map(request);
        }
    }

    public static final class MapRouteNoSpace extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalStateException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            MapRouteRequest request = fill(MapRouteRequest.builder())
                    .build();

            return this.routes.map(request);
        }
    }

    public static final class MapRoutePrivateDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            ListSpaceApplicationsRequest request1 = fillPage(ListSpaceApplicationsRequest.builder())
                    .diego(null)
                    .name("test-applicationName")
                    .spaceId("test-space-id")
                    .build();
            ListSpaceApplicationsResponse response1 = fillPage(ListSpaceApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder())
                            .build())
                    .build();
            when(this.spaces.listApplications(request1)).thenReturn(Mono.just(response1));

            ListOrganizationPrivateDomainsRequest request2 = fillPage(ListOrganizationPrivateDomainsRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response2 = fillPage(ListOrganizationPrivateDomainsResponse.builder(), "privateDomain-")
                    .resource(fill(PrivateDomainResource.builder(), "privateDomain-").build())
                    .build();
            when(this.organizations.listPrivateDomains(request2)).thenReturn(Mono.just(response2));

            org.cloudfoundry.client.v2.routes.CreateRouteRequest request3 = fill(org.cloudfoundry.client.v2.routes.CreateRouteRequest.builder())
                    .domainId("test-privateDomain-id")
                    .generatePort(null)
                    .port(null)
                    .spaceId(TEST_SPACE_ID)
                    .build();
            CreateRouteResponse response3 = fill(CreateRouteResponse.builder(), "createdRoute-")
                    .build();
            when(super.routes.create(request3)).thenReturn(Mono.just(response3));

            AssociateApplicationRouteRequest request4 = fill(AssociateApplicationRouteRequest.builder())
                    .applicationId("test-id")
                    .routeId("test-createdRoute-id")
                    .build();
            AssociateApplicationRouteResponse response4 = fill(AssociateApplicationRouteResponse.builder())
                    .build();
            when(this.applications.associateRoute(request4)).thenReturn(Mono.just(response4));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            MapRouteRequest request = fill(MapRouteRequest.builder())
                    .build();

            return this.routes.map(request);
        }
    }

    public static final class MapRouteSharedDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            ListSpaceApplicationsRequest request1 = fillPage(ListSpaceApplicationsRequest.builder())
                    .diego(null)
                    .name("test-applicationName")
                    .spaceId("test-space-id")
                    .build();
            ListSpaceApplicationsResponse response1 = fillPage(ListSpaceApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder())
                            .build())
                    .build();
            when(this.spaces.listApplications(request1)).thenReturn(Mono.just(response1));

            ListOrganizationPrivateDomainsRequest request2 = fillPage(ListOrganizationPrivateDomainsRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response2 = fillPage(ListOrganizationPrivateDomainsResponse.builder(), "privateDomain-")
                    .build();
            when(this.organizations.listPrivateDomains(request2)).thenReturn(Mono.just(response2));

            ListSharedDomainsRequest request3 = fillPage(ListSharedDomainsRequest.builder())
                    .name("test-domain")
                    .build();
            ListSharedDomainsResponse response3 = fillPage(ListSharedDomainsResponse.builder(), "sharedDomains-")
                    .resource(fill(SharedDomainResource.builder(), "sharedDomain-").build())
                    .build();
            when(this.sharedDomains.list(request3)).thenReturn(Mono.just(response3));

            org.cloudfoundry.client.v2.routes.CreateRouteRequest request4 = fill(org.cloudfoundry.client.v2.routes.CreateRouteRequest.builder())
                    .domainId("test-sharedDomain-id")
                    .generatePort(null)
                    .port(null)
                    .spaceId(TEST_SPACE_ID)
                    .build();
            CreateRouteResponse response4 = fill(CreateRouteResponse.builder(), "createdRoute-")
                    .build();
            when(super.routes.create(request4)).thenReturn(Mono.just(response4));

            AssociateApplicationRouteRequest request5 = fill(AssociateApplicationRouteRequest.builder())
                    .applicationId("test-id")
                    .routeId("test-createdRoute-id")
                    .build();
            AssociateApplicationRouteResponse response5 = fill(AssociateApplicationRouteResponse.builder())
                    .build();
            when(this.applications.associateRoute(request5)).thenReturn(Mono.just(response5));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            MapRouteRequest request = fill(MapRouteRequest.builder())
                    .build();

            return this.routes.map(request);
        }
    }

    public static final class UnmapRouteInvalidApplicationName extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            ListSpaceApplicationsRequest request1 = fillPage(ListSpaceApplicationsRequest.builder())
                    .diego(null)
                    .name("test-applicationName")
                    .spaceId("test-space-id")
                    .build();
            ListSpaceApplicationsResponse response1 = fillPage(ListSpaceApplicationsResponse.builder())
                    .build();
            when(this.spaces.listApplications(request1)).thenReturn(Mono.just(response1));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            UnmapRouteRequest request = fill(UnmapRouteRequest.builder())
                    .build();

            return this.routes.unmap(request);
        }
    }

    public static final class UnmapRouteInvalidDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            ListSpaceApplicationsRequest request1 = fillPage(ListSpaceApplicationsRequest.builder())
                    .diego(null)
                    .name("test-applicationName")
                    .spaceId("test-space-id")
                    .build();
            ListSpaceApplicationsResponse response1 = fillPage(ListSpaceApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "application-")
                            .build())
                    .build();
            when(this.spaces.listApplications(request1)).thenReturn(Mono.just(response1));

            ListOrganizationPrivateDomainsRequest request2 = fillPage(ListOrganizationPrivateDomainsRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response2 = fillPage(ListOrganizationPrivateDomainsResponse.builder(), "privateDomain-")
                    .build();
            when(this.organizations.listPrivateDomains(request2)).thenReturn(Mono.just(response2));

            ListSharedDomainsRequest request3 = fillPage(ListSharedDomainsRequest.builder())
                    .name("test-domain")
                    .build();
            ListSharedDomainsResponse response3 = fillPage(ListSharedDomainsResponse.builder(), "sharedDomains-")
                    .build();
            when(this.sharedDomains.list(request3)).thenReturn(Mono.just(response3));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            UnmapRouteRequest request = fill(UnmapRouteRequest.builder())
                    .build();

            return this.routes.unmap(request);
        }
    }

    public static final class UnmapRouteNoOrganization extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, MISSING_ID, Mono.just(TEST_SPACE_ID));

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalStateException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            UnmapRouteRequest request = fill(UnmapRouteRequest.builder())
                    .build();

            return this.routes.unmap(request);
        }
    }

    public static final class UnmapRouteNoSpace extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalStateException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            UnmapRouteRequest request = fill(UnmapRouteRequest.builder())
                    .build();

            return this.routes.unmap(request);
        }
    }

    public static final class UnmapRouteInvalidRoute extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            ListSpaceApplicationsRequest request1 = fillPage(ListSpaceApplicationsRequest.builder())
                    .diego(null)
                    .name("test-applicationName")
                    .spaceId("test-space-id")
                    .build();
            ListSpaceApplicationsResponse response1 = fillPage(ListSpaceApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "application-")
                            .build())
                    .build();
            when(this.spaces.listApplications(request1)).thenReturn(Mono.just(response1));

            ListOrganizationPrivateDomainsRequest request2 = fillPage(ListOrganizationPrivateDomainsRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response2 = fillPage(ListOrganizationPrivateDomainsResponse.builder(), "privateDomain-")
                    .resource(fill(PrivateDomainResource.builder(), "privateDomain-").build())
                    .build();
            when(this.organizations.listPrivateDomains(request2)).thenReturn(Mono.just(response2));

            org.cloudfoundry.client.v2.routes.ListRoutesRequest request3 = fillPage(org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder())
                    .domainId("test-privateDomain-id")
                    .host("test-host")
                    .organizationId(null)
                    .build();
            ListRoutesResponse response3 = fillPage(ListRoutesResponse.builder())
                    .build();
            when(super.routes.list(request3)).thenReturn(Mono.just(response3));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber.assertError(IllegalArgumentException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            UnmapRouteRequest request = fill(UnmapRouteRequest.builder())
                    .build();

            return this.routes.unmap(request);
        }
    }


    public static final class UnmapRoutePrivateDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            ListSpaceApplicationsRequest request1 = fillPage(ListSpaceApplicationsRequest.builder())
                    .diego(null)
                    .name("test-applicationName")
                    .spaceId("test-space-id")
                    .build();
            ListSpaceApplicationsResponse response1 = fillPage(ListSpaceApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "application-")
                            .build())
                    .build();
            when(this.spaces.listApplications(request1)).thenReturn(Mono.just(response1));

            ListOrganizationPrivateDomainsRequest request2 = fillPage(ListOrganizationPrivateDomainsRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response2 = fillPage(ListOrganizationPrivateDomainsResponse.builder(), "privateDomain-")
                    .resource(fill(PrivateDomainResource.builder(), "privateDomain-").build())
                    .build();
            when(this.organizations.listPrivateDomains(request2)).thenReturn(Mono.just(response2));

            org.cloudfoundry.client.v2.routes.ListRoutesRequest request3 = fillPage(org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder())
                    .domainId("test-privateDomain-id")
                    .host("test-host")
                    .organizationId(null)
                    .build();
            ListRoutesResponse response3 = fillPage(ListRoutesResponse.builder())
                    .resource(fill(RouteResource.builder(), "route-").build())
                    .build();
            when(super.routes.list(request3)).thenReturn(Mono.just(response3));

            RemoveRouteApplicationRequest request4 = fill(RemoveRouteApplicationRequest.builder())
                    .applicationId("test-application-id")
                    .routeId("test-route-id")
                    .build();
            when(super.routes.removeApplication(request4)).thenReturn(Mono.<Void>empty());
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            UnmapRouteRequest request = fill(UnmapRouteRequest.builder())
                    .build();

            return this.routes.unmap(request);
        }
    }

    public static final class UnmapRouteSharedDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            ListSpaceApplicationsRequest request1 = fillPage(ListSpaceApplicationsRequest.builder())
                    .diego(null)
                    .name("test-applicationName")
                    .spaceId("test-space-id")
                    .build();
            ListSpaceApplicationsResponse response1 = fillPage(ListSpaceApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "application-")
                            .build())
                    .build();
            when(this.spaces.listApplications(request1)).thenReturn(Mono.just(response1));

            ListOrganizationPrivateDomainsRequest request2 = fillPage(ListOrganizationPrivateDomainsRequest.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .name("test-domain")
                    .build();
            ListOrganizationPrivateDomainsResponse response2 = fillPage(ListOrganizationPrivateDomainsResponse.builder(), "privateDomain-")
                    .build();
            when(this.organizations.listPrivateDomains(request2)).thenReturn(Mono.just(response2));

            ListSharedDomainsRequest request3 = fillPage(ListSharedDomainsRequest.builder())
                    .name("test-domain")
                    .build();
            ListSharedDomainsResponse response3 = fillPage(ListSharedDomainsResponse.builder(), "sharedDomains-")
                    .resource(fill(SharedDomainResource.builder(), "sharedDomain-").build())
                    .build();
            when(this.sharedDomains.list(request3)).thenReturn(Mono.just(response3));

            org.cloudfoundry.client.v2.routes.ListRoutesRequest request4 = fillPage(org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder())
                    .domainId("test-sharedDomain-id")
                    .host("test-host")
                    .organizationId(null)
                    .build();
            ListRoutesResponse response4 = fillPage(ListRoutesResponse.builder())
                    .resource(fill(RouteResource.builder(), "route-").build())
                    .build();
            when(super.routes.list(request4)).thenReturn(Mono.just(response4));

            RemoveRouteApplicationRequest request5 = fill(RemoveRouteApplicationRequest.builder())
                    .applicationId("test-application-id")
                    .routeId("test-route-id")
                    .build();
            when(super.routes.removeApplication(request5)).thenReturn(Mono.<Void>empty());
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            UnmapRouteRequest request = fill(UnmapRouteRequest.builder())
                    .build();

            return this.routes.unmap(request);
        }
    }

}
