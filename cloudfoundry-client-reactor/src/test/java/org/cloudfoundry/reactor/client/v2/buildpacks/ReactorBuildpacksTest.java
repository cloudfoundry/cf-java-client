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

package org.cloudfoundry.reactor.client.v2.buildpacks;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.buildpacks.BuildpackEntity;
import org.cloudfoundry.client.v2.buildpacks.BuildpackResource;
import org.cloudfoundry.client.v2.buildpacks.CreateBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.CreateBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.DeleteBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.DeleteBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.GetBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.GetBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksResponse;
import org.cloudfoundry.client.v2.buildpacks.UpdateBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.UpdateBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.UploadBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.UploadBuildpackResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
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
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;

public final class ReactorBuildpacksTest extends AbstractClientApiTest {

    private ReactorBuildpacks buildpacks = new ReactorBuildpacks(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/buildpacks")
                .payload("fixtures/client/v2/buildpacks/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/buildpacks/POST_response.json")
                .build())
            .build());

        this.buildpacks
            .create(CreateBuildpackRequest.builder()
                .name("Golang_buildpack")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateBuildpackResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:31Z")
                    .id("11bd4dd1-134e-4d15-b8fe-dd9f36bae66a")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .url("/v2/buildpacks/11bd4dd1-134e-4d15-b8fe-dd9f36bae66a")
                    .build())
                .entity(BuildpackEntity.builder()
                    .enabled(true)
                    .locked(false)
                    .name("Golang_buildpack")
                    .position(1)
                    .stack("cflinuxfs2")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/buildpacks/test-buildpack-id?async=true")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/buildpacks/DELETE_{id}_response.json")
                .build())
            .build());

        this.buildpacks
            .delete(DeleteBuildpackRequest.builder()
                .async(true)
                .buildpackId("test-buildpack-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(DeleteBuildpackResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-07-27T22:43:34Z")
                    .id("c900719e-c70a-4c75-9e6a-9535f118acc3")
                    .url("/v2/jobs/c900719e-c70a-4c75-9e6a-9535f118acc3")
                    .build())
                .entity(JobEntity.builder()
                    .id("c900719e-c70a-4c75-9e6a-9535f118acc3")
                    .status("queued")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/buildpacks/test-buildpack-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/buildpacks/GET_{id}_response.json")
                .build())
            .build());

        this.buildpacks
            .get(GetBuildpackRequest.builder()
                .buildpackId("test-buildpack-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetBuildpackResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-03-17T21:41:28Z")
                    .id("35d3fa06-08db-4b9e-b2a7-58724a179687")
                    .url("/v2/buildpacks/35d3fa06-08db-4b9e-b2a7-58724a179687")
                    .build()
                )
                .entity(BuildpackEntity.builder()
                    .enabled(true)
                    .filename("name-2302")
                    .locked(false)
                    .name("name_1")
                    .position(1)
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/buildpacks?q=name%3Atest-name&page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/buildpacks/GET_response.json")
                .build())
            .build());

        this.buildpacks
            .list(ListBuildpacksRequest.builder()
                .name("test-name")
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListBuildpacksResponse.builder()
                .totalResults(3)
                .totalPages(1)
                .resource(BuildpackResource.builder()
                    .metadata(Metadata.builder()
                        .id("45203d32-475b-4d55-9d34-3ffc935edd49")
                        .url("/v2/buildpacks/45203d32-475b-4d55-9d34-3ffc935edd49")
                        .createdAt("2016-03-17T21:41:28Z")
                        .build())
                    .entity(BuildpackEntity.builder()
                        .enabled(true)
                        .filename("name-2308")
                        .locked(false)
                        .name("name_1")
                        .position(1)
                        .build())
                    .build())
                .resource(BuildpackResource.builder()
                    .metadata(Metadata.builder()
                        .id("1aeb95ef-7058-495c-b260-dea2e8efb976")
                        .url("/v2/buildpacks/1aeb95ef-7058-495c-b260-dea2e8efb976")
                        .createdAt("2016-03-17T21:41:28Z")
                        .build())
                    .entity(BuildpackEntity.builder()
                        .enabled(true)
                        .filename("name-2309")
                        .locked(false)
                        .name("name_2")
                        .position(2)
                        .build())
                    .build())
                .resource(BuildpackResource.builder()
                    .metadata(Metadata.builder()
                        .id("4dd0046a-7a54-4f57-a31f-06d7e57eb463")
                        .url("/v2/buildpacks/4dd0046a-7a54-4f57-a31f-06d7e57eb463")
                        .createdAt("2016-03-17T21:41:28Z")
                        .build())
                    .entity(BuildpackEntity.builder()
                        .enabled(true)
                        .filename("name-2310")
                        .locked(false)
                        .name("name_3")
                        .position(3)
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/buildpacks/test-buildpack-id")
                .payload("fixtures/client/v2/buildpacks/PUT_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/buildpacks/PUT_{id}_response.json")
                .build())
            .build());

        this.buildpacks
            .update(UpdateBuildpackRequest.builder()
                .buildpackId("test-buildpack-id")
                .enabled(false)
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateBuildpackResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-03-17T21:41:28Z")
                    .id("edd64481-e13c-4193-b6cc-2a727a62e817")
                    .updatedAt("2016-03-17T21:41:28Z")
                    .url("/v2/buildpacks/edd64481-e13c-4193-b6cc-2a727a62e817")
                    .build())
                .entity(BuildpackEntity.builder()
                    .enabled(false)
                    .filename("name-2314")
                    .locked(false)
                    .name("name_1")
                    .position(1)
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void upload() throws IOException {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/buildpacks/test-buildpack-id/bits")
                .contents(consumer((headers, body) -> {
                    String boundary = extractBoundary(headers);

                    assertThat(body.readString(Charset.defaultCharset()))
                        .isEqualTo("--" + boundary + "\r\n" +
                            "content-disposition: form-data; name=\"buildpack\"; filename=\"test-filename\"\r\n" +
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
                .payload("fixtures/client/v2/buildpacks/PUT_{id}_bits_response.json")
                .build())
            .build());

        this.buildpacks
            .upload(UploadBuildpackRequest.builder()
                .buildpack(new ClassPathResource("fixtures/client/v2/buildpacks/test-buildpack.zip").getFile().toPath())
                .buildpackId("test-buildpack-id")
                .filename("test-filename")
                .build())
            .as(StepVerifier::create)
            .expectNext(UploadBuildpackResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-04-21T08:51:39Z")
                    .id("353360ea-59bb-414b-a90e-100c37317a02")
                    .updatedAt("2016-04-21T09:38:16Z")
                    .url("/v2/buildpacks/353360ea-59bb-414b-a90e-100c37317a02")
                    .build())
                .entity(BuildpackEntity.builder()
                    .enabled(true)
                    .filename("binary_buildpack-cached-v1.0.1.zip")
                    .locked(false)
                    .name("binary_buildpack")
                    .position(8)
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
