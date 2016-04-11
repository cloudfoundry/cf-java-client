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

package org.cloudfoundry.operations.buildpacks;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.buildpacks.BuildpackResource;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksResponse;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.cloudfoundry.util.test.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultBuildpacksTest {

    private static void requestBuildpacks(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.buildpacks()
            .list(fillPage(ListBuildpacksRequest.builder())
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListBuildpacksResponse.builder())
                    .resource(fill(BuildpackResource.builder(), "buildpack-")
                        .build())
                    .build()));
    }

    public static final class List extends AbstractOperationsApiTest<BuildpackSummary> {

        private final DefaultBuildpacks buildpacks = new DefaultBuildpacks(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestBuildpacks(this.cloudFoundryClient);
        }

        @Override
        protected void assertions(TestSubscriber<BuildpackSummary> testSubscriber) {
            testSubscriber
                .assertEquals(fill(BuildpackSummary.builder(), "buildpack-")
                    .build());
        }

        @Override
        protected Publisher<BuildpackSummary> invoke() {
            return this.buildpacks.list();
        }
    }

}
