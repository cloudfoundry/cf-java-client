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

package org.cloudfoundry.client.spring.v2.info;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.reactivestreams.Publisher;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringInfoTest {

    public static final class Get extends AbstractApiTest<GetInfoRequest, GetInfoResponse> {

        private final SpringInfo info = new SpringInfo(this.restTemplate, this.root);

        @Override
        protected GetInfoRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v2/info")
                    .status(OK)
                    .responsePayload("v2/info/GET_response.json");
        }

        @Override
        protected GetInfoResponse getResponse() {
            return GetInfoResponse.builder()
                    .name("vcap")
                    .buildNumber("2222")
                    .support("http://support.cloudfoundry.com")
                    .version(2)
                    .description("Cloud Foundry sponsored by Pivotal")
                    .authorizationEndpoint("http://localhost:8080/uaa")
                    .tokenEndpoint("http://localhost:8080/uaa")
                    .apiVersion("2.44.0")
                    .applicationSshEndpoint("ssh.system.domain.example.com:2222")
                    .applicationSshHostKeyFingerprint("47:0d:d1:c8:c3:3d:0a:36:d1:49:2f:f2:90:27:31:d0")
                    .routingEndpoint("http://localhost:3000")
                    .loggingEndpoint("ws://loggregator.vcap.me:80")
                    .build();
        }

        @Override
        protected GetInfoRequest getValidRequest() {
            return GetInfoRequest.builder()
                    .build();
        }

        @Override
        protected Publisher<GetInfoResponse> invoke(GetInfoRequest request) {
            return this.info.get(request);
        }

    }

}
