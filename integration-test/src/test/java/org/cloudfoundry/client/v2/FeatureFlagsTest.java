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
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void getEach() {
        Flux
            .fromIterable(coreFeatureFlagNameList)
            .flatMap(flagName -> this.cloudFoundryClient.featureFlags()
                .get(GetFeatureFlagRequest.builder()
                    .name(flagName)
                    .build())
                .map(response -> Tuples.of(flagName, response)))
            .collectList()
            .subscribe(this.<List<Tuple2<String, GetFeatureFlagResponse>>>testSubscriber()
                .expectThat(getFlagList -> {
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
                .expectThat(response -> {
                    Set<String> returnedFlagSet = flagNameSetFrom(response.getFeatureFlags());

                    assertTrue(String.format("feature flags listed (%s) does not include core set (%s)", returnedFlagSet, coreFeatureFlagNameList),
                        returnedFlagSet.containsAll(coreFeatureFlagNameList));
                }));

    }

    @Test
    public void setAndResetEach() {
        Flux
            .fromIterable(coreFeatureFlagNameList)
            .flatMap(flagName -> this.cloudFoundryClient.featureFlags()
                .get(GetFeatureFlagRequest.builder()
                    .name(flagName)
                    .build())
                .then(getResponse -> Mono.when(
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
            .collectList()
            .subscribe(this.<List<Tuple3<GetFeatureFlagResponse, SetFeatureFlagResponse, SetFeatureFlagResponse>>>testSubscriber()
                .expectThat(responsesList -> {
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
