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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.featureflags.FeatureFlagEntity;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagResponse;
import org.cloudfoundry.client.v2.featureflags.ListFeatureFlagsRequest;
import org.cloudfoundry.client.v2.featureflags.ListFeatureFlagsResponse;
import org.cloudfoundry.client.v2.featureflags.SetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.SetFeatureFlagResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.tuple.Tuple2;
import reactor.core.tuple.Tuple3;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class FeatureFlagsTest extends AbstractIntegrationTest {

    private static final List<String> featureFlagNameList = Arrays.asList(
        "route_creation",
        "user_org_creation",
        "unset_roles_by_username",
        "diego_docker",
        "service_instance_creation",
        "app_scaling",
        "app_bits_upload",
        "set_roles_by_username",
        "task_creation",
        "private_domain_creation"
    );

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Test
    public void getEach() {
        Flux
            .fromIterable(featureFlagNameList)
            .flatMap(flagName -> this.cloudFoundryClient.featureFlags()
                .get(GetFeatureFlagRequest.builder()
                    .name(flagName)
                    .build())
                .map(response -> Tuple2.of(flagName, response)))
            .toList()
            .subscribe(this.<List<Tuple2<String, GetFeatureFlagResponse>>>testSubscriber()
                .assertThat(getFlagList -> {
                    for (Tuple2<String, GetFeatureFlagResponse> tuple : getFlagList) {
                        String flagName = tuple.t1;
                        GetFeatureFlagResponse getResponse = tuple.t2;

                        assertEquals("feature flag entity has incorrect name", flagName, getResponse.getName());
                    }
                }));
    }

    @Test
    public void list() {

        this.cloudFoundryClient.featureFlags()
            .list(ListFeatureFlagsRequest.builder()
                .build())
            .subscribe(this.<ListFeatureFlagsResponse>testSubscriber()
                .assertThat(response -> assertEquals("feature flag list incorrect", new HashSet<>(featureFlagNameList), flagNameSetFrom(response))));

    }

    @Test
    public void setAndResetEach() {
        Flux
            .fromIterable(featureFlagNameList)
            .flatMap(flagName -> this.cloudFoundryClient.featureFlags()
                .get(GetFeatureFlagRequest.builder()
                    .name(flagName)
                    .build())
                .then(getResponse -> Mono
                    .when(
                        Mono.just(getResponse),
                        this.cloudFoundryClient.featureFlags()
                            .set(SetFeatureFlagRequest.builder()
                                .name(getResponse.getName())
                                .enabled(!getResponse.getEnabled())
                                .build())
                    ))
                .then(function((getResponse, setResponse) -> Mono
                    .when(
                        Mono.just(getResponse),
                        Mono.just(setResponse),
                        this.cloudFoundryClient.featureFlags()
                            .set(SetFeatureFlagRequest.builder()
                                .name(getResponse.getName())
                                .enabled(getResponse.getEnabled())
                                .build())
                    ))))
            .toList()
            .subscribe(this.<List<Tuple3<GetFeatureFlagResponse, SetFeatureFlagResponse, SetFeatureFlagResponse>>>testSubscriber()
                .assertThat(responsesList -> {
                    for (Tuple3<GetFeatureFlagResponse, SetFeatureFlagResponse, SetFeatureFlagResponse> responses : responsesList) {
                        GetFeatureFlagResponse getResponse = responses.t1;
                        SetFeatureFlagResponse setResponse = responses.t2;
                        SetFeatureFlagResponse resetResponse = responses.t3;

                        String flagName = getResponse.getName();
                        assertTrue(String.format("feature flag %s was %s initially and was set to %s instead of %s during test", flagName, getResponse.getEnabled(), setResponse.getEnabled(),
                            !getResponse.getEnabled()),
                            getResponse.getEnabled().equals(!setResponse.getEnabled()));
                        assertTrue(String.format("feature flag %s was %s but was reset to %s after testing instead of %s", flagName, getResponse.getEnabled(), resetResponse.getEnabled(), getResponse
                                .getEnabled()),
                            getResponse.getEnabled().equals(resetResponse.getEnabled()));
                    }
                }));
    }

    private static Set<String> flagNameSetFrom(List<FeatureFlagEntity> listFlags) {
        return listFlags
            .stream()
            .map(FeatureFlagEntity::getName)
            .collect(Collectors.toSet());
    }
}
