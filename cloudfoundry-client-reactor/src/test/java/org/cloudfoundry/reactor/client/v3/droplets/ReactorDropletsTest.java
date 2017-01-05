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

package org.cloudfoundry.reactor.client.v3.droplets;

import org.cloudfoundry.client.v3.BuildpackData;
import org.cloudfoundry.client.v3.DockerData;
import org.cloudfoundry.client.v3.Hash;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Type;
import org.cloudfoundry.client.v3.droplets.DeleteDropletRequest;
import org.cloudfoundry.client.v3.droplets.DropletResource;
import org.cloudfoundry.client.v3.droplets.GetDropletRequest;
import org.cloudfoundry.client.v3.droplets.GetDropletResponse;
import org.cloudfoundry.client.v3.droplets.ListDropletsRequest;
import org.cloudfoundry.client.v3.droplets.ListDropletsResponse;
import org.cloudfoundry.client.v3.droplets.StagedResult;
import org.cloudfoundry.client.v3.droplets.State;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.cloudfoundry.util.FluentMap;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorDropletsTest extends AbstractClientApiTest {

    private final ReactorDroplets droplets = new ReactorDroplets(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/v3/droplets/test-droplet-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.droplets
            .delete(DeleteDropletRequest.builder()
                .dropletId("test-droplet-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v3/droplets/test-droplet-id")
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
                .state(State.STAGED)
                .lifecycle(Lifecycle.builder()
                    .type(Type.BUILDPACK)
                    .data(BuildpackData.builder()
                        .build())
                    .build())
                .stagingMemoryInMb(1_024)
                .stagingDiskInMb(4_096)
                .result(StagedResult.builder()
                    .executionMetadata("")
                    .processType("rake", "bundle exec rake")
                    .processType("web", "bundle exec rackup config.ru -p $PORT")
                    .hash(Hash.builder()
                        .type("sha1")
                        .value("0cd90dd63dd9f42db25340445d203fba25c69cf6")
                        .build())
                    .buildpack("ruby 1.6.14")
                    .stack("cflinuxfs2")
                    .build())
                .environmentVariable("CF_STACK", "cflinuxfs2")
                .environmentVariable("VCAP_APPLICATION", FluentMap.builder()
                    .entry("limits", FluentMap.builder()
                        .entry("mem", 1_024)
                        .entry("disk", 4_096)
                        .entry("fds", 16_384)
                        .build())
                    .entry("application_id", "7b34f1cf-7e73-428a-bb5a-8a17a8058396")
                    .entry("application_version", "d5985d64-c455-44d0-99a5-285b6521f84c")
                    .entry("application_name", "my_app")
                    .entry("application_uris", Collections.singletonList("my_app.example.com"))
                    .entry("version", "d5985d64-c455-44d0-99a5-285b6521f84c")
                    .entry("name", "my_app")
                    .entry("space_name", "my_space")
                    .entry("space_id", "2f35885d-0c9d-4423-83ad-fd05066f8576")
                    .entry("uris", Collections.singletonList("my_app.example.com"))
                    .entry("users", null)
                    .build())
                .environmentVariable("MEMORY_LIMIT", "1024m")
                .environmentVariable("VCAP_SERVICES", Collections.emptyMap())
                .createdAt("2016-03-28T23:39:34Z")
                .updatedAt("2016-03-28T23:39:47Z")
                .link("self", Link.builder()
                    .href("/v3/droplets/585bc3c1-3743-497d-88b0-403ad6b56d16")
                    .build())
                .link("package", Link.builder()
                    .href("/v3/packages/8222f76a-9e09-4360-b3aa-1ed329945e92")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396")
                    .build())
                .link("assign_current_droplet", Link.builder()
                    .href("/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396/droplets/current")
                    .method("PUT")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v3/droplets")
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
                        .href("/v3/droplets?page=1&per_page=50")
                        .build())
                    .last(Link.builder()
                        .href("/v3/droplets?page=1&per_page=50")
                        .build())
                    .build())
                .resource(DropletResource.builder()
                    .id("585bc3c1-3743-497d-88b0-403ad6b56d16")
                    .state(State.STAGED)
                    .lifecycle(Lifecycle.builder()
                        .type(Type.BUILDPACK)
                        .data(BuildpackData.builder()
                            .build())
                        .build())
                    .stagingMemoryInMb(1024)
                    .stagingDiskInMb(4096)
                    .result(StagedResult.builder()
                        .executionMetadata("[PRIVATE DATA HIDDEN IN LISTS]")
                        .processType("redacted_message", "[PRIVATE DATA HIDDEN IN LISTS]")
                        .hash(Hash.builder()
                            .type("sha1")
                            .value("0cd90dd63dd9f42db25340445d203fba25c69cf6")
                            .build())
                        .buildpack("ruby 1.6.14")
                        .stack("cflinuxfs2")
                        .build())
                    .environmentVariable("redacted_message", "[PRIVATE DATA HIDDEN IN LISTS]")
                    .createdAt("2016-03-28T23:39:34Z")
                    .updatedAt("2016-03-28T23:39:47Z")
                    .link("self", Link.builder()
                        .href("/v3/droplets/585bc3c1-3743-497d-88b0-403ad6b56d16")
                        .build())
                    .link("package", Link.builder()
                        .href("/v3/packages/8222f76a-9e09-4360-b3aa-1ed329945e92")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396")
                        .build())
                    .link("assign_current_droplet", Link.builder()
                        .href("/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396/droplets/current")
                        .method("PUT")
                        .build())
                    .build())
                .resource(DropletResource.builder()
                    .id("fdf3851c-def8-4de1-87f1-6d4543189e22")
                    .state(State.STAGING)
                    .lifecycle(Lifecycle.builder()
                        .type(Type.DOCKER)
                        .data(DockerData.builder()
                            .build())
                        .build())
                    .stagingMemoryInMb(1024)
                    .stagingDiskInMb(4096)
                    .environmentVariable("redacted_message", "[PRIVATE DATA HIDDEN IN LISTS]")
                    .createdAt("2016-03-17T00:00:01Z")
                    .updatedAt("2016-03-17T21:41:32Z")
                    .link("self", Link.builder()
                        .href("/v3/droplets/fdf3851c-def8-4de1-87f1-6d4543189e22")
                        .build())
                    .link("package", Link.builder()
                        .href("/v3/packages/c5725684-a02f-4e59-bc67-8f36ae944688")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396")
                        .build())
                    .link("assign_current_droplet", Link.builder()
                        .href("/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396/droplets/current")
                        .method("PUT")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
