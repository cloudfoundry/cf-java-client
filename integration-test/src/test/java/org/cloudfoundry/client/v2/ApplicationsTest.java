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

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.AbstractApplicationResource;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationEnvironmentRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstanceInfo;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesResponse;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsRequest;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteResponse;
import org.cloudfoundry.client.v2.applications.CopyApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v2.applications.DownloadApplicationDropletRequest;
import org.cloudfoundry.client.v2.applications.DownloadApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationRoutesRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.applications.RemoveApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.RemoveApplicationServiceBindingRequest;
import org.cloudfoundry.client.v2.applications.RestageApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.applications.TerminateApplicationInstanceRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationResponse;
import org.cloudfoundry.client.v2.applications.UploadApplicationRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationResponse;
import org.cloudfoundry.client.v2.domains.CreateDomainRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingEntity;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.OperationUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.subscriber.ScriptedSubscriber;
import reactor.util.function.Tuple2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

import static org.cloudfoundry.client.ZipExpectations.zipEquality;
import static org.cloudfoundry.util.DelayUtils.exponentialBackOff;
import static org.cloudfoundry.util.OperationUtils.thenKeep;
import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static reactor.core.publisher.Mono.when;

public final class ApplicationsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private Mono<String> spaceId;

    @Autowired
    private Mono<String> stackId;

    @Test
    public void associateRoute() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<Tuple2<String, String>> subscriber = tupleEquality();

        Mono
            .when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> Mono.when(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName),
                createRouteWithDomain(this.cloudFoundryClient, organizationId, spaceId, domainName, "test-host", "/test/path")
                    .map(ResourceUtils::getId)
            )))
            .as(thenKeep(function((applicationId, routeId) -> requestAssociateRoute(this.cloudFoundryClient, applicationId, routeId))))
            .then(function((applicationId, routeId) -> Mono.when(
                getSingleRouteId(this.cloudFoundryClient, applicationId),
                Mono.just(routeId)
            )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void copy() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String copyApplicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Tuple2<byte[], byte[]>> subscriber = tupleEquality();

        this.spaceId
            .then(spaceId -> when(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
                    .as(thenKeep(applicationId -> uploadApplication(this.cloudFoundryClient, applicationId))),
                requestCreateApplication(this.cloudFoundryClient, spaceId, copyApplicationName)
                    .map(ResourceUtils::getId)
            ))
            .as(thenKeep(function((sourceId, targetId) -> this.cloudFoundryClient.applicationsV2()
                .copy(CopyApplicationRequest.builder()
                    .applicationId(targetId)
                    .sourceApplicationId(sourceId)
                    .build())
                .then(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, job))
            )))
            .then(function((sourceId, targetId) -> when(
                downloadApplication(this.cloudFoundryClient, sourceId),
                downloadApplication(this.cloudFoundryClient, targetId)
            )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        Function<Tuple2<String, ApplicationEntity>, Optional<String>> assertion = function((spaceId, entity) -> {
            if (!spaceId.equals(entity.getSpaceId())) {
                return Optional.of(String.format("expected space id: %s; actual space id: %s", spaceId, entity.getSpaceId()));
            }

            if (!applicationName.equals(entity.getName())) {
                return Optional.of(String.format("expected application name: %s; actual application name: %s", applicationName, entity.getName()));
            }

            return Optional.empty();
        });

        ScriptedSubscriber<Tuple2<String, ApplicationEntity>> subscriber = ScriptedSubscriber.<Tuple2<String, ApplicationEntity>>create()
            .expectValueWith(tuple -> !assertion.apply(tuple).isPresent(),
                tuple -> assertion.apply(tuple).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching result")))
            .expectComplete();

        this.spaceId
            .then(spaceId -> when(
                Mono.just(spaceId),
                requestCreateApplication(this.cloudFoundryClient, spaceId, applicationName)
                    .map(ResourceUtils::getEntity)
            ))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<AbstractApplicationResource> subscriber = errorExpectation(CloudFoundryException.class, "CF-AppNotFound\\([0-9]+\\): The app could not be found: .*");

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .as(thenKeep(applicationId -> this.cloudFoundryClient.applicationsV2()
                .delete(DeleteApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build())))
            .then(applicationId -> requestGetApplication(this.cloudFoundryClient, applicationId))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void downloadDroplet() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<byte[]> subscriber = isTestApplicationDroplet();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .as(thenKeep(applicationId -> uploadAndStartApplication(this.cloudFoundryClient, applicationId)))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV2()
                .downloadDroplet(DownloadApplicationDropletRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .as(OperationUtils::collectByteArray))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void environment() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Tuple2<String, String>> subscriber = tupleEquality();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> when(
                Mono.just(applicationId),
                this.cloudFoundryClient.applicationsV2()
                    .environment(ApplicationEnvironmentRequest.builder()
                        .applicationId(applicationId)
                        .build())
                    .map(response -> getStringApplicationEnvValue(response.getApplicationEnvironmentJsons(), "VCAP_APPLICATION", "application_id")))
            )
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Tuple2<String, AbstractApplicationResource>> subscriber = applicationIdAndNameEquality(applicationName);

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> when(
                Mono.just(applicationId),
                requestGetApplication(this.cloudFoundryClient, applicationId)
            ))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Tuple2<String, AbstractApplicationResource>> subscriber = applicationIdAndNameEquality(applicationName);

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> when(
                Mono.just(applicationId),
                PaginationUtils
                    .requestClientV2Resources(page -> this.cloudFoundryClient.applicationsV2()
                        .list(ListApplicationsRequest.builder()
                            .page(page)
                            .build()))
                    .filter(resource -> ResourceUtils.getId(resource).equals(applicationId))
                    .single()
                    .cast(AbstractApplicationResource.class)
            ))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByDiego() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Tuple2<String, AbstractApplicationResource>> subscriber = applicationIdAndNameEquality(applicationName);

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> when(
                Mono.just(applicationId),
                PaginationUtils
                    .requestClientV2Resources(page -> this.cloudFoundryClient.applicationsV2()
                        .list(ListApplicationsRequest.builder()
                            .diego(true)
                            .page(page)
                            .build()))
                    .filter(resource -> ResourceUtils.getId(resource).equals(applicationId))
                    .single()
                    .cast(AbstractApplicationResource.class)
            ))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByName() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Tuple2<String, AbstractApplicationResource>> subscriber = applicationIdAndNameEquality(applicationName);

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> when(
                Mono.just(applicationId),
                PaginationUtils
                    .requestClientV2Resources(page -> this.cloudFoundryClient.applicationsV2()
                        .list(ListApplicationsRequest.builder()
                            .name(applicationName)
                            .page(page)
                            .build()))
                    .filter(resource -> ResourceUtils.getId(resource).equals(applicationId))
                    .single()
                    .cast(AbstractApplicationResource.class)
            ))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByOrganizationId() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Tuple2<String, AbstractApplicationResource>> subscriber = applicationIdAndNameEquality(applicationName);

        when(
            this.organizationId,
            this.spaceId
                .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
        )
            .then(function((organizationId, applicationId) -> when(
                Mono.just(applicationId),
                PaginationUtils
                    .requestClientV2Resources(page -> this.cloudFoundryClient.applicationsV2()
                        .list(ListApplicationsRequest.builder()
                            .organizationId(organizationId)
                            .page(page)
                            .build()))
                    .filter(resource -> ResourceUtils.getId(resource).equals(applicationId))
                    .single()
                    .cast(AbstractApplicationResource.class)
            )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterBySpaceId() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Tuple2<String, AbstractApplicationResource>> subscriber = applicationIdAndNameEquality(applicationName);

        this.spaceId
            .then(spaceId -> when(
                Mono.just(spaceId),
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
            ))
            .then(function((spaceId, applicationId) -> when(
                Mono.just(applicationId),
                PaginationUtils
                    .requestClientV2Resources(page -> this.cloudFoundryClient.applicationsV2()
                        .list(ListApplicationsRequest.builder()
                            .spaceId(spaceId)
                            .page(page)
                            .build()))
                    .filter(resource -> ResourceUtils.getId(resource).equals(applicationId))
                    .single()
                    .cast(AbstractApplicationResource.class)
            )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByStackId() {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Tuple2<String, AbstractApplicationResource>> subscriber = applicationIdAndNameEquality(applicationName);

        when(
            this.stackId,
            this.spaceId
                .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
        )
            .then(function((stackId, applicationId) -> when(
                Mono.just(applicationId),
                PaginationUtils
                    .requestClientV2Resources(page -> this.cloudFoundryClient.applicationsV2()
                        .list(ListApplicationsRequest.builder()
                            .stackId(stackId)
                            .page(page)
                            .build()))
                    .filter(resource -> ResourceUtils.getId(resource).equals(applicationId))
                    .single()
                    .cast(AbstractApplicationResource.class)
            )))
            .subscribe(subscriber);
    }

    @Test
    public void listRoutes() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<Tuple2<String, String>> subscriber = tupleEquality();

        when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> when(
                Mono.just(organizationId),
                Mono.just(spaceId),
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
            )))
            .then(function((organizationId, spaceId, applicationId) -> when(
                Mono.just(applicationId),
                createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
            )))
            .then(function((applicationId, routeResponse) -> when(
                Mono.just(ResourceUtils.getId(routeResponse)),
                PaginationUtils
                    .requestClientV2Resources(page -> this.cloudFoundryClient.applicationsV2()
                        .listRoutes(ListApplicationRoutesRequest.builder()
                            .applicationId(applicationId)
                            .page(page)
                            .build()))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRoutesFilterByDomainId() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<Tuple2<String, String>> subscriber = tupleEquality();

        when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> when(
                Mono.just(organizationId),
                Mono.just(spaceId),
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
            )))
            .then(function((organizationId, spaceId, applicationId) -> when(
                Mono.just(applicationId),
                createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
            )))
            .then(function((applicationId, routeResponse) -> when(
                Mono.just(ResourceUtils.getId(routeResponse)),
                PaginationUtils
                    .requestClientV2Resources(page -> this.cloudFoundryClient.applicationsV2()
                        .listRoutes(ListApplicationRoutesRequest.builder()
                            .applicationId(applicationId)
                            .domainId(ResourceUtils.getEntity(routeResponse).getDomainId())
                            .page(page)
                            .build()))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRoutesFilterByHost() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<Tuple2<String, String>> subscriber = tupleEquality();

        when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> when(
                Mono.just(organizationId),
                Mono.just(spaceId),
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
            )))
            .then(function((organizationId, spaceId, applicationId) -> when(
                Mono.just(applicationId),
                createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
            )))
            .then(function((applicationId, routeResponse) -> when(
                Mono.just(ResourceUtils.getId(routeResponse)),
                PaginationUtils
                    .requestClientV2Resources(page -> this.cloudFoundryClient.applicationsV2()
                        .listRoutes(ListApplicationRoutesRequest.builder()
                            .applicationId(applicationId)
                            .host(ResourceUtils.getEntity(routeResponse).getHost())
                            .page(page)
                            .build()))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRoutesFilterByPath() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<Tuple2<String, String>> subscriber = tupleEquality();

        when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> when(
                Mono.just(organizationId),
                Mono.just(spaceId),
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
            )))
            .then(function((organizationId, spaceId, applicationId) -> when(
                Mono.just(applicationId),
                createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
            )))
            .then(function((applicationId, routeResponse) -> when(
                Mono.just(ResourceUtils.getId(routeResponse)),
                PaginationUtils
                    .requestClientV2Resources(page -> this.cloudFoundryClient.applicationsV2()
                        .listRoutes(ListApplicationRoutesRequest.builder()
                            .applicationId(applicationId)
                            .path(ResourceUtils.getEntity(routeResponse).getPath())
                            .page(page)
                            .build()))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRoutesFilterByPort() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<Tuple2<String, String>> subscriber = tupleEquality();

        when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> when(
                Mono.just(organizationId),
                Mono.just(spaceId),
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
            )))
            .then(function((organizationId, spaceId, applicationId) -> when(
                Mono.just(applicationId),
                createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
            )))
            .then(function((applicationId, routeResponse) -> when(
                Mono.just(ResourceUtils.getId(routeResponse)),
                PaginationUtils
                    .requestClientV2Resources(page -> {
                        ListApplicationRoutesRequest.Builder builder = ListApplicationRoutesRequest.builder()
                            .applicationId(applicationId)
                            .page(page);

                        Optional.ofNullable(ResourceUtils.getEntity(routeResponse).getPort()).ifPresent(builder::port);

                        return this.cloudFoundryClient.applicationsV2()
                            .listRoutes(builder
                                .build());
                    })
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceBindings() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        ScriptedSubscriber<Tuple2<String, String>> subscriber = tupleEquality();

        this.spaceId
            .then(spaceId -> when(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName),
                createUserServiceInstanceId(this.cloudFoundryClient, spaceId, serviceInstanceName)
            ))
            .as(thenKeep(function((applicationId, serviceInstanceId) -> createServiceBindingId(this.cloudFoundryClient, applicationId, serviceInstanceId))))
            .then(function((applicationId, serviceInstanceId) -> when(
                Mono.just(serviceInstanceId),
                getSingleServiceBindingInstanceId(this.cloudFoundryClient, applicationId)
            )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceBindingsFilterByServiceInstanceId() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        ScriptedSubscriber<Tuple2<String, String>> subscriber = tupleEquality();

        this.spaceId
            .then(spaceId -> when(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName),
                createUserServiceInstanceId(this.cloudFoundryClient, spaceId, serviceInstanceName)
            ))
            .then(function((applicationId, serviceInstanceId) -> when(
                Mono.just(applicationId),
                Mono.just(serviceInstanceId),
                createServiceBindingId(this.cloudFoundryClient, applicationId, serviceInstanceId)
            )))
            .then(function((applicationId, serviceInstanceId, serviceBindingId) -> when(
                Mono.just(serviceBindingId),
                PaginationUtils
                    .requestClientV2Resources(page -> this.cloudFoundryClient.applicationsV2()
                        .listServiceBindings(ListApplicationServiceBindingsRequest.builder()
                            .applicationId(applicationId)
                            .serviceInstanceId(serviceInstanceId)
                            .page(page)
                            .build()))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeRoute() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<RouteResource> subscriber = ScriptedSubscriber.<RouteResource>create()
            .expectComplete();

        when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> when(
                Mono.just(organizationId),
                Mono.just(spaceId),
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
            )))
            .then(function((organizationId, spaceId, applicationId) -> when(
                Mono.just(applicationId),
                createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
            )))
            .as(thenKeep(function((applicationId, routeResponse) -> this.cloudFoundryClient.applicationsV2()
                .removeRoute(RemoveApplicationRouteRequest.builder()
                    .applicationId(applicationId)
                    .routeId(ResourceUtils.getId(routeResponse))
                    .build()))))
            .flatMap(function((applicationId, routeResponse) -> requestRoutes(this.cloudFoundryClient, applicationId)))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeServiceBinding() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        ScriptedSubscriber<ServiceBindingResource> subscriber = ScriptedSubscriber.<ServiceBindingResource>create()
            .expectComplete();

        this.spaceId
            .then(spaceId -> when(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName),
                createUserServiceInstanceId(this.cloudFoundryClient, spaceId, serviceInstanceName)
            ))
            .then(function((applicationId, serviceInstanceId) -> when(
                Mono.just(applicationId),
                createServiceBindingId(this.cloudFoundryClient, applicationId, serviceInstanceId)
            )))
            .as(thenKeep(function((applicationId, serviceBindingId) -> this.cloudFoundryClient.applicationsV2()
                .removeServiceBinding(RemoveApplicationServiceBindingRequest.builder()
                    .applicationId(applicationId)
                    .serviceBindingId(serviceBindingId)
                    .build()))))
            .flatMap(function((applicationId, serviceBindingId) -> requestServiceBindings(this.cloudFoundryClient, applicationId)))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void restage() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        Function<AbstractApplicationResource, Optional<String>> assertion = resource -> {
            if (!applicationName.equals(ResourceUtils.getEntity(resource).getName())) {
                return Optional.of(String.format("expected value: %s; actual value: %s", applicationName, ResourceUtils.getEntity(resource).getName()));
            }

            return Optional.empty();
        };

        ScriptedSubscriber<AbstractApplicationResource> subscriber = ScriptedSubscriber.<AbstractApplicationResource>create()
            .expectValueWith(resource -> !assertion.apply(resource).isPresent(),
                resource -> assertion.apply(resource).orElseThrow(() -> new IllegalStateException("Cannot generate assertion message for matching result")))
            .expectComplete();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .as(thenKeep(applicationId -> uploadAndStartApplication(this.cloudFoundryClient, applicationId)))
            .as(thenKeep(applicationId -> this.cloudFoundryClient.applicationsV2()
                .restage(RestageApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build())))
            .then(applicationId -> waitForStagingApplication(this.cloudFoundryClient, applicationId))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void statistics() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectValue(applicationName)
            .expectComplete();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .as(thenKeep(applicationId -> uploadAndStartApplication(this.cloudFoundryClient, applicationId)))
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .statistics(ApplicationStatisticsRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .map(instanceStatistics -> instanceStatistics.getInstances().get("0").getStatistics().getName()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void summary() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Tuple2<String, String>> subscriber = tupleEquality();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .summary(SummaryApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .map(SummaryApplicationResponse::getId)
                .and(Mono.just(applicationId)))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void terminateInstance() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<ApplicationInstanceInfo> subscriber = ScriptedSubscriber
            .<ApplicationInstanceInfo>expectValueCount(1)
            .expectComplete();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .as(thenKeep(applicationId -> uploadAndStartApplication(this.cloudFoundryClient, applicationId)))
            .then(applicationId -> when(
                Mono.just(applicationId),
                getInstanceInfo(this.cloudFoundryClient, applicationId, "0")
                    .map(info -> Optional.ofNullable(info.getSince()))
            ))
            .as(thenKeep(function((applicationId, optionalSince) -> this.cloudFoundryClient.applicationsV2()
                .terminateInstance(TerminateApplicationInstanceRequest.builder()
                    .applicationId(applicationId)
                    .index("0")
                    .build()))))
            .then(function((applicationId, optionalSince) -> waitForInstanceRestart(this.cloudFoundryClient, applicationId, "0", optionalSince)))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String applicationName2 = this.nameFactory.getApplicationName();

        Function<Tuple2<ApplicationEntity, ApplicationEntity>, Optional<String>> assertion = function((entity1, entity2) -> {
            if (!applicationName2.equals(entity1.getName())) {
                return Optional.of(String.format("expected changed name: %s; actual changed name: %s", applicationName2, entity1.getName()));
            }

            if (!Collections.singletonMap("test-var", "test-value").equals(entity1.getEnvironmentJsons())) {
                return Optional.of(String.format("expected environment: %s; actual environment: %s", Collections.singletonMap("test-var", "test-value"), entity1.getEnvironmentJsons()));
            }

            if (!entity2.getEnvironmentJsons().isEmpty()) {
                return Optional.of(String.format("expected empty environment; actual environment: %s", entity2.getEnvironmentJsons()));
            }

            return Optional.empty();
        });

        ScriptedSubscriber<Tuple2<ApplicationEntity, ApplicationEntity>> subscriber = ScriptedSubscriber.<Tuple2<ApplicationEntity, ApplicationEntity>>create()
            .expectValueWith(tuple -> !assertion.apply(tuple).isPresent(),
                tuple -> assertion.apply(tuple).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching result")))
            .expectComplete();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .update(UpdateApplicationRequest.builder()
                    .applicationId(applicationId)
                    .name(applicationName2)
                    .environmentJson("test-var", "test-value")
                    .build())
                .map(ResourceUtils::getId))
            .then(applicationId ->
                when(
                    Mono.just(applicationId),
                    requestGetApplication(this.cloudFoundryClient, applicationId)
                        .map(ResourceUtils::getEntity)
                ))
            .then(function((applicationId, entity1) ->
                when(
                    Mono.just(entity1),
                    this.cloudFoundryClient.applicationsV2()
                        .update(UpdateApplicationRequest.builder()
                            .applicationId(applicationId)
                            .environmentJsons(Collections.emptyMap())
                            .build())
                        .then(requestGetApplication(this.cloudFoundryClient, applicationId)
                            .map(ResourceUtils::getEntity))
                )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void uploadAndDownload() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Tuple2<byte[], byte[]>> subscriber = zipEquality();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .as(thenKeep(applicationId -> uploadApplication(this.cloudFoundryClient, applicationId)))
            .then(applicationId -> when(
                downloadApplication(this.cloudFoundryClient, applicationId),
                getBytes("test-application.zip")
            ))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void uploadAndDownloadAsyncFalse() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Tuple2<byte[], byte[]>> subscriber = zipEquality();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .as(thenKeep(applicationId -> uploadApplicationAsyncFalse(this.cloudFoundryClient, applicationId)))
            .then(applicationId -> when(
                downloadApplication(this.cloudFoundryClient, applicationId),
                getBytes("test-application.zip")
            ))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    private static ScriptedSubscriber<Tuple2<String, AbstractApplicationResource>> applicationIdAndNameEquality(String name) {
        Assert.notNull(name, "name must not be null");

        Function<Tuple2<String, AbstractApplicationResource>, Optional<String>> assertion = function((applicationId, resource) -> {
            if (!applicationId.equals(ResourceUtils.getId(resource))) {
                return Optional.of(String.format("expected id: %s; actual id %s", applicationId, ResourceUtils.getId(resource)));
            }

            if (!name.equals(ResourceUtils.getEntity(resource).getName())) {
                return Optional.of(String.format("expected name: %s; actual name %s", name, ResourceUtils.getEntity(resource).getName()));
            }

            return Optional.empty();
        });

        return ScriptedSubscriber.<Tuple2<String, AbstractApplicationResource>>create()
            .expectValueWith(tuple -> !assertion.apply(tuple).isPresent(),
                tuple -> assertion.apply(tuple).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching application id an name")))
            .expectComplete();
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName, "staticfile_buildpack", true, 512, 64)
            .map(ResourceUtils::getId);
    }

    private static Mono<CreateRouteResponse> createApplicationRoute(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceId, String domainName, String applicationId) {
        return createRouteWithDomain(cloudFoundryClient, organizationId, spaceId, domainName, "test-host", "/test-path")
            .then(createRouteResponse -> requestAssociateRoute(cloudFoundryClient, applicationId, createRouteResponse.getMetadata().getId())
                .map(response -> createRouteResponse));
    }

    private static Mono<CreateRouteResponse> createRouteWithDomain(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceId, String domainName, String host, String path) {
        return cloudFoundryClient.domains()
            .create(CreateDomainRequest.builder()
                .name(domainName)
                .owningOrganizationId(organizationId)
                .wildcard(true)
                .build())
            .map(ResourceUtils::getId)
            .then(domainId -> cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .host(host)
                    .path(path)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Mono<String> createServiceBindingId(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId) {
        return requestCreateServiceBinding(cloudFoundryClient, applicationId, serviceInstanceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createUserServiceInstanceId(CloudFoundryClient cloudFoundryClient, String spaceId, String serviceInstanceName) {
        return requestCreateUserServiceInstance(cloudFoundryClient, spaceId, serviceInstanceName)
            .map(ResourceUtils::getId);
    }

    private static Mono<ApplicationInstanceInfo> getInstanceInfo(CloudFoundryClient cloudFoundryClient, String applicationId, String instanceName) {
        return requestInstances(cloudFoundryClient, applicationId)
            .filter(response -> response.getInstances().containsKey(instanceName))
            .map(response -> response.getInstances().get(instanceName));
    }

    private static Mono<String> getSingleRouteId(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestRoutes(cloudFoundryClient, applicationId)
            .single()
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getSingleServiceBindingInstanceId(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestServiceBindings(cloudFoundryClient, applicationId)
            .single()
            .map(ResourceUtils::getEntity)
            .map(ServiceBindingEntity::getServiceInstanceId);
    }

    @SuppressWarnings("unchecked")
    private static String getStringApplicationEnvValue(Map<String, Object> environment, String... keys) {
        for (int i = 0; i < keys.length - 1; ++i) {
            environment = (Map<String, Object>) environment.get(keys[i]);
        }
        return (String) environment.get(keys[keys.length - 1]);
    }

    private static boolean isIdentical(Double expected, Double actual) {
        return expected == null ? actual == null : expected.equals(actual);
    }

    private static ScriptedSubscriber<byte[]> isTestApplicationDroplet() {
        Function<byte[], Optional<String>> assertion = bytes -> {
            Set<String> names = new HashSet<>();

            try (TarArchiveInputStream in = new TarArchiveInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes)))) {
                TarArchiveEntry entry;
                while ((entry = in.getNextTarEntry()) != null) {
                    names.add(entry.getName());
                }
            } catch (IOException e) {
                throw Exceptions.propagate(e);
            }

            if (!names.contains("./app/Staticfile")) {
                return Optional.of("Application droplet does not have ./app/Staticfile");
            }

            if (!names.contains("./app/public/index.html")) {
                return Optional.of("Application droplet does not have ./app/public/index.html");
            }

            return Optional.empty();
        };

        return ScriptedSubscriber.<byte[]>create()
            .expectValueWith(bytes -> !assertion.apply(bytes).isPresent(),
                bytes -> assertion.apply(bytes).orElseThrow(() -> new IllegalStateException("Cannot generate assertion message for matching result")))
            .expectComplete();
    }

    private static Mono<AssociateApplicationRouteResponse> requestAssociateRoute(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return cloudFoundryClient.applicationsV2()
            .associateRoute(AssociateApplicationRouteRequest.builder()
                .applicationId(applicationId)
                .routeId(routeId)
                .build());
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName, null, null, null, null);
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName, String buildpack, Boolean diego, Integer
        diskQuota, Integer memory) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .buildpack(buildpack)
                .diego(diego)
                .diskQuota(diskQuota)
                .memory(memory)
                .name(applicationName)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateServiceBindingResponse> requestCreateServiceBinding(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId) {
        return cloudFoundryClient.serviceBindingsV2()
            .create(CreateServiceBindingRequest.builder()
                .applicationId(applicationId)
                .serviceInstanceId(serviceInstanceId)
                .build());
    }

    private static Mono<CreateUserProvidedServiceInstanceResponse> requestCreateUserServiceInstance(CloudFoundryClient cloudFoundryClient, String spaceId, String name) {
        return cloudFoundryClient.userProvidedServiceInstances()
            .create(CreateUserProvidedServiceInstanceRequest.builder()
                .name(name)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<AbstractApplicationResource> requestGetApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .get(GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build())
            .cast(AbstractApplicationResource.class);
    }

    private static Mono<ApplicationInstancesResponse> requestInstances(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Flux<RouteResource> requestRoutes(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.applicationsV2()
            .listRoutes(ListApplicationRoutesRequest.builder()
                .page(page)
                .applicationId(applicationId)
                .build()));
    }

    private static Flux<ServiceBindingResource> requestServiceBindings(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.applicationsV2()
                .listServiceBindings(ListApplicationServiceBindingsRequest.builder()
                    .applicationId(applicationId)
                    .page(page)
                    .build()));
    }

    private static Mono<UpdateApplicationResponse> requestUpdateApplicationState(CloudFoundryClient cloudFoundryClient, String applicationId, String state) {
        return cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .state(state)
                .build());
    }

    private static Mono<ApplicationInstanceInfo> startApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestUpdateApplicationState(cloudFoundryClient, applicationId, "STARTED")
            .then(waitForStagingApplication(cloudFoundryClient, applicationId))
            .then(waitForStartingInstanceInfo(cloudFoundryClient, applicationId));
    }

    private static Mono<ApplicationInstanceInfo> uploadAndStartApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return uploadApplication(cloudFoundryClient, applicationId)
            .then(startApplication(cloudFoundryClient, applicationId));
    }

    private static Mono<Void> uploadApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        try {
            return cloudFoundryClient.applicationsV2()
                .upload(UploadApplicationRequest.builder()
                    .application(new ClassPathResource("test-application.zip").getInputStream())
                    .async(true)
                    .applicationId(applicationId)
                    .build())
                .then(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Mono<UploadApplicationResponse> uploadApplicationAsyncFalse(CloudFoundryClient cloudFoundryClient, String applicationId) {
        try {
            final InputStream inputStream = new ClassPathResource("test-application.zip").getInputStream();
            return cloudFoundryClient.applicationsV2()
                .upload(UploadApplicationRequest.builder()
                    .application(inputStream)
                    .async(false)
                    .applicationId(applicationId)
                    .build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Mono<ApplicationInstanceInfo> waitForInstanceRestart(CloudFoundryClient cloudFoundryClient, String applicationId, String instanceName, Optional<Double> optionalSince) {
        return getInstanceInfo(cloudFoundryClient, applicationId, instanceName)
            .filter(info -> !isIdentical(info.getSince(), optionalSince.orElse(null)))
            .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5)));
    }

    private static Mono<AbstractApplicationResource> waitForStagingApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestGetApplication(cloudFoundryClient, applicationId)
            .filter(response -> "STAGED".equals(response.getEntity().getPackageState()))
            .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5)));
    }

    private static Mono<ApplicationInstanceInfo> waitForStartingInstanceInfo(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build())
            .flatMap(response -> Flux.fromIterable(response.getInstances().values()))
            .filter(applicationInstanceInfo -> "RUNNING".equals(applicationInstanceInfo.getState()))
            .next()
            .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5)));
    }

    private Mono<byte[]> downloadApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .download(DownloadApplicationRequest.builder()
                .applicationId(applicationId)
                .build())
            .as(OperationUtils::collectByteArray);
    }


}
