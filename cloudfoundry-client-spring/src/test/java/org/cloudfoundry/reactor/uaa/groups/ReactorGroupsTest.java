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

package org.cloudfoundry.reactor.uaa.groups;

import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.uaa.AbstractUaaApiTest;
import org.cloudfoundry.uaa.Metadata;
import org.cloudfoundry.uaa.groups.CreateGroupRequest;
import org.cloudfoundry.uaa.groups.CreateGroupResponse;
import org.cloudfoundry.uaa.groups.GetGroupRequest;
import org.cloudfoundry.uaa.groups.GetGroupResponse;
import org.cloudfoundry.uaa.groups.Member;
import org.cloudfoundry.uaa.groups.MemberType;
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;


public final class ReactorGroupsTest {

    public static final class Create extends AbstractUaaApiTest<CreateGroupRequest, CreateGroupResponse> {

        private final ReactorGroups groups = new ReactorGroups(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .header("X-Identity-Zone-Id", "uaa")
                    .method(POST).path("/Groups")
                    .payload("fixtures/uaa/groups/POST_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/uaa/groups/POST_response.json")
                    .build())
                .build();
        }

        @Override
        protected CreateGroupResponse getResponse() {
            return CreateGroupResponse.builder()
                .id("46081184-7ca9-453d-9bf8-74da7113bec6")
                .metadata(Metadata.builder()
                    .created("2016-06-03T17:59:30.527Z")
                    .lastModified("2016-06-03T17:59:30.527Z")
                    .version(0)
                    .build())
                .description("the cool group")
                .displayName("Cool Group Name")
                .member(Member.builder()
                    .identityProviderOriginKey("uaa")
                    .type(MemberType.USER)
                    .value("f0e6a061-6e3a-4be9-ace5-142ee24e20b7")
                    .build())
                .schema("urn:scim:schemas:core:1.0")
                .zoneId("uaa")
                .build();
        }

        @Override
        protected CreateGroupRequest getValidRequest() throws Exception {
            return CreateGroupRequest.builder()
                .description("the cool group")
                .displayName("Cool Group Name")
                .identityZoneId("uaa")
                .member(Member.builder()
                    .identityProviderOriginKey("uaa")
                    .type(MemberType.USER)
                    .value("f0e6a061-6e3a-4be9-ace5-142ee24e20b7")
                    .build())
                .build();
        }

        @Override
        protected Mono<CreateGroupResponse> invoke(CreateGroupRequest request) {
            return this.groups.create(request);
        }
    }

    public static final class Get extends AbstractUaaApiTest<GetGroupRequest, GetGroupResponse> {

        private final ReactorGroups groups = new ReactorGroups(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/Groups/test-group-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/groups/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetGroupResponse getResponse() {
            return GetGroupResponse.builder()
                .id("test-group-id")
                .metadata(Metadata.builder()
                    .created("2016-06-03T17:59:30.527Z")
                    .lastModified("2016-06-03T17:59:30.561Z")
                    .version(1)
                    .build())
                .description("the cool group")
                .displayName("Cooler Group Name for Retrieve")
                .member(Member.builder()
                    .identityProviderOriginKey("uaa")
                    .type(MemberType.USER)
                    .value("f0e6a061-6e3a-4be9-ace5-142ee24e20b7")
                    .build())
                .schema("urn:scim:schemas:core:1.0")
                .zoneId("uaa")
                .build();
        }

        @Override
        protected GetGroupRequest getValidRequest() throws Exception {
            return GetGroupRequest.builder()
                .groupId("test-group-id")
                .build();
        }

        @Override
        protected Mono<GetGroupResponse> invoke(GetGroupRequest request) {
            return this.groups.get(request);
        }
    }

}