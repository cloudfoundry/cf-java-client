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

package org.cloudfoundry.reactor.client.v3.buildpacks;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Collections;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.buildpacks.BuildpackResource;
import org.cloudfoundry.client.v3.buildpacks.BuildpackState;
import org.cloudfoundry.client.v3.buildpacks.CreateBuildpackRequest;
import org.cloudfoundry.client.v3.buildpacks.CreateBuildpackResponse;
import org.cloudfoundry.client.v3.buildpacks.DeleteBuildpackRequest;
import org.cloudfoundry.client.v3.buildpacks.GetBuildpackRequest;
import org.cloudfoundry.client.v3.buildpacks.GetBuildpackResponse;
import org.cloudfoundry.client.v3.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.client.v3.buildpacks.ListBuildpacksResponse;
import org.cloudfoundry.client.v3.buildpacks.UpdateBuildpackRequest;
import org.cloudfoundry.client.v3.buildpacks.UpdateBuildpackResponse;
import org.cloudfoundry.client.v3.buildpacks.UploadBuildpackRequest;
import org.cloudfoundry.client.v3.buildpacks.UploadBuildpackResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.cloudfoundry.reactor.client.v3.builpacks.ReactorBuildpacksV3;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.test.StepVerifier;

public final class ReactorBuildpacksTest extends AbstractClientApiTest {

    private final ReactorBuildpacksV3 buildpacks =
            new ReactorBuildpacksV3(
                    CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void create() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(POST)
                                        .path("/buildpacks")
                                        .payload("fixtures/client/v3/buildpacks/POST_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(CREATED)
                                        .payload("fixtures/client/v3/buildpacks/POST_response.json")
                                        .build())
                        .build());

        this.buildpacks
                .create(
                        CreateBuildpackRequest.builder()
                                .name("ruby_buildpack")
                                .position(42)
                                .enabled(true)
                                .locked(false)
                                .stack("windows64")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        CreateBuildpackResponse.builder()
                                .id("fd35633f-5c5c-4e4e-a5a9-0722c970a9d2")
                                .createdAt("2016-03-18T23:26:46Z")
                                .updatedAt("2016-10-17T20:00:42Z")
                                .name("ruby_buildpack")
                                .state(BuildpackState.AWAITING_UPLOAD)
                                .stack("windows64")
                                .position(42)
                                .enabled(true)
                                .locked(false)
                                .metadata(
                                        Metadata.builder()
                                                .annotations(Collections.emptyMap())
                                                .labels(Collections.emptyMap())
                                                .build())
                                .link(
                                        "self",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/buildpacks/fd35633f-5c5c-4e4e-a5a9-0722c970a9d2")
                                                .build())
                                .link(
                                        "upload",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/buildpacks/fd35633f-5c5c-4e4e-a5a9-0722c970a9d2/upload")
                                                .method("POST")
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(DELETE)
                                        .path("/buildpacks/test-buildpack-id")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(ACCEPTED)
                                        .header(
                                                "Location",
                                                "https://api.example.org/v3/jobs/[guid]")
                                        .build())
                        .build());

        this.buildpacks
                .delete(DeleteBuildpackRequest.builder().buildpackId("test-buildpack-id").build())
                .as(StepVerifier::create)
                .expectNext("[guid]")
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(GET)
                                        .path("/buildpacks/test-buildpack-id")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/buildpacks/GET_{id}_response.json")
                                        .build())
                        .build());

        this.buildpacks
                .get(GetBuildpackRequest.builder().buildpackId("test-buildpack-id").build())
                .as(StepVerifier::create)
                .expectNext(
                        GetBuildpackResponse.builder()
                                .id("fd35633f-5c5c-4e4e-a5a9-0722c970a9d2")
                                .createdAt("2016-03-18T23:26:46Z")
                                .updatedAt("2016-10-17T20:00:42Z")
                                .name("ruby_buildpack")
                                .state(BuildpackState.AWAITING_UPLOAD)
                                .stack("windows64")
                                .position(42)
                                .enabled(true)
                                .locked(false)
                                .metadata(
                                        Metadata.builder()
                                                .annotations(Collections.emptyMap())
                                                .labels(Collections.emptyMap())
                                                .build())
                                .link(
                                        "self",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/buildpacks/fd35633f-5c5c-4e4e-a5a9-0722c970a9d2")
                                                .build())
                                .link(
                                        "upload",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/buildpacks/fd35633f-5c5c-4e4e-a5a9-0722c970a9d2/upload")
                                                .method("POST")
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(
                InteractionContext.builder()
                        .request(TestRequest.builder().method(GET).path("/buildpacks").build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload("fixtures/client/v3/buildpacks/GET_response.json")
                                        .build())
                        .build());

        this.buildpacks
                .list(ListBuildpacksRequest.builder().build())
                .as(StepVerifier::create)
                .expectNext(
                        ListBuildpacksResponse.builder()
                                .pagination(
                                        Pagination.builder()
                                                .totalResults(3)
                                                .totalPages(2)
                                                .first(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/buildpacks?page=1&per_page=2")
                                                                .build())
                                                .last(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/buildpacks?page=2&per_page=2")
                                                                .build())
                                                .next(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/buildpacks?page=2&per_page=2")
                                                                .build())
                                                .build())
                                .resource(
                                        BuildpackResource.builder()
                                                .id("fd35633f-5c5c-4e4e-a5a9-0722c970a9d2")
                                                .createdAt("2016-03-18T23:26:46Z")
                                                .updatedAt("2016-10-17T20:00:42Z")
                                                .name("my-buildpack")
                                                .state(BuildpackState.AWAITING_UPLOAD)
                                                .stack("my-stack")
                                                .position(1)
                                                .enabled(true)
                                                .locked(false)
                                                .metadata(
                                                        Metadata.builder()
                                                                .annotations(Collections.emptyMap())
                                                                .labels(Collections.emptyMap())
                                                                .build())
                                                .link(
                                                        "self",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/buildpacks/fd35633f-5c5c-4e4e-a5a9-0722c970a9d2")
                                                                .build())
                                                .link(
                                                        "upload",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/buildpacks/fd35633f-5c5c-4e4e-a5a9-0722c970a9d2/upload")
                                                                .method("POST")
                                                                .build())
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(PATCH)
                                        .path("/buildpacks/test-buildpack-id")
                                        .payload(
                                                "fixtures/client/v3/buildpacks/PATCH_{id}_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/buildpacks/PATCH_{id}_response.json")
                                        .build())
                        .build());

        this.buildpacks
                .update(
                        UpdateBuildpackRequest.builder()
                                .buildpackId("test-buildpack-id")
                                .name("ruby_buildpack")
                                .position(42)
                                .enabled(true)
                                .locked(false)
                                .stack("windows64")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        UpdateBuildpackResponse.builder()
                                .id("fd35633f-5c5c-4e4e-a5a9-0722c970a9d2")
                                .createdAt("2016-03-18T23:26:46Z")
                                .updatedAt("2016-10-17T20:00:42Z")
                                .name("ruby_buildpack")
                                .state(BuildpackState.AWAITING_UPLOAD)
                                .stack("windows64")
                                .position(42)
                                .enabled(true)
                                .locked(false)
                                .metadata(
                                        Metadata.builder()
                                                .annotations(Collections.emptyMap())
                                                .labels(Collections.emptyMap())
                                                .build())
                                .link(
                                        "self",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/buildpacks/fd35633f-5c5c-4e4e-a5a9-0722c970a9d2")
                                                .build())
                                .link(
                                        "upload",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/buildpacks/fd35633f-5c5c-4e4e-a5a9-0722c970a9d2/upload")
                                                .method("POST")
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    public void upload() throws IOException {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(POST)
                                        .path("/buildpacks/test-buildpack-id/upload")
                                        .contents(
                                                consumer(
                                                        (headers, body) -> {
                                                            String boundary =
                                                                    extractBoundary(headers);

                                                            assertThat(
                                                                            body.readString(
                                                                                    Charset
                                                                                            .defaultCharset()))
                                                                    .isEqualTo(
                                                                            "--"
                                                                                    + boundary
                                                                                    + "\r\n"
                                                                                    + "content-disposition:"
                                                                                    + " form-data;"
                                                                                    + " name=\"bits\";"
                                                                                    + " filename=\"test-buildpack.zip\"\r\n"
                                                                                    + "content-length:"
                                                                                    + " 12\r\n"
                                                                                    + "content-type:"
                                                                                    + " application/zip\r\n"
                                                                                    + "content-transfer-encoding:"
                                                                                    + " binary\r\n"
                                                                                    + "\r\n"
                                                                                    + "test-content\r\n"
                                                                                    + "--"
                                                                                    + boundary
                                                                                    + "--\r\n");
                                                        }))
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(CREATED)
                                        .payload(
                                                "fixtures/client/v3/buildpacks/POST_{id}_upload_response.json")
                                        .build())
                        .build());

        this.buildpacks
                .upload(
                        UploadBuildpackRequest.builder()
                                .bits(
                                        new ClassPathResource(
                                                        "fixtures/client/v3/buildpacks/test-buildpack.zip")
                                                .getFile()
                                                .toPath())
                                .buildpackId("test-buildpack-id")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        UploadBuildpackResponse.builder()
                                .id("fd35633f-5c5c-4e4e-a5a9-0722c970a9d2")
                                .createdAt("2016-03-18T23:26:46Z")
                                .updatedAt("2016-10-17T20:00:42Z")
                                .name("ruby_buildpack")
                                .state(BuildpackState.AWAITING_UPLOAD)
                                .stack("windows64")
                                .position(42)
                                .enabled(true)
                                .locked(false)
                                .metadata(
                                        Metadata.builder()
                                                .annotations(Collections.emptyMap())
                                                .labels(Collections.emptyMap())
                                                .build())
                                .link(
                                        "self",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/buildpacks/fd35633f-5c5c-4e4e-a5a9-0722c970a9d2")
                                                .build())
                                .link(
                                        "upload",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/buildpacks/fd35633f-5c5c-4e4e-a5a9-0722c970a9d2/upload")
                                                .method("POST")
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }
}
