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

package org.cloudfoundry.spring.client.v2.featureflags;

import org.cloudfoundry.client.v2.featureflags.FeatureFlagEntity;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagResponse;
import org.cloudfoundry.client.v2.featureflags.ListFeatureFlagsRequest;
import org.cloudfoundry.client.v2.featureflags.ListFeatureFlagsResponse;
import org.cloudfoundry.client.v2.featureflags.SetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.SetFeatureFlagResponse;
import org.cloudfoundry.spring.AbstractApiTest;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;


public final class SpringFeatureFlagsTest {

    public static final class GetAppScaling extends AbstractApiTest<GetFeatureFlagRequest, GetFeatureFlagResponse> {

        private final SpringFeatureFlags featureFlags = new SpringFeatureFlags(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetFeatureFlagRequest getInvalidRequest() {
            return GetFeatureFlagRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/config/feature_flags/app_scaling")
                .status(OK)
                .responsePayload("fixtures/client/v2/feature_flags/GET_app_scaling_flag_response.json");
        }

        @Override
        protected GetFeatureFlagResponse getResponse() {
            return GetFeatureFlagResponse.builder()
                .name("app_scaling")
                .enabled(true)
                .url("/v2/config/feature_flags/app_scaling")
                .build();
        }

        @Override
        protected GetFeatureFlagRequest getValidRequest() {
            return GetFeatureFlagRequest.builder()
                .name("app_scaling")
                .build();
        }

        @Override
        protected Mono<GetFeatureFlagResponse> invoke(GetFeatureFlagRequest request) {
            return this.featureFlags.get(request);
        }

    }

    public static final class GetUserRoles extends AbstractApiTest<GetFeatureFlagRequest, GetFeatureFlagResponse> {

        private final SpringFeatureFlags featureFlags = new SpringFeatureFlags(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetFeatureFlagRequest getInvalidRequest() {
            return GetFeatureFlagRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/config/feature_flags/set_roles_by_username")
                .status(OK)
                .responsePayload("fixtures/client/v2/feature_flags/GET_set_user_roles_flag_response.json");
        }

        @Override
        protected GetFeatureFlagResponse getResponse() {
            return GetFeatureFlagResponse.builder()
                .name("set_roles_by_username")
                .enabled(true)
                .url("/v2/config/feature_flags/set_roles_by_username")
                .build();
        }

        @Override
        protected GetFeatureFlagRequest getValidRequest() {
            return GetFeatureFlagRequest.builder()
                .name("set_roles_by_username")
                .build();
        }

        @Override
        protected Mono<GetFeatureFlagResponse> invoke(GetFeatureFlagRequest request) {
            return this.featureFlags.get(request);
        }

    }

    public static final class List extends AbstractApiTest<ListFeatureFlagsRequest, ListFeatureFlagsResponse> {

        private SpringFeatureFlags featureFlags = new SpringFeatureFlags(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListFeatureFlagsRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/config/feature_flags")
                .status(OK)
                .responsePayload("fixtures/client/v2/feature_flags/GET_response.json");
        }

        @Override
        protected ListFeatureFlagsResponse getResponse() {
            return ListFeatureFlagsResponse.builder()
                .featureFlag(FeatureFlagEntity.builder()
                    .name("user_org_creation")
                    .enabled(false)
                    .url("/v2/config/feature_flags/user_org_creation")
                    .build()
                )
                .featureFlag(FeatureFlagEntity.builder()
                    .name("private_domain_creation")
                    .enabled(false)
                    .errorMessage("foobar")
                    .url("/v2/config/feature_flags/private_domain_creation")
                    .build()
                )
                .featureFlag(FeatureFlagEntity.builder()
                    .name("app_bits_upload")
                    .enabled(true)
                    .url("/v2/config/feature_flags/app_bits_upload")
                    .build()
                )
                .featureFlag(FeatureFlagEntity.builder()
                    .name("app_scaling")
                    .enabled(true)
                    .url("/v2/config/feature_flags/app_scaling")
                    .build()
                )
                .featureFlag(FeatureFlagEntity.builder()
                    .name("route_creation")
                    .enabled(true)
                    .url("/v2/config/feature_flags/route_creation")
                    .build()
                )
                .featureFlag(FeatureFlagEntity.builder()
                    .name("service_instance_creation")
                    .enabled(true)
                    .url("/v2/config/feature_flags/service_instance_creation")
                    .build()
                )
                .featureFlag(FeatureFlagEntity.builder()
                    .name("diego_docker")
                    .enabled(false)
                    .url("/v2/config/feature_flags/diego_docker")
                    .build()
                )
                .featureFlag(FeatureFlagEntity.builder()
                    .name("set_roles_by_username")
                    .enabled(true)
                    .url("/v2/config/feature_flags/set_roles_by_username")
                    .build()
                )
                .featureFlag(FeatureFlagEntity.builder()
                    .name("unset_roles_by_username")
                    .enabled(true)
                    .url("/v2/config/feature_flags/unset_roles_by_username")
                    .build()
                )
                .featureFlag(FeatureFlagEntity.builder()
                    .name("task_creation")
                    .enabled(false)
                    .url("/v2/config/feature_flags/task_creation")
                    .build()
                )
                .featureFlag(FeatureFlagEntity.builder()
                    .name("space_scoped_private_broker_creation")
                    .enabled(true)
                    .url("/v2/config/feature_flags/space_scoped_private_broker_creation")
                    .build()
                )
                .featureFlag(FeatureFlagEntity.builder()
                    .name("space_developer_env_var_visibility")
                    .enabled(true)
                    .url("/v2/config/feature_flags/space_developer_env_var_visibility")
                    .build()
                )
                .build();
        }

        @Override
        protected ListFeatureFlagsRequest getValidRequest() throws Exception {
            return ListFeatureFlagsRequest.builder().build();
        }

        @Override
        protected Mono<ListFeatureFlagsResponse> invoke(ListFeatureFlagsRequest request) {
            return this.featureFlags.list(request);
        }

    }

    public static final class Set extends AbstractApiTest<SetFeatureFlagRequest, SetFeatureFlagResponse> {

        private final SpringFeatureFlags featureFlags = new SpringFeatureFlags(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected SetFeatureFlagRequest getInvalidRequest() {
            return SetFeatureFlagRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/config/feature_flags/user_org_creation")
                .requestPayload("fixtures/client/v2/feature_flags/PUT_user_org_creation_request.json")
                .status(OK)
                .responsePayload("fixtures/client/v2/feature_flags/PUT_user_org_creation_response.json");
        }

        @Override
        protected SetFeatureFlagResponse getResponse() {
            return SetFeatureFlagResponse.builder()
                .name("user_org_creation")
                .enabled(true)
                .url("/v2/config/feature_flags/user_org_creation")
                .build();
        }

        @Override
        protected SetFeatureFlagRequest getValidRequest() {
            return SetFeatureFlagRequest.builder()
                .enabled(true)
                .name("user_org_creation")
                .build();
        }

        @Override
        protected Mono<SetFeatureFlagResponse> invoke(SetFeatureFlagRequest request) {
            return this.featureFlags.set(request);
        }

    }

}
