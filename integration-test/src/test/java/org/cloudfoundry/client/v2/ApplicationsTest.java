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
import org.cloudfoundry.client.v2.applications.UploadApplicationRequest;
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
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.cloudfoundry.util.tuple.Consumer2;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.tuple.Tuple2;
import reactor.core.util.Exceptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;

import static org.cloudfoundry.client.CompareZips.zipAssertEquivalent;
import static org.cloudfoundry.util.DelayUtils.exponentialBackOff;
import static org.cloudfoundry.util.OperationUtils.thenKeep;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;
import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void associateRoute() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();

        Mono
            .when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> Mono
                .when(
                    createApplicationId(this.cloudFoundryClient, spaceId, applicationName),
                    createRouteWithDomain(cloudFoundryClient, organizationId, spaceId, domainName, "test-host", "/test/path")
                        .map(ResourceUtils::getId)
                )))
            .as(thenKeep(function((applicationId, routeId) -> requestAssociateRoute(this.cloudFoundryClient, applicationId, routeId))))
            .then(function((applicationId, routeId) -> Mono
                .when(
                    getSingleRouteId(cloudFoundryClient, applicationId),
                    Mono.just(routeId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void copy() {
        String applicationName = getApplicationName();
        String copyApplicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> Mono
                .when(
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
                .map(ResourceUtils::getId)
                .then(jobId -> JobUtils.waitForCompletion(this.cloudFoundryClient, jobId))
            )))
            .then(function((sourceId, targetId) -> Mono
                .when(
                    downloadApplication(this.cloudFoundryClient, sourceId),
                    downloadApplication(this.cloudFoundryClient, targetId)
                )))
            .subscribe(this.<Tuple2<byte[], byte[]>>testSubscriber()
                .assertThat(consumer((Consumer2<byte[], byte[]>) Assert::assertArrayEquals)));
    }

    @Test
    public void create() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> Mono
                .when(
                    Mono.just(spaceId),
                    requestCreateApplication(this.cloudFoundryClient, spaceId, applicationName)
                        .map(ResourceUtils::getEntity)
                ))
            .subscribe(this.<Tuple2<String, ApplicationEntity>>testSubscriber()
                .assertThat(consumer((spaceId, entity) -> {
                    assertEquals(spaceId, entity.getSpaceId());
                    assertEquals(applicationName, entity.getName());
                })));
    }

    @Test
    public void delete() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .as(thenKeep(applicationId -> this.cloudFoundryClient.applicationsV2()
                .delete(DeleteApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build())))
            .then(applicationId -> requestGetApplication(this.cloudFoundryClient, applicationId))
            .subscribe(testSubscriber()
                .assertErrorMatch(CloudFoundryException.class, "CF-AppNotFound\\([0-9]+\\): The app could not be found: .*"));
    }

    @Test
    public void downloadDroplet() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> uploadAndStartApplication(this.cloudFoundryClient, applicationId))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV2()
                .downloadDroplet(DownloadApplicationDropletRequest.builder()
                    .applicationId(applicationId)
                    .build()))
            .reduceWith(ByteArrayOutputStream::new, ApplicationsTest::collectIntoByteArrayOutputStream)
            .map(ByteArrayOutputStream::toByteArray)
            .subscribe(this.<byte[]>testSubscriber()
                .assertThat(ApplicationsTest::assertIsTestApplicationDroplet));
    }

    @Test
    public void environment() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> Mono
                .when(
                    Mono.just(applicationId),
                    this.cloudFoundryClient.applicationsV2()
                        .environment(ApplicationEnvironmentRequest.builder()
                            .applicationId(applicationId)
                            .build())
                        .map(response -> getStringApplicationEnvValue(response.getApplicationEnvironmentJsons(), "VCAP_APPLICATION", "application_id")))
            )
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void get() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> Mono
                .when(
                    Mono.just(applicationId),
                    requestGetApplication(cloudFoundryClient, applicationId)
                ))
            .subscribe(this.<Tuple2<String, AbstractApplicationResource>>testSubscriber()
                .assertThat(applicationIdAndResourceMatchesName(applicationName)));
    }

    @Test
    public void list() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> Mono
                .when(
                    Mono.just(applicationId),
                    PaginationUtils
                        .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                            .list(ListApplicationsRequest.builder()
                                .page(page)
                                .build()))
                        .filter(resource -> ResourceUtils.getId(resource).equals(applicationId))
                        .single()
                        .cast(AbstractApplicationResource.class)
                ))
            .subscribe(this.<Tuple2<String, AbstractApplicationResource>>testSubscriber()
                .assertThat(applicationIdAndResourceMatchesName(applicationName)));
    }

    @Test
    public void listFilterByDiego() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> Mono
                .when(
                    Mono.just(applicationId),
                    PaginationUtils
                        .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                            .list(ListApplicationsRequest.builder()
                                .diego(true)
                                .page(page)
                                .build()))
                        .filter(resource -> ResourceUtils.getId(resource).equals(applicationId))
                        .single()
                        .cast(AbstractApplicationResource.class)
                ))
            .subscribe(this.<Tuple2<String, AbstractApplicationResource>>testSubscriber()
                .assertThat(applicationIdAndResourceMatchesName(applicationName)));
    }

    @Test
    public void listFilterByName() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> Mono
                .when(
                    Mono.just(applicationId),
                    PaginationUtils
                        .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                            .list(ListApplicationsRequest.builder()
                                .name(applicationName)
                                .page(page)
                                .build()))
                        .filter(resource -> ResourceUtils.getId(resource).equals(applicationId))
                        .single()
                        .cast(AbstractApplicationResource.class)
                ))
            .subscribe(this.<Tuple2<String, AbstractApplicationResource>>testSubscriber()
                .assertThat(applicationIdAndResourceMatchesName(applicationName)));
    }

    @Test
    public void listFilterByOrganizationId() {
        String applicationName = getApplicationName();

        Mono
            .when(
                this.organizationId,
                this.spaceId
                    .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            )
            .then(function((organizationId, applicationId) -> Mono
                .when(
                    Mono.just(applicationId),
                    PaginationUtils
                        .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                            .list(ListApplicationsRequest.builder()
                                .organizationId(organizationId)
                                .page(page)
                                .build()))
                        .filter(resource -> ResourceUtils.getId(resource).equals(applicationId))
                        .single()
                        .cast(AbstractApplicationResource.class)
                )))
            .subscribe(this.<Tuple2<String, AbstractApplicationResource>>testSubscriber()
                .assertThat(applicationIdAndResourceMatchesName(applicationName)));
    }

    @Test
    public void listFilterBySpaceId() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> Mono
                .when(
                    Mono.just(spaceId),
                    createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
                ))
            .then(function((spaceId, applicationId) -> Mono
                .when(
                    Mono.just(applicationId),
                    PaginationUtils
                        .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                            .list(ListApplicationsRequest.builder()
                                .spaceId(spaceId)
                                .page(page)
                                .build()))
                        .filter(resource -> ResourceUtils.getId(resource).equals(applicationId))
                        .single()
                        .cast(AbstractApplicationResource.class)
                )))
            .subscribe(this.<Tuple2<String, AbstractApplicationResource>>testSubscriber()
                .assertThat(applicationIdAndResourceMatchesName(applicationName)));
    }

    @Test
    public void listFilterByStackId() {
        String applicationName = getApplicationName();

        Mono
            .when(
                this.stackId,
                this.spaceId
                    .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            )
            .then(function((stackId, applicationId) -> Mono
                .when(
                    Mono.just(applicationId),
                    PaginationUtils
                        .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                            .list(ListApplicationsRequest.builder()
                                .stackId(stackId)
                                .page(page)
                                .build()))
                        .filter(resource -> ResourceUtils.getId(resource).equals(applicationId))
                        .single()
                        .cast(AbstractApplicationResource.class)
                )))
            .subscribe(this.<Tuple2<String, AbstractApplicationResource>>testSubscriber()
                .assertThat(applicationIdAndResourceMatchesName(applicationName)));
    }

    @Test
    public void listRoutes() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();

        Mono
            .when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> {
                return Mono
                    .when(
                        Mono.just(organizationId),
                        Mono.just(spaceId),
                        createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
                    );
            }))
            .then(function((organizationId, spaceId, applicationId) -> Mono
                .when(
                    Mono.just(applicationId),
                    createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
                )))
            .then(function((applicationId, routeResponse) -> Mono
                .when(
                    Mono.just(ResourceUtils.getId(routeResponse)),
                    PaginationUtils
                        .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                            .listRoutes(ListApplicationRoutesRequest.builder()
                                .applicationId(applicationId)
                                .page(page)
                                .build()))
                        .single()
                        .map(ResourceUtils::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listRoutesFilterByDomainId() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();

        Mono
            .when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> {
                return Mono
                    .when(
                        Mono.just(organizationId),
                        Mono.just(spaceId),
                        createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
                    );
            }))
            .then(function((organizationId, spaceId, applicationId) -> Mono
                .when(
                    Mono.just(applicationId),
                    createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
                )))
            .then(function((applicationId, routeResponse) -> Mono
                .when(
                    Mono.just(ResourceUtils.getId(routeResponse)),
                    PaginationUtils
                        .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                            .listRoutes(ListApplicationRoutesRequest.builder()
                                .applicationId(applicationId)
                                .domainId(ResourceUtils.getEntity(routeResponse).getDomainId())
                                .page(page)
                                .build()))
                        .single()
                        .map(ResourceUtils::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listRoutesFilterByHost() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();

        Mono
            .when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> {
                return Mono
                    .when(
                        Mono.just(organizationId),
                        Mono.just(spaceId),
                        createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
                    );
            }))
            .then(function((organizationId, spaceId, applicationId) -> Mono
                .when(
                    Mono.just(applicationId),
                    createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
                )))
            .then(function((applicationId, routeResponse) -> Mono
                .when(
                    Mono.just(ResourceUtils.getId(routeResponse)),
                    PaginationUtils
                        .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                            .listRoutes(ListApplicationRoutesRequest.builder()
                                .applicationId(applicationId)
                                .host(ResourceUtils.getEntity(routeResponse).getHost())
                                .page(page)
                                .build()))
                        .single()
                        .map(ResourceUtils::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listRoutesFilterByPath() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();

        Mono
            .when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> {
                return Mono
                    .when(
                        Mono.just(organizationId),
                        Mono.just(spaceId),
                        createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
                    );
            }))
            .then(function((organizationId, spaceId, applicationId) -> Mono
                .when(
                    Mono.just(applicationId),
                    createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
                )))
            .then(function((applicationId, routeResponse) -> Mono
                .when(
                    Mono.just(ResourceUtils.getId(routeResponse)),
                    PaginationUtils
                        .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                            .listRoutes(ListApplicationRoutesRequest.builder()
                                .applicationId(applicationId)
                                .path(ResourceUtils.getEntity(routeResponse).getPath())
                                .page(page)
                                .build()))
                        .single()
                        .map(ResourceUtils::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listRoutesFilterByPort() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();

        Mono
            .when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> {
                return Mono
                    .when(
                        Mono.just(organizationId),
                        Mono.just(spaceId),
                        createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
                    );
            }))
            .then(function((organizationId, spaceId, applicationId) -> Mono
                .when(
                    Mono.just(applicationId),
                    createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
                )))
            .then(function((applicationId, routeResponse) -> Mono
                .when(
                    Mono.just(ResourceUtils.getId(routeResponse)),
                    PaginationUtils
                        .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                            .listRoutes(ListApplicationRoutesRequest.builder()
                                .applicationId(applicationId)
                                .port(ResourceUtils.getEntity(routeResponse).getPort())
                                .page(page)
                                .build()))
                        .single()
                        .map(ResourceUtils::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listServiceBindings() {
        String applicationName = getApplicationName();
        String serviceInstanceName = getServiceInstanceName();

        this.spaceId
            .then(spaceId -> Mono
                .when(
                    createApplicationId(this.cloudFoundryClient, spaceId, applicationName),
                    createUserServiceInstanceId(this.cloudFoundryClient, spaceId, serviceInstanceName)
                ))
            .as(thenKeep(function((applicationId, serviceInstanceId) -> createServiceBindingId(this.cloudFoundryClient, applicationId, serviceInstanceId))))
            .then(function((applicationId, serviceInstanceId) -> Mono
                .when(
                    Mono.just(serviceInstanceId),
                    getSingleServiceBindingInstanceId(this.cloudFoundryClient, applicationId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listServiceBindingsFilterByServiceInstanceId() {
        String applicationName = getApplicationName();
        String serviceInstanceName = getServiceInstanceName();

        this.spaceId
            .then(spaceId -> Mono
                .when(
                    createApplicationId(this.cloudFoundryClient, spaceId, applicationName),
                    createUserServiceInstanceId(this.cloudFoundryClient, spaceId, serviceInstanceName)
                ))
            .then(function((applicationId, serviceInstanceId) -> Mono
                .when(
                    Mono.just(applicationId),
                    Mono.just(serviceInstanceId),
                    createServiceBindingId(this.cloudFoundryClient, applicationId, serviceInstanceId)
                )))
            .then(function((applicationId, serviceInstanceId, serviceBindingId) -> Mono
                .when(
                    Mono.just(serviceBindingId),
                    PaginationUtils
                        .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                            .listServiceBindings(ListApplicationServiceBindingsRequest.builder()
                                .applicationId(applicationId)
                                .serviceInstanceId(serviceInstanceId)
                                .page(page)
                                .build()))
                        .single()
                        .map(ResourceUtils::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void removeRoute() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();

        Mono
            .when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> {
                return Mono
                    .when(
                        Mono.just(organizationId),
                        Mono.just(spaceId),
                        createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
                    );
            }))
            .then(function((organizationId, spaceId, applicationId) -> Mono
                .when(
                    Mono.just(applicationId),
                    createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
                )))
            .as(thenKeep(function((applicationId, routeResponse) -> this.cloudFoundryClient.applicationsV2()
                .removeRoute(RemoveApplicationRouteRequest.builder()
                    .applicationId(applicationId)
                    .routeId(ResourceUtils.getId(routeResponse))
                    .build()))))
            .flatMap(function((applicationId, routeResponse) -> requestRoutes(this.cloudFoundryClient, applicationId)))
            .subscribe(testSubscriber());
    }

    @Test
    public void removeServiceBinding() {
        String applicationName = getApplicationName();
        String serviceInstanceName = getServiceInstanceName();

        this.spaceId
            .then(spaceId -> Mono
                .when(
                    createApplicationId(this.cloudFoundryClient, spaceId, applicationName),
                    createUserServiceInstanceId(cloudFoundryClient, spaceId, serviceInstanceName)
                ))
            .then(function((applicationId, serviceInstanceId) -> Mono
                .when(
                    Mono.just(applicationId),
                    createServiceBindingId(cloudFoundryClient, applicationId, serviceInstanceId)
                )))
            .as(thenKeep(function((applicationId, serviceBindingId) -> this.cloudFoundryClient.applicationsV2()
                .removeServiceBinding(RemoveApplicationServiceBindingRequest.builder()
                    .applicationId(applicationId)
                    .serviceBindingId(serviceBindingId)
                    .build()))))
            .flatMap(function((applicationId, serviceBindingId) -> requestServiceBindings(this.cloudFoundryClient, applicationId)))
            .subscribe(this.testSubscriber());
    }

    @Test
    public void restage() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> uploadAndStartApplication(this.cloudFoundryClient, applicationId))
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .restage(RestageApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .map(ResourceUtils::getId))
            .then(applicationId -> waitForStagingApplication(this.cloudFoundryClient, applicationId))
            .subscribe(this.<AbstractApplicationResource>testSubscriber()
                .assertThat(resource -> assertEquals(applicationName, ResourceUtils.getEntity(resource).getName())));
    }

    @Test
    public void statistics() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> uploadAndStartApplication(this.cloudFoundryClient, applicationId))
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .statistics(ApplicationStatisticsRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .map(instanceStatistics -> instanceStatistics.get("0").getStatistics().getName()))
            .subscribe(this.testSubscriber()
                .assertEquals(applicationName));
    }

    @Test
    public void summary() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .summary(SummaryApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .map(SummaryApplicationResponse::getId)
                .and(Mono.just(applicationId)))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void terminateInstance() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> uploadAndStartApplication(this.cloudFoundryClient, applicationId))
            .then(applicationId -> Mono
                .when(
                    Mono.just(applicationId),
                    getInstanceInfo(this.cloudFoundryClient, applicationId, "0")
                        .map(info -> Optional.ofNullable(info.getSince()))
                ))
            .as(thenKeep(function((applicationId, optionalSince) -> this.cloudFoundryClient.applicationsV2()
                .terminateInstance(TerminateApplicationInstanceRequest.builder()
                    .applicationId(applicationId)
                    .index("0")
                    .build()))))
            .then(function((applicationId, optionalSince) -> waitForInstanceRestart(cloudFoundryClient, applicationId, "0", optionalSince)))
            .subscribe(this.testSubscriber()
                .assertCount(1));
    }

    @Test
    public void update() {
        String applicationName = getApplicationName();
        String applicationName2 = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .update(UpdateApplicationRequest.builder()
                    .applicationId(applicationId)
                    .name(applicationName2)
                    .build())
                .map(ResourceUtils::getId))
            .then(applicationId -> requestGetApplication(this.cloudFoundryClient, applicationId)
                .map(ResourceUtils::getEntity)
                .map(ApplicationEntity::getName))
            .subscribe(testSubscriber()
                .assertEquals(applicationName2));
    }

    @Test
    public void uploadAndDownload() {
        String applicationName = getApplicationName();

        String testApplicationName = "test-application.zip";
        Resource testApplication = new ClassPathResource(testApplicationName);

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> uploadApplication(this.cloudFoundryClient, applicationId))
            .then(applicationId -> Mono
                .when(
                    downloadApplication(cloudFoundryClient, applicationId),
                    getByteArrayFrom(testApplicationName, testApplication)
                ))
            .subscribe(this.<Tuple2<byte[], byte[]>>testSubscriber()
                .assertThat(consumer((bytes1, bytes2) -> zipAssertEquivalent(new ByteArrayInputStream(bytes1), new ByteArrayInputStream(bytes2)))));
    }

    private static Consumer<Tuple2<String, AbstractApplicationResource>> applicationIdAndResourceMatchesName(String applicationName) {
        return consumer((applicationId, resource) -> {
            assertEquals(applicationId, ResourceUtils.getId(resource));
            assertEquals(applicationName, ResourceUtils.getEntity(resource).getName());
        });
    }

    private static void assertIsTestApplicationDroplet(byte[] byteArray) {
        boolean staticFileFound = false;
        boolean indexFileFound = false;

        try {
            TarArchiveInputStream tis = new TarArchiveInputStream(new GZIPInputStream(new ByteArrayInputStream(byteArray)));
            for (TarArchiveEntry entry = tis.getNextTarEntry(); entry != null; entry = tis.getNextTarEntry()) {
                if (entry.getName().endsWith("Staticfile")) {
                    staticFileFound = true;
                }
                if (entry.getName().endsWith("index.html")) {
                    indexFileFound = true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue("Staticfile and index.html not both found in droplet", staticFileFound && indexFileFound);
    }

    private static ByteArrayOutputStream collectIntoByteArrayOutputStream(ByteArrayOutputStream out, byte[] bytes) {
        try {
            out.write(bytes);
            return out;
        } catch (IOException e) {
            throw new Exceptions.UpstreamException(e);
        }
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

    private static Mono<byte[]> downloadApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .download(DownloadApplicationRequest.builder()
                .applicationId(applicationId)
                .build())
            .reduceWith(ByteArrayOutputStream::new, ApplicationsTest::collectIntoByteArrayOutputStream)
            .map(ByteArrayOutputStream::toByteArray);
    }

    private static Mono<byte[]> getByteArrayFrom(String resourceName, Resource resource) {
        try {
            final InputStream inputStream = resource.getInputStream();

            return Flux
                .fromIterable(() -> new Iterator<byte[]>() {

                    @Override
                    public boolean hasNext() {
                        try {
                            return inputStream.available() > 0;
                        } catch (IOException e) {
                            return false;
                        }
                    }

                    @Override
                    public byte[] next() {
                        final int BUFSIZE = 1024;
                        try {
                            byte[] buffer = new byte[BUFSIZE];
                            int numRead = inputStream.read(buffer, 0, BUFSIZE);
                            if (numRead == BUFSIZE) {
                                return buffer;
                            } else if (numRead <= 0) {
                                return new byte[0];
                            } else {
                                byte[] result = new byte[numRead];
                                System.arraycopy(buffer, 0, result, 0, numRead);
                                return result;
                            }
                        } catch (Exception e) {
                            throw Exceptions.fail(e);
                        }
                    }

                })
                .reduceWith(ByteArrayOutputStream::new, ApplicationsTest::collectIntoByteArrayOutputStream)
                .map(ByteArrayOutputStream::toByteArray);

        } catch (Exception e) {
            return Mono.error(new AssertionError(String.format("Cannot get %s resource", resourceName), e));
        }
    }

    private static Mono<ApplicationInstanceInfo> getInstanceInfo(CloudFoundryClient cloudFoundryClient, String applicationId, String instanceName) {
        return requestInstances(cloudFoundryClient, applicationId)
            .where(response -> response.containsKey(instanceName))
            .map(response -> response.get(instanceName));
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
        return cloudFoundryClient.serviceBindings()
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
        return PaginationUtils.requestResources(page -> cloudFoundryClient.applicationsV2()
            .listRoutes(ListApplicationRoutesRequest.builder()
                .page(page)
                .applicationId(applicationId)
                .build()));
    }

    private static Flux<ServiceBindingResource> requestServiceBindings(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.applicationsV2()
                .listServiceBindings(ListApplicationServiceBindingsRequest.builder()
                    .applicationId(applicationId)
                    .page(page)
                    .build()));
    }

    private static Mono<String> startApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .state("STARTED")
                .build())
            .map(ResourceUtils::getId)
            .then(id -> waitForStaging(cloudFoundryClient, id))
            .then(id -> waitForStarting(cloudFoundryClient, id));
    }

    private static Mono<String> uploadAndStartApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return uploadApplication(cloudFoundryClient, applicationId)
            .then(id -> startApplication(cloudFoundryClient, id));
    }

    private static Mono<String> uploadApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        try {
            return cloudFoundryClient.applicationsV2()
                .upload(UploadApplicationRequest.builder()
                    .application(new ClassPathResource("test-application.zip").getInputStream())
                    .async(true)
                    .applicationId(applicationId)
                    .build())
                .map(ResourceUtils::getId)
                .then(jobId -> JobUtils.waitForCompletion(cloudFoundryClient, jobId))
                .after(() -> Mono.just(applicationId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Mono<ApplicationInstanceInfo> waitForInstanceRestart(CloudFoundryClient cloudFoundryClient, String applicationId, String instanceName, Optional<Double> optionalSince) {
        return getInstanceInfo(cloudFoundryClient, applicationId, instanceName)
            .where(info -> !isIdentical(info.getSince(), optionalSince.orElse(null)))
            .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5)));
    }

    private static Mono<String> waitForStaging(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return waitForStagingApplication(cloudFoundryClient, applicationId)
            .map(ResourceUtils::getId);
    }

    private static Mono<AbstractApplicationResource> waitForStagingApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestGetApplication(cloudFoundryClient, applicationId)
            .where(response -> "STAGED".equals(response.getEntity().getPackageState()))
            .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5)));
    }

    private static Mono<String> waitForStarting(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build())
            .flatMap(response -> Flux.fromIterable(response.values()))
            .filter(applicationInstanceInfo -> "RUNNING".equals(applicationInstanceInfo.getState()))
            .next()
            .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5)))
            .map(info -> applicationId);
    }

}
