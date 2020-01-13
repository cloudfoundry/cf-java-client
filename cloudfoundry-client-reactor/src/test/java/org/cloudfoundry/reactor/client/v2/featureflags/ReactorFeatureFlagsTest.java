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

package org.cloudfoundry.reactor.client.v2.featureflags;

import org.cloudfoundry.client.v2.featureflags.FeatureFlagEntity;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagResponse;
import org.cloudfoundry.client.v2.featureflags.ListFeatureFlagsRequest;
import org.cloudfoundry.client.v2.featureflags.ListFeatureFlagsResponse;
import org.cloudfoundry.client.v2.featureflags.SetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.SetFeatureFlagResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorFeatureFlagsTest extends AbstractClientApiTest {

    private final ReactorFeatureFlags featureFlags = new ReactorFeatureFlags(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void getAppScaling() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/config/feature_flags/app_scaling")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/feature_flags/GET_app_scaling_flag_response.json")
                .build())
            .build());

        this.featureFlags
            .get(GetFeatureFlagRequest.builder()
                .name("app_scaling")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetFeatureFlagResponse.builder()
                .name("app_scaling")
                .enabled(true)
                .url("/v2/config/feature_flags/app_scaling")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getUserRoles() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/config/feature_flags/set_roles_by_username")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/feature_flags/GET_set_user_roles_flag_response.json")
                .build())
            .build());

        this.featureFlags
            .get(GetFeatureFlagRequest.builder()
                .name("set_roles_by_username")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetFeatureFlagResponse.builder()
                .name("set_roles_by_username")
                .enabled(true)
                .url("/v2/config/feature_flags/set_roles_by_username")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/config/feature_flags")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/feature_flags/GET_response.json")
                .build())
            .build());

        this.featureFlags
            .list(ListFeatureFlagsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListFeatureFlagsResponse.builder()
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
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void set() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/config/feature_flags/user_org_creation")
                .payload("fixtures/client/v2/feature_flags/PUT_user_org_creation_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/feature_flags/PUT_user_org_creation_response.json")
                .build())
            .build());

        this.featureFlags
            .set(SetFeatureFlagRequest.builder()
                .enabled(true)
                .name("user_org_creation")
                .build())
            .as(StepVerifier::create)
            .expectNext(SetFeatureFlagResponse.builder()
                .name("user_org_creation")
                .enabled(true)
                .url("/v2/config/feature_flags/user_org_creation")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
