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

package org.cloudfoundry.reactor.client.v3.droplets;

import org.cloudfoundry.client.v3.BuildpackData;
import org.cloudfoundry.client.v3.Checksum;
import org.cloudfoundry.client.v3.ChecksumType;
import org.cloudfoundry.client.v3.DockerData;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.LifecycleType;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.droplets.Buildpack;
import org.cloudfoundry.client.v3.droplets.CopyDropletRequest;
import org.cloudfoundry.client.v3.droplets.CopyDropletResponse;
import org.cloudfoundry.client.v3.droplets.DeleteDropletRequest;
import org.cloudfoundry.client.v3.droplets.DropletRelationships;
import org.cloudfoundry.client.v3.droplets.DropletResource;
import org.cloudfoundry.client.v3.droplets.DropletState;
import org.cloudfoundry.client.v3.droplets.GetDropletRequest;
import org.cloudfoundry.client.v3.droplets.GetDropletResponse;
import org.cloudfoundry.client.v3.droplets.ListDropletsRequest;
import org.cloudfoundry.client.v3.droplets.ListDropletsResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorDropletsTest extends AbstractClientApiTest {

    private final ReactorDroplets droplets = new ReactorDroplets(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void copy() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/droplets?source_guid=test-source-droplet-id")
                .payload("fixtures/client/v3/droplets/POST_source_guid={id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v3/droplets/POST_source_guid={id}_response.json")
                .build())
            .build());

        this.droplets
            .copy(CopyDropletRequest.builder()
                .sourceDropletId("test-source-droplet-id")
                .relationships(DropletRelationships.builder()
                    .application(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("[app-guid]")
                            .build())
                        .build())
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(CopyDropletResponse.builder()
                .id("585bc3c1-3743-497d-88b0-403ad6b56d16")
                .state(DropletState.COPYING)
                .error(null)
                .lifecycle(Lifecycle.builder()
                    .type(LifecycleType.BUILDPACK)
                    .data(BuildpackData.builder()
                        .build())
                    .build())
                .executionMetadata("")
                .processTypes(null)
                .checksum(null)
                .stack(null)
                .image(null)
                .createdAt("2016-03-28T23:39:34Z")
                .updatedAt("2016-06-08T16:41:26Z")
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
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/droplets/test-droplet-id")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .header("Location", "https://api.example.org/v3/jobs/[guid]")
                .build())
            .build());

        this.droplets
            .delete(DeleteDropletRequest.builder()
                .dropletId("test-droplet-id")
                .build())
            .as(StepVerifier::create)
            .expectNext("[guid]")
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/droplets/test-droplet-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/droplets/GET_{id}_response.json")
                .build())
            .build());

        this.droplets
            .get(GetDropletRequest.builder()
                .dropletId("test-droplet-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetDropletResponse.builder()
                .id("585bc3c1-3743-497d-88b0-403ad6b56d16")
                .state(DropletState.STAGED)
                .error(null)
                .lifecycle(Lifecycle.builder()
                    .type(LifecycleType.BUILDPACK)
                    .data(BuildpackData.builder()
                        .build())
                    .build())
                .executionMetadata("")
                .processType("rake", "bundle exec rake")
                .processType("web", "bundle exec rackup config.ru -p $PORT")
                .checksum(Checksum.builder()
                    .type(ChecksumType.SHA256)
                    .value("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
                    .build())
                .buildpack(Buildpack.builder()
                    .buildpackName("ruby")
                    .detectOutput("ruby 1.6.14")
                    .name("ruby_buildpack")
                    .version("1.1.1.")
                    .build())
                .stack("cflinuxfs2")
                .image(null)
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
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/droplets")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/droplets/GET_response.json")
                .build())
            .build());

        this.droplets
            .list(ListDropletsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListDropletsResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(2)
                    .totalPages(1)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/droplets?page=1&per_page=50")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/droplets?page=1&per_page=50")
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
                    .image(null)
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
                        .data(DockerData.builder()
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

}
