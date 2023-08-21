/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.operations.v3.routes;

import org.cloudfoundry.client.v3.domains.CheckReservedRoutesRequest;
import org.cloudfoundry.client.v3.domains.CheckReservedRoutesResponse;
import org.cloudfoundry.client.v3.spaces.DeleteUnmappedRoutesRequest;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.cloudfoundry.operations.v3.mapper.MapperUtils;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.cloudfoundry.client.v3.jobs.GetJobRequest;
import org.cloudfoundry.client.v3.jobs.GetJobResponse;
import org.cloudfoundry.client.v3.jobs.JobState;

import java.time.Duration;

import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import org.mockito.MockedStatic;

public final class DeleteOrphanedRoutesTest extends AbstractOperationsTest {

        private final DefaultRoutes routes = new DefaultRoutes(Mono.just(this.cloudFoundryClient),
                        Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));
        private final String TEST_JOB_ID = "test-job-id";

        @Before
        public void settup() {
 
                when(cloudFoundryClient.spacesV3()
                                .deleteUnmappedRoutes(DeleteUnmappedRoutesRequest.builder()
                                                .spaceId(TEST_SPACE_ID)
                                                .build()))
                                .thenReturn(Mono.just(TEST_JOB_ID));
                when(cloudFoundryClient.jobsV3().get(GetJobRequest.builder().jobId(TEST_JOB_ID).build())).thenReturn(
                                Mono.just(fill(GetJobResponse.builder()).state(JobState.COMPLETE).build()));

        }

        @Test
        public void deleteOrphanedRoutes() {

                this.routes.deleteOrphanedRoutes(DeleteOrphanedRoutesRequest.builder().build())
                                .as(StepVerifier::create)
                                .expectComplete()
                                .verify(Duration.ofSeconds(5));
        }

}
