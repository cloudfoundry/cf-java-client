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
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.routemappings.CreateRouteMappingRequest;
import org.cloudfoundry.client.v2.routemappings.CreateRouteMappingResponse;
import org.cloudfoundry.client.v2.routemappings.DeleteRouteMappingRequest;
import org.cloudfoundry.client.v2.routemappings.GetRouteMappingRequest;
import org.cloudfoundry.client.v2.routemappings.GetRouteMappingResponse;
import org.cloudfoundry.client.v2.routemappings.ListRouteMappingsRequest;
import org.cloudfoundry.client.v2.routemappings.RouteMappingEntity;
import org.cloudfoundry.client.v2.routemappings.RouteMappingResource;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainResponse;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.subscriber.ScriptedSubscriber;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static org.cloudfoundry.util.OperationUtils.thenKeep;
import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static reactor.core.publisher.Mono.when;

public final class RouteMappingsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> spaceId;

    @Test
    public void createSharedDomain() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();

        Function<Tuple3<String, String, RouteMappingEntity>, Optional<String>> assertion = function((applicationId, routeId, entity) -> {
            if (!applicationId.equals(entity.getApplicationId())) {
                return Optional.of(String.format("expected application id: %s; actual application id: %s", applicationId, entity.getApplicationId()));
            }

            if (!routeId.equals(entity.getRouteId())) {
                return Optional.of(String.format("expected route id: %s; actual route id: %s", routeId, entity.getRouteId()));
            }

            return Optional.empty();
        });

        ScriptedSubscriber<Tuple3<String, String, RouteMappingEntity>> subscriber = ScriptedSubscriber.<Tuple3<String, String, RouteMappingEntity>>create()
            .expectValueWith(tuple -> !assertion.apply(tuple).isPresent(),
                tuple -> assertion.apply(tuple).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching entity")))
            .expectComplete();

        Mono
            .when(
                getSharedDomainId(this.cloudFoundryClient, domainName),
                this.spaceId
            )
            .then(function((domainId, spaceId) -> Mono.when(
                getApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                getRouteId(this.cloudFoundryClient, domainId, hostName, spaceId))
            ))
            .then(function((applicationId, routeId) -> Mono
                .when(
                    Mono.just(applicationId),
                    Mono.just(routeId),
                    this.cloudFoundryClient.routeMappings()
                        .create(CreateRouteMappingRequest.builder()
                            .applicationId(applicationId)
                            .routeId(routeId)
                            .build())
                        .map(ResourceUtils::getEntity)
                )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAsyncFalse() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();

        ScriptedSubscriber<GetRouteMappingResponse> subscriber = errorExpectation(CloudFoundryException.class, "CF-RouteMappingNotFound\\([0-9]+\\): The route mapping could not be found: .*");

        this.spaceId
            .then(spaceId -> getRouteMappingId(this.cloudFoundryClient, applicationName, domainName, hostName, spaceId))
            .as(thenKeep(routeMappingId -> this.cloudFoundryClient.routeMappings()
                .delete(DeleteRouteMappingRequest.builder()
                    .async(false)
                    .routeMappingId(routeMappingId)
                    .build())))
            .then(routeMappingId -> requestGetRouteMapping(this.cloudFoundryClient, routeMappingId))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAsyncTrue() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();

        ScriptedSubscriber<GetRouteMappingResponse> subscriber = errorExpectation(CloudFoundryException.class, "CF-RouteMappingNotFound\\([0-9]+\\): The route mapping could not be found: .*");

        this.spaceId
            .then(spaceId -> getRouteMappingId(this.cloudFoundryClient, applicationName, domainName, hostName, spaceId))
            .as(thenKeep(routeMappingId -> this.cloudFoundryClient.routeMappings()
                .delete(DeleteRouteMappingRequest.builder()
                    .async(true)
                    .routeMappingId(routeMappingId)
                    .build())
                .then(job -> JobUtils.waitForCompletion(cloudFoundryClient, job))))
            .then(routeMappingId -> requestGetRouteMapping(this.cloudFoundryClient, routeMappingId))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();

        ScriptedSubscriber<Tuple2<String, String>> subscriber = tupleEquality();

        this.spaceId
            .then(spaceId -> getRouteMappingId(this.cloudFoundryClient, applicationName, domainName, hostName, spaceId))
            .then(routeMappingId -> when(
                Mono.just(routeMappingId),
                this.cloudFoundryClient.routeMappings()
                    .get(GetRouteMappingRequest.builder()
                        .routeMappingId(routeMappingId)
                        .build())
                    .map(ResourceUtils::getId)))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByApplicationId() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();

        ScriptedSubscriber<RouteMappingResource> subscriber = ScriptedSubscriber.<RouteMappingResource>expectValueCount(1)
            .expectComplete();

        this.spaceId
            .then(spaceId -> when(
                getSharedDomainId(this.cloudFoundryClient, domainName),
                Mono.just(spaceId)
            ))
            .then(function((domainId, spaceId) -> when(
                getApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                getRouteId(this.cloudFoundryClient, domainId, hostName, spaceId)
            )))
            .then(function((applicationId, routeId) -> requestCreateRouteMapping(this.cloudFoundryClient, applicationId, routeId)
                .then(Mono.just(applicationId))))
            .flatMap(applicationId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routeMappings()
                    .list(ListRouteMappingsRequest.builder()
                        .page(page)
                        .applicationId(applicationId)
                        .build()))
                .filter(resource -> ResourceUtils.getEntity(resource).getApplicationId().equals(applicationId))
                .single())
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByRouteId() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();

        ScriptedSubscriber<RouteMappingResource> subscriber = ScriptedSubscriber.<RouteMappingResource>expectValueCount(1)
            .expectComplete();

        this.spaceId
            .then(spaceId -> when(
                getSharedDomainId(this.cloudFoundryClient, domainName),
                Mono.just(spaceId)
            ))
            .then(function((domainId, spaceId) -> when(
                getApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                getRouteId(this.cloudFoundryClient, domainId, hostName, spaceId)
            )))
            .then(function((applicationId, routeId) -> requestCreateRouteMapping(this.cloudFoundryClient, applicationId, routeId)
                .then(Mono.just(routeId))))
            .flatMap(routeId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.routeMappings()
                    .list(ListRouteMappingsRequest.builder()
                        .page(page)
                        .routeId(routeId)
                        .build()))
                .filter(resource -> ResourceUtils.getEntity(resource).getRouteId().equals(routeId))
                .single())
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createRouteMappingId(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return requestCreateRouteMapping(cloudFoundryClient, applicationId, routeId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getApplicationId(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getRouteId(CloudFoundryClient cloudFoundryClient, String domainId, String hostName, String spaceId) {
        return requestCreateRoute(cloudFoundryClient, domainId, hostName, spaceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getRouteMappingId(CloudFoundryClient cloudFoundryClient, String applicationName, String domainName, String hostName, String spaceId) {
        return getSharedDomainId(cloudFoundryClient, domainName)
            .then(domainId -> when(
                getApplicationId(cloudFoundryClient, applicationName, spaceId),
                getRouteId(cloudFoundryClient, domainId, hostName, spaceId)
            ))
            .then(function((applicationId, routeId) -> createRouteMappingId(cloudFoundryClient, applicationId, routeId)
            ));
    }

    private static Mono<String> getSharedDomainId(CloudFoundryClient cloudFoundryClient, String domainName) {
        return requestCreateSharedDomain(cloudFoundryClient, domainName)
            .map(ResourceUtils::getId);
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .diego(true)
                .name(applicationName)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, String hostName, String spaceId) {
        return cloudFoundryClient.routes()
            .create(CreateRouteRequest.builder()
                .host(hostName)
                .domainId(domainId)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateRouteMappingResponse> requestCreateRouteMapping(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return cloudFoundryClient.routeMappings()
            .create(CreateRouteMappingRequest.builder()
                .applicationId(applicationId)
                .routeId(routeId)
                .build());
    }

    private static Mono<CreateSharedDomainResponse> requestCreateSharedDomain(CloudFoundryClient cloudFoundryClient, String domainName) {
        return cloudFoundryClient.sharedDomains()
            .create(CreateSharedDomainRequest.builder()
                .name(domainName)
                .build());
    }

    private static Mono<GetRouteMappingResponse> requestGetRouteMapping(CloudFoundryClient cloudFoundryClient, String routeMappingId) {
        return cloudFoundryClient.routeMappings()
            .get(GetRouteMappingRequest.builder()
                .routeMappingId(routeMappingId)
                .build());
    }

}
