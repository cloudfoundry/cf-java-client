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
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
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
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.utils.ResourceUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Mono;
import reactor.core.util.Exceptions;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Promise;
import reactor.rx.Stream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipFile;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.cloudfoundry.utils.tuple.TupleUtils.consumer;
import static org.cloudfoundry.utils.tuple.TupleUtils.function;
import static org.junit.Assert.assertEquals;

public final class ApplicationsTest extends AbstractIntegrationTest {

    public static final String TEST_APPLICATION_NAME = "test-application";

    private Mono<String> applicationId;

    @Test
    public void associateRoute() {
        createApplicationRoute()
            .then(response -> this.applicationId)
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
        Mono
            .when(this.applicationId, this.spaceId)
            .then(function((sourceId, spaceId) -> this.cloudFoundryClient.applicationsV2()
                .create(CreateApplicationRequest.builder()
                    .name("copy-application")
                    .spaceId(spaceId)
                    .build())
                .map(ResourceUtils::getId)
                .and(Mono.just(sourceId))))
            .then(function((targetId, sourceId) -> this.cloudFoundryClient.applicationsV2()
                .copy(CopyApplicationRequest.builder()
                    .applicationId(targetId)
                    .sourceApplicationId(sourceId)
                    .build())
                .map(ResourceUtils::getId)))
            .flatMap(applicationCopyId -> Stream
                .from(this.cloudFoundryClient.applicationsV2()
                    .list(ListApplicationsRequest.builder()
                        .build()))
                .flatMap(ResourceUtils::getResources)
                .filter(r -> {
                    String name = ResourceUtils.getEntity(r).getName();
                    return TEST_APPLICATION_NAME.equals(name) || "copy-application".equals(name);
                }))
            .subscribe(testSubscriber()
                .assertCount(2));
    }

    @Test
    public void create() {
        this.spaceId
            .then(spaceId -> Mono
                .when(
                    Mono.just(spaceId),
                    this.cloudFoundryClient.applicationsV2()
                        .create(CreateApplicationRequest.builder()
                            .name("test-application-2")
                            .spaceId(spaceId)
                            .build())
                        .map(ResourceUtils::getEntity)
                ))
            .subscribe(this.<Tuple2<String, ApplicationEntity>>testSubscriber()
                .assertThat(consumer((spaceId, entity) -> {
                    assertEquals(spaceId, entity.getSpaceId());
                    assertEquals("test-application-2", entity.getName());
                })));
    }

    @Before
    public void createApplicationId() throws Exception {
        this.applicationId = this.spaceId
            .then(spaceId -> this.cloudFoundryClient.applicationsV2()
                .create(CreateApplicationRequest.builder()
                    .buildpack("staticfile_buildpack")
                    .diego(true)
                    .diskQuota(512)
                    .memory(64)
                    .name(TEST_APPLICATION_NAME)
                    .spaceId(spaceId)
                    .build()))
            .map(ResourceUtils::getId)
            .as(Promise::from);
    }

    @Test
    public void delete() {
        this.applicationId
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .delete(DeleteApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void download() throws IOException {
        this.applicationId
            .then(this::uploadApplication)
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV2()
                .download(DownloadApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build()))
            .as(Stream::from)
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
        this.applicationId
            .then(this::uploadAndStartApplication)
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV2()
                .downloadDroplet(DownloadApplicationDropletRequest.builder()
                    .applicationId(applicationId)
                    .build()))
            .as(Stream::from)
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
        this.applicationId
            .then(applicationId -> Mono
                .when(
                    Mono.just(applicationId),
                    this.cloudFoundryClient.applicationsV2()
                        .environment(ApplicationEnvironmentRequest.builder()
                            .applicationId(applicationId)
                            .build())
                ))
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
        this.applicationId
            .then(applicationId -> Mono
                .when(
                    Mono.just(applicationId),
                    this.cloudFoundryClient.applicationsV2()
                        .get(GetApplicationRequest.builder()
                            .applicationId(applicationId)
                            .build())
                        .map(ResourceUtils::getId)
                ))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void list() {
        this.applicationId
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV2()
                .list(ListApplicationsRequest.builder()
                    .build())
                .map(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listFilterByDiego() {
        this.applicationId
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .list(ListApplicationsRequest.builder()
                    .diego(true)
                    .build())
                .map(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listFilterByName() {
        this.applicationId
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .list(ListApplicationsRequest.builder()
                    .name(TEST_APPLICATION_NAME)
                    .build())
                .map(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listFilterByOrganizationId() {
        this.applicationId
            .then(applicationId -> this.organizationId)
            .then(organizationId -> this.cloudFoundryClient.applicationsV2()
                .list(ListApplicationsRequest.builder()
                    .organizationId(organizationId)
                    .build())
                .map(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listFilterBySpaceId() {
        this.applicationId
            .then(applicationId -> this.spaceId)
            .then(spaceId -> this.cloudFoundryClient.applicationsV2()
                .list(ListApplicationsRequest.builder()
                    .spaceId(spaceId)
                    .build())
                .map(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listFilterByStackId() {
        this.applicationId
            .then(applicationId -> this.stackId)
            .then(stackId -> this.cloudFoundryClient.applicationsV2()
                .list(ListApplicationsRequest.builder()
                    .stackId(stackId)
                    .build())
                .map(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listRoutes() {
        createApplicationRoute()
            .then(response -> this.applicationId)
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .listRoutes(ListApplicationRoutesRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .map(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listRoutesFilterByDomainId() {
        createApplicationRoute()
            .and(this.applicationId)
            .then(function((routeResponse, applicationId) -> this.cloudFoundryClient.applicationsV2()
                .listRoutes(ListApplicationRoutesRequest.builder()
                    .applicationId(applicationId)
                    .domainId(routeResponse.getEntity().getDomainId())
                    .build())
                .map(ResourceUtils::getResources)))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listRoutesFilterByHost() {
        createApplicationRoute()
            .and(this.applicationId)
            .then(function((routeResponse, applicationId) -> this.cloudFoundryClient.applicationsV2()
                .listRoutes(ListApplicationRoutesRequest.builder()
                    .applicationId(applicationId)
                    .host(routeResponse.getEntity().getHost())
                    .build())
                .map(ResourceUtils::getResources)))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listRoutesFilterByPath() {
        createApplicationRoute()
            .and(this.applicationId)
            .then(function((routeResponse, applicationId) -> this.cloudFoundryClient.applicationsV2()
                .listRoutes(ListApplicationRoutesRequest.builder()
                    .applicationId(applicationId)
                    .path(routeResponse.getEntity().getPath())
                    .build())
                .map(ResourceUtils::getResources)))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listRoutesFilterByPort() {
        createApplicationRoute()
            .and(this.applicationId)
            .then(function((routeResponse, applicationId) -> this.cloudFoundryClient.applicationsV2()
                .listRoutes(ListApplicationRoutesRequest.builder()
                    .applicationId(applicationId)
                    .port(routeResponse.getEntity().getPort())
                    .build())
                .map(ResourceUtils::getResources)))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    //TODO Implement missing client API
    @Ignore("TODO: Awaiting CUPS https://www.pivotaltracker.com/story/show/101452058")
    @Test
    public void listServiceBindings() {
        Mono
            .when(this.applicationId, Mono.just(""))
            .then(function((applicationId, serviceInstanceId) -> this.cloudFoundryClient.serviceBindings()
                .create(CreateServiceBindingRequest.builder()
                    .applicationId(applicationId)
                    .serviceInstanceId(serviceInstanceId)
                    .build())
                .map(response -> applicationId)))
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .listServiceBindings(ListApplicationServiceBindingsRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .map(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    //TODO Implement missing client API
    @Ignore("TODO: Awaiting CUPS https://www.pivotaltracker.com/story/show/101452058")
    @Test
    public void listServiceBindingsFilterByServiceInstanceId() {
        this.applicationId
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .listServiceBindings(ListApplicationServiceBindingsRequest.builder()
                    .applicationId(applicationId)
                    .serviceInstanceId("CREATE ME")
                    .build())
                .map(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void removeRoute() {
        createApplicationRoute()
            .and(this.applicationId)
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
        this.applicationId
            .then(this::uploadAndStartApplication)
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .restage(RestageApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .map(ResourceUtils::getId))
            .then(this::waitForStaging)
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void statistics() {
        this.applicationId
            .then(this::uploadAndStartApplication)
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .statistics(ApplicationStatisticsRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .map(instanceStatistics -> instanceStatistics.get("0").getStatistics().getName()))
            .subscribe(testSubscriber()
                .assertEquals(TEST_APPLICATION_NAME));
    }

    @Test
    public void summary() {
        this.applicationId
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
        this.applicationId
            .then(this::uploadAndStartApplication)
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .terminateInstance(TerminateApplicationInstanceRequest.builder()
                    .applicationId(applicationId)
                    .index("0")
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void update() {
        this.applicationId
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .update(UpdateApplicationRequest.builder()
                    .applicationId(applicationId)
                    .name("another-test-application-name")
                    .build())
                .map(ResourceUtils::getId))
            .then(applicationId -> this.cloudFoundryClient.applicationsV2()
                .get(GetApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .map(response -> response.getEntity().getName()))
            .subscribe(testSubscriber()
                .assertEquals("another-test-application-name"));
    }

    @Test
    public void upload() throws IOException {
        this.applicationId
            .then(this::uploadApplication)
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

    private Mono<CreateRouteResponse> createApplicationRoute() {
        return this.organizationId
            .then(organizationId -> this.cloudFoundryClient.domains()
                .create(CreateDomainRequest.builder()
                    .name("test.domain.name")
                    .owningOrganizationId(organizationId)
                    .wildcard(true)
                    .build())
                .map(ResourceUtils::getId))
            .and(this.spaceId)
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .host("test-host")
                    .path("/test-path")
                    .spaceId(spaceId)
                    .build())
                .and(this.applicationId)))
            .then(function((createRouteResponse, applicationId) -> this.cloudFoundryClient.applicationsV2()
                .associateRoute(AssociateApplicationRouteRequest.builder()
                    .applicationId(applicationId)
                    .routeId(createRouteResponse.getMetadata().getId())
                    .build())
                .map(response -> createRouteResponse)));
    }

    private Mono<String> startApplication(String applicationId) {
        return this.cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .state("STARTED")
                .build())
            .map(ResourceUtils::getId)
            .then(this::waitForStaging)
            .then(this::waitForStarting);
    }

    private Mono<String> uploadAndStartApplication(String applicationId) {
        return uploadApplication(applicationId)
            .then(this::startApplication);
    }

    private Mono<String> uploadApplication(String applicationId) {
        return this.cloudFoundryClient.applicationsV2()
            .upload(UploadApplicationRequest.builder()
                .application(new File("./src/test/resources/testApplication.zip"))
                .async(false)
                .applicationId(applicationId)
                .build())
            .map(response -> applicationId);
    }

    private Mono<String> waitForStaging(String applicationId) {
        return this.cloudFoundryClient.applicationsV2()
            .get(GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build())
            .map(response -> response.getEntity().getPackageState())
            .where("STAGED"::equals)
            .as(Stream::from)                                   // TODO: Remove once Mono.repeatWhen()
            .repeatWhen(volumes -> volumes
                .takeWhile(count -> count == 0)
                .flatMap(count -> Mono.delay(2, SECONDS)))
            .single()                                           // TODO: Remove once Mono.repeatWhen()
            .map(state -> applicationId);
    }

    private Mono<String> waitForStarting(String applicationId) {
        return this.cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build())
            .flatMap(response -> Stream.fromIterable(response.values()))
            .as(Stream::from)
            .filter(applicationInstanceInfo -> "RUNNING".equals(applicationInstanceInfo.getState()))
            .repeatWhen(volumes -> volumes
                .takeWhile(count -> count == 0)
                .flatMap(count -> Mono.delay(2, SECONDS)))
            .single()
            .map(info -> applicationId);
    }

}
