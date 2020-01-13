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

package org.cloudfoundry.reactor.client.v2.jobs;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.jobs.ErrorDetails;
import org.cloudfoundry.client.v2.jobs.GetJobRequest;
import org.cloudfoundry.client.v2.jobs.GetJobResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorJobsTest extends AbstractClientApiTest {

    private final ReactorJobs jobs = new ReactorJobs(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/jobs/test-job-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/jobs/GET_{id}_response.json")
                .build())
            .build());

        this.jobs
            .get(GetJobRequest.builder()
                .jobId("test-job-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetJobResponse.builder()
                .metadata(Metadata.builder()
                    .id("e86ffe00-a243-48f7-be05-8f1f41bee864")
                    .createdAt("2015-11-30T23:38:44Z")
                    .url("/v2/jobs/e86ffe00-a243-48f7-be05-8f1f41bee864")
                    .build())
                .entity(JobEntity.builder()
                    .id("e86ffe00-a243-48f7-be05-8f1f41bee864")
                    .status("failed")
                    .error("Use of entity>error is deprecated in favor of entity>error_details.")
                    .errorDetails(ErrorDetails.builder()
                        .errorCode("UnknownError")
                        .description("An unknown error occurred.")
                        .code(10001)
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
