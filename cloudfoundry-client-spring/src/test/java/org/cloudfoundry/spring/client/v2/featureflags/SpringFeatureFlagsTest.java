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

import org.cloudfoundry.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;


public class SpringFeatureFlagsTest {

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
                .responsePayload("client/v2/feature_flags/GET_set_user_roles_flag_response.json");
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
                .responsePayload("client/v2/feature_flags/GET_app_scaling_flag_response.json");
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
}
