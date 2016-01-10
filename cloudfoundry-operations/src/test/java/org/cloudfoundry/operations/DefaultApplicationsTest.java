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

package org.cloudfoundry.operations;

import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.Mono;

import static org.mockito.Mockito.when;

public final class DefaultApplicationsTest {

    public static final class List extends AbstractOperationsApiTest<Application> {

        private final Applications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE));

        @Before
        public void setUp() throws Exception {
            GetSpaceSummaryRequest request = GetSpaceSummaryRequest.builder()
                    .id(TEST_SPACE)
                    .build();

            GetSpaceSummaryResponse response = GetSpaceSummaryResponse.builder()
                    .id(TEST_SPACE)
                    .application(SpaceApplicationSummary.builder()
                            .spaceId(TEST_SPACE)
                            .diskQuota(1073741824)
                            .id("test-id-1")
                            .instances(2)
                            .memory(536870912)
                            .name("test-name-1")
                            .state("RUNNING")
                            .runningInstances(2)
                            .url("foo.com")
                            .build())
                    .application(SpaceApplicationSummary.builder()
                            .spaceId(TEST_SPACE)
                            .diskQuota(1073741824)
                            .id("test-id-2")
                            .instances(2)
                            .memory(536870912)
                            .name("test-name-2")
                            .state("RUNNING")
                            .runningInstances(2)
                            .url("bar.com")
                            .build())
                    .build();

            when(this.cloudFoundryClient.spaces().getSummary(request)).thenReturn(Mono.just(response));
        }

        @Override
        protected void assertions(TestSubscriber<Application> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(Application.builder()
                            .diskQuota(1073741824)
                            .id("test-id-1")
                            .instances(2)
                            .memoryLimit(536870912)
                            .name("test-name-1")
                            .requestedState("RUNNING")
                            .runningInstances(2)
                            .url("foo.com")
                            .build())
                    .assertEquals(Application.builder()
                            .diskQuota(1073741824)
                            .id("test-id-2")
                            .instances(2)
                            .memoryLimit(536870912)
                            .name("test-name-2")
                            .requestedState("RUNNING")
                            .runningInstances(2)
                            .url("bar.com")
                            .build());
        }

        @Override
        protected Publisher<Application> invoke() {
            return this.applications.list();
        }
    }

    public static final class ListNoSpace extends AbstractOperationsApiTest<Application> {

        private Applications applications = new DefaultApplications(this.cloudFoundryClient, MISSING_ID);

        @Override
        protected void assertions(TestSubscriber<Application> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalStateException.class);
        }

        @Override
        protected Publisher<Application> invoke() {
            return this.applications.list();
        }

    }

}
