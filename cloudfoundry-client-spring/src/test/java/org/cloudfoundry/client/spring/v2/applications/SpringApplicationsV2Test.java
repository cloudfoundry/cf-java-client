/*
 * Copyright 2013-2015 the original author or authors.
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

import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.spring.util.StringMap;
import org.cloudfoundry.client.v2.CloudFoundryException;
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
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.applications.TerminateApplicationInstanceRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationBitsRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationBitsResponse;
import org.cloudfoundry.client.v2.applications.UploadApplicationBitsResponse.UploadApplicationBitsEntity;
import org.cloudfoundry.client.v2.domains.Domain;
import org.cloudfoundry.client.v2.routes.Route;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstance;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.rx.Streams;

import java.io.IOException;
import java.util.Collections;

import static org.cloudfoundry.client.v2.serviceinstances.ServiceInstance.Plan.Service;
import static org.cloudfoundry.client.v2.serviceinstances.ServiceInstance.Plan.builder;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;

public final class SpringApplicationsV2Test extends AbstractRestTest {

    private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root);

    @Test
    public void create() {
        mockRequest(new RequestContext()
                .method(POST).path("/v2/apps")
                .requestPayload("v2/apps/POST_request.json")
                .status(CREATED)
                .responsePayload("v2/apps/POST_response.json"));

        CreateApplicationRequest request = CreateApplicationRequest.builder()
                .name("my_super_app")
                .spaceId("86dc4dc4-a2f7-438a-9f85-19a35bd15165")
                .build();

        CreateApplicationResponse expected = CreateApplicationResponse.builder()
                .metadata(Resource.Metadata.builder()
                        .createdAt("2015-07-27T22:43:20Z")
                        .id("508f0995-cbec-494c-99d1-f8c238117817")
                        .url("/v2/apps/508f0995-cbec-494c-99d1-f8c238117817")
                        .build())
                .entity(ApplicationEntity.builder()
                        .console(false)
                        .detectedStartCommand("")
                        .diego(false)
                        .diskQuota(1024)
                        .dockerCredentialsJson("redacted_message", "[PRIVATE DATA HIDDEN]")
                        .enableSsh(true)
                        .eventsUrl("/v2/apps/508f0995-cbec-494c-99d1-f8c238117817/events")
                        .healthCheckType("port")
                        .instances(1)
                        .memory(1024)
                        .name("my_super_app")
                        .packageState("PENDING")
                        .production(false)
                        .routesUrl("/v2/apps/508f0995-cbec-494c-99d1-f8c238117817/routes")
                        .serviceBindingsUrl("/v2/apps/508f0995-cbec-494c-99d1-f8c238117817/service_bindings")
                        .spaceId("86dc4dc4-a2f7-438a-9f85-19a35bd15165")
                        .spaceUrl("/v2/spaces/86dc4dc4-a2f7-438a-9f85-19a35bd15165")
                        .stackId("d449ecea-669f-448a-a9e7-ec84d51e2fdb")
                        .stackUrl("/v2/stacks/d449ecea-669f-448a-a9e7-ec84d51e2fdb")
                        .state("STOPPED")
                        .version("5d8a5c4a-9e74-4958-af55-abc8b8d1968d")
                        .build())
                .build();

        CreateApplicationResponse actual = Streams.wrap(this.applications.create(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void createError() {
        mockRequest(new RequestContext()
                .method(POST).path("/v2/apps")
                .requestPayload("v2/apps/POST_request.json")
                .errorResponse());

        CreateApplicationRequest request = CreateApplicationRequest.builder()
                .name("my_super_app")
                .spaceId("86dc4dc4-a2f7-438a-9f85-19a35bd15165")
                .build();

        Streams.wrap(this.applications.create(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void createInvalidRequest() {
        CreateApplicationRequest request = CreateApplicationRequest.builder()
                .build();

        Streams.wrap(this.applications.create(request)).next().poll();
    }

    @Test
    public void delete() {
        mockRequest(new RequestContext()
                .method(DELETE).path("/v2/apps/test-id")
                .status(NO_CONTENT));

        DeleteApplicationRequest request = DeleteApplicationRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.applications.delete(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void deleteError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("/v2/apps/test-id")
                .errorResponse());

        DeleteApplicationRequest request = DeleteApplicationRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.applications.delete(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void deleteInvalidRequest() {
        DeleteApplicationRequest request = DeleteApplicationRequest.builder()
                .build();

        Streams.wrap(this.applications.delete(request)).next().poll();
    }

    @Test
    public void environment() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/apps/test-id/env")
                .status(OK)
                .responsePayload("v2/apps/GET_{id}_env_response.json"));

        ApplicationEnvironmentRequest request = ApplicationEnvironmentRequest.builder()
                .id("test-id")
                .build();

        ApplicationEnvironmentResponse expected = ApplicationEnvironmentResponse.builder()
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

        ApplicationEnvironmentResponse actual = Streams.wrap(this.applications.environment(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void environmentError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/apps/test-id/env")
                .errorResponse());

        ApplicationEnvironmentRequest request = ApplicationEnvironmentRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.applications.environment(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void environmentInvalidRequest() {
        ApplicationEnvironmentRequest request = ApplicationEnvironmentRequest.builder()
                .build();

        Streams.wrap(this.applications.environment(request)).next().poll();
    }

    @Test
    public void get() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/apps/test-id")
                .status(OK)
                .responsePayload("v2/apps/GET_{id}_response.json"));

        GetApplicationRequest request = GetApplicationRequest.builder()
                .id("test-id")
                .build();

        GetApplicationResponse expected = GetApplicationResponse.builder()
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

        GetApplicationResponse actual = Streams.wrap(this.applications.get(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void getError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/apps/test-id")
                .errorResponse());

        GetApplicationRequest request = GetApplicationRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.applications.get(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void getInvalidRequest() {
        GetApplicationRequest request = GetApplicationRequest.builder()
                .build();

        Streams.wrap(this.applications.get(request)).next().poll();
    }

    @Test
    public void instances() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/apps/test-id/instances")
                .status(OK)
                .responsePayload("v2/apps/GET_{id}_instances_response.json"));

        ApplicationInstancesRequest request = ApplicationInstancesRequest.builder()
                .id("test-id")
                .build();

        ApplicationInstancesResponse expected = ApplicationInstancesResponse.builder()
                .instance("0",
                        ApplicationInstanceInfo.builder()
                                .since(1403140717.984577d)
                                .state("RUNNING")
                                .build())
                .build();

        ApplicationInstancesResponse actual =
                Streams.wrap(this.applications.instances(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void instancesError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/apps/test-id/instances")
                .errorResponse());

        ApplicationInstancesRequest request = ApplicationInstancesRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.applications.instances(request)).next().get();

    }

    @Test(expected = RequestValidationException.class)
    public void instancesInvalidRequest() {
        ApplicationInstancesRequest request = ApplicationInstancesRequest.builder()
                .build();

        Streams.wrap(this.applications.instances(request)).next().get();
    }

    @Test
    public void list() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/apps?q=name%20IN%20test-name&page=-1")
                .status(OK)
                .responsePayload("v2/apps/GET_apps_response.json"));

        ListApplicationsRequest request = ListApplicationsRequest.builder()
                .name("test-name")
                .page(-1)
                .build();

        ListApplicationsResponse expected = ListApplicationsResponse.builder()
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
                                .serviceBindingsUrl("/v2/apps/3d294ed0-105c-4ccd-8f79-5605d6b7198c/service_bindings")
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
                                .serviceBindingsUrl("/v2/apps/522c5382-29e9-48aa-9db0-9f6cfa643ec1/service_bindings")
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
                                .serviceBindingsUrl("/v2/apps/ec31bfbd-ab5c-490d-8e83-3c1ea5d1bedf/service_bindings")
                                .spaceId("e438b2bf-17d6-4265-8813-18e0ab95c029")
                                .spaceUrl("/v2/spaces/e438b2bf-17d6-4265-8813-18e0ab95c029")
                                .stackId("8d42ba27-60df-420e-9208-535e753b706a")
                                .stackUrl("/v2/stacks/8d42ba27-60df-420e-9208-535e753b706a")
                                .state("STOPPED")
                                .version("8e74d312-1bc9-4953-b4fe-d2613ea4972a")
                                .build())
                        .build())
                .build();

        ListApplicationsResponse actual = Streams.wrap(this.applications.list(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/apps?q=name%20IN%20test-name&page=-1")
                .errorResponse());

        ListApplicationsRequest request = ListApplicationsRequest.builder()
                .name("test-name")
                .page(-1)
                .build();

        Streams.wrap(this.applications.list(request)).next().get();
    }

    @Test
    public void statistics() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/apps/test-id/stats")
                .status(OK)
                .responsePayload("v2/apps/GET_{id}_stats_response.json"));

        ApplicationStatisticsRequest request = ApplicationStatisticsRequest.builder()
                .id("test-id")
                .build();

        ApplicationStatisticsResponse expected = ApplicationStatisticsResponse.builder()
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

        ApplicationStatisticsResponse actual = Streams.wrap(this.applications.statistics(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void statisticsError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/apps/test-id/stats")
                .errorResponse());

        ApplicationStatisticsRequest request = ApplicationStatisticsRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.applications.statistics(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void statisticsInvalidRequest() {
        ApplicationStatisticsRequest request = ApplicationStatisticsRequest.builder()
                .build();

        Streams.wrap(this.applications.statistics(request)).next().poll();
    }

    @Test
    public void summary() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/apps/test-id/summary")
                .status(OK)
                .responsePayload("v2/apps/GET_{id}_summary_response.json"));

        SummaryApplicationRequest request = SummaryApplicationRequest.builder()
                .id("test-id")
                .build();

        SummaryApplicationResponse expected = SummaryApplicationResponse.builder()
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

        SummaryApplicationResponse actual = Streams.wrap(this.applications.summary(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void summaryError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/apps/test-id/summary")
                .errorResponse());

        SummaryApplicationRequest request = SummaryApplicationRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.applications.summary(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void summaryInvalidRequest() {
        SummaryApplicationRequest request = SummaryApplicationRequest.builder()
                .build();

        Streams.wrap(this.applications.summary(request)).next().poll();
    }

    @Test
    public void terminateInstance() {
        mockRequest(new RequestContext()
                .method(DELETE).path("/v2/apps/test-id/instances/-1")
                .status(NO_CONTENT));

        TerminateApplicationInstanceRequest request = TerminateApplicationInstanceRequest.builder()
                .id("test-id")
                .index(-1)
                .build();

        Streams.wrap(this.applications.terminateInstance(request)).next().get();
    }

    @Test(expected = CloudFoundryException.class)
    public void terminateInstanceError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("/v2/apps/test-id/instances/-1")
                .errorResponse());

        TerminateApplicationInstanceRequest request = TerminateApplicationInstanceRequest.builder()
                .id("test-id")
                .index(-1)
                .build();

        Streams.wrap(this.applications.terminateInstance(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void terminateInstanceInvalidRequest() {
        TerminateApplicationInstanceRequest request = TerminateApplicationInstanceRequest.builder()
                .build();

        Streams.wrap(this.applications.terminateInstance(request)).next().get();
    }

    @Test
    public void upload() throws IOException {
        mockRequest(new RequestContext()
                .method(PUT).path("/v2/apps/test-id/bits")
                .requestMatcher(header("Content-Type", startsWith(MULTIPART_FORM_DATA_VALUE)))
                .anyRequestPayload()
                .status(CREATED)
                .responsePayload("v2/apps/PUT_{id}_bits_response.json"));

        UploadApplicationBitsRequest request = UploadApplicationBitsRequest.builder()
                .application(new ClassPathResource("v2/apps/test-file").getFile())
                .id("test-id")
                .build();

        UploadApplicationBitsResponse expected = UploadApplicationBitsResponse.builder()
                .metadata(Resource.Metadata.builder()
                        .id("eff6a47e-67a1-4e3b-99a5-4f9bcab7620a")
                        .createdAt("2015-07-27T22:43:33Z")
                        .url("/v2/jobs/eff6a47e-67a1-4e3b-99a5-4f9bcab7620a")
                        .build())
                .entity(UploadApplicationBitsEntity.builder()
                        .id("eff6a47e-67a1-4e3b-99a5-4f9bcab7620a")
                        .status("queued")
                        .build())
                .build();

        UploadApplicationBitsResponse actual = Streams.wrap(this.applications.upload(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void uploadError() throws IOException {
        mockRequest(new RequestContext()
                .method(PUT).path("/v2/apps/test-id/bits")
                .requestMatcher(header("Content-Type", startsWith(MULTIPART_FORM_DATA_VALUE)))
                .anyRequestPayload()
                .errorResponse());

        UploadApplicationBitsRequest request = UploadApplicationBitsRequest.builder()
                .application(new ClassPathResource("v2/apps/test-file").getFile())
                .id("test-id")
                .build();

        Streams.wrap(this.applications.upload(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void uploadInvalidRequest() {
        UploadApplicationBitsRequest request = UploadApplicationBitsRequest.builder()
                .build();

        Streams.wrap(this.applications.upload(request)).next().poll();
    }
}
