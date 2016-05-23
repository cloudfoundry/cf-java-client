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

package org.cloudfoundry.reactor.client.v2.jobs;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.jobs.GetJobRequest;
import org.cloudfoundry.client.v2.jobs.GetJobResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorJobsTest {

    public static final class Get extends AbstractClientApiTest<GetJobRequest, GetJobResponse> {

        private final ReactorJobs jobs = new ReactorJobs(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/jobs/test-job-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/jobs/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetJobRequest getInvalidRequest() {
            return GetJobRequest.builder()
                .build();
        }

        @Override
        protected GetJobResponse getResponse() {
            return GetJobResponse.builder()
                .metadata(Metadata.builder()
                    .id("e86ffe00-a243-48f7-be05-8f1f41bee864")
                    .createdAt("2015-11-30T23:38:44Z")
                    .url("/v2/jobs/e86ffe00-a243-48f7-be05-8f1f41bee864")
                    .build())
                .entity(JobEntity.builder()
                    .id("e86ffe00-a243-48f7-be05-8f1f41bee864")
                    .status("failed")
                    .error("Use of entity>error is deprecated in favor of entity>error_details.")
                    .errorDetails(JobEntity.ErrorDetails.builder()
                        .errorCode("UnknownError")
                        .description("An unknown error occurred.")
                        .code(10001)
                        .build())
                    .build())
                .build();
        }

        @Override
        protected GetJobRequest getValidRequest() throws Exception {
            return GetJobRequest.builder()
                .jobId("test-job-id")
                .build();
        }

        @Override
        protected Mono<GetJobResponse> invoke(GetJobRequest request) {
            return this.jobs.get(request);
        }

    }

}
