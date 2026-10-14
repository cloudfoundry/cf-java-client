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

package org.cloudfoundry.client.v3;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.featureflags.GetFeatureFlagRequest;
import org.cloudfoundry.client.v3.featureflags.GetFeatureFlagResponse;
import org.cloudfoundry.client.v3.featureflags.ListFeatureFlagsRequest;
import org.cloudfoundry.client.v3.featureflags.UpdateFeatureFlagRequest;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

public class FeatureFlagsV3Test extends AbstractIntegrationTest {

    private static final String FLAG_NAME = "app_scaling";

    @Autowired private CloudFoundryClient cloudFoundryClient;

    @Test
    public void get() {
        this.cloudFoundryClient
                .featureFlagsV3()
                .get(GetFeatureFlagRequest.builder().name(FLAG_NAME).build())
                .map(GetFeatureFlagResponse::getName)
                .as(StepVerifier::create)
                .expectNext(FLAG_NAME)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        PaginationUtils.requestClientV3Resources(
                        page ->
                                this.cloudFoundryClient
                                        .featureFlagsV3()
                                        .list(ListFeatureFlagsRequest.builder().page(page).build()))
                .map(GetFeatureFlagResponse::getName)
                .collect(Collectors.toSet())
                .as(StepVerifier::create)
                .consumeNextWith(
                        names ->
                                assertThat(names)
                                        .containsAll(
                                                Arrays.asList(
                                                        "app_bits_upload",
                                                        "app_scaling",
                                                        "diego_docker",
                                                        "private_domain_creation",
                                                        "route_creation",
                                                        "service_instance_creation",
                                                        "set_roles_by_username",
                                                        "unset_roles_by_username",
                                                        "user_org_creation")))
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        this.cloudFoundryClient
                .featureFlagsV3()
                .get(GetFeatureFlagRequest.builder().name(FLAG_NAME).build())
                .flatMap(
                        original ->
                                this.cloudFoundryClient
                                        .featureFlagsV3()
                                        .update(
                                                UpdateFeatureFlagRequest.builder()
                                                        .name(FLAG_NAME)
                                                        .enabled(!original.getEnabled())
                                                        .build())
                                        .flatMap(
                                                toggled ->
                                                        this.cloudFoundryClient
                                                                .featureFlagsV3()
                                                                .update(
                                                                        UpdateFeatureFlagRequest
                                                                                .builder()
                                                                                .name(FLAG_NAME)
                                                                                .enabled(
                                                                                        original
                                                                                                .getEnabled())
                                                                                .build())
                                                                .thenReturn(
                                                                        toggled.getEnabled()
                                                                                != original
                                                                                        .getEnabled())))
                .as(StepVerifier::create)
                .expectNext(true)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }
}
