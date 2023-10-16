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

package org.cloudfoundry.client.v2;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.AbstractApplicationResource;
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
import org.cloudfoundry.client.v2.applications.GetApplicationPermissionsRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationPermissionsResponse;
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
import org.cloudfoundry.client.v2.applications.UploadApplicationDropletRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationResponse;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingEntity;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.util.DelayUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.OperationUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.client.ZipExpectations.zipEquality;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

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
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        Mono
            .zip(this.organizationId, this.spaceId)
            .flatMap(function((organizationId, spaceId) -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName),
                createRouteWithDomain(this.cloudFoundryClient, organizationId, spaceId, domainName, "test-host", "/test/path")
                    .map(ResourceUtils::getId)
            )))
            .delayUntil(function((applicationId, routeId) -> requestAssociateRoute(this.cloudFoundryClient, applicationId, routeId)))
            .flatMap(function((applicationId, routeId) -> Mono.zip(
                getSingleRouteId(this.cloudFoundryClient, applicationId),
                Mono.just(routeId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void copy() {
        String applicationName = this.nameFactory.getApplicationName();
        String copyApplicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
                    .delayUntil(applicationId -> uploadApplication(this.cloudFoundryClient, applicationId)),
                requestCreateApplication(this.cloudFoundryClient, spaceId, copyApplicationName)
                    .map(ResourceUtils::getId)
            ))
            .delayUntil(function((sourceId, targetId) -> this.cloudFoundryClient.applicationsV2()
                .copy(CopyApplicationRequest.builder()
                    .applicationId(targetId)
                    .sourceApplicationId(sourceId)
                    .build())
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job))
            ))
            .flatMap(function((sourceId, targetId) -> Mono.zip(
                downloadApplication(this.cloudFoundryClient, sourceId),
                downloadApplication(this.cloudFoundryClient, targetId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> Mono.zip(
                Mono.just(spaceId),
                requestCreateApplication(this.cloudFoundryClient, spaceId, applicationName)
                    .map(ResourceUtils::getEntity)
            ))
            .as(StepVerifier::create)
            .consumeNextWith(consumer((spaceId, entity) -> {
                assertThat(entity.getSpaceId()).isEqualTo(spaceId);
                assertThat(entity.getName()).isEqualTo(applicationName);
            }))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void createDocker() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> Mono.zip(
                Mono.just(spaceId),
                this.cloudFoundryClient.applicationsV2()
                    .create(CreateApplicationRequest.builder()
                        .dockerImage("cloudfoundry/test-app")
                        .name(applicationName)
                        .spaceId(spaceId)
                        .build())
                    .map(ResourceUtils::getEntity)
            ))
            .as(StepVerifier::create)
            .consumeNextWith(consumer((spaceId, entity) -> {
                assertThat(entity.getSpaceId()).isEqualTo(spaceId);
                assertThat(entity.getName()).isEqualTo(applicationName);
            }))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .delayUntil(applicationId -> this.cloudFoundryClient.applicationsV2()
                .delete(DeleteApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build()))
            .flatMap(applicationId -> requestGetApplication(this.cloudFoundryClient, applicationId))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessageMatching("CF-AppNotFound\\([0-9]+\\): The app could not be found: .*"))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void downloadDroplet() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .delayUntil(applicationId -> uploadAndStartApplication(this.cloudFoundryClient, applicationId))
            .flatMapMany(applicationId -> this.cloudFoundryClient.applicationsV2()
                .downloadDroplet(DownloadApplicationDropletRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .as(OperationUtils::collectByteArray))
            .as(StepVerifier::create)
            .consumeNextWith(isTestApplicationDroplet())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void environment() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                this.cloudFoundryClient.applicationsV2()
                    .environment(ApplicationEnvironmentRequest.builder()
                        .applicationId(applicationId)
                        .build())
                    .map(response -> getStringApplicationEnvValue(response.getApplicationEnvironmentJsons(), "VCAP_APPLICATION", "application_id"))
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                requestGetApplication(this.cloudFoundryClient, applicationId)
            ))
            .as(StepVerifier::create)
            .consumeNextWith(applicationIdAndNameEquality(applicationName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_1_9)
    @Test
    public void getPermissions() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV2()
                .getPermissions(GetApplicationPermissionsRequest.builder()
                    .applicationId(applicationId)
                    .build()))
            .as(StepVerifier::create)
            .expectNext(GetApplicationPermissionsResponse.builder()
                .readBasicData(true)
                .readSensitiveData(true)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void instances() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .delayUntil(applicationId -> uploadAndStartApplication(this.cloudFoundryClient, applicationId))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV2()
                .instances(ApplicationInstancesRequest.builder()
                    .applicationId(applicationId)
                    .build()))
            .map(ApplicationInstancesResponse::getInstances)
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .flatMap(applicationId -> Mono.zip(
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
            .as(StepVerifier::create)
            .consumeNextWith(applicationIdAndNameEquality(applicationName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByDiego() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .flatMap(applicationId -> Mono.zip(
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
            .as(StepVerifier::create)
            .consumeNextWith(applicationIdAndNameEquality(applicationName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByName() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .flatMap(applicationId -> Mono.zip(
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
            .as(StepVerifier::create)
            .consumeNextWith(applicationIdAndNameEquality(applicationName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByOrganizationId() {
        String applicationName = this.nameFactory.getApplicationName();

        Mono
            .zip(
                this.organizationId,
                this.spaceId
                    .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            )
            .flatMap(function((organizationId, applicationId) -> Mono.zip(
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
            .as(StepVerifier::create)
            .consumeNextWith(applicationIdAndNameEquality(applicationName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterBySpaceId() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> Mono.zip(
                Mono.just(spaceId),
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
            ))
            .flatMap(function((spaceId, applicationId) -> Mono.zip(
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
            .as(StepVerifier::create)
            .consumeNextWith(applicationIdAndNameEquality(applicationName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByStackId() {
        String applicationName = this.nameFactory.getApplicationName();

        Mono.zip(this.spaceId, this.stackId)
            .flatMap(function((spaceId, stackId) -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName, stackId),
                Mono.just(stackId))))
            .flatMap(function((applicationId, stackId) -> Mono.zip(
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
            .as(StepVerifier::create)
            .consumeNextWith(applicationIdAndNameEquality(applicationName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRoutes() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        Mono
            .zip(this.organizationId, this.spaceId)
            .flatMap(function((organizationId, spaceId) -> Mono.zip(
                Mono.just(organizationId),
                Mono.just(spaceId),
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
            )))
            .flatMap(function((organizationId, spaceId, applicationId) -> Mono.zip(
                Mono.just(applicationId),
                createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
            )))
            .flatMap(function((applicationId, routeResponse) -> Mono.zip(
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
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRoutesFilterByDomainId() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        Mono
            .zip(this.organizationId, this.spaceId)
            .flatMap(function((organizationId, spaceId) -> Mono.zip(
                Mono.just(organizationId),
                Mono.just(spaceId),
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
            )))
            .flatMap(function((organizationId, spaceId, applicationId) -> Mono.zip(
                Mono.just(applicationId),
                createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
            )))
            .flatMap(function((applicationId, routeResponse) -> Mono.zip(
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
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRoutesFilterByHost() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        Mono
            .zip(this.organizationId, this.spaceId)
            .flatMap(function((organizationId, spaceId) -> Mono.zip(
                Mono.just(organizationId),
                Mono.just(spaceId),
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
            )))
            .flatMap(function((organizationId, spaceId, applicationId) -> Mono.zip(
                Mono.just(applicationId),
                createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
            )))
            .flatMap(function((applicationId, routeResponse) -> Mono.zip(
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
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRoutesFilterByPath() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        Mono
            .zip(this.organizationId, this.spaceId)
            .flatMap(function((organizationId, spaceId) -> Mono.zip(
                Mono.just(organizationId),
                Mono.just(spaceId),
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
            )))
            .flatMap(function((organizationId, spaceId, applicationId) -> Mono.zip(
                Mono.just(applicationId),
                createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
            )))
            .flatMap(function((applicationId, routeResponse) -> Mono.zip(
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
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceBindings() {
        String applicationName = this.nameFactory.getApplicationName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName),
                createUserServiceInstanceId(this.cloudFoundryClient, spaceId, serviceInstanceName)
            ))
            .delayUntil(function((applicationId, serviceInstanceId) -> createServiceBindingId(this.cloudFoundryClient, applicationId, serviceInstanceId)))
            .flatMap(function((applicationId, serviceInstanceId) -> Mono.zip(
                Mono.just(serviceInstanceId),
                getSingleServiceBindingInstanceId(this.cloudFoundryClient, applicationId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceBindingsFilterByServiceInstanceId() {
        String applicationName = this.nameFactory.getApplicationName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName),
                createUserServiceInstanceId(this.cloudFoundryClient, spaceId, serviceInstanceName)
            ))
            .flatMap(function((applicationId, serviceInstanceId) -> Mono.zip(
                Mono.just(applicationId),
                Mono.just(serviceInstanceId),
                createServiceBindingId(this.cloudFoundryClient, applicationId, serviceInstanceId)
            )))
            .flatMap(function((applicationId, serviceInstanceId, serviceBindingId) -> Mono.zip(
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
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeRoute() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        Mono
            .zip(this.organizationId, this.spaceId)
            .flatMap(function((organizationId, spaceId) -> Mono.zip(
                Mono.just(organizationId),
                Mono.just(spaceId),
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
            )))
            .flatMap(function((organizationId, spaceId, applicationId) -> Mono.zip(
                Mono.just(applicationId),
                createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
            )))
            .delayUntil(function((applicationId, routeResponse) -> this.cloudFoundryClient.applicationsV2()
                .removeRoute(RemoveApplicationRouteRequest.builder()
                    .applicationId(applicationId)
                    .routeId(ResourceUtils.getId(routeResponse))
                    .build())))
            .flatMapMany(function((applicationId, routeResponse) -> requestRoutes(this.cloudFoundryClient, applicationId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeServiceBinding() {
        String applicationName = this.nameFactory.getApplicationName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName),
                createUserServiceInstanceId(this.cloudFoundryClient, spaceId, serviceInstanceName)
            ))
            .flatMap(function((applicationId, serviceInstanceId) -> Mono.zip(
                Mono.just(applicationId),
                createServiceBindingId(this.cloudFoundryClient, applicationId, serviceInstanceId)
            )))
            .delayUntil(function((applicationId, serviceBindingId) -> this.cloudFoundryClient.applicationsV2()
                .removeServiceBinding(RemoveApplicationServiceBindingRequest.builder()
                    .applicationId(applicationId)
                    .serviceBindingId(serviceBindingId)
                    .build())))
            .flatMapMany(function((applicationId, serviceBindingId) -> requestServiceBindings(this.cloudFoundryClient, applicationId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void restage() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .delayUntil(applicationId -> uploadAndStartApplication(this.cloudFoundryClient, applicationId))
            .delayUntil(applicationId -> this.cloudFoundryClient.applicationsV2()
                .restage(RestageApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build()))
            .flatMap(applicationId -> waitForStagingApplication(this.cloudFoundryClient, applicationId))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .as(StepVerifier::create)
            .expectNext(applicationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void statistics() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .delayUntil(applicationId -> uploadAndStartApplication(this.cloudFoundryClient, applicationId))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV2()
                .statistics(ApplicationStatisticsRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .map(instanceStatistics -> instanceStatistics.getInstances().get("0").getStatistics().getName()))
            .as(StepVerifier::create)
            .expectNext(applicationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void summary() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV2()
                .summary(SummaryApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .map(SummaryApplicationResponse::getId)
                .zipWith(Mono.just(applicationId)))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void terminateInstance() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .delayUntil(applicationId -> uploadAndStartApplication(this.cloudFoundryClient, applicationId))
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                getInstanceInfo(this.cloudFoundryClient, applicationId, "0")
                    .map(info -> Optional.ofNullable(info.getSince()))
            ))
            .delayUntil(function((applicationId, optionalSince) -> this.cloudFoundryClient.applicationsV2()
                .terminateInstance(TerminateApplicationInstanceRequest.builder()
                    .applicationId(applicationId)
                    .index("0")
                    .build())))
            .flatMap(function((applicationId, optionalSince) -> waitForInstanceRestart(this.cloudFoundryClient, applicationId, "0", optionalSince)))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String applicationName = this.nameFactory.getApplicationName();
        String applicationName2 = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV2()
                .update(UpdateApplicationRequest.builder()
                    .applicationId(applicationId)
                    .environmentJson("test-var", "test-value")
                    .name(applicationName2)
                    .ports(60606, 60607)
                    .build())
                .map(ResourceUtils::getId))
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                requestGetApplication(this.cloudFoundryClient, applicationId)
                    .map(ResourceUtils::getEntity)
            ))
            .flatMap(function((applicationId, entity1) -> Mono.zip(
                Mono.just(entity1),
                this.cloudFoundryClient.applicationsV2()
                    .update(UpdateApplicationRequest.builder()
                        .applicationId(applicationId)
                        .environmentJsons(Collections.emptyMap())
                        .build())
                    .then(requestGetApplication(this.cloudFoundryClient, applicationId)
                        .map(ResourceUtils::getEntity))
            )))
            .as(StepVerifier::create)
            .consumeNextWith(consumer((entity1, entity2) -> {
                assertThat(entity1.getName()).isEqualTo(applicationName2);
                assertThat(entity1.getPorts().containsAll(Arrays.asList(60606, 60607)));
                assertThat(entity1.getEnvironmentJsons()).containsEntry("test-var", "test-value");
                assertThat(entity2.getEnvironmentJsons()).isEmpty();
            }))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void uploadAndDownload() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .delayUntil(applicationId -> uploadApplication(this.cloudFoundryClient, applicationId))
            .flatMap(applicationId -> Mono.zip(
                downloadApplication(this.cloudFoundryClient, applicationId),
                getBytes("test-application.zip")
            ))
            .as(StepVerifier::create)
            .consumeNextWith(zipEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void uploadAndDownloadAsyncFalse() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .delayUntil(applicationId -> uploadApplicationAsyncFalse(this.cloudFoundryClient, applicationId))
            .flatMap(applicationId -> Mono.zip(
                downloadApplication(this.cloudFoundryClient, applicationId),
                getBytes("test-application.zip")
            ))
            .as(StepVerifier::create)
            .consumeNextWith(zipEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void uploadDirectory() throws IOException {
        Path application = new ClassPathResource("test-application").getFile().toPath();
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV2()
                .upload(UploadApplicationRequest.builder()
                    .application(application)
                    .async(true)
                    .applicationId(applicationId)
                    .build())
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_1_9)
    @Test
    public void uploadDroplet() throws IOException {
        Path droplet = new ClassPathResource("test-droplet.tgz").getFile().toPath();
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV2()
                .uploadDroplet(UploadApplicationDropletRequest.builder()
                    .applicationId(applicationId)
                    .droplet(droplet)
                    .build())
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Consumer<Tuple2<String, AbstractApplicationResource>> applicationIdAndNameEquality(String name) {
        Assert.notNull(name, "name must not be null");

        return consumer((applicationId, resource) -> {
            assertThat(ResourceUtils.getId(resource)).isEqualTo(applicationId);
            assertThat(ResourceUtils.getEntity(resource).getName()).isEqualTo(name);
        });
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName, "staticfile_buildpack", true, 512, 64, null)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName, String stackId) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName, "staticfile_buildpack", true, 512, 64, stackId)
            .map(ResourceUtils::getId);
    }

    private static Mono<CreateRouteResponse> createApplicationRoute(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceId, String domainName, String applicationId) {
        return createRouteWithDomain(cloudFoundryClient, organizationId, spaceId, domainName, "test-host", "/test-path")
            .flatMap(createRouteResponse -> requestAssociateRoute(cloudFoundryClient, applicationId, createRouteResponse.getMetadata().getId())
                .map(response -> createRouteResponse));
    }

    private static Mono<String> createPrivateDomainId(CloudFoundryClient cloudFoundryClient, String name, String organizationId) {
        return requestCreatePrivateDomain(cloudFoundryClient, name, organizationId)
            .map(ResourceUtils::getId);
    }

    private static Mono<CreateRouteResponse> createRouteWithDomain(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceId, String domainName, String host, String path) {
        return createPrivateDomainId(cloudFoundryClient, domainName, organizationId)
            .flatMap(domainId -> cloudFoundryClient.routes()
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

    private static Consumer<byte[]> isTestApplicationDroplet() {
        return bytes -> {
            Set<String> names = new HashSet<>();

            try (TarArchiveInputStream in = new TarArchiveInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes)))) {
                TarArchiveEntry entry;
                while ((entry = in.getNextTarEntry()) != null) {
                    names.add(entry.getName());
                }
            } catch (IOException e) {
                throw Exceptions.propagate(e);
            }

            assertThat(names).contains("./app/Staticfile", "./app/public/index.html");
        };
    }

    private static Mono<AssociateApplicationRouteResponse> requestAssociateRoute(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return cloudFoundryClient.applicationsV2()
            .associateRoute(AssociateApplicationRouteRequest.builder()
                .applicationId(applicationId)
                .routeId(routeId)
                .build());
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName, null, null, null, null, null);
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName, String buildpack, Boolean diego,
                                                                            Integer diskQuota, Integer memory, String stackId) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .buildpack(buildpack)
                .diego(diego)
                .diskQuota(diskQuota)
                .memory(memory)
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
                    .application(new ClassPathResource("test-application.zip").getFile().toPath())
                    .async(true)
                    .applicationId(applicationId)
                    .build())
                .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, Duration.ofMinutes(5), job));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Mono<UploadApplicationResponse> uploadApplicationAsyncFalse(CloudFoundryClient cloudFoundryClient, String applicationId) {
        try {
            return cloudFoundryClient.applicationsV2()
                .upload(UploadApplicationRequest.builder()
                    .application(new ClassPathResource("test-application.zip").getFile().toPath())
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
            .repeatWhenEmpty(DelayUtils.exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5)));
    }

    private static Mono<AbstractApplicationResource> waitForStagingApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestGetApplication(cloudFoundryClient, applicationId)
            .filter(response -> "STAGED".equals(response.getEntity().getPackageState()))
            .repeatWhenEmpty(DelayUtils.exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5)));
    }

    private static Mono<ApplicationInstanceInfo> waitForStartingInstanceInfo(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build())
            .flatMapMany(response -> Flux.fromIterable(response.getInstances().values()))
            .filter(applicationInstanceInfo -> "RUNNING".equals(applicationInstanceInfo.getState()))
            .next()
            .repeatWhenEmpty(DelayUtils.exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5)));
    }

    private Mono<byte[]> downloadApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .download(DownloadApplicationRequest.builder()
                .applicationId(applicationId)
                .build())
            .as(OperationUtils::collectByteArray);
    }


}
