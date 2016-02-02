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

package org.cloudfoundry.client.spring.v2.users;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.Resource.Metadata;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.client.v2.users.ListUsersResponse;
import org.cloudfoundry.client.v2.users.UserEntity;
import org.cloudfoundry.client.v2.users.UserResource;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringUsersTest {

    public static final class ListUsers extends AbstractApiTest<ListUsersRequest, ListUsersResponse> {

        private final SpringUsers users = new SpringUsers(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListUsersRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("v2/users?page=-1")
                .status(OK)
                .responsePayload("v2/users/GET_response.json");
        }

        @Override
        protected ListUsersResponse getResponse() {
            return ListUsersResponse.builder()
                .totalResults(2)
                .totalPages(1)
                .resource(UserResource.builder()
                    .metadata(Metadata.builder()
                        .id("uaa-id-133")
                        .url("/v2/users/uaa-id-133")
                        .createdAt("2015-12-22T18:28:01Z")
                        .build())
                    .entity(UserEntity.builder()
                        .active(false)
                        .admin(false)
                        .auditedOrganizationsUrl("/v2/users/uaa-id-133/audited_organizations")
                        .auditedSpacesUrl("/v2/users/uaa-id-133/audited_spaces")
                        .billingManagedOrganizationsUrl("/v2/users/uaa-id-133/billing_managed_organizations")
                        .defaultSpaceId("/v2/spaces/55b306f6-b956-4c85-a7dc-64358121d39e")
                        .defaultSpaceUrl("55b306f6-b956-4c85-a7dc-64358121d39e")
                        .managedOrganizationsUrl("/v2/users/uaa-id-133/managed_organizations")
                        .managedSpacesUrl("/v2/users/uaa-id-133/managed_spaces")
                        .organizationsUrl("/v2/users/uaa-id-133/organizations")
                        .spacesUrl("/v2/users/uaa-id-133/spaces")
                        .username("user@example.com")
                        .build())
                    .build())
                .resource(UserResource.builder()
                    .metadata(Metadata.builder()
                        .id("uaa-id-134")
                        .url("/v2/users/uaa-id-134")
                        .createdAt("2015-12-22T18:28:01Z")
                        .build())
                    .entity(UserEntity.builder()
                        .active(true)
                        .admin(false)
                        .auditedOrganizationsUrl("/v2/users/uaa-id-134/audited_organizations")
                        .auditedSpacesUrl("/v2/users/uaa-id-134/audited_spaces")
                        .billingManagedOrganizationsUrl("/v2/users/uaa-id-134/billing_managed_organizations")
                        .managedOrganizationsUrl("/v2/users/uaa-id-134/managed_organizations")
                        .managedSpacesUrl("/v2/users/uaa-id-134/managed_spaces")
                        .organizationsUrl("/v2/users/uaa-id-134/organizations")
                        .spacesUrl("/v2/users/uaa-id-134/spaces")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListUsersRequest getValidRequest() throws Exception {
            return ListUsersRequest.builder()
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListUsersResponse> invoke(ListUsersRequest request) {
            return this.users.listUsers(request);
        }

    }

}
