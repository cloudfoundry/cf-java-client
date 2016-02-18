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

package org.cloudfoundry.spring.logging;

import org.cloudfoundry.logging.LogMessage;
import org.cloudfoundry.logging.RecentLogsRequest;
import org.cloudfoundry.spring.AbstractApiTest;
import org.cloudfoundry.util.test.TestSubscriber;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringRecentTest {

    public static final class Recent extends AbstractApiTest<RecentLogsRequest, LogMessage> {

        private static final MediaType MEDIA_TYPE = MediaType.parseMediaType("multipart/x-protobuf; boundary=90ad9060c87222ee30ddcffe751393a7c5734c48e070a623121abf82eb3c");

        private final SpringRecent recent = new SpringRecent(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected void assertions(TestSubscriber<LogMessage> testSubscriber, LogMessage expected) {
            testSubscriber
                .assertCount(14);
        }

        @Override
        protected RecentLogsRequest getInvalidRequest() {
            return RecentLogsRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/recent?app=test-application-id")
                .status(OK)
                .contentType(MEDIA_TYPE).responsePayload("logging/loggregator_response.bin");
        }

        @Override
        protected LogMessage getResponse() {
            return null;
        }

        @Override
        protected RecentLogsRequest getValidRequest() throws Exception {
            return RecentLogsRequest.builder()
                .applicationId("test-application-id")
                .build();
        }

        @Override
        protected Publisher<LogMessage> invoke(RecentLogsRequest request) {
            return this.recent.recent(request);
        }

    }

}
