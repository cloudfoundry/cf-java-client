/*
 * Copyright 2026 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.featureflags;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.time.Duration;
import java.util.Collections;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.featureflags.GetFeatureFlagRequest;
import org.cloudfoundry.client.v3.featureflags.GetFeatureFlagResponse;
import org.cloudfoundry.client.v3.featureflags.ListFeatureFlagsRequest;
import org.cloudfoundry.client.v3.featureflags.ListFeatureFlagsResponse;
import org.cloudfoundry.client.v3.featureflags.UpdateFeatureFlagRequest;
import org.cloudfoundry.client.v3.featureflags.UpdateFeatureFlagResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

final class ReactorFeatureFlagsV3Test extends AbstractClientApiTest {

    private static final String UPDATED_AT = "2020-03-10T15:49:32Z";

    private final ReactorFeatureFlagsV3 featureFlags =
            new ReactorFeatureFlagsV3(
                    CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    void get() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(GET)
                                        .path("/feature_flags/app_scaling")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/featureflags/GET_{feature_flag_name}_response.json")
                                        .build())
                        .build());

        this.featureFlags
                .get(GetFeatureFlagRequest.builder().name("app_scaling").build())
                .as(StepVerifier::create)
                .expectNext(
                        GetFeatureFlagResponse.builder()
                                .name("app_scaling")
                                .enabled(true)
                                .updatedAt(UPDATED_AT)
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void list() {
        mockRequest(
                InteractionContext.builder()
                        .request(TestRequest.builder().method(GET).path("/feature_flags").build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/featureflags/GET_response.json")
                                        .build())
                        .build());

        this.featureFlags
                .list(ListFeatureFlagsRequest.builder().build())
                .as(StepVerifier::create)
                .expectNext(
                        ListFeatureFlagsResponse.builder()
                                .pagination(
                                        Pagination.builder()
                                                .totalResults(2)
                                                .first(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/feature_flags?page=1&per_page=50")
                                                                .build())
                                                .last(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/feature_flags?page=1&per_page=50")
                                                                .build())
                                                .build())
                                .resource(
                                        GetFeatureFlagResponse.builder()
                                                .name("app_scaling")
                                                .enabled(true)
                                                .updatedAt(UPDATED_AT)
                                                .build())
                                .resource(
                                        GetFeatureFlagResponse.builder()
                                                .name("user_org_creation")
                                                .enabled(false)
                                                .customErrorMessage("This feature is disabled")
                                                .updatedAt(UPDATED_AT)
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void update() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(PATCH)
                                        .path("/feature_flags/app_scaling")
                                        .payload(
                                                "fixtures/client/v3/featureflags/PATCH_{feature_flag_name}_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/featureflags/PATCH_{feature_flag_name}_response.json")
                                        .build())
                        .build());

        this.featureFlags
                .update(
                        UpdateFeatureFlagRequest.builder()
                                .name("app_scaling")
                                .enabled(false)
                                .customErrorMessage("This feature is disabled")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        UpdateFeatureFlagResponse.builder()
                                .name("app_scaling")
                                .enabled(false)
                                .customErrorMessage("This feature is disabled")
                                .updatedAt(UPDATED_AT)
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }
}
