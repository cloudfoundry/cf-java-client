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

package org.cloudfoundry.reactor.client.v2.securitygroups;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.securitygroups.AssociateSecurityGroupSpaceRequest;
import org.cloudfoundry.client.v2.securitygroups.AssociateSecurityGroupSpaceResponse;
import org.cloudfoundry.client.v2.securitygroups.CreateSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.CreateSecurityGroupResponse;
import org.cloudfoundry.client.v2.securitygroups.DeleteSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.DeleteSecurityGroupResponse;
import org.cloudfoundry.client.v2.securitygroups.GetSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.GetSecurityGroupResponse;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupRunningDefaultsRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupRunningDefaultsResponse;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupSpacesRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupSpacesResponse;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupStagingDefaultsRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupStagingDefaultsResponse;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupsRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupsResponse;
import org.cloudfoundry.client.v2.securitygroups.RemoveSecurityGroupRunningDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.RemoveSecurityGroupSpaceRequest;
import org.cloudfoundry.client.v2.securitygroups.RemoveSecurityGroupStagingDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.RuleEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupResource;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupRunningDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupRunningDefaultResponse;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupStagingDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupStagingDefaultResponse;
import org.cloudfoundry.client.v2.securitygroups.UpdateSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.UpdateSecurityGroupResponse;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
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
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.cloudfoundry.client.v2.securitygroups.Protocol.ALL;
import static org.cloudfoundry.client.v2.securitygroups.Protocol.ICMP;
import static org.cloudfoundry.client.v2.securitygroups.Protocol.TCP;
import static org.cloudfoundry.client.v2.securitygroups.Protocol.UDP;

public final class ReactorSecurityGroupsTest extends AbstractClientApiTest {

    private final ReactorSecurityGroups securityGroups = new ReactorSecurityGroups(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void associateSpace() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/security_groups/1452e164-0c3e-4a6c-b3c3-c40ad9fd0159/spaces/1305ec2b-a31c-4d2e-adc8-d9b764237e96")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/security_groups/PUT_{id}_spaces_{space-id}_response.json")
                .build())
            .build());

        this.securityGroups
            .associateSpace(AssociateSecurityGroupSpaceRequest.builder()
                .securityGroupId("1452e164-0c3e-4a6c-b3c3-c40ad9fd0159")
                .spaceId("1305ec2b-a31c-4d2e-adc8-d9b764237e96")
                .build())
            .as(StepVerifier::create)
            .expectNext(AssociateSecurityGroupSpaceResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:21Z")
                    .id("1452e164-0c3e-4a6c-b3c3-c40ad9fd0159")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .url("/v2/security_groups/1452e164-0c3e-4a6c-b3c3-c40ad9fd0159")
                    .build())
                .entity(SecurityGroupEntity.builder()
                    .name("dummy1")
                    .rules()
                    .runningDefault(false)
                    .stagingDefault(false)
                    .spacesUrl("/v2/security_groups/1452e164-0c3e-4a6c-b3c3-c40ad9fd0159/spaces")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/security_groups")
                .payload("fixtures/client/v2/security_groups/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/security_groups/POST_response.json")
                .build())
            .build());

        this.securityGroups
            .create(CreateSecurityGroupRequest.builder()
                .name("my_super_sec_group")
                .rule(RuleEntity.builder()
                    .protocol(ICMP)
                    .destination("0.0.0.0/0")
                    .type(0)
                    .code(1)
                    .build())
                .rule(RuleEntity.builder()
                    .protocol(TCP)
                    .destination("0.0.0.0/0")
                    .ports("2048-3000")
                    .log(true)
                    .build())
                .rule(RuleEntity.builder()
                    .protocol(UDP)
                    .destination("0.0.0.0/0")
                    .ports("53, 5353")
                    .build())
                .rule(RuleEntity.builder()
                    .protocol(ALL)
                    .destination("0.0.0.0/0")
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateSecurityGroupResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-05-12T00:45:26Z")
                    .id("966e7ac0-1c1a-4ca9-8a5f-77c96576beb7")
                    .url("/v2/security_groups/966e7ac0-1c1a-4ca9-8a5f-77c96576beb7")
                    .build())
                .entity(SecurityGroupEntity.builder()
                    .name("my_super_sec_group")
                    .rule(RuleEntity.builder()
                        .protocol(ICMP)
                        .destination("0.0.0.0/0")
                        .type(0)
                        .code(1)
                        .build())
                    .rule(RuleEntity.builder()
                        .protocol(TCP)
                        .destination("0.0.0.0/0")
                        .ports("2048-3000")
                        .log(true)
                        .build())
                    .rule(RuleEntity.builder()
                        .protocol(UDP)
                        .destination("0.0.0.0/0")
                        .ports("53, 5353")
                        .build())
                    .rule(RuleEntity.builder()
                        .protocol(ALL)
                        .destination("0.0.0.0/0")
                        .build())
                    .runningDefault(false)
                    .stagingDefault(false)
                    .spacesUrl("/v2/security_groups/966e7ac0-1c1a-4ca9-8a5f-77c96576beb7/spaces")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/security_groups/test-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.securityGroups
            .delete(DeleteSecurityGroupRequest.builder()
                .securityGroupId("test-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteAsync() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/security_groups/test-id?async=true")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .payload("fixtures/client/v2/security_groups/DELETE_{id}_async_response.json")
                .build())
            .build());

        this.securityGroups
            .delete(DeleteSecurityGroupRequest.builder()
                .async(true)
                .securityGroupId("test-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(DeleteSecurityGroupResponse.builder()
                .metadata(Metadata.builder()
                    .id("260ba675-47b6-4094-be7a-349d58e3d36a")
                    .createdAt("2016-02-02T17:16:31Z")
                    .url("/v2/jobs/260ba675-47b6-4094-be7a-349d58e3d36a")
                    .build())
                .entity(JobEntity.builder()
                    .id("260ba675-47b6-4094-be7a-349d58e3d36a")
                    .status("queued")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteRunning() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/config/running_security_groups/test-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.securityGroups
            .removeRunningDefault(RemoveSecurityGroupRunningDefaultRequest.builder()
                .securityGroupId("test-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteStaging() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/config/staging_security_groups/test-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.securityGroups
            .removeStagingDefault(RemoveSecurityGroupStagingDefaultRequest.builder()
                .securityGroupId("test-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/security_groups/1452e164-0c3e-4a6c-b3c3-c40ad9fd0159")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/security_groups/GET_{id}_response.json")
                .build())
            .build());

        this.securityGroups
            .get(GetSecurityGroupRequest.builder()
                .securityGroupId("1452e164-0c3e-4a6c-b3c3-c40ad9fd0159")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetSecurityGroupResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:21Z")
                    .id("1452e164-0c3e-4a6c-b3c3-c40ad9fd0159")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .url("/v2/security_groups/1452e164-0c3e-4a6c-b3c3-c40ad9fd0159")
                    .build())
                .entity(SecurityGroupEntity.builder()
                    .name("dummy1")
                    .rules()
                    .runningDefault(false)
                    .stagingDefault(false)
                    .spacesUrl("/v2/security_groups/1452e164-0c3e-4a6c-b3c3-c40ad9fd0159/spaces")
                    .stagingSpacesUrl("/v2/security_groups/1452e164-0c3e-4a6c-b3c3-c40ad9fd0159/staging_spaces")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/security_groups")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/security_groups/GET_security_groups_response.json")
                .build())
            .build());

        this.securityGroups
            .list(ListSecurityGroupsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListSecurityGroupsResponse.builder()
                .totalResults(5)
                .totalPages(1)
                .resource(SecurityGroupResource.builder()
                    .metadata(Metadata.builder()
                        .id("1452e164-0c3e-4a6c-b3c3-c40ad9fd0159")
                        .url("/v2/security_groups/1452e164-0c3e-4a6c-b3c3-c40ad9fd0159")
                        .createdAt("2016-06-08T16:41:21Z")
                        .build())
                    .entity(SecurityGroupEntity.builder()
                        .name("dummy1")
                        .rules()
                        .runningDefault(false)
                        .stagingDefault(false)
                        .spacesUrl("/v2/security_groups/1452e164-0c3e-4a6c-b3c3-c40ad9fd0159/spaces")
                        .build())
                    .build())
                .resource(SecurityGroupResource.builder()
                    .metadata(Metadata.builder()
                        .id("61a3df25-f372-4554-9b77-811aaa5374c1")
                        .url("/v2/security_groups/61a3df25-f372-4554-9b77-811aaa5374c1")
                        .createdAt("2016-06-08T16:41:21Z")
                        .build())
                    .entity(SecurityGroupEntity.builder()
                        .name("dummy2")
                        .rules()
                        .runningDefault(false)
                        .stagingDefault(false)
                        .spacesUrl("/v2/security_groups/61a3df25-f372-4554-9b77-811aaa5374c1/spaces")
                        .build())
                    .build())
                .resource(SecurityGroupResource.builder()
                    .metadata(Metadata.builder()
                        .id("26bdad19-b077-4542-aac0-f7e4c53c344d")
                        .url("/v2/security_groups/26bdad19-b077-4542-aac0-f7e4c53c344d")
                        .createdAt("2016-06-08T16:41:22Z")
                        .build())
                    .entity(SecurityGroupEntity.builder()
                        .name("name-67")
                        .rule(RuleEntity.builder()
                            .protocol(UDP)
                            .ports("8080")
                            .destination("198.41.191.47/1")
                            .build())
                        .runningDefault(false)
                        .stagingDefault(false)
                        .spacesUrl("/v2/security_groups/26bdad19-b077-4542-aac0-f7e4c53c344d/spaces")
                        .build())
                    .build())
                .resource(SecurityGroupResource.builder()
                    .metadata(Metadata.builder()
                        .id("0a2b8908-66f5-4bef-80f3-ca21ed86fbb3")
                        .url("/v2/security_groups/0a2b8908-66f5-4bef-80f3-ca21ed86fbb3")
                        .createdAt("2016-06-08T16:41:22Z")
                        .build())
                    .entity(SecurityGroupEntity.builder()
                        .name("name-68")
                        .rule(RuleEntity.builder()
                            .protocol(UDP)
                            .ports("8080")
                            .destination("198.41.191.47/1")
                            .build())
                        .runningDefault(false)
                        .stagingDefault(false)
                        .spacesUrl("/v2/security_groups/0a2b8908-66f5-4bef-80f3-ca21ed86fbb3/spaces")
                        .build())
                    .build())
                .resource(SecurityGroupResource.builder()
                    .metadata(Metadata.builder()
                        .id("f5b93b76-cd25-4fed-bed6-0d9d0acff542")
                        .url("/v2/security_groups/f5b93b76-cd25-4fed-bed6-0d9d0acff542")
                        .createdAt("2016-06-08T16:41:22Z")
                        .build())
                    .entity(SecurityGroupEntity.builder()
                        .name("name-69")
                        .rule(RuleEntity.builder()
                            .protocol(UDP)
                            .ports("8080")
                            .destination("198.41.191.47/1")
                            .build())
                        .runningDefault(false)
                        .stagingDefault(false)
                        .spacesUrl("/v2/security_groups/f5b93b76-cd25-4fed-bed6-0d9d0acff542/spaces")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listRunning() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/config/running_security_groups")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/config/GET_running_security_groups_response.json")
                .build())
            .build());

        this.securityGroups
            .listRunningDefaults(ListSecurityGroupRunningDefaultsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListSecurityGroupRunningDefaultsResponse.builder()
                .totalPages(1)
                .totalResults(1)
                .resource(SecurityGroupResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2016-04-06T00:17:17Z")
                        .id("1f2f24f8-f68c-4a3b-b51a-8134fe2626d8")
                        .url("/v2/config/running_security_groups/1f2f24f8-f68c-4a3b-b51a-8134fe2626d8")
                        .build())
                    .entity(SecurityGroupEntity.builder()
                        .name("name-114")
                        .rule(RuleEntity.builder()
                            .destination("198.41.191.47/1")
                            .ports("8080")
                            .protocol(UDP)
                            .build())
                        .runningDefault(true)
                        .stagingDefault(false)
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listSpaces() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/security_groups/1452e164-0c3e-4a6c-b3c3-c40ad9fd0159/spaces?space_guid=09a060b2-f97a-4a57-b7d2-35e06ad71050")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/config/GET_{id}_spaces_response.json")
                .build())
            .build());

        this.securityGroups
            .listSpaces(ListSecurityGroupSpacesRequest.builder()
                .securityGroupId("1452e164-0c3e-4a6c-b3c3-c40ad9fd0159")
                .spaceId("09a060b2-f97a-4a57-b7d2-35e06ad71050")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListSecurityGroupSpacesResponse.builder()
                .totalPages(1)
                .totalResults(1)
                .resource(SpaceResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2016-06-08T16:41:21Z")
                        .id("3435dd59-f289-4191-83e6-6201d6fb6a22")
                        .updatedAt("2016-06-08T16:41:26Z")
                        .url("/v2/spaces/3435dd59-f289-4191-83e6-6201d6fb6a22")
                        .build())
                    .entity(SpaceEntity.builder()
                        .allowSsh(true)
                        .applicationEventsUrl("/v2/spaces/3435dd59-f289-4191-83e6-6201d6fb6a22/app_events")
                        .applicationsUrl("/v2/spaces/3435dd59-f289-4191-83e6-6201d6fb6a22/apps")
                        .auditorsUrl("/v2/spaces/3435dd59-f289-4191-83e6-6201d6fb6a22/auditors")
                        .developersUrl("/v2/spaces/3435dd59-f289-4191-83e6-6201d6fb6a22/developers")
                        .domainsUrl("/v2/spaces/3435dd59-f289-4191-83e6-6201d6fb6a22/domains")
                        .eventsUrl("/v2/spaces/3435dd59-f289-4191-83e6-6201d6fb6a22/events")
                        .managersUrl("/v2/spaces/3435dd59-f289-4191-83e6-6201d6fb6a22/managers")
                        .name("name-40")
                        .organizationId("1d1dd3f4-36bd-4380-8d01-3c7a934a9281")
                        .organizationUrl("/v2/organizations/1d1dd3f4-36bd-4380-8d01-3c7a934a9281")
                        .routesUrl("/v2/spaces/3435dd59-f289-4191-83e6-6201d6fb6a22/routes")
                        .securityGroupsUrl("/v2/spaces/3435dd59-f289-4191-83e6-6201d6fb6a22/security_groups")
                        .serviceInstancesUrl("/v2/spaces/3435dd59-f289-4191-83e6-6201d6fb6a22/service_instances")
                        .spaceQuotaDefinitionId(null)
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listStaging() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/config/staging_security_groups")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/config/GET_staging_security_groups_response.json")
                .build())
            .build());

        this.securityGroups
            .listStagingDefaults(ListSecurityGroupStagingDefaultsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListSecurityGroupStagingDefaultsResponse.builder()
                .totalPages(1)
                .totalResults(1)
                .resource(SecurityGroupResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2016-04-16T01:23:52Z")
                        .id("c0bb3afb-ae01-4af0-96cf-a5b0d2dca894")
                        .url("/v2/config/staging_security_groups/c0bb3afb-ae01-4af0-96cf-a5b0d2dca894")
                        .build())
                    .entity(SecurityGroupEntity.builder()
                        .name("name-570")
                        .rule(RuleEntity.builder()
                            .destination("198.41.191.47/1")
                            .ports("8080")
                            .protocol(UDP)
                            .build())
                        .runningDefault(false)
                        .stagingDefault(true)
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void removeSpace() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/security_groups/1452e164-0c3e-4a6c-b3c3-c40ad9fd0159/spaces/ca8f04d1-bc2b-40ef-975e-fda2cc785c2a")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.securityGroups
            .removeSpace(RemoveSecurityGroupSpaceRequest.builder()
                .securityGroupId("1452e164-0c3e-4a6c-b3c3-c40ad9fd0159")
                .spaceId("ca8f04d1-bc2b-40ef-975e-fda2cc785c2a")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void setRunning() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/config/running_security_groups/test-security-group-default-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/config/PUT_{id}_running_security_groups_response.json")
                .build())
            .build());

        this.securityGroups
            .setRunningDefault(SetSecurityGroupRunningDefaultRequest.builder()
                .securityGroupId("test-security-group-default-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(SetSecurityGroupRunningDefaultResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-04-06T00:17:17Z")
                    .id("9aa7ab9c-997f-4f87-be50-87105521881a")
                    .url("/v2/config/running_security_groups/9aa7ab9c-997f-4f87-be50-87105521881a")
                    .updatedAt("2016-04-06T00:17:17Z")
                    .build())
                .entity(SecurityGroupEntity.builder()
                    .name("name-109")
                    .rule(RuleEntity.builder()
                        .destination("198.41.191.47/1")
                        .ports("8080")
                        .protocol(UDP)
                        .build())
                    .runningDefault(true)
                    .stagingDefault(false)
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void setStaging() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/config/staging_security_groups/test-security-group-default-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/config/PUT_{id}_staging_security_groups_response.json")
                .build())
            .build());

        this.securityGroups
            .setStagingDefault(SetSecurityGroupStagingDefaultRequest.builder()
                .securityGroupId("test-security-group-default-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(SetSecurityGroupStagingDefaultResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-04-16T01:23:52Z")
                    .id("50165fce-6c41-4c35-a4d8-3858ee217d36")
                    .url("/v2/config/staging_security_groups/50165fce-6c41-4c35-a4d8-3858ee217d36")
                    .updatedAt("2016-04-16T01:23:52Z")
                    .build())
                .entity(SecurityGroupEntity.builder()
                    .name("name-567")
                    .rule(RuleEntity.builder()
                        .destination("198.41.191.47/1")
                        .ports("8080")
                        .protocol(UDP)
                        .build())
                    .runningDefault(false)
                    .stagingDefault(true)
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/security_groups/1452e164-0c3e-4a6c-b3c3-c40ad9fd0159")
                .payload("fixtures/client/v2/security_groups/PUT_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/security_groups/PUT_{id}_response.json")
                .build())
            .build());

        this.securityGroups
            .update(UpdateSecurityGroupRequest.builder()
                .name("new_name")
                .rules()
                .securityGroupId("1452e164-0c3e-4a6c-b3c3-c40ad9fd0159")
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateSecurityGroupResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:21Z")
                    .id("1452e164-0c3e-4a6c-b3c3-c40ad9fd0159")
                    .updatedAt("2016-06-08T16:41:21Z")
                    .url("/v2/security_groups/1452e164-0c3e-4a6c-b3c3-c40ad9fd0159")
                    .build())
                .entity(SecurityGroupEntity.builder()
                    .name("new_name")
                    .rules()
                    .runningDefault(false)
                    .stagingDefault(false)
                    .spacesUrl("/v2/security_groups/1452e164-0c3e-4a6c-b3c3-c40ad9fd0159/spaces")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
