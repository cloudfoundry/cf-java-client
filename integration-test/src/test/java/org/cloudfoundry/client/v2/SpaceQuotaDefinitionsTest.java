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
import org.cloudfoundry.client.v2.spacequotadefinitions.ListSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.fail;

public final class SpaceQuotaDefinitionsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Ignore("TODO: finish story https://www.pivotaltracker.com/story/show/101527366")
    @Test
    public void create() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101527366");
    }

    @Ignore("TODO: finish story https://www.pivotaltracker.com/story/show/101527368")
    @Test
    public void delete() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101527368");
    }

    @Ignore("TODO: finish story https://www.pivotaltracker.com/story/show/101527366")
    @Test
    public void get() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101527366");
    }

    @Ignore("TODO: finish story https://www.pivotaltracker.com/story/show/101527366")
    @Test
    public void list() throws TimeoutException, InterruptedException {
        PaginationUtils
            .requestClientV2Resources(page -> this.cloudFoundryClient.spaceQuotaDefinitions()
                .list(ListSpaceQuotaDefinitionsRequest.builder()
                    .page(page)
                    .build()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Ignore("TODO: finish story https://www.pivotaltracker.com/story/show/101527372")
    @Test
    public void listSpaces() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101527372");
    }

    @Ignore("TODO: finish story https://www.pivotaltracker.com/story/show/101527372")
    @Test
    public void listSpacesFilterByApplicationId() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101527372");
    }

    @Ignore("TODO: finish story https://www.pivotaltracker.com/story/show/101527372")
    @Test
    public void listSpacesFilterByDeveloperId() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101527372");
    }

    @Ignore("TODO: finish story https://www.pivotaltracker.com/story/show/101527372")
    @Test
    public void listSpacesFilterByName() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101527372");
    }

    @Ignore("TODO: finish story https://www.pivotaltracker.com/story/show/101527372")
    @Test
    public void listSpacesFilterByOrganizationId() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101527372");
    }

    @Ignore("TODO: finish story https://www.pivotaltracker.com/story/show/101527372")
    @Test
    public void listSpacesNotFound() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101527372");
    }

    @Ignore("TODO: finish story https://www.pivotaltracker.com/story/show/101527378")
    @Test
    public void update() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101527378");
    }

}
