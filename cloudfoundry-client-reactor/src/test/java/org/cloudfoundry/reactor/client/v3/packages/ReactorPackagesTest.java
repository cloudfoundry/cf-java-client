/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.packages;

import org.cloudfoundry.client.v3.BuildpackData;
import org.cloudfoundry.client.v3.Checksum;
import org.cloudfoundry.client.v3.ChecksumType;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.LifecycleType;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.droplets.Buildpack;
import org.cloudfoundry.client.v3.droplets.DropletResource;
import org.cloudfoundry.client.v3.droplets.DropletState;
import org.cloudfoundry.client.v3.packages.BitsData;
import org.cloudfoundry.client.v3.packages.CopyPackageRequest;
import org.cloudfoundry.client.v3.packages.CopyPackageResponse;
import org.cloudfoundry.client.v3.packages.CreatePackageRequest;
import org.cloudfoundry.client.v3.packages.CreatePackageResponse;
import org.cloudfoundry.client.v3.packages.DeletePackageRequest;
import org.cloudfoundry.client.v3.packages.DockerData;
import org.cloudfoundry.client.v3.packages.DownloadPackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageResponse;
import org.cloudfoundry.client.v3.packages.ListPackageDropletsRequest;
import org.cloudfoundry.client.v3.packages.ListPackageDropletsResponse;
import org.cloudfoundry.client.v3.packages.ListPackagesRequest;
import org.cloudfoundry.client.v3.packages.ListPackagesResponse;
import org.cloudfoundry.client.v3.packages.PackageRelationships;
import org.cloudfoundry.client.v3.packages.PackageResource;
import org.cloudfoundry.client.v3.packages.PackageState;
import org.cloudfoundry.client.v3.packages.PackageType;
import org.cloudfoundry.client.v3.packages.UploadPackageRequest;
import org.cloudfoundry.client.v3.packages.UploadPackageResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.cloudfoundry.util.OperationUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;

public final class ReactorPackagesTest extends AbstractClientApiTest {

    private final ReactorPackages packages = new ReactorPackages(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void copy() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/packages?source_guid=test-source-package-id")
                .payload("fixtures/client/v3/packages/POST_source_guid={id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v3/packages/POST_source_guid={id}_response.json")
                .build())
            .build());

        this.packages
            .copy(CopyPackageRequest.builder()
                .sourcePackageId("test-source-package-id")
                .relationships(PackageRelationships.builder()
                    .application(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("[destination-app-guid]")
                            .build())
                        .build())
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(CopyPackageResponse.builder()
                .id("44f7c078-0934-470f-9883-4fcddc5b8f13")
                .type(PackageType.BITS)
                .data(BitsData.builder()
                    .checksum(Checksum.builder()
                        .type(ChecksumType.SHA256)
                        .value(null)
                        .build())
                    .error(null)
                    .build())
                .state(PackageState.PROCESSING_UPLOAD)
                .createdAt("2015-11-13T17:02:56Z")
                .updatedAt("2016-06-08T16:41:26Z")
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/packages/44f7c078-0934-470f-9883-4fcddc5b8f13")
                    .build())
                .link("upload", Link.builder()
                    .href("https://api.example.org/v3/packages/44f7c078-0934-470f-9883-4fcddc5b8f13/upload")
                    .method("POST")
                    .build())
                .link("download", Link.builder()
                    .href("https://api.example.org/v3/packages/44f7c078-0934-470f-9883-4fcddc5b8f13/download")
                    .method("GET")
                    .build())
                .link("app", Link.builder()
                    .href("https://api.example.org/v3/apps/1d3bf0ec-5806-43c4-b64e-8364dba1086a")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/packages")
                .payload("fixtures/client/v3/packages/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v3/packages/POST_response.json")
                .build())
            .build());

        this.packages
            .create(CreatePackageRequest.builder()
                .type(PackageType.BITS)
                .relationships(PackageRelationships.builder()
                    .application(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("[guid]")
                            .build())
                        .build())
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(CreatePackageResponse.builder()
                .id("44f7c078-0934-470f-9883-4fcddc5b8f13")
                .type(PackageType.BITS)
                .data(BitsData.builder()
                    .checksum(Checksum.builder()
                        .type(ChecksumType.SHA256)
                        .value(null)
                        .build())
                    .error(null)
                    .build())
                .state(PackageState.PROCESSING_UPLOAD)
                .createdAt("2015-11-13T17:02:56Z")
                .updatedAt("2016-06-08T16:41:26Z")
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/packages/44f7c078-0934-470f-9883-4fcddc5b8f13")
                    .build())
                .link("upload", Link.builder()
                    .href("https://api.example.org/v3/packages/44f7c078-0934-470f-9883-4fcddc5b8f13/upload")
                    .method("POST")
                    .build())
                .link("download", Link.builder()
                    .href("https://api.example.org/v3/packages/44f7c078-0934-470f-9883-4fcddc5b8f13/download")
                    .method("GET")
                    .build())
                .link("app", Link.builder()
                    .href("https://api.example.org/v3/apps/1d3bf0ec-5806-43c4-b64e-8364dba1086a")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/packages/test-package-id")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .header("Location", "https://api.example.org/v3/jobs/[guid]")
                .build())
            .build());

        this.packages
            .delete(DeletePackageRequest.builder()
                .packageId("test-package-id")
                .build())
            .as(StepVerifier::create)
            .expectNext("[guid]")
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void download() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/packages/test-package-id/download")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/packages/GET_{id}_download_response.bin")
                .build())
            .build());

        this.packages
            .download(DownloadPackageRequest.builder()
                .packageId("test-package-id")
                .build())
            .as(OperationUtils::collectByteArray)
            .as(StepVerifier::create)
            .consumeNextWith(actual -> assertThat(actual).isEqualTo(getBytes("fixtures/client/v3/packages/GET_{id}_download_response.bin")))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/packages/test-package-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/packages/GET_{id}_response.json")
                .build())
            .build());

        this.packages
            .get(GetPackageRequest.builder()
                .packageId("test-package-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetPackageResponse.builder()
                .id("44f7c078-0934-470f-9883-4fcddc5b8f13")
                .type(PackageType.BITS)
                .data(BitsData.builder()
                    .checksum(Checksum.builder()
                        .type(ChecksumType.SHA256)
                        .value(null)
                        .build())
                    .error(null)
                    .build())
                .state(PackageState.PROCESSING_UPLOAD)
                .createdAt("2015-11-13T17:02:56Z")
                .updatedAt("2016-06-08T16:41:26Z")
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/packages/44f7c078-0934-470f-9883-4fcddc5b8f13")
                    .build())
                .link("upload", Link.builder()
                    .href("https://api.example.org/v3/packages/44f7c078-0934-470f-9883-4fcddc5b8f13/upload")
                    .method("POST")
                    .build())
                .link("download", Link.builder()
                    .href("https://api.example.org/v3/packages/44f7c078-0934-470f-9883-4fcddc5b8f13/download")
                    .method("GET")
                    .build())
                .link("app", Link.builder()
                    .href("https://api.example.org/v3/apps/1d3bf0ec-5806-43c4-b64e-8364dba1086a")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/packages")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/packages/GET_response.json")
                .build())
            .build());

        this.packages
            .list(ListPackagesRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListPackagesResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(2)
                    .totalPages(1)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/packages?types=bits%2Cdocker&page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/packages?types=bits%2Cdocker&page=2&per_page=2")
                        .build())
                    .build())
                .resource(PackageResource.builder()
                    .id("a57fd932-85db-483a-a27e-b00efbb3b0a4")
                    .type(PackageType.BITS)
                    .data(BitsData.builder()
                        .checksum(Checksum.builder()
                            .type(ChecksumType.SHA256)
                            .value(null)
                            .build())
                        .error(null)
                        .build())
                    .state(PackageState.AWAITING_UPLOAD)
                    .createdAt("2015-11-03T00:53:54Z")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/packages/a57fd932-85db-483a-a27e-b00efbb3b0a4")
                        .build())
                    .link("upload", Link.builder()
                        .href("https://api.example.org/v3/packages/a57fd932-85db-483a-a27e-b00efbb3b0a4/upload")
                        .method("POST")
                        .build())
                    .link("download", Link.builder()
                        .href("https://api.example.org/v3/packages/a57fd932-85db-483a-a27e-b00efbb3b0a4/download")
                        .method("GET")
                        .build())
                    .link("app", Link.builder()
                        .href("https://api.example.org/v3/apps/fa3558ce-1c4d-46fc-9776-54b9c8021745")
                        .build())
                    .build())
                .resource(PackageResource.builder()
                    .id("8f1f294d-cef8-4c11-9f0b-3bcdc0bd2691")
                    .type(PackageType.DOCKER)
                    .data(DockerData.builder()
                        .image("registry/image:latest")
                        .username("username")
                        .password("***")
                        .build())
                    .state(PackageState.READY)
                    .createdAt("2015-11-03T00:53:54Z")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/packages/8f1f294d-cef8-4c11-9f0b-3bcdc0bd2691")
                        .build())
                    .link("app", Link.builder()
                        .href("https://api.example.org/v3/apps/fa3558ce-1c4d-46fc-9776-54b9c8021745")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listDroplets() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/packages/test-package-id/droplets")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/packages/GET_{id}_droplets_response.json")
                .build())
            .build());

        this.packages
            .listDroplets(ListPackageDropletsRequest.builder()
                .packageId("test-package-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListPackageDropletsResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(2)
                    .totalPages(1)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/packages/7b34f1cf-7e73-428a-bb5a-8a17a8058396/droplets?page=1&per_page=50")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/packages/7b34f1cf-7e73-428a-bb5a-8a17a8058396/droplets?page=1&per_page=50")
                        .build())
                    .build())
                .resource(DropletResource.builder()
                    .id("585bc3c1-3743-497d-88b0-403ad6b56d16")
                    .state(DropletState.STAGED)
                    .error(null)
                    .lifecycle(Lifecycle.builder()
                        .type(LifecycleType.BUILDPACK)
                        .data(BuildpackData.builder()
                            .build())
                        .build())
                    .image(null)
                    .executionMetadata("PRIVATE DATA HIDDEN")
                    .processType("redacted_message", "[PRIVATE DATA HIDDEN IN LISTS]")
                    .checksum(Checksum.builder()
                        .type(ChecksumType.SHA256)
                        .value("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
                        .build())
                    .buildpack(Buildpack.builder()
                        .name("ruby_buildpack")
                        .detectOutput("ruby 1.6.14")
                        .build())
                    .stack("cflinuxfs2")
                    .createdAt("2016-03-28T23:39:34Z")
                    .updatedAt("2016-03-28T23:39:47Z")
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/droplets/585bc3c1-3743-497d-88b0-403ad6b56d16")
                        .build())
                    .link("package", Link.builder()
                        .href("https://api.example.org/v3/packages/8222f76a-9e09-4360-b3aa-1ed329945e92")
                        .build())
                    .link("app", Link.builder()
                        .href("https://api.example.org/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396")
                        .build())
                    .link("assign_current_droplet", Link.builder()
                        .href("https://api.example.org/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396/relationships/current_droplet")
                        .method("PATCH")
                        .build())
                    .build())
                .resource(DropletResource.builder()
                    .id("fdf3851c-def8-4de1-87f1-6d4543189e22")
                    .state(DropletState.STAGED)
                    .error(null)
                    .lifecycle(Lifecycle.builder()
                        .type(LifecycleType.DOCKER)
                        .data(org.cloudfoundry.client.v3.DockerData.builder()
                            .build())
                        .build())
                    .executionMetadata("[PRIVATE DATA HIDDEN IN LISTS]")
                    .processType("redacted_message", "[PRIVATE DATA HIDDEN IN LISTS]")
                    .image("cloudfoundry/diego-docker-app-custom:latest")
                    .checksum(null)
                    .stack(null)
                    .createdAt("2016-03-17T00:00:01Z")
                    .updatedAt("2016-03-17T21:41:32Z")
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/droplets/fdf3851c-def8-4de1-87f1-6d4543189e22")
                        .build())
                    .link("package", Link.builder()
                        .href("https://api.example.org/v3/packages/c5725684-a02f-4e59-bc67-8f36ae944688")
                        .build())
                    .link("app", Link.builder()
                        .href("https://api.example.org/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396")
                        .build())
                    .link("assign_current_droplet", Link.builder()
                        .href("https://api.example.org/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396/relationships/current_droplet")
                        .method("PATCH")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void upload() throws IOException {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/packages/test-package-id/upload")
                .contents(consumer((headers, body) -> {
                    String boundary = extractBoundary(headers);

                    assertThat(body.readString(Charset.defaultCharset()))
                        .isEqualTo("--" + boundary + "\r\n" +
                            "content-disposition: form-data; name=\"bits\"; filename=\"test-package.zip\"\r\n" +
                            "content-length: 12\r\n" +
                            "content-type: application/zip\r\n" +
                            "content-transfer-encoding: binary\r\n" +
                            "\r\n" +
                            "test-content" +
                            "\r\n" +
                            "--" + boundary + "--\r\n");
                }))
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v3/packages/POST_{id}_upload_response.json")
                .build())
            .build());

        this.packages
            .upload(UploadPackageRequest.builder()
                .bits(new ClassPathResource("fixtures/client/v3/packages/test-package.zip").getFile().toPath())
                .packageId("test-package-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(UploadPackageResponse.builder()
                .id("44f7c078-0934-470f-9883-4fcddc5b8f13")
                .type(PackageType.BITS)
                .data(BitsData.builder()
                    .checksum(Checksum.builder()
                        .type(ChecksumType.SHA256)
                        .value(null)
                        .build())
                    .error(null)
                    .build())
                .state(PackageState.PROCESSING_UPLOAD)
                .createdAt("2015-11-13T17:02:56Z")
                .updatedAt("2016-06-08T16:41:26Z")
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/packages/44f7c078-0934-470f-9883-4fcddc5b8f13")
                    .build())
                .link("upload", Link.builder()
                    .href("https://api.example.org/v3/packages/44f7c078-0934-470f-9883-4fcddc5b8f13/upload")
                    .method("POST")
                    .build())
                .link("download", Link.builder()
                    .href("https://api.example.org/v3/packages/44f7c078-0934-470f-9883-4fcddc5b8f13/download")
                    .method("GET")
                    .build())
                .link("app", Link.builder()
                    .href("https://api.example.org/v3/apps/1d3bf0ec-5806-43c4-b64e-8364dba1086a")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
