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
import org.cloudfoundry.client.v2.applications.ApplicationEnvironmentResponse;
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
import org.cloudfoundry.operations.util.v2.Resources;
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
import static org.cloudfoundry.operations.util.Tuples.consumer;
import static org.cloudfoundry.operations.util.Tuples.function;
import static org.junit.Assert.assertEquals;

public final class ApplicationsTest extends AbstractIntegrationTest {

    public static final String TEST_APPLICATION_NAME = "test-application";

    private Mono<String> applicationId;

    @Test
    public void associateRoute() {
        createApplicationRoute()
                .then(response -> this.applicationId)
                .then(applicationId -> {
                    ListApplicationRoutesRequest request = ListApplicationRoutesRequest.builder()
                            .applicationId(applicationId)
                            .build();

                    return this.cloudFoundryClient.applicationsV2().listRoutes(request)
                            .map(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void copy() {
        Mono
                .when(this.applicationId, this.spaceId)
                .then(function((sourceId, spaceId) -> {
                    CreateApplicationRequest createApplicationRequest = CreateApplicationRequest.builder()
                            .name("copy-application")
                            .spaceId(spaceId)
                            .build();

                    return this.cloudFoundryClient.applicationsV2().create(createApplicationRequest)
                            .map(Resources::getId)
                            .and(Mono.just(sourceId));
                }))
                .then(function((targetId, sourceId) -> {
                    CopyApplicationRequest copyApplicationRequest = CopyApplicationRequest.builder()
                            .applicationId(targetId)
                            .sourceApplicationId(sourceId)
                            .build();

                    return this.cloudFoundryClient.applicationsV2().copy(copyApplicationRequest)
                            .map(Resources::getId);
                }))
                .flatMap(applicationCopyId -> {
                    ListApplicationsRequest request = ListApplicationsRequest.builder()
                            .build();

                    return this.cloudFoundryClient.applicationsV2().list(request)
                            .flatMap(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(2));
    }

    @Test
    public void create() {
        this.spaceId
                .then(spaceId -> {
                    CreateApplicationRequest request = CreateApplicationRequest.builder()
                            .name("test-application-2")
                            .spaceId(spaceId)
                            .build();

                    Mono<ApplicationEntity> entity = this.cloudFoundryClient.applicationsV2().create(request)
                            .map(Resources::getEntity);

                    return Mono.when(Mono.just(spaceId), entity);
                })
                .subscribe(this.<Tuple2<String, ApplicationEntity>>testSubscriber()
                        .assertThat(consumer((spaceId, entity) -> {
                            assertEquals(spaceId, entity.getSpaceId());
                            assertEquals("test-application-2", entity.getName());
                        })));
    }

    @Before
    public void createApplicationId() throws Exception {
        this.applicationId = this.spaceId
                .then(spaceId -> {
                    CreateApplicationRequest request = CreateApplicationRequest.builder()
                            .buildpack("staticfile_buildpack")
                            .diego(true)
                            .diskQuota(512)
                            .memory(64)
                            .name(TEST_APPLICATION_NAME)
                            .spaceId(spaceId)
                            .build();

                    return this.cloudFoundryClient.applicationsV2().create(request);
                })
                .map(Resources::getId)
                .as(Promise::from);
    }

    @Test
    public void delete() {
        this.applicationId
                .then(applicationId -> {
                    DeleteApplicationRequest request = DeleteApplicationRequest.builder()
                            .applicationId(applicationId)
                            .build();

                    return this.cloudFoundryClient.applicationsV2().delete(request);
                })
                .subscribe(testSubscriber());
    }

    @Test
    public void download() throws IOException {
        this.applicationId
                .then(this::uploadApplication)
                .flatMap(applicationId -> {
                    DownloadApplicationRequest request = DownloadApplicationRequest.builder()
                            .applicationId(applicationId)
                            .build();

                    return this.cloudFoundryClient.applicationsV2().download(request);
                })
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
                .flatMap(applicationId -> {
                    DownloadApplicationDropletRequest request = DownloadApplicationDropletRequest.builder()
                            .applicationId(applicationId)
                            .build();

                    return this.cloudFoundryClient.applicationsV2().downloadDroplet(request);
                })
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
                .then(applicationId -> {
                    ApplicationEnvironmentRequest request = ApplicationEnvironmentRequest.builder()
                            .applicationId(applicationId)
                            .build();

                    Mono<ApplicationEnvironmentResponse> environment = this.cloudFoundryClient.applicationsV2().environment(request);

                    return Mono.when(Mono.just(applicationId), environment);
                })
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
                .then(applicationId -> {
                    GetApplicationRequest request = GetApplicationRequest.builder()
                            .applicationId(applicationId)
                            .build();

                    Mono<String> actual = this.cloudFoundryClient.applicationsV2().get(request)
                            .map(Resources::getId);

                    return Mono.when(Mono.just(applicationId), actual);
                })
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void list() {
        this.applicationId
                .flatMap(applicationId -> {
                    ListApplicationsRequest request = ListApplicationsRequest.builder()
                            .build();

                    return this.cloudFoundryClient.applicationsV2().list(request)
                            .map(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void listFilterByDiego() {
        this.applicationId
                .then(applicationId -> {
                    ListApplicationsRequest expectFound = ListApplicationsRequest.builder()
                            .diego(true)
                            .build();

                    return ApplicationsTest.this.cloudFoundryClient.applicationsV2().list(expectFound)
                            .map(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void listFilterByName() {
        this.applicationId
                .then(applicationId -> {
                    ListApplicationsRequest expectFound = ListApplicationsRequest.builder()
                            .name(TEST_APPLICATION_NAME)
                            .build();

                    return ApplicationsTest.this.cloudFoundryClient.applicationsV2().list(expectFound)
                            .map(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void listFilterByOrganizationId() {
        this.applicationId
                .then(applicationId -> this.organizationId)
                .then(organizationId -> {
                    ListApplicationsRequest expectFound = ListApplicationsRequest.builder()
                            .organizationId(organizationId)
                            .build();

                    return ApplicationsTest.this.cloudFoundryClient.applicationsV2().list(expectFound)
                            .map(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void listFilterBySpaceId() {
        this.applicationId
                .then(applicationId -> this.spaceId)
                .then(spaceId -> {
                    ListApplicationsRequest expectFound = ListApplicationsRequest.builder()
                            .spaceId(spaceId)
                            .build();

                    return ApplicationsTest.this.cloudFoundryClient.applicationsV2().list(expectFound)
                            .map(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void listFilterByStackId() {
        this.applicationId
                .then(applicationId -> this.stackId)
                .then(stackId -> {
                    ListApplicationsRequest expectFound = ListApplicationsRequest.builder()
                            .stackId(stackId)
                            .build();

                    return ApplicationsTest.this.cloudFoundryClient.applicationsV2().list(expectFound)
                            .map(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void listRoutes() {
        createApplicationRoute()
                .then(response -> this.applicationId)
                .then(applicationId -> {
                    ListApplicationRoutesRequest request = ListApplicationRoutesRequest.builder()
                            .applicationId(applicationId)
                            .build();

                    return this.cloudFoundryClient.applicationsV2().listRoutes(request)
                            .map(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void listRoutesFilterByDomainId() {
        createApplicationRoute()
                .and(this.applicationId)
                .then(function((routeResponse, applicationId) -> {
                    ListApplicationRoutesRequest expectFound = ListApplicationRoutesRequest.builder()
                            .applicationId(applicationId)
                            .domainId(routeResponse.getEntity().getDomainId())
                            .build();

                    return ApplicationsTest.this.cloudFoundryClient.applicationsV2().listRoutes(expectFound)
                            .map(Resources::getResources);
                }))
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void listRoutesFilterByHost() {
        createApplicationRoute()
                .and(this.applicationId)
                .then(function((routeResponse, applicationId) -> {
                    ListApplicationRoutesRequest expectFound = ListApplicationRoutesRequest.builder()
                            .applicationId(applicationId)
                            .host(routeResponse.getEntity().getHost())
                            .build();

                    return ApplicationsTest.this.cloudFoundryClient.applicationsV2().listRoutes(expectFound)
                            .map(Resources::getResources);
                }))
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void listRoutesFilterByPath() {
        createApplicationRoute()
                .and(this.applicationId)
                .then(function((routeResponse, applicationId) -> {
                    ListApplicationRoutesRequest expectFound = ListApplicationRoutesRequest.builder()
                            .applicationId(applicationId)
                            .path(routeResponse.getEntity().getPath())
                            .build();

                    return ApplicationsTest.this.cloudFoundryClient.applicationsV2().listRoutes(expectFound)
                            .map(Resources::getResources);
                }))
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void listRoutesFilterByPort() {
        createApplicationRoute()
                .and(this.applicationId)
                .then(function((routeResponse, applicationId) -> {
                    ListApplicationRoutesRequest expectFound = ListApplicationRoutesRequest.builder()
                            .applicationId(applicationId)
                            .port(routeResponse.getEntity().getPort())
                            .build();

                    return ApplicationsTest.this.cloudFoundryClient.applicationsV2().listRoutes(expectFound)
                            .map(Resources::getResources);
                }))
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    //TODO Implement missing client API
    @Ignore("TODO: Awaiting CUPS https://www.pivotaltracker.com/story/show/101452058")
    @Test
    public void listServiceBindings() {
        this.applicationId
                .then(applicationId -> {
                    Mono<String> serviceInstanceId = Mono.just("");
                    return Mono.just(applicationId).and(serviceInstanceId);
                })
                .then(function((applicationId, serviceInstanceId) -> {
                    CreateServiceBindingRequest serviceBindingRequest = CreateServiceBindingRequest.builder()
                            .applicationId(applicationId)
                            .serviceInstanceId(serviceInstanceId)
                            .build();

                    return this.cloudFoundryClient.serviceBindings().create(serviceBindingRequest)
                            .map(response -> applicationId);
                }))
                .then(applicationId -> {
                    ListApplicationServiceBindingsRequest request = ListApplicationServiceBindingsRequest.builder()
                            .applicationId(applicationId)
                            .build();

                    return ApplicationsTest.this.cloudFoundryClient.applicationsV2().listServiceBindings(request)
                            .map(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    //TODO Implement missing client API
    @Ignore("TODO: Awaiting CUPS https://www.pivotaltracker.com/story/show/101452058")
    @Test
    public void listServiceBindingsFilterByServiceInstanceId() {
        this.applicationId
                .then(applicationId -> {
                    String serviceInstanceId = "CREATE ME";

                    ListApplicationServiceBindingsRequest expectFound = ListApplicationServiceBindingsRequest.builder()
                            .applicationId(applicationId)
                            .serviceInstanceId(serviceInstanceId)
                            .build();

                    return ApplicationsTest.this.cloudFoundryClient.applicationsV2().listServiceBindings(expectFound)
                            .map(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void removeRoute() {
        createApplicationRoute()
                .and(this.applicationId)
                .then(function((routeResponse, applicationId) -> {
                    String routeId = Resources.getId(routeResponse);

                    RemoveApplicationRouteRequest request = RemoveApplicationRouteRequest.builder()
                            .applicationId(applicationId)
                            .routeId(routeId)
                            .build();

                    return this.cloudFoundryClient.applicationsV2().removeRoute(request);
                }))
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
                .then(applicationId -> {
                    RestageApplicationRequest request = RestageApplicationRequest.builder()
                            .applicationId(applicationId)
                            .build();

                    return this.cloudFoundryClient.applicationsV2().restage(request)
                            .map(Resources::getId);
                })
                .then(applicationId -> {
                    return waitForStaging(applicationId)
                            .map(state -> true);
                })
                .subscribe(testSubscriber()
                        .assertEquals(true));
    }

    @Test
    public void statistics() {
        this.applicationId
                .then(this::uploadAndStartApplication)
                .then(applicationId -> {
                    ApplicationStatisticsRequest request = ApplicationStatisticsRequest.builder()
                            .applicationId(applicationId)
                            .build();

                    return this.cloudFoundryClient.applicationsV2().statistics(request)
                            .map(instanceStatistics -> instanceStatistics.get("0").getStatistics().getName())
                            .as(Stream::from)
                            .retry(5l, throwable -> throwable instanceof NullPointerException)
                            .single();
                })
                .subscribe(testSubscriber()
                        .assertEquals(TEST_APPLICATION_NAME));
    }

    @Test
    public void summary() {
        this.applicationId
                .then(applicationId -> {
                    SummaryApplicationRequest request = SummaryApplicationRequest.builder()
                            .applicationId(applicationId)
                            .build();

                    return this.cloudFoundryClient.applicationsV2().summary(request)
                            .map(SummaryApplicationResponse::getId)
                            .and(Mono.just(applicationId));
                })
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void terminateInstance() {
        this.applicationId
                .then(this::uploadAndStartApplication)
                .then(applicationId -> {

                    TerminateApplicationInstanceRequest request = TerminateApplicationInstanceRequest.builder()
                            .applicationId(applicationId)
                            .index("0")
                            .build();

                    return this.cloudFoundryClient.applicationsV2().terminateInstance(request);
                })
                .subscribe(testSubscriber());
    }

    @Test
    public void update() {
        this.applicationId
                .then(applicationId -> {
                    UpdateApplicationRequest request = UpdateApplicationRequest.builder()
                            .applicationId(applicationId)
                            .name("another-test-application-name")
                            .build();

                    return this.cloudFoundryClient.applicationsV2().update(request)
                            .map(Resources::getId);
                })
                .then(applicationId -> {
                    GetApplicationRequest request = GetApplicationRequest.builder()
                            .applicationId(applicationId)
                            .build();

                    return this.cloudFoundryClient.applicationsV2().get(request)
                            .map(response -> response.getEntity().getName());
                })
                .subscribe(testSubscriber()
                        .assertEquals("another-test-application-name"));
    }

    @Test
    public void upload() throws IOException {
        this.applicationId
                .then(this::uploadApplication)
                .flatMap(applicationId -> {
                    DownloadApplicationRequest request = DownloadApplicationRequest.builder()
                            .applicationId(applicationId)
                            .build();

                    return this.cloudFoundryClient.applicationsV2().download(request);
                })
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
                .then(organizationId -> {
                    CreateDomainRequest request = CreateDomainRequest.builder()
                            .name("test.domain.name")
                            .owningOrganizationId(organizationId)
                            .wildcard(true)
                            .build();

                    return this.cloudFoundryClient.domains().create(request)
                            .map(Resources::getId);
                })
                .and(this.spaceId)
                .then(function((domainId, spaceId) -> {
                    CreateRouteRequest createRouteRequest = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .host("test-host")
                            .path("/test-path")
                            .spaceId(spaceId)
                            .build();

                    return this.cloudFoundryClient.routes().create(createRouteRequest)
                            .and(this.applicationId);
                }))
                .then(function((createRouteResponse, applicationId) -> {
                    AssociateApplicationRouteRequest request = AssociateApplicationRouteRequest.builder()
                            .applicationId(applicationId)
                            .routeId(createRouteResponse.getMetadata().getId())
                            .build();

                    return this.cloudFoundryClient.applicationsV2().associateRoute(request)
                            .map(response -> createRouteResponse);
                }));
    }

    private Mono<String> startApplication(String applicationId) {
        UpdateApplicationRequest request = UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .state("STARTED")
                .build();

        return this.cloudFoundryClient.applicationsV2().update(request)
                .map(Resources::getId)
                .then(this::waitForStaging)
                .then(this::waitForStarting);
    }

    private Mono<String> uploadAndStartApplication(String applicationId) {
        return uploadApplication(applicationId)
                .then(this::startApplication);
    }

    private Mono<String> uploadApplication(String applicationId) {
        UploadApplicationRequest request = UploadApplicationRequest.builder()
                .application(new File("./src/test/resources/testApplication.zip"))
                .async(false)
                .applicationId(applicationId)
                .build();

        return this.cloudFoundryClient.applicationsV2().upload(request)
                .map(response -> applicationId);
    }

    private Mono<String> waitForStaging(String applicationId) {
        GetApplicationRequest request = GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build();

        return this.cloudFoundryClient.applicationsV2().get(request)
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
        ApplicationInstancesRequest request = ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build();

        return this.cloudFoundryClient.applicationsV2().instances(request)
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
