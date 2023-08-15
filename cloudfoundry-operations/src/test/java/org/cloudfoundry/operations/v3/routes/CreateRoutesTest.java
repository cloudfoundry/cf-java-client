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

package org.cloudfoundry.operations.routes;

// import org.cloudfoundry.client.v3.Link;
// import org.cloudfoundry.client.v3.Pagination;
// import org.cloudfoundry.client.CloudFoundryClient;
// import org.cloudfoundry.client.v2.ClientV2Exception;
// import org.cloudfoundry.client.v2.Metadata;
// import org.cloudfoundry.client.v2.applications.ApplicationResource;
// import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
// import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteResponse;
// import org.cloudfoundry.client.v2.applications.RemoveApplicationRouteRequest;
// import org.cloudfoundry.client.v2.jobs.ErrorDetails;
// import org.cloudfoundry.client.v2.jobs.GetJobRequest;
// import org.cloudfoundry.client.v2.jobs.GetJobResponse;
// import org.cloudfoundry.client.v2.jobs.JobEntity;
// import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
// import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsResponse;
// import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
// import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
// import org.cloudfoundry.client.v2.routes.DeleteRouteResponse;
// import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
// import org.cloudfoundry.client.v2.routes.ListRouteApplicationsResponse;
// import org.cloudfoundry.client.v2.routes.ListRoutesResponse;
// import org.cloudfoundry.client.v2.routes.RouteEntity;
// import org.cloudfoundry.client.v3.domains.CheckReservedRoutesRequest;
// import org.cloudfoundry.client.v3.domains.CheckReservedRoutesResponse;
// import org.cloudfoundry.client.v2.routes.RouteResource;
// import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceRequest;
// import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceResponse;
// import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
// import org.cloudfoundry.client.v2.serviceinstances.UnionServiceInstanceEntity;
// import org.cloudfoundry.client.v2.serviceinstances.UnionServiceInstanceResource;
// import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
// import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsResponse;
// import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
// import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
// import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
// import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesRequest;
// import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesResponse;
// import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesRequest;
// import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesResponse;
// import org.cloudfoundry.client.v3.spaces.ListSpacesRequest;
// import org.cloudfoundry.client.v3.spaces.ListSpacesResponse;
// import org.cloudfoundry.client.v3.spaces.SpaceResource;
// import org.cloudfoundry.client.v3.spaces.SpaceRelationships;
// import org.cloudfoundry.client.v3.ToOneRelationship;
// import org.cloudfoundry.client.v3.Relationship;
// import org.cloudfoundry.client.v3.domains.CheckReservedRoutesResponse;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.junit.Test;
import reactor.core.publisher.Mono;
// import reactor.test.StepVerifier;
// import reactor.test.scheduler.VirtualTimeScheduler;

// import java.time.Duration;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.LinkedList;
// import java.util.Optional;
// import java.util.Queue;
// import java.util.function.Supplier;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.cloudfoundry.operations.TestObjects.fill;
// import static org.mockito.Mockito.when;

public final class CreateRoutesTest extends AbstractOperationsTest {

    private final DefaultRoutes routes = new DefaultRoutes(Mono.just(this.cloudFoundryClient),
            Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

    @Test
    public void createRoute() {

    }

    // @Test
    // public void createRouteAssignedPort() {
    // requestSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID,
    // TEST_SPACE_NAME);
    // requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID,
    // "test-domain");
    // requestSharedDomains(this.cloudFoundryClient, "test-domain");
    // requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-metadata-id",
    // null, null, null, 9999,
    // "test-space-id");

    // this.routes
    // .create(CreateRouteRequest.builder()
    // .domain("test-domain")
    // .port(9999)
    // .space(TEST_SPACE_NAME)
    // .build())
    // .as(StepVerifier::create)
    // .expectNext(1)
    // .expectComplete()
    // .verify(Duration.ofSeconds(5));
    // }

    // @Test
    // public void createRouteInvalidDomain() {
    // requestSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID,
    // TEST_SPACE_NAME);
    // requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID,
    // "test-domain");
    // requestSharedDomainsEmpty(this.cloudFoundryClient, "test-domain");

    // this.routes
    // .create(CreateRouteRequest.builder()
    // .domain("test-domain")
    // .host("test-host")
    // .space(TEST_SPACE_NAME)
    // .build())
    // .as(StepVerifier::create)
    // .consumeErrorWith(t ->
    // assertThat(t).isInstanceOf(IllegalArgumentException.class)
    // .hasMessage("Domain test-domain does not exist"))
    // .verify(Duration.ofSeconds(5));
    // }

    // @Test
    // public void createRouteInvalidSpace() {
    // requestSpacesEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID,
    // TEST_SPACE_NAME);
    // requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID,
    // "test-domain");

    // this.routes
    // .create(CreateRouteRequest.builder()
    // .domain("test-domain")
    // .host("test-host")
    // .path("test-path")
    // .space(TEST_SPACE_NAME)
    // .build())
    // .as(StepVerifier::create)
    // .consumeErrorWith(t ->
    // assertThat(t).isInstanceOf(IllegalArgumentException.class)
    // .hasMessage("Space test-space-name does not exist"))
    // .verify(Duration.ofSeconds(5));
    // }

    // @Test
    // public void createRouteNoHost() {
    // requestSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID,
    // TEST_SPACE_NAME);
    // requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID,
    // "test-domain");
    // requestCreateRoute(this.cloudFoundryClient,
    // "test-private-domain-metadata-id", null, null, "test-path", null,
    // "test-space-id");

    // this.routes
    // .create(CreateRouteRequest.builder()
    // .domain("test-domain")
    // .path("test-path")
    // .space(TEST_SPACE_NAME)
    // .build())
    // .as(StepVerifier::create)
    // .expectNext(1)
    // .expectComplete()
    // .verify(Duration.ofSeconds(5));
    // }

    // @Test
    // public void createRouteNoPath() {
    // requestSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID,
    // TEST_SPACE_NAME);
    // requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID,
    // "test-domain");
    // requestCreateRoute(this.cloudFoundryClient,
    // "test-private-domain-metadata-id", "test-host", null, null, null,
    // "test-space-id");

    // this.routes
    // .create(CreateRouteRequest.builder()
    // .domain("test-domain")
    // .host("test-host")
    // .space(TEST_SPACE_NAME)
    // .build())
    // .as(StepVerifier::create)
    // .expectNext(1)
    // .expectComplete()
    // .verify(Duration.ofSeconds(5));
    // }

    // @Test
    // public void createRoutePrivateDomain() {
    // requestSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID,
    // TEST_SPACE_NAME);
    // requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID,
    // "test-domain");
    // requestCreateRoute(this.cloudFoundryClient,
    // "test-private-domain-metadata-id", "test-host", null, "test-path",
    // null, "test-space-id");

    // this.routes
    // .create(CreateRouteRequest.builder()
    // .domain("test-domain")
    // .host("test-host")
    // .path("test-path")
    // .space(TEST_SPACE_NAME)
    // .build())
    // .as(StepVerifier::create)
    // .expectNext(1)
    // .expectComplete()
    // .verify(Duration.ofSeconds(5));
    // }

    // @Test
    // public void createRouteRandomPort() {
    // requestSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID,
    // TEST_SPACE_NAME);
    // requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID,
    // "test-domain");
    // requestSharedDomains(this.cloudFoundryClient, "test-domain");
    // requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-metadata-id",
    // null, true, null, null,
    // "test-space-id");

    // this.routes
    // .create(CreateRouteRequest.builder()
    // .domain("test-domain")
    // .randomPort(true)
    // .space(TEST_SPACE_NAME)
    // .build())
    // .as(StepVerifier::create)
    // .expectNext(1)
    // .expectComplete()
    // .verify(Duration.ofSeconds(5));
    // }

}
