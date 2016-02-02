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
import org.cloudfoundry.client.v3.Hash;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.droplets.DeleteDropletRequest;
import org.cloudfoundry.client.v3.droplets.GetDropletRequest;
import org.cloudfoundry.client.v3.droplets.GetDropletResponse;
import org.cloudfoundry.client.v3.droplets.ListDropletsRequest;
import org.cloudfoundry.client.v3.droplets.ListDropletsResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;

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
                .id("whatuuid")
                .state("PENDING")
                .hash(Hash.builder()
                    .type("sha1")
                    .build())
                .buildpack("http://github.com/myorg/awesome-buildpack")
                .environmentVariable("CUSTOM_ENV_VAR", "hello")
                .environmentVariable("VCAP_APPLICATION", StringMap.builder()
                    .entry("limits", StringMap.builder()
                        .entry("mem", 1024)
                        .entry("disk", 4096)
                        .entry("fds", 16384)
                        .build())
                    .entry("application_id", "guid-a174c559-deb6-4db7-b3ef-2a5d778d8866")
                    .entry("application_version", "whatuuid")
                    .entry("application_name", "name-454")
                    .entry("application_uris", Collections.emptyList())
                    .entry("version", "whatuuid")
                    .entry("name", "name-454")
                    .entry("space_name", "name-451")
                    .entry("space_id", "a9573106-2d65-45bb-9a93-55bfe029be33")
                    .entry("uris", Collections.emptyList())
                    .entry("users", null)
                    .build())
                .environmentVariable("CF_STACK", "cflinuxfs2")
                .createdAt("2015-07-27T22:43:16Z")
                .link("self", Link.builder()
                    .href("/v3/droplets/whatuuid")
                    .build())
                .link("package", Link.builder()
                    .href("/v3/packages/guid-c89ed121-a2f1-4f78-9d98-e3b607a07d09")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/guid-a174c559-deb6-4db7-b3ef-2a5d778d8866")
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
                        .href("/v3/droplets?page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("/v3/droplets?page=1&per_page=2")
                        .build())
                    .build())
                .resource(Resource.builder()
                    .id("guid-5be1225e-5f49-499a-87db-bcdff646eed6")
                    .state("STAGING")
                    .hash(Hash.builder()
                        .type("sha1")
                        .build())
                    .buildpack("name-2141")
                    .environmentVariable("yuu", "huuu")
                    .createdAt("2015-07-27T22:43:30Z")
                    .link("self", Link.builder()
                        .href("/v3/droplets/guid-5be1225e-5f49-499a-87db-bcdff646eed6")
                        .build())
                    .link("package", Link.builder()
                        .href("/v3/packages/guid-09037508-293d-4923-9552-12fe9cda5f98")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/guid-d686e53a-9a5b-4bad-b1f5-0fe264b2b0c0")
                        .build())
                    .link("buildpack", Link.builder()
                        .href("/v2/buildpacks/b0179650-8a4f-4b3a-b485-255118b0c619")
                        .build())
                    .build())
                .resource(Resource.builder()
                    .id("guid-74a54cf4-99a5-40b1-8f81-74377c36240d")
                    .state("STAGED")
                    .hash(Hash.builder()
                        .type("sha1")
                        .value("my-hash")
                        .build())
                    .buildpack("https://github.com/cloudfoundry/my-buildpack.git")
                    .createdAt("2015-07-27T22:43:30Z")
                    .link("self", Link.builder()
                        .href("/v3/droplets/guid-74a54cf4-99a5-40b1-8f81-74377c36240d")
                        .build())
                    .link("package", Link.builder()
                        .href("/v3/packages/guid-09037508-293d-4923-9552-12fe9cda5f98")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/guid-d686e53a-9a5b-4bad-b1f5-0fe264b2b0c0")
                        .build())
                    .link("assign_current_droplet", Link.builder()
                        .href("/v3/apps/guid-d686e53a-9a5b-4bad-b1f5-0fe264b2b0c0/current_droplet")
                        .method("PUT")
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
