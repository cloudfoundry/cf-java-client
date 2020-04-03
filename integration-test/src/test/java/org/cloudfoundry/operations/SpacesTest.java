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

package org.cloudfoundry.operations;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.operations.spaces.CreateSpaceRequest;
import org.cloudfoundry.operations.spaces.GetSpaceRequest;
import org.cloudfoundry.operations.spaces.SpaceDetail;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public final class SpacesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Autowired
    private String organizationName;

    @Test
    public void create() {
        String spaceName = this.nameFactory.getSpaceName();

        this.cloudFoundryOperations.spaces()
            .create(CreateSpaceRequest.builder()
                .name(spaceName)
                .organization(this.organizationName)
                .build())
            .thenMany(this.cloudFoundryOperations.spaces()
                .list())
            .filter(spaceSummary -> spaceName.equals(spaceSummary.getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getWithURLReservedCharacterInName() {
        String spaceName = this.nameFactory.getSpaceName() + "+test";

        this.cloudFoundryOperations.spaces()
            .create(CreateSpaceRequest.builder()
                .name(spaceName)
                .organization(this.organizationName)
                .build())
            .then(this.cloudFoundryOperations.spaces()
                .get(GetSpaceRequest.builder()
                    .name(spaceName)
                    .build()))
            .map(SpaceDetail::getName)
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        this.cloudFoundryOperations.spaces()
            .list()
            .count()
            .as(StepVerifier::create)
            .consumeNextWith(count -> assertThat(count).isGreaterThan(0))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

}
