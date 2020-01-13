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

package org.cloudfoundry.reactor.client.v3.builds;

import org.cloudfoundry.client.v3.BuildpackData;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.LifecycleType;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.builds.BuildResource;
import org.cloudfoundry.client.v3.builds.BuildState;
import org.cloudfoundry.client.v3.builds.CreateBuildRequest;
import org.cloudfoundry.client.v3.builds.CreateBuildResponse;
import org.cloudfoundry.client.v3.builds.CreatedBy;
import org.cloudfoundry.client.v3.builds.Droplet;
import org.cloudfoundry.client.v3.builds.GetBuildRequest;
import org.cloudfoundry.client.v3.builds.GetBuildResponse;
import org.cloudfoundry.client.v3.builds.ListBuildsRequest;
import org.cloudfoundry.client.v3.builds.ListBuildsResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorBuildsTest extends AbstractClientApiTest {

    private final ReactorBuilds builds = new ReactorBuilds(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/builds")
                .payload("fixtures/client/v3/builds/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v3/builds/POST_response.json")
                .build())
            .build());

        this.builds
            .create(CreateBuildRequest.builder()
                .getPackage(Relationship.builder()
                    .id("[package-guid]")
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateBuildResponse.builder()
                .id("585bc3c1-3743-497d-88b0-403ad6b56d16")
                .createdAt("2016-03-28T23:39:34Z")
                .updatedAt("2016-06-08T16:41:26Z")
                .createdBy(CreatedBy.builder()
                    .id("3cb4e243-bed4-49d5-8739-f8b45abdec1c")
                    .name("bill")
                    .email("bill@example.com")
                    .build())
                .state(BuildState.STAGING)
                .error(null)
                .lifecycle(Lifecycle.builder()
                    .type(LifecycleType.BUILDPACK)
                    .data(BuildpackData.builder()
                        .buildpack("ruby_buildpack")
                        .stack("cflinuxfs2")
                        .build())
                    .build())
                .inputPackage(Relationship.builder()
                    .id("8e4da443-f255-499c-8b47-b3729b5b7432")
                    .build())
                .droplet(null)
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/builds/585bc3c1-3743-497d-88b0-403ad6b56d16")
                    .build())
                .link("app", Link.builder()
                    .href("https://api.example.org/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/builds/test-build-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/builds/GET_{id}_response.json")
                .build())
            .build());

        this.builds
            .get(GetBuildRequest.builder()
                .buildId("test-build-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetBuildResponse.builder()
                .id("585bc3c1-3743-497d-88b0-403ad6b56d16")
                .createdAt("2016-03-28T23:39:34Z")
                .updatedAt("2016-03-28T23:39:47Z")
                .createdBy(CreatedBy.builder()
                    .id("3cb4e243-bed4-49d5-8739-f8b45abdec1c")
                    .name("bill")
                    .email("bill@example.com")
                    .build())
                .state(BuildState.STAGED)
                .error(null)
                .lifecycle(Lifecycle.builder()
                    .type(LifecycleType.BUILDPACK)
                    .data(BuildpackData.builder()
                        .buildpack("ruby_buildpack")
                        .stack("cflinuxfs2")
                        .build())
                    .build())
                .inputPackage(Relationship.builder()
                    .id("8e4da443-f255-499c-8b47-b3729b5b7432")
                    .build())
                .droplet(Droplet.builder()
                    .id("1e1186e7-d803-4c46-b9d6-5c81e50fe55a")
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/builds/585bc3c1-3743-497d-88b0-403ad6b56d16")
                    .build())
                .link("app", Link.builder()
                    .href("https://api.example.org/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/builds")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/builds/GET_response.json")
                .build())
            .build());

        this.builds
            .list(ListBuildsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListBuildsResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(1)
                    .totalPages(1)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/builds?states=STAGING&page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/builds?states=STAGING&page=1&per_page=2")
                        .build())
                    .build())
                .resource(BuildResource.builder()
                    .id("585bc3c1-3743-497d-88b0-403ad6b56d16")
                    .createdAt("2016-03-28T23:39:34Z")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .createdBy(CreatedBy.builder()
                        .id("3cb4e243-bed4-49d5-8739-f8b45abdec1c")
                        .name("bill")
                        .email("bill@example.com")
                        .build())
                    .state(BuildState.STAGING)
                    .error(null)
                    .lifecycle(Lifecycle.builder()
                        .type(LifecycleType.BUILDPACK)
                        .data(BuildpackData.builder()
                            .buildpack("ruby_buildpack")
                            .stack("cflinuxfs2")
                            .build())
                        .build())
                    .inputPackage(Relationship.builder()
                        .id("8e4da443-f255-499c-8b47-b3729b5b7432")
                        .build())
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/builds/585bc3c1-3743-497d-88b0-403ad6b56d16")
                        .build())
                    .link("app", Link.builder()
                        .href("https://api.example.org/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
