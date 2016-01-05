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

package org.cloudfoundry.client.spring.v2.job;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.job.GetJobRequest;
import org.cloudfoundry.client.v2.job.GetJobResponse;
import org.cloudfoundry.client.v2.job.JobEntity;
import org.reactivestreams.Publisher;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringJobsTest {

    public static final class Get extends AbstractApiTest<GetJobRequest, GetJobResponse> {

        private final SpringJobs jobs = new SpringJobs(this.restTemplate, this.root, this.processorGroup);

        @Override
        protected GetJobRequest getInvalidRequest() {
            return GetJobRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v2/jobs/test-id")
                    .status(OK)
                    .responsePayload("v2/jobs/GET_{id}_response.json");
        }

        @Override
        protected GetJobResponse getResponse() {
            return GetJobResponse.builder()
                    .metadata(Resource.Metadata.builder()
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
                    .id("test-id")
                    .build();
        }

        @Override
        protected Publisher<GetJobResponse> invoke(GetJobRequest request) {
            return this.jobs.get(request);
        }

    }

}
