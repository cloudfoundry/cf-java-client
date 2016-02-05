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

package org.cloudfoundry.client.spring.v3.packages;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.spring.util.StringMap;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.PaginatedResponse.Pagination;
import org.cloudfoundry.client.v3.packages.CopyPackageRequest;
import org.cloudfoundry.client.v3.packages.CopyPackageResponse;
import org.cloudfoundry.client.v3.packages.CreatePackageRequest;
import org.cloudfoundry.client.v3.packages.CreatePackageResponse;
import org.cloudfoundry.client.v3.packages.DeletePackageRequest;
import org.cloudfoundry.client.v3.packages.DownloadPackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageResponse;
import org.cloudfoundry.client.v3.packages.ListPackagesRequest;
import org.cloudfoundry.client.v3.packages.ListPackagesResponse;
import org.cloudfoundry.client.v3.packages.StagePackageRequest;
import org.cloudfoundry.client.v3.packages.StagePackageResponse;
import org.cloudfoundry.client.v3.packages.UploadPackageRequest;
import org.cloudfoundry.client.v3.packages.UploadPackageResponse;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.cloudfoundry.client.v3.packages.CreatePackageRequest.PackageType.DOCKER;
import static org.cloudfoundry.client.v3.packages.ListPackagesResponse.Resource;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;

public final class SpringPackagesTest {

    public static final class Copy extends AbstractApiTest<CopyPackageRequest, CopyPackageResponse> {

        private final SpringPackages packages = new SpringPackages(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CopyPackageRequest getInvalidRequest() {
            return CopyPackageRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(POST).path("/v3/apps/test-application-id/packages?source_package_guid=test-source-package-id")
                .status(OK)
                .responsePayload("v3/apps/POST_{id}_packages_copy_response.json");
        }

        @Override
        protected CopyPackageResponse getResponse() {
            return CopyPackageResponse.builder()
                .id("041af871-9d09-45de-ad2d-df8c4771a1ee")
                .type("docker")
                .data("image", "http://awesome-sauce.com")
                .state("READY")
                .createdAt("2016-01-26T22:20:12Z")
                .link("self", Link.builder()
                    .href("/v3/packages/041af871-9d09-45de-ad2d-df8c4771a1ee")
                    .build())
                .link("stage", Link.builder()
                    .href("/v3/packages/041af871-9d09-45de-ad2d-df8c4771a1ee/droplets")
                    .method("POST")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/guid-459a9d65-c9d0-40ad-ae6d-4cd2bd042b4e")
                    .build())
                .build();
        }

        @Override
        protected CopyPackageRequest getValidRequest() {
            return CopyPackageRequest.builder()
                .applicationId("test-application-id")
                .sourcePackageId("test-source-package-id")
                .build();
        }

        @Override
        protected Mono<CopyPackageResponse> invoke(CopyPackageRequest request) {
            return this.packages.copy(request);
        }

    }

    public static final class Create extends AbstractApiTest<CreatePackageRequest, CreatePackageResponse> {

        private final SpringPackages packages = new SpringPackages(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreatePackageRequest getInvalidRequest() {
            return CreatePackageRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(POST).path("/v3/apps/test-application-id/packages")
                .requestPayload("v3/apps/POST_{id}_packages_request.json")
                .status(CREATED)
                .responsePayload("v3/apps/POST_{id}_packages_response.json");
        }

        @Override
        protected CreatePackageResponse getResponse() {
            return CreatePackageResponse.builder()
                .id("909affe0-4aa1-42f4-b399-1a67cb5a90fa")
                .type("docker")
                .data("image", "registry/image:latest")
                .state("READY")
                .createdAt("2016-01-26T22:20:12Z")
                .link("self", Link.builder()
                    .href("/v3/packages/909affe0-4aa1-42f4-b399-1a67cb5a90fa")
                    .build())
                .link("stage", Link.builder()
                    .href("/v3/packages/909affe0-4aa1-42f4-b399-1a67cb5a90fa/droplets")
                    .method("POST")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/guid-1c19b0bf-dded-45f3-8f98-85f3746f97cf")
                    .build())
                .build();
        }

        @Override
        protected CreatePackageRequest getValidRequest() {
            return CreatePackageRequest.builder()
                .applicationId("test-application-id")
                .type(DOCKER)
                .data("image", "registry/image:latest")
                .build();
        }

        @Override
        protected Mono<CreatePackageResponse> invoke(CreatePackageRequest request) {
            return this.packages.create(request);
        }

    }

    public static final class Delete extends AbstractApiTest<DeletePackageRequest, Void> {

        private final SpringPackages packages = new SpringPackages(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeletePackageRequest getInvalidRequest() {
            return DeletePackageRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v3/packages/test-package-id")
                .status(OK);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected DeletePackageRequest getValidRequest() {
            return DeletePackageRequest.builder()
                .packageId("test-package-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(DeletePackageRequest request) {
            return this.packages.delete(request);
        }

    }

    public static final class Download extends AbstractApiTest<DownloadPackageRequest, byte[]> {

        private final SpringPackages packages = new SpringPackages(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected void assertions(TestSubscriber<byte[]> testSubscriber, final byte[] expected) {
            testSubscriber
                .assertThat(arrayEqualsExpectation(expected));
        }

        @Override
        protected DownloadPackageRequest getInvalidRequest() {
            return DownloadPackageRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("v3/packages/test-package-id/download")
                .status(OK)
                .responsePayload("v3/packages/GET_{id}_download_response.bin");
        }

        @Override
        protected byte[] getResponse() {
            return getContents(new ClassPathResource("v3/packages/GET_{id}_download_response.bin"));
        }

        @Override
        protected DownloadPackageRequest getValidRequest() {
            return DownloadPackageRequest.builder()
                .packageId("test-package-id")
                .build();
        }

        @Override
        protected Mono<byte[]> invoke(DownloadPackageRequest request) {
            return getContents(this.packages.download(request));
        }

    }

    public static final class Get extends AbstractApiTest<GetPackageRequest, GetPackageResponse> {

        private final SpringPackages packages = new SpringPackages(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetPackageRequest getInvalidRequest() {
            return GetPackageRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v3/packages/test-package-id")
                .status(OK)
                .responsePayload("v3/packages/GET_{id}_response.json");
        }

        @Override
        protected GetPackageResponse getResponse() {
            return GetPackageResponse.builder()
                .id("guid-ebaae129-a8ee-43cf-a0a6-734c7ed0d1b4")
                .type("bits")
                .data("error", null)
                .data("hash", StringMap.builder()
                    .entry("type", "sha1")
                    .entry("value", null)
                    .build())
                .state("AWAITING_UPLOAD")
                .createdAt("2016-01-26T22:20:12Z")
                .link("self", Link.builder()
                    .href("/v3/packages/guid-ebaae129-a8ee-43cf-a0a6-734c7ed0d1b4")
                    .build())
                .link("upload", Link.builder()
                    .href("/v3/packages/guid-ebaae129-a8ee-43cf-a0a6-734c7ed0d1b4/upload")
                    .method("POST")
                    .build())
                .link("download", Link.builder()
                    .href("/v3/packages/guid-ebaae129-a8ee-43cf-a0a6-734c7ed0d1b4/download")
                    .method("GET")
                    .build())
                .link("stage", Link.builder()
                    .href("/v3/packages/guid-ebaae129-a8ee-43cf-a0a6-734c7ed0d1b4/droplets")
                    .method("POST")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/guid-6ca8ed35-67b6-4139-afe3-aeda3b26d647")
                    .build())
                .build();
        }

        @Override
        protected GetPackageRequest getValidRequest() {
            return GetPackageRequest.builder()
                .packageId("test-package-id")
                .build();
        }

        @Override
        protected Mono<GetPackageResponse> invoke(GetPackageRequest request) {
            return this.packages.get(request);
        }

    }

    public static final class List extends AbstractApiTest<ListPackagesRequest, ListPackagesResponse> {

        private final SpringPackages packages = new SpringPackages(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListPackagesRequest getInvalidRequest() {
            return ListPackagesRequest.builder()
                .page(-1)
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v3/packages")
                .status(OK)
                .responsePayload("v3/packages/GET_response.json");
        }

        @Override
        protected ListPackagesResponse getResponse() {
            return ListPackagesResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(3)
                    .first(Link.builder()
                        .href("/v3/packages?page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("/v3/packages?page=2&per_page=2")
                        .build())
                    .next(Link.builder()
                        .href("/v3/packages?page=2&per_page=2")
                        .build())
                    .build())
                .resource(Resource.builder()
                    .id("guid-2731172f-0714-430e-81e7-d662509d555b")
                    .type("bits")
                    .data("error", null)
                    .data("hash", StringMap.builder()
                        .entry("type", "sha1")
                        .entry("value", null)
                        .build())
                    .state("AWAITING_UPLOAD")
                    .createdAt("2016-01-26T22:20:12Z")
                    .link("self", Link.builder()
                        .href("/v3/packages/guid-2731172f-0714-430e-81e7-d662509d555b")
                        .build())
                    .link("upload", Link.builder()
                        .href("/v3/packages/guid-2731172f-0714-430e-81e7-d662509d555b/upload")
                        .method("POST")
                        .build())
                    .link("download", Link.builder()
                        .href("/v3/packages/guid-2731172f-0714-430e-81e7-d662509d555b/download")
                        .method("GET")
                        .build())
                    .link("stage", Link.builder()
                        .href("/v3/packages/guid-2731172f-0714-430e-81e7-d662509d555b/droplets")
                        .method("POST")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/guid-f4384453-4610-4075-b2c3-c2290401dbb9")
                        .build())
                    .build())
                .resource(Resource.builder()
                    .id("guid-10217847-a68c-4c08-89d6-b247d8afe647")
                    .type("docker")
                    .data("image", "http://location-of-image.com")
                    .state("READY")
                    .createdAt("2016-01-26T22:20:12Z")
                    .link("self", Link.builder()
                        .href("/v3/packages/guid-10217847-a68c-4c08-89d6-b247d8afe647")
                        .build())
                    .link("stage", Link.builder()
                        .href("/v3/packages/guid-10217847-a68c-4c08-89d6-b247d8afe647/droplets")
                        .method("POST")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/guid-f4384453-4610-4075-b2c3-c2290401dbb9")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListPackagesRequest getValidRequest() {
            return ListPackagesRequest.builder()
                .build();
        }

        @Override
        protected Mono<ListPackagesResponse> invoke(ListPackagesRequest request) {
            return this.packages.list(request);
        }

    }

    public static final class Stage extends AbstractApiTest<StagePackageRequest, StagePackageResponse> {

        private final SpringPackages packages = new SpringPackages(this.restTemplate, this.root, PROCESSOR_GROUP);


        @Override
        protected StagePackageRequest getInvalidRequest() {
            return StagePackageRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(POST).path("/v3/packages/test-package-id/droplets")
                .requestPayload("v3/packages/POST_{id}_droplets_request.json")
                .status(CREATED)
                .responsePayload("v3/packages/POST_{id}_droplets_response.json");
        }

        @Override
        protected StagePackageResponse getResponse() {
            return StagePackageResponse.builder()
                .id("whatuuid")
                .state("PENDING")
                .lifecycle(Lifecycle.builder()
                    .type("buildpack")
                    .data("buildpack", "http://github.com/myorg/awesome-buildpack")
                    .data("stack", "cflinuxfs2")
                    .build())
                .memoryLimit(1_024)
                .diskLimit(4_096)
                .environmentVariable("CUSTOM_ENV_VAR", "hello")
                .environmentVariable("CF_STACK", "cflinuxfs2")
                .environmentVariable("VCAP_APPLICATION", StringMap.builder()
                    .entry("limits", StringMap.builder()
                        .entry("mem", 1_024)
                        .entry("disk", 4_096)
                        .entry("fds", 16_384)
                        .build())
                    .entry("application_id", "guid-81b0a3b1-19e5-4e93-b9c4-1730cdd99695")
                    .entry("application_version", "whatuuid")
                    .entry("application_name", "name-487")
                    .entry("application_uris", Collections.emptyList())
                    .entry("version", "whatuuid")
                    .entry("name", "name-487")
                    .entry("space_name", "name-484")
                    .entry("space_id", "78a77c68-55cc-45e2-ac82-01df0290fca9")
                    .entry("uris", Collections.emptyList())
                    .entry("users", null)
                    .build())
                .environmentVariable("MEMORY_LIMIT", 1_024)
                .environmentVariable("VCAP_SERVICES", Collections.emptyMap())
                .createdAt("2016-01-26T22:20:12Z")
                .link("self", Link.builder()
                    .href("/v3/droplets/whatuuid")
                    .build())
                .link("package", Link.builder()
                    .href("/v3/packages/guid-c613ad85-308b-4ba6-9097-8b21f60eef95")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/guid-81b0a3b1-19e5-4e93-b9c4-1730cdd99695")
                    .build())
                .link("assign_current_droplet", Link.builder()
                    .href("/v3/apps/guid-81b0a3b1-19e5-4e93-b9c4-1730cdd99695/current_droplet")
                    .method("PUT")
                    .build())
                .build();
        }

        @Override
        protected StagePackageRequest getValidRequest() {
            return StagePackageRequest.builder()
                .packageId("test-package-id")
                .environmentVariable("CUSTOM_ENV_VAR", "hello")
                .lifecycle(Lifecycle.builder()
                    .type("buildpack")
                    .data("buildpack", "http://github.com/myorg/awesome-buildpack")
                    .data("stack", "cflinuxfs2")
                    .build())
                .build();
        }

        @Override
        protected Mono<StagePackageResponse> invoke(StagePackageRequest request) {
            return this.packages.stage(request);
        }
    }

    public static final class Upload extends AbstractApiTest<UploadPackageRequest, UploadPackageResponse> {

        private final SpringPackages packages = new SpringPackages(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected UploadPackageRequest getInvalidRequest() {
            return UploadPackageRequest.builder()
                .build();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(POST).path("/v3/packages/test-package-id/upload")
                .requestMatcher(header("Content-Type", startsWith(MULTIPART_FORM_DATA_VALUE)))
                .anyRequestPayload()
                .status(CREATED)
                .responsePayload("v3/packages/POST_{id}_upload_response.json");
        }

        @Override
        protected UploadPackageResponse getResponse() {
            return UploadPackageResponse.builder()
                .id("guid-f582d3d1-320c-4524-9c4f-480252ab5bff")
                .type("bits")
                .data("error", null)
                .data("hash", StringMap.builder()
                    .entry("type", "sha1")
                    .entry("value", null)
                    .build())
                .state("PROCESSING_UPLOAD")
                .createdAt("2016-01-26T22:20:12Z")
                .updatedAt("2016-01-26T22:20:12Z")
                .link("self", Link.builder()
                    .href("/v3/packages/guid-f582d3d1-320c-4524-9c4f-480252ab5bff")
                    .build())
                .link("upload", Link.builder()
                    .href("/v3/packages/guid-f582d3d1-320c-4524-9c4f-480252ab5bff/upload")
                    .method("POST")
                    .build())
                .link("download", Link.builder()
                    .href("/v3/packages/guid-f582d3d1-320c-4524-9c4f-480252ab5bff/download")
                    .method("GET")
                    .build())
                .link("stage", Link.builder()
                    .href("/v3/packages/guid-f582d3d1-320c-4524-9c4f-480252ab5bff/droplets")
                    .method("POST")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/guid-a1546101-9467-4525-a3eb-d47fc9485bb1")
                    .build())
                .build();
        }

        @Override
        protected UploadPackageRequest getValidRequest() throws Exception {
            return UploadPackageRequest.builder()
                .file(new ClassPathResource("v3/packages/test-file").getFile())
                .packageId("test-package-id")
                .build();
        }

        @Override
        protected Mono<UploadPackageResponse> invoke(UploadPackageRequest request) {
            return this.packages.upload(request);
        }

    }

}
