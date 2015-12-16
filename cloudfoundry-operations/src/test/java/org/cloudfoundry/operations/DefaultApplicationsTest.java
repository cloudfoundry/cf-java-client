/*
 * Copyright 2013-2015 the original author or authors.
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

import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.junit.Test;
import reactor.Publishers;
import reactor.rx.Streams;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public final class DefaultApplicationsTest extends AbstractOperationsTest {

    private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, TEST_SPACE);

    private DefaultApplications applicationsNoSpace = new DefaultApplications(this.cloudFoundryClient, null);

    @Test
    public void list() {

        SpaceApplicationSummary spaceApplicationSummaryOne = SpaceApplicationSummary.builder()
                .spaceId(TEST_SPACE)
                .diskQuota(1024)
                .id("test-id-1")
                .instances(2)
                .memory(512)
                .name("test-name-1")
                .state("RUNNING")
                .runningInstances(2)
                .urls(Collections.singletonList("foo.com"))
                .build();

        SpaceApplicationSummary spaceApplicationSummaryTwo = SpaceApplicationSummary.builder()
                .spaceId(TEST_SPACE)
                .diskQuota(1024)
                .id("test-id-2")
                .instances(2)
                .memory(512)
                .name("test-name-2")
                .state("RUNNING")
                .runningInstances(2)
                .urls(Collections.singletonList("bar.com"))
                .build();

        GetSpaceSummaryResponse response = GetSpaceSummaryResponse.builder()
                .id(TEST_SPACE)
                .application(spaceApplicationSummaryOne)
                .application(spaceApplicationSummaryTwo)
                .build();

        when(this.cloudFoundryClient.spaces().getSummary(
                GetSpaceSummaryRequest.builder()
                        .id(TEST_SPACE)
                        .build()))
                .thenReturn(Publishers.just(response));

        List<Application> expected = Arrays.asList(
                Application.builder()
                        .disk(1024)
                        .id("test-id-1")
                        .instances(2)
                        .memory(512)
                        .name("test-name-1")
                        .requestedState("RUNNING")
                        .runningInstances(2)
                        .urls(Collections.singletonList("foo.com"))
                        .build(),
                Application.builder()
                        .disk(1024)
                        .id("test-id-2")
                        .instances(2)
                        .memory(512)
                        .name("test-name-2")
                        .requestedState("RUNNING")
                        .runningInstances(2)
                        .urls(Collections.singletonList("bar.com"))
                        .build()
        );

        List<Application> actual = Streams.wrap(this.applications.list()).toList().poll();

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void listNoSpace() {
        this.applicationsNoSpace.list();
    }

}
