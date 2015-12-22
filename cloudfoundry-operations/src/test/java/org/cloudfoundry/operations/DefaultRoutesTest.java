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
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsResponse;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.Publishers;

import java.util.Arrays;

import static org.mockito.Mockito.when;

public class DefaultRoutesTest extends AbstractOperationsTest {

    public static final class ListCurrentOrganization extends AbstractOperationsApiTest<Route> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, TEST_ORGANIZATION, TEST_SPACE);

        @Before
        public void setUp() throws Exception {
            when(this.cloudFoundryClient.routes().list(
                    org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder()
                            .organizationId("test-organization-id")
                            .page(1)
                            .build()))
                    .thenReturn(Publishers.just(
                            org.cloudfoundry.client.v2.routes.ListRoutesResponse.builder()
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
                                    .build()));

            when(this.cloudFoundryClient.domains().get(
                    GetDomainRequest.builder()
                            .id("domain-id")
                            .build()
            )).thenReturn(Publishers.just(
                    GetDomainResponse.builder()
                            .entity(DomainEntity.builder()
                                    .name("domain")
                                    .build())
                            .build()));

            when(this.cloudFoundryClient.spaces().get(
                    GetSpaceRequest.builder()
                            .id("test-space-id")
                            .build())).thenReturn(Publishers.just(
                    GetSpaceResponse.builder()
                            .entity(SpaceEntity.builder()
                                    .name("test-space")
                                    .build())
                            .build()));

            when(this.cloudFoundryClient.routes().listApplications(
                    ListRouteApplicationsRequest.builder()
                            .id("route-id")
                            .page(1)
                            .build())).thenReturn(Publishers.just(
                    ListRouteApplicationsResponse.builder()
                            .resource(ApplicationResource.builder()
                                    .entity(ApplicationEntity.builder()
                                            .name("application")
                                            .build())
                                    .build())
                            .totalPages(1)
                            .build()));
        }

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(Route.builder()
                            .routeId("route-id")
                            .applications(Arrays.asList("application"))
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

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, null, null);

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

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, TEST_ORGANIZATION, TEST_SPACE);

        @Before
        public void setUp() throws Exception {
            when(this.cloudFoundryClient.spaces().listRoutes(
                    ListSpaceRoutesRequest.builder()
                            .id("test-space-id")
                            .page(1)
                            .build()))
                    .thenReturn(Publishers.just(
                            ListSpaceRoutesResponse.builder()
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
                                    .build()));

            when(this.cloudFoundryClient.domains().get(
                    GetDomainRequest.builder()
                            .id("domain-id")
                            .build()
            )).thenReturn(Publishers.just(
                    GetDomainResponse.builder()
                            .entity(DomainEntity.builder()
                                    .name("domain")
                                    .build())
                            .build()));

            when(this.cloudFoundryClient.spaces().get(
                    GetSpaceRequest.builder()
                            .id("test-space-id")
                            .build())).thenReturn(Publishers.just(
                    GetSpaceResponse.builder()
                            .entity(SpaceEntity.builder()
                                    .name("test-space")
                                    .build())
                            .build()));

            when(this.cloudFoundryClient.routes().listApplications(
                    ListRouteApplicationsRequest.builder()
                            .id("route-id")
                            .page(1)
                            .build())).thenReturn(Publishers.just(
                    ListRouteApplicationsResponse.builder()
                            .resource(ApplicationResource.builder()
                                    .entity(ApplicationEntity.builder()
                                            .name("application")
                                            .build())
                                    .build())
                            .totalPages(1)
                            .build()));
        }

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(Route.builder()
                            .routeId("route-id")
                            .applications(Arrays.asList("application"))
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

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, null, null);

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

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, TEST_ORGANIZATION, null);

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

    public static final class ListInvalid extends AbstractOperationsTest {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, TEST_ORGANIZATION, TEST_SPACE);

        @Test(expected = IllegalArgumentException.class)
        public final void listInvalid() {
            this.routes.list(ListRoutesRequest.builder()
                    .build());
        }

    }

}
