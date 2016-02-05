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

package org.cloudfoundry.client.spring.v3.droplets;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.spring.util.StringMap;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.droplets.DeleteDropletRequest;
import org.cloudfoundry.client.v3.droplets.GetDropletRequest;
import org.cloudfoundry.client.v3.droplets.GetDropletResponse;
import org.cloudfoundry.client.v3.droplets.ListDropletsRequest;
import org.cloudfoundry.client.v3.droplets.ListDropletsResponse;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.client.v3.PaginatedResponse.Pagination;
import static org.cloudfoundry.client.v3.droplets.ListDropletsResponse.Resource;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringDropletsTest {

    public static final class Delete extends AbstractApiTest<DeleteDropletRequest, Void> {

        private final SpringDroplets droplets = new SpringDroplets(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteDropletRequest getInvalidRequest() {
            return DeleteDropletRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v3/droplets/test-droplet-id")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected DeleteDropletRequest getValidRequest() throws Exception {
            return DeleteDropletRequest.builder()
                .dropletId("test-droplet-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(DeleteDropletRequest request) {
            return this.droplets.delete(request);
        }

    }

    public static final class Get extends AbstractApiTest<GetDropletRequest, GetDropletResponse> {

        private final SpringDroplets droplets = new SpringDroplets(this.restTemplate, this.root, PROCESSOR_GROUP);


        @Override
        protected GetDropletRequest getInvalidRequest() {
            return GetDropletRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v3/droplets/test-droplet-id")
                .status(OK)
                .responsePayload("v3/droplets/GET_{id}_response.json");
        }

        @Override
        protected GetDropletResponse getResponse() {
            return GetDropletResponse.builder()
                .id("guid-92bd22f3-7e80-4be8-b603-ce7e97ab68b2")
                .state("STAGED")
                .error("example error")
                .lifecycle(Lifecycle.builder()
                    .type("buildpack")
                    .data("buildpack", "name-2466")
                    .data("stack", "name-2467")
                    .build())
                .result("execution_metadata", null)
                .result("process_types", null)
                .result("hash", StringMap.builder()
                    .entry("type", "sha1")
                    .entry("value", null)
                    .build())
                .result("buildpack", "http://buildpack.git.url.com")
                .result("stack", null)
                .environmentVariable("cloud", "foundry")
                .createdAt("2016-01-26T22:20:36Z")
                .updatedAt("2016-01-26T22:20:36Z")
                .link("self", Link.builder()
                    .href("/v3/droplets/guid-92bd22f3-7e80-4be8-b603-ce7e97ab68b2")
                    .build())
                .link("package", Link.builder()
                    .href("/v3/packages/guid-f5dfcb97-6e61-4b69-8198-0c115498b70b")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/guid-efbab1ea-3e76-45ca-baa3-98bc95f881de")
                    .build())
                .link("assign_current_droplet", Link.builder()
                    .href("/v3/apps/guid-efbab1ea-3e76-45ca-baa3-98bc95f881de/current_droplet")
                    .method("PUT")
                    .build())
                .build();
        }

        @Override
        protected GetDropletRequest getValidRequest() throws Exception {
            return GetDropletRequest.builder()
                .dropletId("test-droplet-id")
                .build();
        }

        @Override
        protected Mono<GetDropletResponse> invoke(GetDropletRequest request) {
            return this.droplets.get(request);
        }

    }

    public static final class List extends AbstractApiTest<ListDropletsRequest, ListDropletsResponse> {

        private final SpringDroplets droplets = new SpringDroplets(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListDropletsRequest getInvalidRequest() {
            return ListDropletsRequest.builder()
                .page(0)
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v3/droplets")
                .status(OK)
                .responsePayload("v3/droplets/GET_response.json");
        }

        @Override
        protected ListDropletsResponse getResponse() {
            return ListDropletsResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(2)
                    .first(Link.builder()
                        .href("/v3/droplets?order_by=-created_at&page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("/v3/droplets?order_by=-created_at&page=1&per_page=2")
                        .build())
                    .build())
                .resource(Resource.builder()
                    .id("guid-b0fd2996-83ad-4409-a596-366408d7148e")
                    .state("STAGED")
                    .lifecycle(Lifecycle.builder()
                        .type("buildpack")
                        .data("buildpack", "name-2488")
                        .data("stack", "name-2489")
                        .build())
                    .memoryLimit(123)
                    .diskLimit(456)
                    .result("execution_metadata", "black-box-secrets")
                    .result("process_types", StringMap.builder()
                        .entry("web", "started")
                        .build())
                    .result("hash", StringMap.builder()
                        .entry("type", "sha1")
                        .entry("value", "my-hash")
                        .build())
                    .result("buildpack", "https://github.com/cloudfoundry/detected-buildpack.git")
                    .result("stack", null)
                    .createdAt("1970-01-01T00:00:02Z")
                    .updatedAt("2016-01-26T22:20:36Z")
                    .link("self", Link.builder()
                        .href("/v3/droplets/guid-b0fd2996-83ad-4409-a596-366408d7148e")
                        .build())
                    .link("package", Link.builder()
                        .href("/v3/packages/guid-4397eaca-603d-421d-bdf5-a32e9eca5acd")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/guid-de30fc31-f424-46ff-b1ff-204ea05b6839")
                        .build())
                    .link("assign_current_droplet", Link.builder()
                        .href("/v3/apps/guid-de30fc31-f424-46ff-b1ff-204ea05b6839/current_droplet")
                        .method("PUT")
                        .build())
                    .build())
                .resource(Resource.builder()
                    .id("guid-17d1deca-7c7a-4ab6-992a-84d9832fd94b")
                    .state("STAGING")
                    .lifecycle(Lifecycle.builder()
                        .type("buildpack")
                        .data("buildpack", "name-2490")
                        .data("stack", "name-2491")
                        .build())
                    .environmentVariable("yuu", "huuu")
                    .createdAt("1970-01-01T00:00:01Z")
                    .updatedAt("2016-01-26T22:20:36Z")
                    .link("self", Link.builder()
                        .href("/v3/droplets/guid-17d1deca-7c7a-4ab6-992a-84d9832fd94b")
                        .build())
                    .link("package", Link.builder()
                        .href("/v3/packages/guid-4397eaca-603d-421d-bdf5-a32e9eca5acd")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/guid-de30fc31-f424-46ff-b1ff-204ea05b6839")
                        .build())
                    .link("assign_current_droplet", Link.builder()
                        .href("/v3/apps/guid-de30fc31-f424-46ff-b1ff-204ea05b6839/current_droplet")
                        .method("PUT")
                        .build())
                    .link("buildpack", Link.builder()
                        .href("/v2/buildpacks/19a8ac85-3e12-40bc-b4eb-6daffc67061b")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListDropletsRequest getValidRequest() throws Exception {
            return ListDropletsRequest.builder()
                .build();
        }

        @Override
        protected Mono<ListDropletsResponse> invoke(ListDropletsRequest request) {
            return this.droplets.list(request);
        }

    }

}
