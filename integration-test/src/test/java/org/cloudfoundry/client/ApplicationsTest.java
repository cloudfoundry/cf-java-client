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

package org.cloudfoundry.client;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationEnvironmentRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesRequest;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsRequest;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.CopyApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v2.applications.DownloadApplicationDropletRequest;
import org.cloudfoundry.client.v2.applications.DownloadApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationRoutesRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.applications.RemoveApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.RestageApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.applications.TerminateApplicationInstanceRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationRequest;
import org.cloudfoundry.client.v2.domains.CreateDomainRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.util.DelayUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Mono;
import reactor.core.tuple.Tuple2;
import reactor.core.util.Exceptions;
import reactor.rx.Fluxion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipFile;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.cloudfoundry.util.OperationUtils.afterComplete;
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
            .then(function((organizationId, spaceId) -> {
                return Mono
                    .when(
                        Mono.just(organizationId),
                        Mono.just(spaceId),
                        createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
                    );
            }))
            .then(function((organizationId, spaceId, applicationId) ->
                createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
                    .map(response -> applicationId)))
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .listRoutes(ListApplicationRoutesRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .map(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void copy() {
        String applicationName = getApplicationName();
        String copyApplicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> Mono
                .just(spaceId)
                .and(createApplicationId(this.cloudFoundryClient, spaceId, applicationName)))
            .then(function((spaceId, applicationId) -> Mono
                .just(applicationId)
                .and(this.cloudFoundryClient.applicationsV2()
                    .create(CreateApplicationRequest.builder()
                        .name(copyApplicationName)
                        .spaceId(spaceId)
                        .build())
                    .map(ResourceUtils::getId))))
            .then(function((sourceId, targetId) -> this.cloudFoundryClient.applicationsV2()
                .copy(CopyApplicationRequest.builder()
                    .applicationId(targetId)
                    .sourceApplicationId(sourceId)
                    .build())
                .map(ResourceUtils::getId)))
            .flatMap(targetId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                    .list(ListApplicationsRequest.builder()
                        .page(page)
                        .build())))
            .as(Fluxion::from)
            .filter(r -> {
                String name = ResourceUtils.getEntity(r).getName();
                return applicationName.equals(name) || copyApplicationName.equals(name);
            })
            .subscribe(testSubscriber()
                .assertCount(2));
    }

    @Test
    public void create() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> Mono
                .just(spaceId)
                .and(this.cloudFoundryClient.applicationsV2()
                    .create(CreateApplicationRequest.builder()
                        .name(applicationName)
                        .spaceId(spaceId)
                        .build())
                    .map(ResourceUtils::getEntity)))
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
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .delete(DeleteApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build()))

            .subscribe(testSubscriber());
    }

    @Test
    public void download() throws IOException {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> uploadApplication(this.cloudFoundryClient, applicationId))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV2()
                .download(DownloadApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build()))
            .as(Fluxion::from)
            .reduce(new ByteArrayOutputStream(), ApplicationsTest::collectIntoByteArrayInputStream)
            .map(ByteArrayOutputStream::toByteArray)
            .map(bytes -> {
                try {
                    File tempFile = File.createTempFile("downloadedFile", "zip");
                    new FileOutputStream(tempFile).write(bytes);
                    return new ZipFile(tempFile).size();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .subscribe(testSubscriber()
                .assertEquals(new ZipFile(new ClassPathResource("testApplication.zip").getFile()).size()));
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
            .as(Fluxion::from)
            .reduceWith(ByteArrayOutputStream::new, ApplicationsTest::collectIntoByteArrayInputStream)
            .map(bytes -> {
                boolean staticFile = false;
                boolean indexFile = false;

                try {
                    TarArchiveInputStream tis = new TarArchiveInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes.toByteArray())));
                    for (TarArchiveEntry entry = tis.getNextTarEntry(); entry != null; entry = tis.getNextTarEntry()) {
                        if (entry.getName().contains("Staticfile")) {
                            staticFile = true;
                        }
                        if (entry.getName().contains("index.html")) {
                            indexFile = true;
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return staticFile && indexFile;
            })
            .subscribe(testSubscriber()
                .assertEquals(true));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void environment() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> Mono
                .just(applicationId)
                .and(this.cloudFoundryClient.applicationsV2()
                    .environment(ApplicationEnvironmentRequest.builder()
                        .applicationId(applicationId)
                        .build())))
            .then(function((applicationId, response) -> {
                Map<String, String> vcapApplication = (Map<String, String>) response.getApplicationEnvironmentJsons().get("VCAP_APPLICATION");
                String actual = vcapApplication.get("application_id");

                return Mono.when(Mono.just(applicationId), Mono.just(actual));
            }))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void get() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .then(applicationId -> Mono
                .just(applicationId)
                .and(this.cloudFoundryClient.applicationsV2()
                    .get(GetApplicationRequest.builder()
                        .applicationId(applicationId)
                        .build())
                    .map(ResourceUtils::getId)))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void list() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .flatMap(applicationId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                    .list(ListApplicationsRequest.builder()
                        .page(page)
                        .build())))
            .as(Fluxion::from)
            .count()
            .subscribe(this.<Long>testSubscriber()
                .assertThat(count -> assertTrue(count > 1)));
    }

    @Test
    public void listFilterByDiego() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .flatMap(applicationId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                    .list(ListApplicationsRequest.builder()
                        .diego(true)
                        .page(page)
                        .build())))
            .as(Fluxion::from)
            .count()
            .subscribe(this.<Long>testSubscriber()
                .assertThat(count -> assertTrue(count > 1)));
    }

    @Test
    public void listFilterByName() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .flatMap(applicationId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                    .list(ListApplicationsRequest.builder()
                        .name(applicationName)
                        .page(page)
                        .build())))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listFilterByOrganizationId() {
        String applicationName = getApplicationName();

        Mono
            .when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) ->
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
                    .map(applicationId -> organizationId)))
            .flatMap(organizationId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                    .list(ListApplicationsRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build())))
            .as(Fluxion::from)
            .count()
            .subscribe(this.<Long>testSubscriber()
                .assertThat(count -> assertTrue(count > 1)));
    }

    @Test
    public void listFilterBySpaceId() {
        String applicationName = getApplicationName();

        this.spaceId
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
                .map(applicationId -> spaceId))
            .flatMap(spaceId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                    .list(ListApplicationsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build())))
            .as(Fluxion::from)
            .count()
            .subscribe(this.<Long>testSubscriber()
                .assertThat(count -> assertTrue(count > 1)));
    }

    @Test
    public void listFilterByStackId() {
        String applicationName = getApplicationName();

        Mono
            .when(this.spaceId, this.stackId)
            .then(function((spaceId, stackId) -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
                .map(applicationId -> stackId)))
            .flatMap(stackId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                    .list(ListApplicationsRequest.builder()
                        .page(page)
                        .stackId(stackId)
                        .build())))
            .as(Fluxion::from)
            .count()
            .subscribe(this.<Long>testSubscriber()
                .assertThat(count -> assertTrue(count > 1)));
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
            .then(function((organizationId, spaceId, applicationId) ->
                createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
                    .map(response -> applicationId)))
            .flatMap(applicationId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                    .listRoutes(ListApplicationRoutesRequest.builder()
                        .applicationId(applicationId)
                        .page(page)
                        .build())))
            .subscribe(testSubscriber()
                .assertCount(1));
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
            .then(function((organizationId, spaceId, applicationId) -> {
                return createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
                    .and(Mono.just(applicationId));
            }))
            .flatMap(function((routeResponse, applicationId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                    .listRoutes(ListApplicationRoutesRequest.builder()
                        .applicationId(applicationId)
                        .domainId(routeResponse.getEntity().getDomainId())
                        .page(page)
                        .build()))))
            .subscribe(testSubscriber()
                .assertCount(1));
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
            .then(function((organizationId, spaceId, applicationId) -> {
                return createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
                    .and(Mono.just(applicationId));
            }))
            .flatMap(function((routeResponse, applicationId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                    .listRoutes(ListApplicationRoutesRequest.builder()
                        .applicationId(applicationId)
                        .host(routeResponse.getEntity().getHost())
                        .page(page)
                        .build()))))
            .subscribe(testSubscriber()
                .assertCount(1));
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
            .then(function((organizationId, spaceId, applicationId) -> {
                return createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
                    .and(Mono.just(applicationId));
            }))
            .flatMap(function((routeResponse, applicationId) -> PaginationUtils
                .requestPages(page -> this.cloudFoundryClient.applicationsV2()
                    .listRoutes(ListApplicationRoutesRequest.builder()
                        .applicationId(applicationId)
                        .path(routeResponse.getEntity().getPath())
                        .page(page)
                        .build()))))
            .subscribe(testSubscriber()
                .assertCount(1));
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
            .then(function((organizationId, spaceId, applicationId) -> {
                return createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
                    .and(Mono.just(applicationId));
            }))
            .flatMap(function((routeResponse, applicationId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.applicationsV2()
                    .listRoutes(ListApplicationRoutesRequest.builder()
                        .applicationId(applicationId)
                        .page(page)
                        .port(routeResponse.getEntity().getPort())
                        .build()))))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    //TODO Implement missing client API
    @Ignore("TODO: Awaiting CUPS https://www.pivotaltracker.com/story/show/101452058")
    @Test
    public void listServiceBindings() {
        Assert.fail();
    }

    //TODO Implement missing client API
    @Ignore("TODO: Awaiting CUPS https://www.pivotaltracker.com/story/show/101452058")
    @Test
    public void listServiceBindingsFilterByServiceInstanceId() {
        Assert.fail();
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
            .then(function((organizationId, spaceId, applicationId) -> {
                return createApplicationRoute(this.cloudFoundryClient, organizationId, spaceId, domainName, applicationId)
                    .and(Mono.just(applicationId));
            }))
            .then(function((routeResponse, applicationId) -> this.cloudFoundryClient.applicationsV2()
                .removeRoute(RemoveApplicationRouteRequest.builder()
                    .applicationId(applicationId)
                    .routeId(ResourceUtils.getId(routeResponse))
                    .build())))
            .subscribe(testSubscriber());
    }

    //TODO Implement missing client API
    @Ignore("TODO: Awaiting CUPS https://www.pivotaltracker.com/story/show/101452058")
    @Test
    public void removeServiceBinding() {
        Assert.fail();
    }

    @Test
    public void restage() {
        String applicationName = getApplicationName();

        Mono
            .when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName)))
            .then(applicationId -> uploadAndStartApplication(this.cloudFoundryClient, applicationId))
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .restage(RestageApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .map(ResourceUtils::getId))
            .then(applicationId -> waitForStaging(this.cloudFoundryClient, applicationId))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void statistics() {
        String applicationName = getApplicationName();

        Mono
            .when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName)))
            .then(applicationId -> uploadAndStartApplication(this.cloudFoundryClient, applicationId))
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .statistics(ApplicationStatisticsRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .map(instanceStatistics -> instanceStatistics.get("0").getStatistics().getName()))
            .subscribe(testSubscriber()
                .assertEquals(applicationName));
    }

    @Test
    public void summary() {
        String applicationName = getApplicationName();

        Mono
            .when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName)))
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

        Mono
            .when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName)))
            .then(applicationId -> uploadAndStartApplication(this.cloudFoundryClient, applicationId))
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .terminateInstance(TerminateApplicationInstanceRequest.builder()
                    .applicationId(applicationId)
                    .index("0")
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void update() {
        String applicationName = getApplicationName();
        String applicationName2 = getApplicationName();

        Mono
            .when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName)))
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .update(UpdateApplicationRequest.builder()
                    .applicationId(applicationId)
                    .name(applicationName2)
                    .build())
                .map(ResourceUtils::getId))
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .get(GetApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .map(response -> response.getEntity().getName()))
            .subscribe(testSubscriber()
                .assertEquals(applicationName2));
    }

    @Test
    public void upload() throws IOException {
        String applicationName = getApplicationName();

        Mono
            .when(this.organizationId, this.spaceId)
            .then(function((organizationId, spaceId) -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName)))
            .then(applicationId -> uploadApplication(this.cloudFoundryClient, applicationId))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV2()
                .download(DownloadApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build()))
            .after()
            .subscribe(testSubscriber());
    }

    private static ByteArrayOutputStream collectIntoByteArrayInputStream(ByteArrayOutputStream out, byte[] bytes) {
        try {
            out.write(bytes);
            return out;
        } catch (IOException e) {
            throw new Exceptions.UpstreamException(e);
        }
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .buildpack("staticfile_buildpack")
                .diego(true)
                .diskQuota(512)
                .memory(64)
                .name(applicationName)
                .spaceId(spaceId)
                .build())
            .map(ResourceUtils::getId);
    }

    private static Mono<CreateRouteResponse> createApplicationRoute(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceId, String domainName, String applicationId) {
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
                    .host("test-host")
                    .path("/test-path")
                    .spaceId(spaceId)
                    .build()))
            .then(createRouteResponse -> cloudFoundryClient.applicationsV2()
                .associateRoute(AssociateApplicationRouteRequest.builder()
                    .applicationId(applicationId)
                    .routeId(createRouteResponse.getMetadata().getId())
                    .build())
                .map(response -> createRouteResponse));
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
                    .application(new ClassPathResource("testApplication.zip").getInputStream())
                    .async(true)
                    .applicationId(applicationId)
                    .build())
                .map(ResourceUtils::getId)
                .then(jobId -> JobUtils.waitForCompletion(cloudFoundryClient, jobId))
                .as(afterComplete(() -> Mono.just(applicationId)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Mono<String> waitForStaging(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .get(GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build())
            .map(response -> response.getEntity().getPackageState())
            .where("STAGED"::equals)
            .as(Fluxion::from)                                               // TODO: Remove once Mono.repeatWhen()
            .repeatWhen(DelayUtils.exponentialBackOff(1, 10, SECONDS, 10))
            .single()                                                       // TODO: Remove once Mono.repeatWhen()
            .map(state -> applicationId);
    }

    private static Mono<String> waitForStarting(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build())
            .flatMap(response -> Fluxion.fromIterable(response.values()))
            .as(Fluxion::from)
            .filter(applicationInstanceInfo -> "RUNNING".equals(applicationInstanceInfo.getState()))
            .repeatWhen(DelayUtils.exponentialBackOff(1, 10, SECONDS, 10))
            .single()
            .map(info -> applicationId);
    }

}
