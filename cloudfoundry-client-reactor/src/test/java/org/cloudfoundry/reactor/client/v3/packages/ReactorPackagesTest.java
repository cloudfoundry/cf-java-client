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

package org.cloudfoundry.reactor.client.v3.packages;

import org.cloudfoundry.client.v3.Checksum;
import org.cloudfoundry.client.v3.ChecksumType;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
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

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;

public final class ReactorPackagesTest extends AbstractClientApiTest {

    private final ReactorPackages packages = new ReactorPackages(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

    @Test
    public void copy() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/v3/packages?source_guid=test-source-package-id")
                .payload("fixtures/client/v3/packages/POST_?source_guid={id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v3/packages/POST_?source_guid={id}_response.json")
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
                .method(POST).path("/v3/packages")
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
                .method(DELETE).path("/v3/packages/test-package-id")
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
                .method(GET).path("/v3/packages/test-package-id/download")
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
                .method(GET).path("/v3/packages/test-package-id")
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
                .method(GET).path("/v3/packages")
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
    public void upload() throws IOException {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/v3/packages/test-package-id/upload")
                .contents(consumer((headers, body) -> {
                    String boundary = extractBoundary(headers);

                    assertThat(body.readString(Charset.defaultCharset()))
                        .isEqualTo("\r\n--" + boundary + "\r\n" +
                            "content-disposition: form-data; name=\"bits\"; filename=\"application.zip\"\r\n" +
                            "content-length: 13\r\n" +
                            "content-type: application/zip\r\n" +
                            "\r\n" +
                            "test-content\n" +
                            "\r\n" +
                            "--" + boundary + "--");
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
