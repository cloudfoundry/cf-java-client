/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.featureflags.FeatureFlagEntity;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.ListFeatureFlagsRequest;
import org.cloudfoundry.client.v2.featureflags.SetFeatureFlagRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class FeatureFlagsTest extends AbstractIntegrationTest {

    private static final List<String> coreFeatureFlagNameList = Arrays.asList(
        "app_bits_upload",
        "app_scaling",
        "diego_docker",
        "private_domain_creation",
        "route_creation",
        "service_instance_creation",
        "set_roles_by_username",
        "unset_roles_by_username",
        "user_org_creation"
    );

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Test
    public void getEach() throws TimeoutException, InterruptedException {
        Flux
            .fromIterable(coreFeatureFlagNameList)
            .flatMap(flagName -> this.cloudFoundryClient.featureFlags()
                .get(GetFeatureFlagRequest.builder()
                    .name(flagName)
                    .build())
                .map(response -> Tuples.of(flagName, response)))
            .collectList()
            .as(StepVerifier::create)
            .consumeNextWith(list -> list.forEach(consumer((name, entity) -> assertThat(entity.getName()).isEqualTo(name))))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        this.cloudFoundryClient.featureFlags()
            .list(ListFeatureFlagsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .consumeNextWith(response -> {
                Set<String> returnedFlagSet = flagNameSetFrom(response.getFeatureFlags());
                assertThat(returnedFlagSet).containsAll(coreFeatureFlagNameList);
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void setAndResetEach() throws TimeoutException, InterruptedException {
        Flux.fromIterable(coreFeatureFlagNameList)
            .flatMap(flagName -> this.cloudFoundryClient.featureFlags()
                .get(GetFeatureFlagRequest.builder()
                    .name(flagName)
                    .build())
                .flatMap(getResponse -> Mono.when(
                    Mono.just(getResponse),
                    this.cloudFoundryClient.featureFlags()
                        .set(SetFeatureFlagRequest.builder()
                            .name(getResponse.getName())
                            .enabled(!getResponse.getEnabled())
                            .build())
                ))
                .flatMap(function((getResponse, setResponse) -> Mono
                    .when(
                        Mono.just(getResponse),
                        Mono.just(setResponse),
                        this.cloudFoundryClient.featureFlags()
                            .set(SetFeatureFlagRequest.builder()
                                .name(getResponse.getName())
                                .enabled(getResponse.getEnabled())
                                .build())
                    ))))
            .collectList()
            .as(StepVerifier::create)
            .consumeNextWith(list -> list.forEach(consumer((getResponse, setResponse, resetResponse) -> {
                assertThat(setResponse.getEnabled()).isNotEqualTo(getResponse.getEnabled());
                assertThat(resetResponse.getEnabled()).isEqualTo(getResponse.getEnabled());
            })))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Set<String> flagNameSetFrom(List<FeatureFlagEntity> listFlags) {
        return listFlags
            .stream()
            .map(FeatureFlagEntity::getName)
            .collect(Collectors.toSet());
    }

}
