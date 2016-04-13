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

package org.cloudfoundry.spring.client.v2.securitygroups;

import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupRunningDefaultsRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupRunningDefaultsResponse;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupResource;
import org.cloudfoundry.spring.AbstractApiTest;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringSecurityGroupsTest {

    public static final class List extends AbstractApiTest<ListSecurityGroupRunningDefaultsRequest, ListSecurityGroupRunningDefaultsResponse> {

        private final SpringSecurityGroups securityGroups = new SpringSecurityGroups(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListSecurityGroupRunningDefaultsRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/config/running_security_groups")
                .status(OK)
                .responsePayload("fixtures/client/v2/config/GET_running_security_groups_response.json");
        }

        @Override
        protected ListSecurityGroupRunningDefaultsResponse getResponse() {
            return ListSecurityGroupRunningDefaultsResponse.builder()
                .totalPages(1)
                .totalResults(1)
                .resource(SecurityGroupResource.builder()
                    .metadata(Resource.Metadata.builder()
                        .createdAt("2016-04-06T00:17:17Z")
                        .id("1f2f24f8-f68c-4a3b-b51a-8134fe2626d8")
                        .url("/v2/config/running_security_groups/1f2f24f8-f68c-4a3b-b51a-8134fe2626d8")
                        .build())
                    .entity(SecurityGroupEntity.builder()
                        .name("name-114")
                        .rule(SecurityGroupEntity.RuleEntity.builder()
                            .destination("198.41.191.47/1")
                            .ports("8080")
                            .protocol("udp")
                            .build())
                        .runningDefault(true)
                        .stagingDefault(false)
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListSecurityGroupRunningDefaultsRequest getValidRequest() throws Exception {
            return ListSecurityGroupRunningDefaultsRequest.builder().build();
        }

        @Override
        protected Mono<ListSecurityGroupRunningDefaultsResponse> invoke(ListSecurityGroupRunningDefaultsRequest request) {
            return this.securityGroups.listRunningDefaults(request);
        }

    }

}
