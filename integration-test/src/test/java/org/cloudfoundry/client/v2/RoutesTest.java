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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.domains.CreateDomainRequest;
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
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.routes.UpdateRouteRequest;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.subscriber.ScriptedSubscriber;
import reactor.util.function.Tuple3;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.OperationUtils.thenKeep;
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

        ScriptedSubscriber<ApplicationResource> subscriber = ScriptedSubscriber.<ApplicationResource>create()
            .expectNextCount(1)
            .expectComplete();

        Mono
            .when(
                this.organizationId
                    .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName)),
                this.spaceId
            )
            .then(function((domainId, spaceId) -> Mono.when(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName, null),
                createRouteId(this.cloudFoundryClient, domainId, spaceId)
            )))
            .as(thenKeep(function((applicationId, routeId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId))))
            .flatMap(function((applicationId, routeId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .page(page)
                        .routeId(routeId)
                        .build()))))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<Tuple3<String, String, RouteEntity>> subscriber = domainIdSpaceIdEquality();

        Mono
            .when(
                this.organizationId
                    .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName)),
                this.spaceId
            )
            .then(function((domainId, spaceId) -> Mono.when(
                Mono.just(domainId),
                Mono.just(spaceId),
                this.cloudFoundryClient.routes()
                    .create(CreateRouteRequest.builder()
                        .domainId(domainId)
                        .spaceId(spaceId)
                        .build())
                    .map(ResourceUtils::getEntity))
            ))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<GetRouteResponse> subscriber = ScriptedSubscriber.<GetRouteResponse>create()
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(CloudFoundryException.class).hasMessageMatching("CF-RouteNotFound\\([0-9]+\\): The route could not be found: .*"));

        Mono
            .when(
                this.organizationId
                    .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName)),
                this.spaceId
            )
            .then(function((domainId, spaceId) -> createRouteId(this.cloudFoundryClient, domainId, spaceId)))
            .as(thenKeep(routeId -> this.cloudFoundryClient.routes()
                .delete(DeleteRouteRequest.builder()
                    .async(true)
                    .routeId(routeId)
                    .build())
                .then(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, job))))
            .then(routeId -> requestGetRoute(this.cloudFoundryClient, routeId))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAsyncFalse() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<GetRouteResponse> subscriber = ScriptedSubscriber.<GetRouteResponse>create()
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(CloudFoundryException.class).hasMessageMatching("CF-RouteNotFound\\([0-9]+\\): The route could not be found: .*"));

        Mono
            .when(
                this.organizationId
                    .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName)),
                this.spaceId
            )
            .then(function((domainId, spaceId) -> createRouteId(this.cloudFoundryClient, domainId, spaceId)))
            .as(thenKeep(routeId -> this.cloudFoundryClient.routes()
                .delete(DeleteRouteRequest.builder()
                    .async(false)
                    .routeId(routeId)
                    .build())))
            .then(routeId -> requestGetRoute(this.cloudFoundryClient, routeId))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void exists() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();

        ScriptedSubscriber<Boolean> subscriber = ScriptedSubscriber.<Boolean>create()
            .expectNext(true)
            .expectComplete();

        Mono
            .when(
                this.organizationId
                    .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName)),
                this.spaceId
            )
            .as(thenKeep(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .host(hostName)
                    .spaceId(spaceId)
                    .build()))))
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .exists(RouteExistsRequest.builder()
                    .domainId(domainId)
                    .host(hostName)
                    .build())))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void existsDoesNotExist() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String hostName1 = this.nameFactory.getHostName();
        String hostName2 = this.nameFactory.getHostName();

        ScriptedSubscriber<Boolean> subscriber = ScriptedSubscriber.<Boolean>create()
            .expectNext(false)
            .expectComplete();

        Mono
            .when(
                this.organizationId
                    .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName)),
                this.spaceId
            )
            .as(thenKeep(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .host(hostName1)
                    .spaceId(spaceId)
                    .build()))))
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .exists(RouteExistsRequest.builder()
                    .domainId(domainId)
                    .host(hostName2)
                    .build())))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<Tuple3<String, String, RouteEntity>> subscriber = domainIdSpaceIdEquality();

        Mono
            .when(
                this.organizationId
                    .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName)),
                this.spaceId
            )
            .then(function((domainId, spaceId) -> Mono
                .when(
                    Mono.just(domainId),
                    Mono.just(spaceId),
                    createRouteId(this.cloudFoundryClient, domainId, spaceId))
            ))
            .then(function((domainId, spaceId, routeId) -> Mono
                .when(
                    Mono.just(domainId),
                    Mono.just(spaceId),
                    this.cloudFoundryClient.routes()
                        .get(GetRouteRequest.builder()
                            .routeId(routeId)
                            .build())
                        .map(ResourceUtils::getEntity))
            ))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listApplications() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<ApplicationResource> subscriber = ScriptedSubscriber.<ApplicationResource>create()
            .expectNextCount(1)
            .expectComplete();

        Mono
            .when(
                this.organizationId
                    .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName)),
                this.spaceId
            )
            .then(function((domainId, spaceId) -> Mono.when(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName, null),
                createRouteId(this.cloudFoundryClient, domainId, spaceId)
            )))
            .as(thenKeep(function((applicationId, routeId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId))))
            .flatMap(function((applicationId, routeId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .page(page)
                        .routeId(routeId)
                        .build()))))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listApplicationsFilterByDiego() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<ApplicationResource> subscriber = ScriptedSubscriber.<ApplicationResource>create()
            .expectNextCount(1)
            .expectComplete();

        Mono
            .when(
                this.organizationId
                    .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName)),
                this.spaceId
            )
            .then(function((domainId, spaceId) -> Mono.when(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName, null),
                createRouteId(this.cloudFoundryClient, domainId, spaceId)
            )))
            .as(thenKeep(function((applicationId, routeId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId))))
            .flatMap(function((applicationId, routeId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .diego(true)
                        .page(page)
                        .routeId(routeId)
                        .build()))))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listApplicationsFilterByName() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<ApplicationResource> subscriber = ScriptedSubscriber.<ApplicationResource>create()
            .expectNextCount(1)
            .expectComplete();

        Mono
            .when(
                this.organizationId
                    .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName)),
                this.spaceId
            )
            .then(function((domainId, spaceId) -> Mono.when(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName, null),
                createRouteId(this.cloudFoundryClient, domainId, spaceId)
            )))
            .as(thenKeep(function((applicationId, routeId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId))))
            .flatMap(function((applicationId, routeId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .name(applicationName)
                        .page(page)
                        .routeId(routeId)
                        .build()))))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listApplicationsFilterByOrganizationId() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<ApplicationResource> subscriber = ScriptedSubscriber.<ApplicationResource>create()
            .expectNextCount(1)
            .expectComplete();

        this.organizationId
            .then(organizationId -> Mono
                .when(
                    createDomainId(this.cloudFoundryClient, organizationId, domainName),
                    this.spaceId,
                    Mono.just(organizationId)
                ))
            .then(function((domainId, spaceId, organizationId) -> Mono
                .when(
                    createApplicationId(this.cloudFoundryClient, spaceId, applicationName, null),
                    createRouteId(this.cloudFoundryClient, domainId, spaceId),
                    Mono.just(organizationId)
                )))
            .as(thenKeep(function((applicationId, routeId, organizationId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId))))
            .flatMap(function((applicationId, routeId, organizationId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .routeId(routeId)
                        .build()))))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listApplicationsFilterBySpaceId() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<ApplicationResource> subscriber = ScriptedSubscriber.<ApplicationResource>create()
            .expectNextCount(1)
            .expectComplete();

        Mono
            .when(
                this.organizationId
                    .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName)),
                this.spaceId
            )
            .then(function((domainId, spaceId) -> Mono.when(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName, null),
                createRouteId(this.cloudFoundryClient, domainId, spaceId),
                Mono.just(spaceId)
            )))
            .as(thenKeep(function((applicationId, routeId, spaceId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId))))
            .flatMap(function((applicationId, routeId, spaceId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .page(page)
                        .routeId(routeId)
                        .spaceId(spaceId)
                        .build()))))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listApplicationsFilterByStackId() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<ApplicationResource> subscriber = ScriptedSubscriber.<ApplicationResource>create()
            .expectNextCount(1)
            .expectComplete();

        Mono
            .when(
                this.organizationId
                    .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName)),
                this.spaceId,
                this.stackId
            )
            .then(function((domainId, spaceId, stackId) -> Mono
                .when(
                    createApplicationId(this.cloudFoundryClient, spaceId, applicationName, stackId),
                    createRouteId(this.cloudFoundryClient, domainId, spaceId),
                    Mono.just(stackId)
                )
            ))
            .as(thenKeep(function((applicationId, routeId, stackId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId))))
            .flatMap(function((aplicationId, routeId, stackId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .page(page)
                        .routeId(routeId)
                        .stackId(stackId)
                        .build()))))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByDomainId() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<RouteResource> subscriber = ScriptedSubscriber.<RouteResource>create()
            .expectNextCount(1)
            .expectComplete();

        Mono
            .when(
                this.organizationId
                    .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName)),
                this.spaceId
            )
            .as(thenKeep(function((domainId, spaceId) -> requestCreateRoute(this.cloudFoundryClient, domainId, spaceId))))
            .flatMap(function((domainId, spaceId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .list(ListRoutesRequest.builder()
                        .domainId(domainId)
                        .page(page)
                        .build()))))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByHost() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String host = this.nameFactory.getHostName();

        ScriptedSubscriber<RouteResource> subscriber = ScriptedSubscriber.<RouteResource>create()
            .expectNextCount(1)
            .expectComplete();

        Mono
            .when(
                this.organizationId
                    .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName)),
                this.spaceId
            )
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .host(host)
                    .spaceId(spaceId)
                    .build())))
            .flatMap(response -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .list(ListRoutesRequest.builder()
                        .host(host)
                        .page(page)
                        .build())))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByOrganizationId() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<Long> subscriber = ScriptedSubscriber.<Long>create()
            .consumeNextWith(count -> assertThat(count).isGreaterThan(0))
            .expectComplete();

        this.organizationId
            .then(organizationId -> Mono
                .when(
                    createDomainId(this.cloudFoundryClient, organizationId, domainName),
                    this.spaceId,
                    Mono.just(organizationId)
                ))
            .as(thenKeep(function((domainId, spaceId, organizationId) -> requestCreateRoute(this.cloudFoundryClient, domainId, spaceId))))
            .flatMap(function((domainId, spaceId, organizationId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .list(ListRoutesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build()))))
            .count()
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByPath() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String path = this.nameFactory.getPath();

        ScriptedSubscriber<RouteResource> subscriber = ScriptedSubscriber.<RouteResource>create()
            .expectNextCount(1)
            .expectComplete();

        Mono
            .when(
                this.organizationId
                    .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName)),
                this.spaceId
            )
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .path(path)
                    .spaceId(spaceId)
                    .build())))
            .flatMap(response -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .list(ListRoutesRequest.builder()
                        .page(page)
                        .path(path)
                        .build())))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeApplication() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<ApplicationResource> subscriber = ScriptedSubscriber.<ApplicationResource>create()
            .expectComplete();

        Mono
            .when(
                this.organizationId
                    .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName)),
                this.spaceId
            )
            .then(function((domainId, spaceId) -> Mono.when(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName, null),
                createRouteId(this.cloudFoundryClient, domainId, spaceId)
            )))
            .as(thenKeep(function((applicationId, routeId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId))))
            .as(thenKeep(function((applicationId, routeId) -> this.cloudFoundryClient.routes()
                .removeApplication(RemoveRouteApplicationRequest.builder()
                    .applicationId(applicationId)
                    .routeId(routeId)
                    .build()))))
            .flatMap(function((applicationId, routeId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .page(page)
                        .routeId(routeId)
                        .build()))))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectNext("test-host")
            .expectComplete();

        Mono
            .when(
                this.organizationId
                    .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName)),
                this.spaceId
            )
            .then(function((domainId, spaceId) -> createRouteId(this.cloudFoundryClient, domainId, spaceId)))
            .then(routeId -> this.cloudFoundryClient.routes()
                .update(UpdateRouteRequest.builder()
                    .host("test-host")
                    .routeId(routeId)
                    .build())
                .map(ResourceUtils::getEntity)
                .map(RouteEntity::getHost))
            .subscribe(subscriber);
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

    private static Mono<String> createDomainId(CloudFoundryClient cloudFoundryClient, String organizationId, String domainName) {
        return cloudFoundryClient.domains()
            .create(CreateDomainRequest.builder()
                .name(domainName)
                .owningOrganizationId(organizationId)
                .wildcard(true)
                .build())
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createRouteId(CloudFoundryClient cloudFoundryClient, String domainId, String spaceId) {
        return requestCreateRoute(cloudFoundryClient, domainId, spaceId)
            .map(ResourceUtils::getId);
    }

    private static ScriptedSubscriber<Tuple3<String, String, RouteEntity>> domainIdSpaceIdEquality() {
        return ScriptedSubscriber.<Tuple3<String, String, RouteEntity>>create()
            .consumeNextWith(consumer((domainId, spaceId, entity) -> {
                assertThat(entity.getDomainId()).isEqualTo(domainId);
                assertThat(entity.getSpaceId()).isEqualTo(spaceId);
            }))
            .expectComplete();
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
