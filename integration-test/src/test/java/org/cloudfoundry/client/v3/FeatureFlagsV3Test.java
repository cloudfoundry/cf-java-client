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
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.featureflags.GetFeatureFlagRequest;
import org.cloudfoundry.client.v3.featureflags.GetFeatureFlagResponse;
import org.cloudfoundry.client.v3.featureflags.ListFeatureFlagsRequest;
import org.cloudfoundry.client.v3.featureflags.UpdateFeatureFlagRequest;
import org.cloudfoundry.client.v3.featureflags.UpdateFeatureFlagResponse;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public final class FeatureFlagsV3Test extends AbstractIntegrationTest {

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
    public void updateCustomErrorMessage() {
        String newMessage = "Integration test custom error message";

        withRestoredFeatureFlag(
                        original ->
                                update(
                                        UpdateFeatureFlagRequest.builder()
                                                .name(FLAG_NAME)
                                                .customErrorMessage(newMessage)
                                                .build()))
                .map(UpdateFeatureFlagResponse::getCustomErrorMessage)
                .as(StepVerifier::create)
                .expectNext(newMessage)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void updateCustomErrorMessageToNull() {
        String tempMessage = "Integration test custom error message";

        withRestoredFeatureFlag(
                        original ->
                                update(
                                                UpdateFeatureFlagRequest.builder()
                                                        .name(FLAG_NAME)
                                                        .customErrorMessage(tempMessage)
                                                        .build())
                                        .then(
                                                update(
                                                        UpdateFeatureFlagRequest.builder()
                                                                .name(FLAG_NAME)
                                                                .clearCustomErrorMessage(true)
                                                                .build())))
                .as(StepVerifier::create)
                .consumeNextWith(cleared -> assertThat(cleared.getCustomErrorMessage()).isNull())
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        withRestoredFeatureFlag(
                        original ->
                                update(
                                                UpdateFeatureFlagRequest.builder()
                                                        .name(FLAG_NAME)
                                                        .enabled(!original.getEnabled())
                                                        .build())
                                        .map(
                                                toggled ->
                                                        !toggled.getEnabled()
                                                                .equals(original.getEnabled())))
                .as(StepVerifier::create)
                .expectNext(true)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void updateMessage() {
        String newMessage = "Integration test updateMessage";

        withRestoredFeatureFlag(
                        original ->
                                update(
                                        UpdateFeatureFlagRequest.builder()
                                                .name(FLAG_NAME)
                                                .customErrorMessage(newMessage)
                                                .build()))
                .map(UpdateFeatureFlagResponse::getCustomErrorMessage)
                .as(StepVerifier::create)
                .expectNext(newMessage)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void updateNothing() {
        withRestoredFeatureFlag(
                        original ->
                                update(UpdateFeatureFlagRequest.builder().name(FLAG_NAME).build())
                                        .map(
                                                unchanged ->
                                                        unchanged
                                                                        .getEnabled()
                                                                        .equals(
                                                                                original
                                                                                        .getEnabled())
                                                                && Objects.equals(
                                                                        unchanged
                                                                                .getCustomErrorMessage(),
                                                                        original
                                                                                .getCustomErrorMessage())))
                .as(StepVerifier::create)
                .expectNext(true)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    private Mono<UpdateFeatureFlagResponse> update(UpdateFeatureFlagRequest request) {
        return this.cloudFoundryClient.featureFlagsV3().update(request);
    }

    /**
     * Runs {@code test} with the current state of {@link #FLAG_NAME} and restores that state
     * afterwards, also when {@code test} fails or is cancelled. The restore is part of the returned
     * {@link Mono}, so it never blocks a reactor thread.
     */
    private <T> Mono<T> withRestoredFeatureFlag(Function<GetFeatureFlagResponse, Mono<T>> test) {
        return Mono.usingWhen(
                this.cloudFoundryClient
                        .featureFlagsV3()
                        .get(GetFeatureFlagRequest.builder().name(FLAG_NAME).build()),
                test,
                this::restore,
                (original, error) -> restore(original),
                this::restore);
    }

    private Mono<Void> restore(GetFeatureFlagResponse original) {
        UpdateFeatureFlagRequest.Builder request =
                UpdateFeatureFlagRequest.builder().name(FLAG_NAME).enabled(original.getEnabled());
        if (original.getCustomErrorMessage() != null) {
            request.customErrorMessage(original.getCustomErrorMessage());
        } else {
            request.clearCustomErrorMessage(true);
        }
        return update(request.build()).then();
    }
}
