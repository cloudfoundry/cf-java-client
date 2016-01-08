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

package org.cloudfoundry.client.spring.v2.applications;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.spring.util.StringMap;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationEnvironmentRequest;
import org.cloudfoundry.client.v2.applications.ApplicationEnvironmentResponse;
import org.cloudfoundry.client.v2.applications.ApplicationInstanceInfo;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesResponse;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsRequest;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsResponse;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteResponse;
import org.cloudfoundry.client.v2.applications.CopyApplicationRequest;
import org.cloudfoundry.client.v2.applications.CopyApplicationResponse;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v2.applications.DownloadApplicationRequest;
import org.cloudfoundry.client.v2.applications.DownloadDropletRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationRoutesRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationRoutesResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v2.applications.RemoveApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.RemoveApplicationServiceBindingRequest;
import org.cloudfoundry.client.v2.applications.RestageApplicationEntity;
import org.cloudfoundry.client.v2.applications.RestageApplicationRequest;
import org.cloudfoundry.client.v2.applications.RestageApplicationResponse;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.applications.TerminateApplicationInstanceRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationResponse;
import org.cloudfoundry.client.v2.applications.UploadApplicationRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationResponse;
import org.cloudfoundry.client.v2.domains.Domain;
import org.cloudfoundry.client.v2.job.JobEntity;
import org.cloudfoundry.client.v2.routes.Route;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingEntity;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstance;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.springframework.core.io.ClassPathResource;
import reactor.Mono;

import java.util.Collections;

import static org.cloudfoundry.client.v2.serviceinstances.ServiceInstance.Plan.Service;
import static org.cloudfoundry.client.v2.serviceinstances.ServiceInstance.Plan.builder;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;

public final class SpringApplicationsV2Test {

    public static final class AssociateRoute
            extends AbstractApiTest<AssociateApplicationRouteRequest, AssociateApplicationRouteResponse> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateApplicationRouteRequest getInvalidRequest() {
            return AssociateApplicationRouteRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(PUT).path("v2/apps/test-id/routes/test-route-id")
                    .status(OK)
                    .responsePayload("v2/apps/PUT_{id}_routes_{route-id}_response.json");
        }

        @Override
        protected AssociateApplicationRouteResponse getResponse() {
            return AssociateApplicationRouteResponse.builder()
                    .metadata(Resource.Metadata.builder()
                            .createdAt("2015-07-27T22:43:19Z")
                            .id("638e90b6-502f-47a8-a3bf-b18fdf3fb70a")
                            .url("/v2/apps/638e90b6-502f-47a8-a3bf-b18fdf3fb70a")
                            .updatedAt("2015-07-27T22:43:19Z")
                            .build())
                    .entity(ApplicationEntity.builder()
                            .console(false)
                            .detectedStartCommand("")
                            .diego(false)
                            .diskQuota(1024)
                            .dockerCredentialsJson("redacted_message", "[PRIVATE DATA HIDDEN]")
                            .enableSsh(true)
                            .eventsUrl("/v2/apps/638e90b6-502f-47a8-a3bf-b18fdf3fb70a/events")
                            .healthCheckType("port")
                            .instances(1)
                            .memory(1024)
                            .name("name-657")
                            .packageState("PENDING")
                            .packageUpdatedAt("2015-07-27T22:43:19Z")
                            .production(false)
                            .routesUrl("/v2/apps/638e90b6-502f-47a8-a3bf-b18fdf3fb70a/routes")
                            .serviceBindingsUrl("/v2/apps/638e90b6-502f-47a8-a3bf-b18fdf3fb70a/service_bindings")
                            .spaceId("bc900bc3-df1f-4842-9621-e69b90207ad1")
                            .spaceUrl("/v2/spaces/bc900bc3-df1f-4842-9621-e69b90207ad1")
                            .stackId("46576b60-3d3e-42a6-bdb3-171bc2dedfc4")
                            .stackUrl("/v2/stacks/46576b60-3d3e-42a6-bdb3-171bc2dedfc4")
                            .state("STOPPED")
                            .version("eee05ab9-9d6c-490c-932a-996d061b5fe4")
                            .build())
                    .build();
        }

        @Override
        protected AssociateApplicationRouteRequest getValidRequest() {
            return AssociateApplicationRouteRequest.builder()
                    .id("test-id")
                    .routeId("test-route-id")
                    .build();
        }

        @Override
        protected Mono<AssociateApplicationRouteResponse> invoke(AssociateApplicationRouteRequest request) {
            return this.applications.associateRoute(request);
        }

    }

    public static final class Copy extends AbstractApiTest<CopyApplicationRequest, CopyApplicationResponse> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CopyApplicationRequest getInvalidRequest() {
            return CopyApplicationRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(POST).path("v2/apps/test-id/copy_bits")
                    .requestPayload("v2/apps/POST_{id}_copy_bits_request.json")
                    .status(OK)
                    .responsePayload("v2/apps/POST_{id}_copy_bits_response.json");
        }

        @Override
        protected CopyApplicationResponse getResponse() {
            return CopyApplicationResponse.builder()
                    .entity(JobEntity.builder()
                            .id("c900719e-c70a-4c75-9e6a-9535f118acc3")
                            .status("queued")
                            .build())
                    .metadata(Resource.Metadata.builder()
                            .createdAt("2015-07-27T22:43:34Z")
                            .id("c900719e-c70a-4c75-9e6a-9535f118acc3")
                            .url("/v2/jobs/c900719e-c70a-4c75-9e6a-9535f118acc3")
                            .build())
                    .build();
        }

        @Override
        protected CopyApplicationRequest getValidRequest() {
            return CopyApplicationRequest.builder()
                    .id("test-id")
                    .sourceAppId("af6ab819-3fb7-42e3-a0f6-947022881b7b")
                    .build();
        }

        @Override
        protected Mono<CopyApplicationResponse> invoke(CopyApplicationRequest request) {
            return this.applications.copy(request);
        }
    }

    public static final class Create extends AbstractApiTest<CreateApplicationRequest, CreateApplicationResponse> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreateApplicationRequest getInvalidRequest() {
            return CreateApplicationRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(POST).path("/v2/apps")
                    .requestPayload("v2/apps/POST_request.json")
                    .status(CREATED)
                    .responsePayload("v2/apps/POST_response.json");
        }

        @Override
        protected CreateApplicationResponse getResponse() {
            return CreateApplicationResponse.builder()
                    .metadata(Resource.Metadata.builder()
                            .createdAt("2015-07-27T22:43:20Z")
                            .id("78d1a119-2ded-405f-8675-421d8dade602")
                            .url("/v2/apps/78d1a119-2ded-405f-8675-421d8dade602")
                            .build())
                    .entity(ApplicationEntity.builder()
                            .console(false)
                            .detectedStartCommand("")
                            .diego(true)
                            .diskQuota(1024)
                            .dockerCredentialsJson("redacted_message", "[PRIVATE DATA HIDDEN]")
                            .dockerImage("cloudfoundry/hello:latest")
                            .enableSsh(true)
                            .eventsUrl("/v2/apps/78d1a119-2ded-405f-8675-421d8dade602/events")
                            .healthCheckType("port")
                            .instances(1)
                            .memory(1024)
                            .name("docker_app")
                            .packageState("PENDING")
                            .packageUpdatedAt("2015-07-27T22:43:20Z")
                            .production(false)
                            .routesUrl("/v2/apps/78d1a119-2ded-405f-8675-421d8dade602/routes")
                            .serviceBindingsUrl("/v2/apps/78d1a119-2ded-405f-8675-421d8dade602/service_bindings")
                            .spaceId("6ef4e580-c189-49c8-959e-4a3d021b3307")
                            .spaceUrl("/v2/spaces/6ef4e580-c189-49c8-959e-4a3d021b3307")
                            .stackId("d449ecea-669f-448a-a9e7-ec84d51e2fdb")
                            .stackUrl("/v2/stacks/d449ecea-669f-448a-a9e7-ec84d51e2fdb")
                            .state("STOPPED")
                            .version("69ebcffe-d79b-482a-91b6-39dfc86e7692")
                            .build())
                    .build();
        }

        @Override
        protected CreateApplicationRequest getValidRequest() {
            return CreateApplicationRequest.builder()
                    .diego(true)
                    .dockerImage("cloudfoundry/hello")
                    .name("docker_app")
                    .spaceId("6ef4e580-c189-49c8-959e-4a3d021b3307")
                    .build();
        }

        @Override
        protected Mono<CreateApplicationResponse> invoke(CreateApplicationRequest request) {
            return this.applications.create(request);
        }
    }

    public static final class Delete extends AbstractApiTest<DeleteApplicationRequest, Void> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteApplicationRequest getInvalidRequest() {
            return DeleteApplicationRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(DELETE).path("/v2/apps/test-id")
                    .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected DeleteApplicationRequest getValidRequest() {
            return DeleteApplicationRequest.builder()
                    .id("test-id")
                    .build();
        }

        @Override
        protected Mono<Void> invoke(DeleteApplicationRequest request) {
            return this.applications.delete(request);
        }

    }

    public static final class Download extends AbstractApiTest<DownloadApplicationRequest, byte[]> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected void assertions(TestSubscriber<byte[]> testSubscriber, final byte[] expected) {
            testSubscriber
                    .assertThat(arrayEqualsExpectation(expected));
        }

        @Override
        protected DownloadApplicationRequest getInvalidRequest() {
            return DownloadApplicationRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("v2/apps/test-id/download")
                    .status(OK)
                    .responsePayload("v2/apps/GET_{id}_download_response.bin");
        }

        @Override
        protected byte[] getResponse() {
            return getContents(new ClassPathResource("v2/apps/GET_{id}_download_response.bin"));
        }

        @Override
        protected DownloadApplicationRequest getValidRequest() throws Exception {
            return DownloadApplicationRequest.builder()
                    .id("test-id")
                    .build();
        }

        @Override
        protected Mono<byte[]> invoke(DownloadApplicationRequest request) {
            return getContents(this.applications.download(request));
        }

    }

    public static final class DownloadDroplet extends AbstractApiTest<DownloadDropletRequest, byte[]> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected void assertions(TestSubscriber<byte[]> testSubscriber, final byte[] expected) {
            testSubscriber
                    .assertThat(arrayEqualsExpectation(expected));
        }

        @Override
        protected DownloadDropletRequest getInvalidRequest() {
            return DownloadDropletRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("v2/apps/test-id/droplet/download")
                    .status(OK)
                    .responsePayload("v2/apps/GET_{id}_download_response.bin");
        }

        @Override
        protected byte[] getResponse() {
            return getContents(new ClassPathResource("v2/apps/GET_{id}_download_response.bin"));
        }

        @Override
        protected DownloadDropletRequest getValidRequest() throws Exception {
            return DownloadDropletRequest.builder()
                    .id("test-id")
                    .build();
        }

        @Override
        protected Mono<byte[]> invoke(DownloadDropletRequest request) {
            return getContents(this.applications.downloadDroplet(request));
        }
    }

    public static final class Environment
            extends AbstractApiTest<ApplicationEnvironmentRequest, ApplicationEnvironmentResponse> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ApplicationEnvironmentRequest getInvalidRequest() {
            return ApplicationEnvironmentRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v2/apps/test-id/env")
                    .status(OK)
                    .responsePayload("v2/apps/GET_{id}_env_response.json");
        }

        @Override
        protected ApplicationEnvironmentResponse getResponse() {
            return ApplicationEnvironmentResponse.builder()
                    .stagingEnvironmentJson("STAGING_ENV", "staging_value")
                    .runningEnvironmentJson("RUNNING_ENV", "running_value")
                    .environmentJson("env_var", "env_val")
                    .systemEnvironmentJson("VCAP_SERVICES", Collections.emptyMap())
                    .applicationEnvironmentJson("VCAP_APPLICATION", StringMap.builder()
                            .entry("limits", StringMap.builder()
                                    .entry("mem", 1024)
                                    .entry("disk", 1024)
                                    .entry("fds", 16384)
                                    .build())
                            .entry("application_id", "96e63272-5da0-44b8-90a9-63d12b2692bb")
                            .entry("application_version", "86cef6fd-fb03-4f02-97ff-cc6b9d80bbf4")
                            .entry("application_name", "name-897")
                            .entry("application_uris", Collections.emptyList())
                            .entry("version", "86cef6fd-fb03-4f02-97ff-cc6b9d80bbf4")
                            .entry("name", "name-897")
                            .entry("space_name", "name-898")
                            .entry("space_id", "147eef57-aadb-43b0-9518-b355ab4db678")
                            .entry("uris", Collections.emptyList())
                            .entry("users", null)
                            .build())
                    .build();
        }

        @Override
        protected ApplicationEnvironmentRequest getValidRequest() throws Exception {
            return ApplicationEnvironmentRequest.builder()
                    .id("test-id")
                    .build();
        }

        @Override
        protected Mono<ApplicationEnvironmentResponse> invoke(ApplicationEnvironmentRequest request) {
            return this.applications.environment(request);
        }

    }

    public static final class Get extends AbstractApiTest<GetApplicationRequest, GetApplicationResponse> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetApplicationRequest getInvalidRequest() {
            return GetApplicationRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v2/apps/test-id")
                    .status(OK)
                    .responsePayload("v2/apps/GET_{id}_response.json");
        }

        @Override
        protected GetApplicationResponse getResponse() {
            return GetApplicationResponse.builder()
                    .metadata(Resource.Metadata.builder()
                            .createdAt("2015-07-27T22:43:20Z")
                            .id("03f286bb-f17c-42b4-8dcd-b818b0b798af")
                            .updatedAt("2015-07-27T22:43:20Z")
                            .url("/v2/apps/03f286bb-f17c-42b4-8dcd-b818b0b798af")
                            .build())
                    .entity(ApplicationEntity.builder()
                            .console(false)
                            .detectedStartCommand("")
                            .diego(false)
                            .diskQuota(1024)
                            .dockerCredentialsJson("redacted_message", "[PRIVATE DATA HIDDEN]")
                            .enableSsh(true)
                            .eventsUrl("/v2/apps/03f286bb-f17c-42b4-8dcd-b818b0b798af/events")
                            .healthCheckType("port")
                            .instances(1)
                            .memory(1024)
                            .name("name-751")
                            .packageState("PENDING")
                            .packageUpdatedAt("2015-07-27T22:43:20Z")
                            .production(false)
                            .routesUrl("/v2/apps/03f286bb-f17c-42b4-8dcd-b818b0b798af/routes")
                            .serviceBindingsUrl("/v2/apps/03f286bb-f17c-42b4-8dcd-b818b0b798af/service_bindings")
                            .spaceId("b10ca4ed-fa71-4597-8567-e7dd1719c0c7")
                            .spaceUrl("/v2/spaces/b10ca4ed-fa71-4597-8567-e7dd1719c0c7")
                            .stackId("160fb300-c60e-4682-8527-8500e0318839")
                            .stackUrl("/v2/stacks/160fb300-c60e-4682-8527-8500e0318839")
                            .state("STOPPED")
                            .version("2b0d7e20-ce57-44b4-b0ec-7ca6d1d50e20")
                            .build())
                    .build();
        }

        @Override
        protected GetApplicationRequest getValidRequest() throws Exception {
            return GetApplicationRequest.builder()
                    .id("test-id")
                    .build();
        }

        @Override
        protected Mono<GetApplicationResponse> invoke(GetApplicationRequest request) {
            return this.applications.get(request);
        }

    }

    public static final class Instances
            extends AbstractApiTest<ApplicationInstancesRequest, ApplicationInstancesResponse> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ApplicationInstancesRequest getInvalidRequest() {
            return ApplicationInstancesRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v2/apps/test-id/instances")
                    .status(OK)
                    .responsePayload("v2/apps/GET_{id}_instances_response.json");
        }

        @Override
        protected ApplicationInstancesResponse getResponse() {
            return ApplicationInstancesResponse.builder()
                    .instance("0", ApplicationInstanceInfo.builder()
                            .since(1403140717.984577d)
                            .state("RUNNING")
                            .build())
                    .build();
        }

        @Override
        protected ApplicationInstancesRequest getValidRequest() throws Exception {
            return ApplicationInstancesRequest.builder()
                    .id("test-id")
                    .build();
        }

        @Override
        protected Mono<ApplicationInstancesResponse> invoke(ApplicationInstancesRequest request) {
            return this.applications.instances(request);
        }

    }

    public static final class List extends AbstractApiTest<ListApplicationsRequest, ListApplicationsResponse> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);


        @Override
        protected ListApplicationsRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v2/apps?q=name%20IN%20test-name&page=-1")
                    .status(OK)
                    .responsePayload("v2/apps/GET_apps_response.json");
        }

        @Override
        protected ListApplicationsResponse getResponse() {
            return ListApplicationsResponse.builder()
                    .totalResults(3)
                    .totalPages(1)
                    .resource(ApplicationResource.builder()
                            .metadata(Resource.Metadata.builder()
                                    .id("3d294ed0-105c-4ccd-8f79-5605d6b7198c")
                                    .url("/v2/apps/3d294ed0-105c-4ccd-8f79-5605d6b7198c")
                                    .createdAt("2015-07-27T22:43:20Z")
                                    .updatedAt("2015-07-27T22:43:20Z")
                                    .build())
                            .entity(ApplicationEntity.builder()
                                    .console(false)
                                    .detectedStartCommand("")
                                    .diego(false)
                                    .diskQuota(1024)
                                    .dockerCredentialsJson("redacted_message", "[PRIVATE DATA HIDDEN]")
                                    .enableSsh(true)
                                    .eventsUrl("/v2/apps/3d294ed0-105c-4ccd-8f79-5605d6b7198c/events")
                                    .healthCheckType("port")
                                    .instances(1)
                                    .memory(1024)
                                    .name("name-761")
                                    .packageState("PENDING")
                                    .packageUpdatedAt("2015-07-27T22:43:20Z")
                                    .production(false)
                                    .routesUrl("/v2/apps/3d294ed0-105c-4ccd-8f79-5605d6b7198c/routes")
                                    .serviceBindingsUrl
                                            ("/v2/apps/3d294ed0-105c-4ccd-8f79-5605d6b7198c/service_bindings")
                                    .spaceId("30d5165d-0bef-4103-97cd-72269b9d7a4c")
                                    .spaceUrl("/v2/spaces/30d5165d-0bef-4103-97cd-72269b9d7a4c")
                                    .stackId("d3476df6-534d-4140-b85b-401fa4923234")
                                    .stackUrl("/v2/stacks/d3476df6-534d-4140-b85b-401fa4923234")
                                    .state("STOPPED")
                                    .version("3ca77d11-93e0-4a60-bab5-30f38b8a8649")
                                    .build())
                            .build())
                    .resource(ApplicationResource.builder()
                            .metadata(Resource.Metadata.builder()
                                    .id("522c5382-29e9-48aa-9db0-9f6cfa643ec1")
                                    .url("/v2/apps/522c5382-29e9-48aa-9db0-9f6cfa643ec1")
                                    .createdAt("2015-07-27T22:43:20Z")
                                    .updatedAt("2015-07-27T22:43:20Z")
                                    .build())
                            .entity(ApplicationEntity.builder()
                                    .console(false)
                                    .detectedStartCommand("")
                                    .diego(false)
                                    .diskQuota(1024)
                                    .dockerCredentialsJson("redacted_message", "[PRIVATE DATA HIDDEN]")
                                    .enableSsh(true)
                                    .eventsUrl("/v2/apps/522c5382-29e9-48aa-9db0-9f6cfa643ec1/events")
                                    .healthCheckType("port")
                                    .instances(1)
                                    .memory(1024)
                                    .name("name-766")
                                    .packageState("PENDING")
                                    .packageUpdatedAt("2015-07-27T22:43:20Z")
                                    .production(false)
                                    .routesUrl("/v2/apps/522c5382-29e9-48aa-9db0-9f6cfa643ec1/routes")
                                    .serviceBindingsUrl
                                            ("/v2/apps/522c5382-29e9-48aa-9db0-9f6cfa643ec1/service_bindings")
                                    .spaceId("cf929611-97ab-4c42-93e5-9ec26e26f456")
                                    .spaceUrl("/v2/spaces/cf929611-97ab-4c42-93e5-9ec26e26f456")
                                    .stackId("14b4a0b7-7c7b-4cf2-99f0-cc3ed1473f09")
                                    .stackUrl("/v2/stacks/14b4a0b7-7c7b-4cf2-99f0-cc3ed1473f09")
                                    .state("STOPPED")
                                    .version("cddf0ec1-acf6-48e7-831b-884972cb7ac3")
                                    .build())
                            .build())
                    .resource(ApplicationResource.builder()
                            .metadata(Resource.Metadata.builder()
                                    .id("ec31bfbd-ab5c-490d-8e83-3c1ea5d1bedf")
                                    .url("/v2/apps/ec31bfbd-ab5c-490d-8e83-3c1ea5d1bedf")
                                    .createdAt("2015-07-27T22:43:20Z")
                                    .updatedAt("2015-07-27T22:43:20Z")
                                    .build())
                            .entity(ApplicationEntity.builder()
                                    .console(false)
                                    .detectedStartCommand("")
                                    .diego(false)
                                    .diskQuota(1024)
                                    .dockerCredentialsJson("redacted_message", "[PRIVATE DATA HIDDEN]")
                                    .enableSsh(true)
                                    .eventsUrl("/v2/apps/ec31bfbd-ab5c-490d-8e83-3c1ea5d1bedf/events")
                                    .healthCheckType("port")
                                    .instances(1)
                                    .memory(1024)
                                    .name("name-771")
                                    .packageState("PENDING")
                                    .packageUpdatedAt("2015-07-27T22:43:20Z")
                                    .production(false)
                                    .routesUrl("/v2/apps/ec31bfbd-ab5c-490d-8e83-3c1ea5d1bedf/routes")
                                    .serviceBindingsUrl
                                            ("/v2/apps/ec31bfbd-ab5c-490d-8e83-3c1ea5d1bedf/service_bindings")
                                    .spaceId("e438b2bf-17d6-4265-8813-18e0ab95c029")
                                    .spaceUrl("/v2/spaces/e438b2bf-17d6-4265-8813-18e0ab95c029")
                                    .stackId("8d42ba27-60df-420e-9208-535e753b706a")
                                    .stackUrl("/v2/stacks/8d42ba27-60df-420e-9208-535e753b706a")
                                    .state("STOPPED")
                                    .version("8e74d312-1bc9-4953-b4fe-d2613ea4972a")
                                    .build())
                            .build())
                    .build();
        }

        @Override
        protected ListApplicationsRequest getValidRequest() throws Exception {
            return ListApplicationsRequest.builder()
                    .name("test-name")
                    .page(-1)
                    .build();
        }

        @Override
        protected Mono<ListApplicationsResponse> invoke(ListApplicationsRequest request) {
            return this.applications.list(request);
        }

    }

    public static final class ListRoutes
            extends AbstractApiTest<ListApplicationRoutesRequest, ListApplicationRoutesResponse> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListApplicationRoutesRequest getInvalidRequest() {
            return ListApplicationRoutesRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("v2/apps/test-id/routes?page=-1")
                    .status(OK)
                    .responsePayload("v2/apps/GET_{id}_routes_response.json");
        }

        @Override
        protected ListApplicationRoutesResponse getResponse() {
            return ListApplicationRoutesResponse.builder()
                    .totalResults(1)
                    .totalPages(1)
                    .resource(RouteResource.builder()
                            .metadata(Resource.Metadata.builder()
                                    .createdAt("2015-07-27T22:43:19Z")
                                    .id("ea95782f-e852-42a1-83dd-7d266ad9f32d")
                                    .url("/v2/routes/ea95782f-e852-42a1-83dd-7d266ad9f32d")
                                    .build())
                            .entity(RouteEntity.builder()
                                    .applicationsUrl("/v2/routes/ea95782f-e852-42a1-83dd-7d266ad9f32d/apps")
                                    .domainId("1f36d1d3-fcba-49dc-9320-d60ead679d35")
                                    .domainUrl("/v2/domains/1f36d1d3-fcba-49dc-9320-d60ead679d35")
                                    .host("host-14")
                                    .path("")
                                    .spaceId("dd314ba4-3690-48d1-becb-25abe5da801c")
                                    .spaceUrl("/v2/spaces/dd314ba4-3690-48d1-becb-25abe5da801c")
                                    .build())
                            .build())
                    .build();
        }

        @Override
        protected ListApplicationRoutesRequest getValidRequest() throws Exception {
            return ListApplicationRoutesRequest.builder()
                    .id("test-id")
                    .page(-1)
                    .build();
        }

        @Override
        protected Mono<ListApplicationRoutesResponse> invoke(ListApplicationRoutesRequest request) {
            return this.applications.listRoutes(request);
        }

    }

    public static final class ListServiceBindings
            extends AbstractApiTest<ListApplicationServiceBindingsRequest, ListApplicationServiceBindingsResponse> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListApplicationServiceBindingsRequest getInvalidRequest() {
            return ListApplicationServiceBindingsRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET)
                    .path("v2/apps/test-id/service_bindings?q=service_instance_guid%20IN%20test-instance-id&page=-1")
                    .status(OK)
                    .responsePayload("v2/apps/GET_{id}_service_bindings_response.json");
        }

        @Override
        protected ListApplicationServiceBindingsResponse getResponse() {
            return ListApplicationServiceBindingsResponse.builder()
                    .totalResults(1)
                    .totalPages(1)
                    .resource(ServiceBindingResource.builder()
                            .metadata(Resource.Metadata.builder()
                                    .createdAt("2015-07-27T22:43:20Z")
                                    .id("42eda707-fe4d-4eed-9b39-7cb5e665c226")
                                    .url("/v2/service_bindings/42eda707-fe4d-4eed-9b39-7cb5e665c226")
                                    .build())
                            .entity(ServiceBindingEntity.builder()
                                    .applicationId("26ddc1de-3eeb-424b-82f3-f7f30a38b610")
                                    .serviceInstanceId("650d0eb7-3b83-414a-82a0-d503d1c8eb5f")
                                    .credential("creds-key-356", "creds-val-356")
                                    .gatewayName("")
                                    .applicationUrl("/v2/apps/26ddc1de-3eeb-424b-82f3-f7f30a38b610")
                                    .serviceInstanceUrl("/v2/service_instances/650d0eb7-3b83-414a-82a0-d503d1c8eb5f")
                                    .build())
                            .build())
                    .build();
        }

        @Override
        protected ListApplicationServiceBindingsRequest getValidRequest() throws Exception {
            return ListApplicationServiceBindingsRequest.builder()
                    .id("test-id")
                    .serviceInstanceId("test-instance-id")
                    .page(-1)
                    .build();
        }

        @Override
        protected Mono<ListApplicationServiceBindingsResponse> invoke(ListApplicationServiceBindingsRequest request) {
            return this.applications.listServiceBindings(request);
        }

    }

    public static final class RemoveRoute extends AbstractApiTest<RemoveApplicationRouteRequest, Void> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveApplicationRouteRequest getInvalidRequest() {
            return RemoveApplicationRouteRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(DELETE).path("v2/apps/test-id/routes/test-route-id")
                    .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveApplicationRouteRequest getValidRequest() throws Exception {
            return RemoveApplicationRouteRequest.builder()
                    .id("test-id")
                    .routeId("test-route-id")
                    .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveApplicationRouteRequest request) {
            return this.applications.removeRoute(request);
        }

    }

    public static final class RemoveServiceBinding
            extends AbstractApiTest<RemoveApplicationServiceBindingRequest, Void> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveApplicationServiceBindingRequest getInvalidRequest() {
            return RemoveApplicationServiceBindingRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(DELETE).path("v2/apps/test-id/service_bindings/test-service-binding-id")
                    .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveApplicationServiceBindingRequest getValidRequest() throws Exception {
            return RemoveApplicationServiceBindingRequest.builder()
                    .id("test-id")
                    .serviceBindingId("test-service-binding-id")
                    .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveApplicationServiceBindingRequest request) {
            return this.applications.removeServiceBinding(request);
        }
    }

    public static final class Restage extends AbstractApiTest<RestageApplicationRequest, RestageApplicationResponse> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RestageApplicationRequest getInvalidRequest() {
            return RestageApplicationRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(POST).path("v2/apps/test-id/restage")
                    .status(OK)
                    .responsePayload("v2/apps/POST_{id}_restage_response.json");
        }

        @Override
        protected RestageApplicationResponse getResponse() {
            return RestageApplicationResponse.builder()
                    .metadata(Resource.Metadata.builder()
                            .createdAt("2015-07-27T22:43:33Z")
                            .id("2c6b3d3c-47bb-4060-be49-a56496ab57d4")
                            .url("/v2/apps/2c6b3d3c-47bb-4060-be49-a56496ab57d4")
                            .updatedAt("2015-07-27T22:43:33Z")
                            .build())
                    .entity(RestageApplicationEntity.builder()
                            .name("name-2307")
                            .production(false)
                            .spaceId("1b59d670-770e-48b7-9056-b2eb64c8445d")
                            .stackId("ae6c816a-887f-44a4-af1a-a611902ba09c")
                            .memory(1024)
                            .instances(1)
                            .diskQuota(1024)
                            .state("STARTED")
                            .version("102573ce-4e28-4271-b042-3539098c7b30")
                            .console(false)
                            .packageState("PENDING")
                            .healthCheckType("port")
                            .diego(false)
                            .packageUpdatedAt("2015-07-27T22:43:33Z")
                            .detectedStartCommand("")
                            .enableSsh(true)
                            .dockerCredentialsJson("redacted_message", "[PRIVATE DATA HIDDEN]")
                            .build())
                    .build();
        }

        @Override
        protected RestageApplicationRequest getValidRequest() throws Exception {
            return RestageApplicationRequest.builder()
                    .id("test-id")
                    .build();
        }

        @Override
        protected Mono<RestageApplicationResponse> invoke(RestageApplicationRequest request) {
            return this.applications.restage(request);
        }

    }

    public static final class Statistics
            extends AbstractApiTest<ApplicationStatisticsRequest, ApplicationStatisticsResponse> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ApplicationStatisticsRequest getInvalidRequest() {
            return ApplicationStatisticsRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v2/apps/test-id/stats")
                    .status(OK)
                    .responsePayload("v2/apps/GET_{id}_stats_response.json");
        }

        @Override
        protected ApplicationStatisticsResponse getResponse() {
            return ApplicationStatisticsResponse.builder()
                    .instance("0", ApplicationStatisticsResponse.InstanceStats.builder()
                            .state("RUNNING")
                            .statistics(ApplicationStatisticsResponse.InstanceStats.Statistics.builder()
                                    .usage(ApplicationStatisticsResponse.InstanceStats.Statistics.Usage.builder()
                                            .disk(66392064l)
                                            .memory(29880320l)
                                            .cpu(0.13511219703079957d)
                                            .time("2014-06-19 22:37:58 +0000")
                                            .build())
                                    .name("app_name")
                                    .uri("app_name.example.com")
                                    .host("10.0.0.1")
                                    .port(61035)
                                    .uptime(65007l)
                                    .memoryQuota(536870912l)
                                    .diskQuota(1073741824l)
                                    .fdsQuota(16384)
                                    .build())
                            .build())
                    .build();
        }

        @Override
        protected ApplicationStatisticsRequest getValidRequest() throws Exception {
            return ApplicationStatisticsRequest.builder()
                    .id("test-id")
                    .build();
        }

        @Override
        protected Mono<ApplicationStatisticsResponse> invoke(ApplicationStatisticsRequest request) {
            return this.applications.statistics(request);
        }

    }

    public static final class Summary extends AbstractApiTest<SummaryApplicationRequest, SummaryApplicationResponse> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected SummaryApplicationRequest getInvalidRequest() {
            return SummaryApplicationRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v2/apps/test-id/summary")
                    .status(OK)
                    .responsePayload("v2/apps/GET_{id}_summary_response.json");
        }

        @Override
        protected SummaryApplicationResponse getResponse() {
            return SummaryApplicationResponse.builder()
                    .id("f501634a-c6a8-44a7-aafc-88862ec727ba")
                    .name("name-2124")
                    .route(Route.builder()
                            .id("7b0b080a-c567-48a3-acee-28a2c252e959")
                            .host("host-18")
                            .domain(Domain.builder()
                                    .id("011461ec-476b-444d-a048-b0e2a3ff0f30")
                                    .name("domain-55.example.com")
                                    .build())
                            .build())
                    .runningInstances(0)
                    .service(ServiceInstance.builder()
                            .id("b74ee576-9eb9-4e7e-8185-0c48db893d97")
                            .name("name-2126")
                            .boundApplicationCount(1)
                            .servicePlan(builder()
                                    .id("192be526-c7c0-4899-aa2a-3aca7996ccb0")
                                    .name("name-2127")
                                    .service(Service.builder()
                                            .id("0e586250-a340-4924-8e2e-f13250f595ce")
                                            .label("label-75")
                                            .build())
                                    .build())
                            .build())
                    .availableDomain(Domain.builder()
                            .id("011461ec-476b-444d-a048-b0e2a3ff0f30")
                            .name("domain-55.example.com")
                            .owningOrganizationId("aadb707d-bf3b-4a0e-8b22-c06aabad53c4")
                            .build())
                    .availableDomain(Domain.builder()
                            .id("7c4e943b-5e9b-44ac-85ce-fcff57134d3b")
                            .name("customer-app-domain1.com")
                            .build())
                    .availableDomain(Domain.builder()
                            .id("1477a304-2af6-48d0-9e8f-6e2cdf06a9a2")
                            .name("customer-app-domain2.com")
                            .build())
                    .production(false)
                    .spaceId("3736ea8c-7ff0-44ba-8ed0-bd60971e8155")
                    .stackId("21a9a6e8-127a-41ce-886a-0294d807c5e9")
                    .memory(1024)
                    .instances(1)
                    .diskQuota(1024)
                    .state("STOPPED")
                    .version("53188475-8b6c-4e13-b321-2955431b27de")
                    .console(false)
                    .packageState("PENDING")
                    .healthCheckType("port")
                    .diego(false)
                    .packageUpdatedAt("2015-07-27T22:43:29Z")
                    .detectedStartCommand("")
                    .enableSsh(true)
                    .dockerCredentialsJson("redacted_message", "[PRIVATE DATA HIDDEN]")
                    .build();
        }

        @Override
        protected SummaryApplicationRequest getValidRequest() throws Exception {
            return SummaryApplicationRequest.builder()
                    .id("test-id")
                    .build();
        }

        @Override
        protected Mono<SummaryApplicationResponse> invoke(SummaryApplicationRequest request) {
            return this.applications.summary(request);
        }

    }

    public static final class TerminateInstance extends AbstractApiTest<TerminateApplicationInstanceRequest, Void> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);


        @Override
        protected TerminateApplicationInstanceRequest getInvalidRequest() {
            return TerminateApplicationInstanceRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(DELETE).path("/v2/apps/test-id/instances/test-index")
                    .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected TerminateApplicationInstanceRequest getValidRequest() throws Exception {
            return TerminateApplicationInstanceRequest.builder()
                    .id("test-id")
                    .index("test-index")
                    .build();
        }

        @Override
        protected Mono<Void> invoke(TerminateApplicationInstanceRequest request) {
            return this.applications.terminateInstance(request);
        }

    }

    public static final class Update extends AbstractApiTest<UpdateApplicationRequest, UpdateApplicationResponse> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected UpdateApplicationRequest getInvalidRequest() {
            return UpdateApplicationRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(PUT).path("/v2/apps/test-id")
                    .requestPayload("v2/apps/PUT_{id}_request.json")
                    .status(CREATED)
                    .responsePayload("v2/apps/PUT_{id}_response.json");
        }

        @Override
        protected UpdateApplicationResponse getResponse() {
            return UpdateApplicationResponse.builder()
                    .entity(ApplicationEntity.builder()
                            .name("new_name")
                            .production(false)
                            .spaceId("701aebe5-92fd-44cf-a7e6-bc54685c32ea")
                            .stackId("2cdc06a4-cb6e-4191-9ce8-b6bca4a16aaf")
                            .memory(1024)
                            .instances(1)
                            .diskQuota(1024)
                            .state("STOPPED")
                            .version("89c2beaa-5f16-49f2-bf8c-cbe49edf555b")
                            .console(false)
                            .packageState("PENDING")
                            .healthCheckType("port")
                            .diego(false)
                            .packageUpdatedAt("2015-07-27T22:43:21Z")
                            .detectedStartCommand("")
                            .enableSsh(true)
                            .dockerCredentialsJson("redacted_message", "[PRIVATE DATA HIDDEN]")
                            .spaceUrl("/v2/spaces/701aebe5-92fd-44cf-a7e6-bc54685c32ea")
                            .stackUrl("/v2/stacks/2cdc06a4-cb6e-4191-9ce8-b6bca4a16aaf")
                            .eventsUrl("/v2/apps/0c71909b-3d44-49c3-b65d-13894d70972c/events")
                            .serviceBindingsUrl("/v2/apps/0c71909b-3d44-49c3-b65d-13894d70972c/service_bindings")
                            .routesUrl("/v2/apps/0c71909b-3d44-49c3-b65d-13894d70972c/routes")
                            .build())
                    .metadata(Resource.Metadata.builder()
                            .updatedAt("2015-07-27T22:43:21Z")
                            .createdAt("2015-07-27T22:43:21Z")
                            .id("0c71909b-3d44-49c3-b65d-13894d70972c")
                            .url("/v2/apps/0c71909b-3d44-49c3-b65d-13894d70972c")
                            .build())
                    .build();
        }

        @Override
        protected UpdateApplicationRequest getValidRequest() throws Exception {
            return UpdateApplicationRequest.builder()
                    .id("test-id")
                    .name("new_name")
                    .build();
        }

        @Override
        protected Mono<UpdateApplicationResponse> invoke(UpdateApplicationRequest request) {
            return this.applications.update(request);
        }

    }

    public static final class Upload extends AbstractApiTest<UploadApplicationRequest, UploadApplicationResponse> {

        private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected UploadApplicationRequest getInvalidRequest() {
            return UploadApplicationRequest.builder()
                    .build();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(PUT).path("/v2/apps/test-id/bits")
                    .requestMatcher(header("Content-Type", startsWith(MULTIPART_FORM_DATA_VALUE)))
                    .anyRequestPayload()
                    .status(CREATED)
                    .responsePayload("v2/apps/PUT_{id}_bits_response.json");
        }

        @Override
        protected UploadApplicationResponse getResponse() {
            return UploadApplicationResponse.builder()
                    .entity(JobEntity.builder()
                            .id("eff6a47e-67a1-4e3b-99a5-4f9bcab7620a")
                            .status("queued")
                            .build())
                    .metadata(Resource.Metadata.builder()
                            .createdAt("2015-07-27T22:43:33Z")
                            .id("eff6a47e-67a1-4e3b-99a5-4f9bcab7620a")
                            .url("/v2/jobs/eff6a47e-67a1-4e3b-99a5-4f9bcab7620a")
                            .build())
                    .build();
        }

        @Override
        protected UploadApplicationRequest getValidRequest() throws Exception {
            return UploadApplicationRequest.builder()
                    .application(new ClassPathResource("v2/apps/application.zip").getFile())
                    .id("test-id")
                    .resource(UploadApplicationRequest.Resource.builder()
                            .hash("b907173290db6a155949ab4dc9b2d019dea0c901")
                            .path("path/to/content.txt")
                            .size(123)
                            .build())
                    .resource(UploadApplicationRequest.Resource.builder()
                            .hash("ff84f89760317996b9dd180ab996b079f418396f")
                            .path("path/to/code.jar")
                            .size(123)
                            .build())
                    .build();
        }

        @Override
        protected Mono<UploadApplicationResponse> invoke(UploadApplicationRequest request) {
            return this.applications.upload(request);
        }

    }

}
