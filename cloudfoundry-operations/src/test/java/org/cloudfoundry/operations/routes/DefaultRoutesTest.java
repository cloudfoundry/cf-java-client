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

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteResponse;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.GetDomainResponse;
import org.cloudfoundry.client.v2.job.GetJobRequest;
import org.cloudfoundry.client.v2.job.GetJobResponse;
import org.cloudfoundry.client.v2.job.JobEntity;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.DeleteRouteResponse;
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
import org.cloudfoundry.util.RequestValidationException;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.cloudfoundry.util.test.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultRoutesTest {

    private static void requestApplications(CloudFoundryClient cloudFoundryClient, final String routeId) {
        when(cloudFoundryClient.routes()
            .listApplications(fillPage(ListRouteApplicationsRequest.builder())
                .diego(null)
                .routeId(routeId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListRouteApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "application-")
                        .build())
                    .build()));
    }

    private static void requestApplications(CloudFoundryClient cloudFoundryClient, final String application, final String spaceId) {
        when(cloudFoundryClient.spaces()
            .listApplications(fillPage(ListSpaceApplicationsRequest.builder())
                .diego(null)
                .name(application)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "application-")
                        .build())
                    .build()));
    }

    private static void requestApplicationsEmpty(CloudFoundryClient cloudFoundryClient, final String routeId) {
        when(cloudFoundryClient.routes()
            .listApplications(fillPage(ListRouteApplicationsRequest.builder())
                .diego(null)
                .routeId(routeId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListRouteApplicationsResponse.builder())
                    .build()));
    }

    private static void requestApplicationsEmpty(CloudFoundryClient cloudFoundryClient, final String application, final String spaceId) {
        when(cloudFoundryClient.spaces()
            .listApplications(fillPage(ListSpaceApplicationsRequest.builder())
                .diego(null)
                .name(application)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceApplicationsResponse.builder())
                    .build()));
    }

    private static void requestAssociateRoute(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        when(cloudFoundryClient.applicationsV2()
            .associateRoute(AssociateApplicationRouteRequest.builder()
                .applicationId(applicationId)
                .routeId(routeId)
                .build()))
            .thenReturn(Mono
                .just(fill(AssociateApplicationRouteResponse.builder())
                    .build()));
    }

    private static void requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, String host, String path, String spaceId) {
        when(cloudFoundryClient.routes()
            .create(org.cloudfoundry.client.v2.routes.CreateRouteRequest.builder()
                .domainId(domainId)
                .host(host)
                .path(path)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateRouteResponse.builder(), "route-")
                    .build()));
    }

    private static void requestDeleteRoute(CloudFoundryClient cloudFoundryClient, String routeId) {
        when(cloudFoundryClient.routes()
            .delete(org.cloudfoundry.client.v2.routes.DeleteRouteRequest.builder()
                .async(true)
                .routeId(routeId)
                .build()))
            .thenReturn(Mono
                .just(fill(DeleteRouteResponse.builder())
                    .entity(fill(JobEntity.builder(), "job-entity-")
                        .build())
                    .build()));
    }

    private static void requestDomain(CloudFoundryClient cloudFoundryClient, String domainId) {
        when(cloudFoundryClient.domains()
            .get(GetDomainRequest.builder()
                .domainId(domainId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetDomainResponse.builder(), "domain-")
                    .build()));
    }

    private static void requestJobFailure(CloudFoundryClient cloudFoundryClient, String jobId) {
        when(cloudFoundryClient.jobs()
            .get(GetJobRequest.builder()
                .jobId(jobId)
                .build()))
            .thenReturn(Mono
                .defer(new Supplier<Mono<GetJobResponse>>() {

                    private final Queue<GetJobResponse> responses = new LinkedList<>(Arrays.asList(
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("running")
                                .build())
                            .build(),
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .errorDetails(fill(JobEntity.ErrorDetails.builder(), "error-details-")
                                    .build())
                                .status("failed")
                                .build())
                            .build()
                    ));

                    @Override
                    public Mono<GetJobResponse> get() {
                        return Mono.just(responses.poll());
                    }

                }));
    }

    private static void requestJobSuccess(CloudFoundryClient cloudFoundryClient, String jobId) {
        when(cloudFoundryClient.jobs()
            .get(GetJobRequest.builder()
                .jobId(jobId)
                .build()))
            .thenReturn(Mono
                .defer(new Supplier<Mono<GetJobResponse>>() {

                    private final Queue<GetJobResponse> responses = new LinkedList<>(Arrays.asList(
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("running")
                                .build())
                            .build(),
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("finished")
                                .build())
                            .build()
                    ));

                    @Override
                    public Mono<GetJobResponse> get() {
                        return Mono.just(responses.poll());
                    }

                }));
    }

    private static void requestOrganizationsRoutes(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.routes()
            .list(fillPage(org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder())
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListRoutesResponse.builder())
                    .resource(fill(RouteResource.builder())
                        .entity(fill(RouteEntity.builder(), "route-entity-").build())
                        .build())
                    .build()));
    }

    private static void requestOrganizationsRoutesEmpty(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.routes()
            .list(fillPage(org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder())
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListRoutesResponse.builder())
                    .build()));
    }

    private static void requestPrivateDomains(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        when(cloudFoundryClient.organizations()
            .listPrivateDomains(fillPage(ListOrganizationPrivateDomainsRequest.builder())
                .organizationId(organizationId)
                .name(domain)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationPrivateDomainsResponse.builder())
                    .resource(fill(PrivateDomainResource.builder(), "private-domain-")
                        .metadata(fill(Resource.Metadata.builder(), "private-domain-metadata-")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestPrivateDomainsEmpty(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        when(cloudFoundryClient.organizations()
            .listPrivateDomains(fillPage(ListOrganizationPrivateDomainsRequest.builder())
                .organizationId(organizationId)
                .name(domain)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationPrivateDomainsResponse.builder())
                    .build()));
    }

    private static void requestRemoveApplication(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        when(cloudFoundryClient.routes()
            .removeApplication(fill(RemoveRouteApplicationRequest.builder())
                .applicationId(applicationId)
                .routeId(routeId)
                .build()))
            .thenReturn(Mono.<Void>empty());
    }

    private static void requestRouteExistsFalse(CloudFoundryClient cloudFoundryClient, String domainId, String host, String path) {
        when(cloudFoundryClient.routes()
            .exists(RouteExistsRequest.builder()
                .domainId(domainId)
                .host(host)
                .path(path)
                .build()))
            .thenReturn(Mono
                .just(false));
    }

    private static void requestRouteExistsTrue(CloudFoundryClient cloudFoundryClient, String domainId, String host, String path) {
        when(cloudFoundryClient.routes()
            .exists(RouteExistsRequest.builder()
                .domainId(domainId)
                .host(host)
                .path(path)
                .build()))
            .thenReturn(Mono
                .just(true));
    }

    private static void requestRoutes(CloudFoundryClient cloudFoundryClient, String domainId, String host, String path) {
        when(cloudFoundryClient.routes()
            .list(fillPage(org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder())
                .domainId(domainId)
                .host(host)
                .organizationId(null)
                .path(path)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListRoutesResponse.builder())
                    .resource(fill(RouteResource.builder(), "route-")
                        .build())
                    .build()));
    }

    private static void requestRoutesEmpty(CloudFoundryClient cloudFoundryClient, String domainId, String host, String path) {
        when(cloudFoundryClient.routes()
            .list(fillPage(org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder())
                .domainId(domainId)
                .host(host)
                .organizationId(null)
                .path(path)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListRoutesResponse.builder())
                    .build()));
    }

    private static void requestSharedDomains(CloudFoundryClient cloudFoundryClient, String domain) {
        when(cloudFoundryClient.sharedDomains()
            .list(fillPage(ListSharedDomainsRequest.builder())
                .name(domain)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSharedDomainsResponse.builder())
                    .resource(fill(SharedDomainResource.builder(), "shared-domain-")
                        .metadata(fill(Resource.Metadata.builder(), "shared-domain-metadata-")
                            .build())
                        .build())
                    .build()));

    }

    private static void requestSharedDomainsEmpty(CloudFoundryClient cloudFoundryClient, String domain) {
        when(cloudFoundryClient.sharedDomains()
            .list(fillPage(ListSharedDomainsRequest.builder())
                .name(domain)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSharedDomainsResponse.builder())
                    .build()));
    }

    private static void requestSpace(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .get(GetSpaceRequest.builder()
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetSpaceResponse.builder())
                    .entity(fill(SpaceEntity.builder(), "space-entity-")
                        .build())
                    .build()));
    }

    private static void requestSpaceRoutes(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listRoutes(fillPage(ListSpaceRoutesRequest.builder())
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceRoutesResponse.builder())
                    .resource(fill(RouteResource.builder(), "route-").build())
                    .build()));
    }

    private static void requestSpaceRoutesEmpty(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listRoutes(fillPage(ListSpaceRoutesRequest.builder())
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceRoutesResponse.builder())
                    .build()));
    }

    private static void requestSpaces(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        when(cloudFoundryClient.organizations()
            .listSpaces(fillPage(ListOrganizationSpacesRequest.builder())
                .organizationId(organizationId)
                .name(space)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationSpacesResponse.builder())
                    .resource(fill(SpaceResource.builder(), "space-").build())
                    .build()));
    }

    private static void requestSpacesEmpty(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        when(cloudFoundryClient.organizations()
            .listSpaces(fillPage(ListOrganizationSpacesRequest.builder())
                .organizationId(organizationId)
                .name(space)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationSpacesResponse.builder())
                    .build()));
    }

    public static final class CheckRouteInvalidDomain extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_SPACE_ID);

        @Before
        public void setUp() throws Exception {
            requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestSharedDomainsEmpty(this.cloudFoundryClient, "test-domain");
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(false);
        }

        @Override
        protected Mono<Boolean> invoke() {
            return this.routes
                .check(CheckRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .build());
        }

    }

    public static final class CheckRouteInvalidHost extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_SPACE_ID);

        @Before
        public void setUp() throws Exception {
            requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestRouteExistsFalse(this.cloudFoundryClient, "test-private-domain-metadata-id", "test-host", "test-path");
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(false);
        }

        @Override
        protected Mono<Boolean> invoke() {
            return this.routes
                .check(CheckRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }

    }

    public static final class CheckRouteNoOrganization extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, MISSING_ORGANIZATION_ID, MISSING_SPACE_ID);

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_ORGANIZATION_ID");
        }

        @Override
        protected Mono<Boolean> invoke() {
            return this.routes
                .check(CheckRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }

    }

    public static final class CheckRoutePrivateDomain extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_SPACE_ID);

        @Before
        public void setUp() throws Exception {
            requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestRouteExistsTrue(this.cloudFoundryClient, "test-private-domain-metadata-id", "test-host", "test-path");
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(true);
        }

        @Override
        protected Mono<Boolean> invoke() {
            return this.routes
                .check(CheckRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }

    }

    public static final class CheckRouteSharedDomain extends AbstractOperationsApiTest<Boolean> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_SPACE_ID);

        @Before
        public void setUp() throws Exception {
            requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestSharedDomains(this.cloudFoundryClient, "test-domain");
            requestRouteExistsTrue(this.cloudFoundryClient, "test-shared-domain-metadata-id", "test-host", "test-path");
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(true);
        }

        @Override
        protected Mono<Boolean> invoke() {
            return this.routes
                .check(CheckRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }

    }

    public static final class CreateRouteInvalidDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_SPACE_ID);

        @Before
        public void setUp() throws Exception {
            requestSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_SPACE_NAME);
            requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestSharedDomainsEmpty(this.cloudFoundryClient, "test-domain");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Domain test-domain does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .create(CreateRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .space(TEST_SPACE_NAME)
                    .build());
        }

    }

    public static final class CreateRouteInvalidSpace extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_SPACE_ID);

        @Before
        public void setUp() throws Exception {
            requestSpacesEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_SPACE_NAME);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Space test-space-name does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .create(CreateRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .space(TEST_SPACE_NAME)
                    .build());
        }

    }

    public static final class CreateRouteNoOrganization extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, MISSING_ORGANIZATION_ID, MISSING_SPACE_ID);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_ORGANIZATION_ID");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .create(CreateRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .space(TEST_SPACE_NAME)
                    .build());
        }

    }

    public static final class CreateRoutePrivateDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_SPACE_ID);

        @Before
        public void setUp() throws Exception {
            requestSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_SPACE_NAME);
            requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestCreateRoute(this.cloudFoundryClient, "test-private-domain-metadata-id", "test-host", "test-path", "test-space-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .create(CreateRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .space(TEST_SPACE_NAME)
                    .build());
        }

    }

    public static final class DeleteFailure extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() {
            requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestRoutes(this.cloudFoundryClient, "test-private-domain-metadata-id", "test-host", "test-path");
            requestDeleteRoute(this.cloudFoundryClient, "test-route-id");
            requestJobFailure(this.cloudFoundryClient, "test-id");
        }


        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(CloudFoundryException.class, "test-error-details-errorCode(1): test-error-details-description");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .delete(DeleteRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }

    }

    public static final class DeleteInvalidDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() {
            requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestSharedDomainsEmpty(this.cloudFoundryClient, "test-domain");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Domain test-domain does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .delete(DeleteRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }

    }

    public static final class DeleteInvalidRequest extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(RequestValidationException.class, "Request is invalid: domain must be specified");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .delete(DeleteRouteRequest.builder()
                    .build());
        }

    }

    public static final class DeleteInvalidRoute extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() {
            requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestRoutesEmpty(this.cloudFoundryClient, "test-private-domain-metadata-id", "test-host", "test-path");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Route test-host.test-domain does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .delete(DeleteRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }

    }

    public static final class DeleteNoOrganization extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, MISSING_ORGANIZATION_ID, Mono.just(TEST_SPACE_ID));

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_ORGANIZATION_ID");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .delete(DeleteRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }

    }

    public static final class DeleteOrphanedRoutesAssociatedApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceRoutes(this.cloudFoundryClient, TEST_SPACE_ID);
            requestApplications(this.cloudFoundryClient, "test-route-id");
            requestDeleteRoute(this.cloudFoundryClient, "test-route-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .deleteOrphanedRoutes();
        }

    }

    public static final class DeleteOrphanedRoutesNoAssociatedApplications extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceRoutes(this.cloudFoundryClient, TEST_SPACE_ID);
            requestApplicationsEmpty(this.cloudFoundryClient, "test-route-id");
            requestDeleteRoute(this.cloudFoundryClient, "test-route-id");
            requestJobSuccess(this.cloudFoundryClient, "test-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .deleteOrphanedRoutes();
        }

    }

    public static final class DeleteOrphanedRoutesNoAssociatedApplicationsFailure extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceRoutes(this.cloudFoundryClient, TEST_SPACE_ID);
            requestApplicationsEmpty(this.cloudFoundryClient, "test-route-id");
            requestDeleteRoute(this.cloudFoundryClient, "test-route-id");
            requestJobFailure(this.cloudFoundryClient, "test-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(CloudFoundryException.class, "test-error-details-errorCode(1): test-error-details-description");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .deleteOrphanedRoutes();
        }

    }

    public static final class DeleteOrphanedRoutesNoRoutes extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceRoutesEmpty(this.cloudFoundryClient, TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .deleteOrphanedRoutes();
        }

    }

    public static final class DeletePrivateDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() {
            requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestRoutes(this.cloudFoundryClient, "test-private-domain-metadata-id", "test-host", "test-path");
            requestDeleteRoute(this.cloudFoundryClient, "test-route-id");
            requestJobSuccess(this.cloudFoundryClient, "test-id");
        }


        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {

        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .delete(DeleteRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }

    }

    public static final class DeleteSharedDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() {
            requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestSharedDomains(this.cloudFoundryClient, "test-domain");
            requestRoutes(this.cloudFoundryClient, "test-shared-domain-metadata-id", "test-host", "test-path");
            requestDeleteRoute(this.cloudFoundryClient, "test-route-id");
            requestJobSuccess(this.cloudFoundryClient, "test-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {

        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .delete(DeleteRouteRequest.builder()
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }

    }

    public static final class ListCurrentOrganizationNoOrganization extends AbstractOperationsApiTest<Route> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, MISSING_ORGANIZATION_ID, Mono.just(TEST_SPACE_ID));

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_ORGANIZATION_ID");
        }

        @Override
        protected Publisher<Route> invoke() {
            return this.routes
                .list(ListRoutesRequest.builder()
                    .level(ListRoutesRequest.Level.ORGANIZATION)
                    .build());
        }
    }

    public static final class ListCurrentOrganizationNoOrganizationNoSpace extends AbstractOperationsApiTest<Route> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, MISSING_ORGANIZATION_ID, MISSING_SPACE_ID);

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_ORGANIZATION_ID");
        }

        @Override
        protected Publisher<Route> invoke() {
            return this.routes
                .list(ListRoutesRequest.builder()
                    .level(ListRoutesRequest.Level.ORGANIZATION)
                    .build());
        }
    }

    public static final class ListCurrentOrganizationNoSpace extends AbstractOperationsApiTest<Route> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_SPACE_ID);

        @Before
        public void setUp() throws Exception {
            requestOrganizationsRoutes(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
            requestDomain(this.cloudFoundryClient, "test-route-entity-domainId");
            requestSpace(this.cloudFoundryClient, "test-route-entity-spaceId");
            requestApplications(this.cloudFoundryClient, "test-id");
        }

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(Route.builder())
                    .application("test-application-name")
                    .domain("test-domain-name")
                    .host("test-route-entity-host")
                    .path("test-route-entity-path")
                    .routeId("test-id")
                    .space("test-space-entity-name")
                    .build());
        }

        @Override
        protected Publisher<Route> invoke() {
            return this.routes
                .list(ListRoutesRequest.builder()
                    .level(ListRoutesRequest.Level.ORGANIZATION)
                    .build());
        }
    }

    public static final class ListCurrentOrganizationNoSpaceNoRoutes extends AbstractOperationsApiTest<Route> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_SPACE_ID);

        @Before
        public void setUp() throws Exception {
            requestOrganizationsRoutesEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
            requestDomain(this.cloudFoundryClient, "test-route-entity-domainId");
            requestSpace(this.cloudFoundryClient, "test-route-entity-spaceId");
            requestApplications(this.cloudFoundryClient, "test-id");
        }

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            // onComplete and not onNext
        }

        @Override
        protected Publisher<Route> invoke() {
            return this.routes
                .list(ListRoutesRequest.builder()
                    .level(ListRoutesRequest.Level.ORGANIZATION)
                    .build());
        }
    }

    public static final class ListCurrentSpace extends AbstractOperationsApiTest<Route> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceRoutes(this.cloudFoundryClient, TEST_SPACE_ID);
            requestDomain(this.cloudFoundryClient, "test-route-domainId");
            requestSpace(this.cloudFoundryClient, "test-route-spaceId");
            requestApplications(this.cloudFoundryClient, "test-route-id");
        }

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(Route.builder())
                    .application("test-application-name")
                    .domain("test-domain-name")
                    .host("test-route-host")
                    .path("test-route-path")
                    .routeId("test-route-id")
                    .space("test-space-entity-name")
                    .build());
        }

        @Override
        protected Publisher<Route> invoke() {
            return this.routes
                .list(ListRoutesRequest.builder()
                    .level(ListRoutesRequest.Level.SPACE)
                    .build());
        }
    }

    public static final class ListCurrentSpaceNoOrganization extends AbstractOperationsApiTest<Route> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, MISSING_ORGANIZATION_ID, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceRoutes(this.cloudFoundryClient, TEST_SPACE_ID);
            requestDomain(this.cloudFoundryClient, "test-route-domainId");
            requestSpace(this.cloudFoundryClient, "test-route-spaceId");
            requestApplications(this.cloudFoundryClient, "test-route-id");
        }

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(Route.builder())
                    .application("test-application-name")
                    .domain("test-domain-name")
                    .host("test-route-host")
                    .path("test-route-path")
                    .routeId("test-route-id")
                    .space("test-space-entity-name")
                    .build());
        }

        @Override
        protected Publisher<Route> invoke() {
            return this.routes
                .list(ListRoutesRequest.builder()
                    .level(ListRoutesRequest.Level.SPACE)
                    .build());
        }
    }

    public static final class ListCurrentSpaceNoOrganizationNoSpace extends AbstractOperationsApiTest<Route> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, MISSING_ORGANIZATION_ID, MISSING_SPACE_ID);

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_SPACE_ID");
        }

        @Override
        protected Publisher<Route> invoke() {
            return this.routes
                .list(ListRoutesRequest.builder()
                    .level(ListRoutesRequest.Level.SPACE)
                    .build());
        }
    }

    public static final class ListCurrentSpaceNoSpace extends AbstractOperationsApiTest<Route> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_SPACE_ID);

        @Override
        protected void assertions(TestSubscriber<Route> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_SPACE_ID");
        }

        @Override
        protected Publisher<Route> invoke() {
            return this.routes
                .list(ListRoutesRequest.builder()
                    .level(ListRoutesRequest.Level.SPACE)
                    .build());
        }
    }

    public static final class MapRouteExists extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestRoutes(this.cloudFoundryClient, "test-private-domain-metadata-id", "test-host", "test-path");
            requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .map(MapRouteRequest.builder()
                    .applicationName("test-application-name")
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }
    }

    public static final class MapRouteInvalidApplicationName extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsEmpty(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestRoutesEmpty(this.cloudFoundryClient, "test-private-domain-metadata-id", "test-host", "test-path");
            requestCreateRoute(this.cloudFoundryClient, "test-private-domain-metadata-id", "test-host", "test-path", "test-space-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Application test-application-name does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .map(MapRouteRequest.builder()
                    .applicationName("test-application-name")
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }
    }

    public static final class MapRouteInvalidDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestSharedDomainsEmpty(this.cloudFoundryClient, "test-domain");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Domain test-domain does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .map(MapRouteRequest.builder()
                    .applicationName("test-application-name")
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }
    }

    public static final class MapRouteNoOrganization extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, MISSING_ORGANIZATION_ID, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_ORGANIZATION_ID");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .map(MapRouteRequest.builder()
                    .applicationName("test-application-name")
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }
    }

    public static final class MapRouteNoSpace extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_SPACE_ID);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_SPACE_ID");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .map(MapRouteRequest.builder()
                    .applicationName("test-application-name")
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }
    }

    public static final class MapRoutePrivateDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestRoutesEmpty(this.cloudFoundryClient, "test-private-domain-metadata-id", "test-host", "test-path");
            requestCreateRoute(this.cloudFoundryClient, "test-private-domain-metadata-id", "test-host", "test-path", TEST_SPACE_ID);
            requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .map(MapRouteRequest.builder()
                    .applicationName("test-application-name")
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }
    }

    public static final class MapRouteSharedDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestSharedDomains(this.cloudFoundryClient, "test-domain");
            requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-metadata-id", "test-host", "test-path");
            requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-metadata-id", "test-host", "test-path", TEST_SPACE_ID);
            requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .map(MapRouteRequest.builder()
                    .applicationName("test-application-name")
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }
    }

    public static final class UnmapRouteInvalidApplicationName extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsEmpty(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Application test-application-name does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .unmap(UnmapRouteRequest.builder()
                    .applicationName("test-application-name")
                    .domain("test-domain")
                    .host("test-host")
                    .build());
        }
    }

    public static final class UnmapRouteInvalidDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestSharedDomainsEmpty(this.cloudFoundryClient, "test-domain");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Domain test-domain does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .unmap(UnmapRouteRequest.builder()
                    .applicationName("test-application-name")
                    .domain("test-domain")
                    .host("test-host")
                    .build());
        }
    }

    public static final class UnmapRouteInvalidRoute extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestRoutesEmpty(this.cloudFoundryClient, "test-private-domain-metadata-id", "test-host", "test-path");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Route test-host.test-domain does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .unmap(UnmapRouteRequest.builder()
                    .applicationName("test-application-name")
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }
    }

    public static final class UnmapRouteNoOrganization extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, MISSING_ORGANIZATION_ID, Mono.just(TEST_SPACE_ID));

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_ORGANIZATION_ID");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .unmap(UnmapRouteRequest.builder()
                    .applicationName("test-application-name")
                    .domain("test-domain")
                    .host("test-host")
                    .build());
        }
    }

    public static final class UnmapRouteNoSpace extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_SPACE_ID);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_SPACE_ID");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .unmap(UnmapRouteRequest.builder()
                    .applicationName("test-application-name")
                    .domain("test-domain")
                    .host("test-host")
                    .build());
        }
    }

    public static final class UnmapRoutePrivateDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestRoutes(this.cloudFoundryClient, "test-private-domain-metadata-id", "test-host", "test-path");
            requestRemoveApplication(this.cloudFoundryClient, "test-application-id", "test-route-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .unmap(UnmapRouteRequest.builder()
                    .applicationName("test-application-name")
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }

    }

    public static final class UnmapRouteSharedDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultRoutes routes = new DefaultRoutes(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-domain");
            requestSharedDomains(this.cloudFoundryClient, "test-domain");
            requestRoutes(this.cloudFoundryClient, "test-shared-domain-metadata-id", "test-host", "test-path");
            requestRemoveApplication(this.cloudFoundryClient, "test-application-id", "test-route-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.routes
                .unmap(UnmapRouteRequest.builder()
                    .applicationName("test-application-name")
                    .domain("test-domain")
                    .host("test-host")
                    .path("test-path")
                    .build());
        }

    }

}
