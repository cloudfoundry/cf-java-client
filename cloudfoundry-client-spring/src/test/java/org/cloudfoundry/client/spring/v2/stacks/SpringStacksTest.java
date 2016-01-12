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

package org.cloudfoundry.client.spring.v2.stacks;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.Resource.Metadata;
import org.cloudfoundry.client.v2.stacks.GetStackRequest;
import org.cloudfoundry.client.v2.stacks.GetStackResponse;
import org.cloudfoundry.client.v2.stacks.StackEntity;
import org.reactivestreams.Publisher;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringStacksTest {

    public static final class Get extends AbstractApiTest<GetStackRequest, GetStackResponse> {

        private final SpringStacks stacks = new SpringStacks(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetStackRequest getInvalidRequest() {
            return GetStackRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v2/stacks/test-id")
                    .status(OK)
                    .responsePayload("v2/stacks/GET_{id}_response.json");
        }

        @Override
        protected GetStackResponse getResponse() {
            return GetStackResponse.builder()
                    .metadata(Metadata.builder()
                            .id("fe4999cf-a207-4d40-bb03-f4bbf697edac")
                            .url("/v2/stacks/fe4999cf-a207-4d40-bb03-f4bbf697edac")
                            .createdAt("2015-12-22T18:27:59Z")
                            .build())
                    .entity(StackEntity.builder()
                            .name("cflinuxfs2")
                            .description("cflinuxfs2")
                            .build())
                    .build();
        }

        @Override
        protected GetStackRequest getValidRequest() throws Exception {
            return GetStackRequest.builder()
                    .id("test-id")
                    .build();
        }

        @Override
        protected Publisher<GetStackResponse> invoke(GetStackRequest request) {
            return this.stacks.get(request);
        }

    }

}
