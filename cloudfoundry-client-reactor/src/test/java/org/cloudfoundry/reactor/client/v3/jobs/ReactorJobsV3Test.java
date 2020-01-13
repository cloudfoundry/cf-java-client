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

package org.cloudfoundry.reactor.client.v3.jobs;

import org.cloudfoundry.client.v3.Error;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.jobs.GetJobRequest;
import org.cloudfoundry.client.v3.jobs.GetJobResponse;
import org.cloudfoundry.client.v3.jobs.JobState;
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

public final class ReactorJobsV3Test extends AbstractClientApiTest {

    private final ReactorJobsV3 jobs = new ReactorJobsV3(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/jobs/test-job-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/jobs/GET_{id}_response.json")
                .build())
            .build());

        this.jobs
            .get(GetJobRequest.builder()
                .jobId("test-job-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetJobResponse.builder()
                .id("b19ae525-cbd3-4155-b156-dc0c2a431b4c")
                .createdAt("2016-10-19T20:25:04Z")
                .updatedAt("2016-11-08T16:41:26Z")
                .operation("app.delete")
                .state(JobState.FAILED)
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/jobs/b19ae525-cbd3-4155-b156-dc0c2a431b4c")
                    .build())
                .link("app", Link.builder()
                    .href("https://api.example.org/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396")
                    .build())
                .error(Error.builder()
                    .code(10008)
                    .title("CF-UnprocessableEntity")
                    .detail("something went wrong")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
