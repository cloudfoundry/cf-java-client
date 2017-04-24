/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.client.v2.routes.AssociateRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.AssociateRouteApplicationResponse;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.GetRouteRequest;
import org.cloudfoundry.client.v2.routes.GetRouteResponse;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.routes.RemoveRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteExistsRequest;
import org.cloudfoundry.client.v2.routes.UpdateRouteRequest;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple3;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class RoutesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private Mono<String> spaceId;

    @Autowired
    private Mono<String> stackId;

    @Test
    public void associateApplication() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        Mono
            .when(
                this.organizationId
                    .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
                this.spaceId
            )
            .flatMap(function((domainId, spaceId) -> Mono.when(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName, null),
                createRouteId(this.cloudFoundryClient, domainId, spaceId)
            )))
            .delayUntil(function((applicationId, routeId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId)))
            .flatMapMany(function((applicationId, routeId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .page(page)
                        .routeId(routeId)
                        .build()))))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        Mono
            .when(
                this.organizationId
                    .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
                this.spaceId
            )
            .flatMap(function((domainId, spaceId) -> Mono.when(
                Mono.just(domainId),
                Mono.just(spaceId),
                this.cloudFoundryClient.routes()
                    .create(CreateRouteRequest.builder()
                        .domainId(domainId)
                        .spaceId(spaceId)
                        .build())
                    .map(ResourceUtils::getEntity))
            ))
            .as(StepVerifier::create)
            .consumeNextWith(domainIdSpaceIdEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        Mono
            .when(
                this.organizationId
                    .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
                this.spaceId
            )
            .flatMap(function((domainId, spaceId) -> createRouteId(this.cloudFoundryClient, domainId, spaceId)))
            .delayUntil(routeId -> this.cloudFoundryClient.routes()
                .delete(DeleteRouteRequest.builder()
                    .async(true)
                    .routeId(routeId)
                    .build())
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job)))
            .flatMap(routeId -> requestGetRoute(this.cloudFoundryClient, routeId))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessageMatching("CF-RouteNotFound\\([0-9]+\\): The route could not be found: .*"))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAsyncFalse() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        Mono
            .when(
                this.organizationId
                    .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
                this.spaceId
            )
            .flatMap(function((domainId, spaceId) -> createRouteId(this.cloudFoundryClient, domainId, spaceId)))
            .delayUntil(routeId -> this.cloudFoundryClient.routes()
                .delete(DeleteRouteRequest.builder()
                    .async(false)
                    .routeId(routeId)
                    .build()))
            .flatMap(routeId -> requestGetRoute(this.cloudFoundryClient, routeId))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessageMatching("CF-RouteNotFound\\([0-9]+\\): The route could not be found: .*"))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void exists() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();

        Mono
            .when(
                this.organizationId
                    .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
                this.spaceId
            )
            .delayUntil(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .host(hostName)
                    .spaceId(spaceId)
                    .build())))
            .flatMap(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .exists(RouteExistsRequest.builder()
                    .domainId(domainId)
                    .host(hostName)
                    .build())))
            .as(StepVerifier::create)
            .expectNext(true)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void existsDoesNotExist() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String hostName1 = this.nameFactory.getHostName();
        String hostName2 = this.nameFactory.getHostName();

        Mono
            .when(
                this.organizationId
                    .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
                this.spaceId
            )
            .delayUntil(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .host(hostName1)
                    .spaceId(spaceId)
                    .build())))
            .flatMap(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .exists(RouteExistsRequest.builder()
                    .domainId(domainId)
                    .host(hostName2)
                    .build())))
            .as(StepVerifier::create)
            .expectNext(false)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        Mono
            .when(
                this.organizationId
                    .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
                this.spaceId
            )
            .flatMap(function((domainId, spaceId) -> Mono
                .when(
                    Mono.just(domainId),
                    Mono.just(spaceId),
                    createRouteId(this.cloudFoundryClient, domainId, spaceId))
            ))
            .flatMap(function((domainId, spaceId, routeId) -> Mono
                .when(
                    Mono.just(domainId),
                    Mono.just(spaceId),
                    this.cloudFoundryClient.routes()
                        .get(GetRouteRequest.builder()
                            .routeId(routeId)
                            .build())
                        .map(ResourceUtils::getEntity))
            ))
            .as(StepVerifier::create)
            .consumeNextWith(domainIdSpaceIdEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listApplications() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        Mono
            .when(
                this.organizationId
                    .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
                this.spaceId
            )
            .flatMap(function((domainId, spaceId) -> Mono.when(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName, null),
                createRouteId(this.cloudFoundryClient, domainId, spaceId)
            )))
            .delayUntil(function((applicationId, routeId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId)))
            .flatMapMany(function((applicationId, routeId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .page(page)
                        .routeId(routeId)
                        .build()))))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listApplicationsFilterByDiego() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        Mono
            .when(
                this.organizationId
                    .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
                this.spaceId
            )
            .flatMap(function((domainId, spaceId) -> Mono.when(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName, null),
                createRouteId(this.cloudFoundryClient, domainId, spaceId)
            )))
            .delayUntil(function((applicationId, routeId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId)))
            .flatMapMany(function((applicationId, routeId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .diego(true)
                        .page(page)
                        .routeId(routeId)
                        .build()))))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listApplicationsFilterByName() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        Mono
            .when(
                this.organizationId
                    .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
                this.spaceId
            )
            .flatMap(function((domainId, spaceId) -> Mono.when(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName, null),
                createRouteId(this.cloudFoundryClient, domainId, spaceId)
            )))
            .delayUntil(function((applicationId, routeId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId)))
            .flatMapMany(function((applicationId, routeId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .name(applicationName)
                        .page(page)
                        .routeId(routeId)
                        .build()))))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listApplicationsFilterByOrganizationId() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono
                .when(
                    createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId),
                    this.spaceId,
                    Mono.just(organizationId)
                ))
            .flatMap(function((domainId, spaceId, organizationId) -> Mono
                .when(
                    createApplicationId(this.cloudFoundryClient, spaceId, applicationName, null),
                    createRouteId(this.cloudFoundryClient, domainId, spaceId),
                    Mono.just(organizationId)
                )))
            .delayUntil(function((applicationId, routeId, organizationId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId)))
            .flatMapMany(function((applicationId, routeId, organizationId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .routeId(routeId)
                        .build()))))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listApplicationsFilterBySpaceId() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        Mono
            .when(
                this.organizationId
                    .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
                this.spaceId
            )
            .flatMap(function((domainId, spaceId) -> Mono.when(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName, null),
                createRouteId(this.cloudFoundryClient, domainId, spaceId),
                Mono.just(spaceId)
            )))
            .delayUntil(function((applicationId, routeId, spaceId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId)))
            .flatMapMany(function((applicationId, routeId, spaceId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .page(page)
                        .routeId(routeId)
                        .spaceId(spaceId)
                        .build()))))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listApplicationsFilterByStackId() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        Mono
            .when(
                this.organizationId
                    .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
                this.spaceId,
                this.stackId
            )
            .flatMap(function((domainId, spaceId, stackId) -> Mono
                .when(
                    createApplicationId(this.cloudFoundryClient, spaceId, applicationName, stackId),
                    createRouteId(this.cloudFoundryClient, domainId, spaceId),
                    Mono.just(stackId)
                )
            ))
            .delayUntil(function((applicationId, routeId, stackId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId)))
            .flatMapMany(function((aplicationId, routeId, stackId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .page(page)
                        .routeId(routeId)
                        .stackId(stackId)
                        .build()))))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByDomainId() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        Mono
            .when(
                this.organizationId
                    .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
                this.spaceId
            )
            .delayUntil(function((domainId, spaceId) -> requestCreateRoute(this.cloudFoundryClient, domainId, spaceId)))
            .flatMapMany(function((domainId, spaceId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .list(ListRoutesRequest.builder()
                        .domainId(domainId)
                        .page(page)
                        .build()))))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByHost() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String host = this.nameFactory.getHostName();

        Mono
            .when(
                this.organizationId
                    .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
                this.spaceId
            )
            .flatMap(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .host(host)
                    .spaceId(spaceId)
                    .build())))
            .flatMapMany(response -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .list(ListRoutesRequest.builder()
                        .host(host)
                        .page(page)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByOrganizationId() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono
                .when(
                    createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId),
                    this.spaceId,
                    Mono.just(organizationId)
                ))
            .delayUntil(function((domainId, spaceId, organizationId) -> requestCreateRoute(this.cloudFoundryClient, domainId, spaceId)))
            .flatMapMany(function((domainId, spaceId, organizationId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .list(ListRoutesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build()))))
            .count()
            .as(StepVerifier::create)
            .consumeNextWith(count -> assertThat(count).isGreaterThan(0))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByPath() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String path = this.nameFactory.getPath();

        Mono
            .when(
                this.organizationId
                    .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
                this.spaceId
            )
            .flatMap(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .path(path)
                    .spaceId(spaceId)
                    .build())))
            .flatMapMany(response -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .list(ListRoutesRequest.builder()
                        .page(page)
                        .path(path)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeApplication() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        Mono
            .when(
                this.organizationId
                    .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
                this.spaceId
            )
            .flatMap(function((domainId, spaceId) -> Mono.when(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName, null),
                createRouteId(this.cloudFoundryClient, domainId, spaceId)
            )))
            .delayUntil(function((applicationId, routeId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId)))
            .delayUntil(function((applicationId, routeId) -> this.cloudFoundryClient.routes()
                .removeApplication(RemoveRouteApplicationRequest.builder()
                    .applicationId(applicationId)
                    .routeId(routeId)
                    .build())))
            .flatMapMany(function((applicationId, routeId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .page(page)
                        .routeId(routeId)
                        .build()))))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String domainName = this.nameFactory.getDomainName();

        Mono
            .when(
                this.organizationId
                    .flatMap(organizationId -> createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId)),
                this.spaceId
            )
            .flatMap(function((domainId, spaceId) -> createRouteId(this.cloudFoundryClient, domainId, spaceId)))
            .flatMap(routeId -> this.cloudFoundryClient.routes()
                .update(UpdateRouteRequest.builder()
                    .host("test-host")
                    .routeId(routeId)
                    .build())
                .map(ResourceUtils::getEntity)
                .map(RouteEntity::getHost))
            .as(StepVerifier::create)
            .expectNext("test-host")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<AssociateRouteApplicationResponse> associateApplicationWithRoute(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return cloudFoundryClient.routes()
            .associateApplication(AssociateRouteApplicationRequest.builder()
                .applicationId(applicationId)
                .routeId(routeId)
                .build());
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName, String stackId) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName, stackId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createPrivateDomainId(CloudFoundryClient cloudFoundryClient, String name, String organizationId) {
        return requestCreatePrivateDomain(cloudFoundryClient, name, organizationId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createRouteId(CloudFoundryClient cloudFoundryClient, String domainId, String spaceId) {
        return requestCreateRoute(cloudFoundryClient, domainId, spaceId)
            .map(ResourceUtils::getId);
    }

    private static Consumer<Tuple3<String, String, RouteEntity>> domainIdSpaceIdEquality() {
        return consumer((domainId, spaceId, entity) -> {
            assertThat(entity.getDomainId()).isEqualTo(domainId);
            assertThat(entity.getSpaceId()).isEqualTo(spaceId);
        });
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName, String stackId) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .diego(true)
                .name(applicationName)
                .spaceId(spaceId)
                .stackId(stackId)
                .build());
    }

    private static Mono<CreatePrivateDomainResponse> requestCreatePrivateDomain(CloudFoundryClient cloudFoundryClient, String name, String organizationId) {
        return cloudFoundryClient.privateDomains()
            .create(CreatePrivateDomainRequest.builder()
                .name(name)
                .owningOrganizationId(organizationId)
                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, String spaceId) {
        return cloudFoundryClient.routes()
            .create(CreateRouteRequest.builder()
                .domainId(domainId)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<GetRouteResponse> requestGetRoute(CloudFoundryClient cloudFoundryClient, String routeId) {
        return cloudFoundryClient.routes()
            .get(GetRouteRequest.builder()
                .routeId(routeId)
                .build());
    }

}
