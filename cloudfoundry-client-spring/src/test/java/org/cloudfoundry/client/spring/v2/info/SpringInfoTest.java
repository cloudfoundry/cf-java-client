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

import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.junit.Test;
import reactor.rx.Streams;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringInfoTest extends AbstractRestTest {

    private final SpringInfo info = new SpringInfo(this.restTemplate, this.root);

    @Test
    public void get() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/info")
                .status(OK)
                .responsePayload("v2/info/GET_response.json"));

        GetInfoResponse expected = GetInfoResponse.builder()
                .apiVersion("2.33.0")
                .appSshEndpoint("ssh.run.pivotal.io:2222")
                .authorizationEndpoint("https://login.run.pivotal.io")
                .buildNumber("2222")
                .description("Cloud Foundry sponsored by Pivotal")
                .dopplerLoggingEndpoint("wss://doppler.run.pivotal.io:443")
                .loggingEndpoint("wss://loggregator.run.pivotal.io:4443")
                .name("vcap")
                .support("http://support.cloudfoundry.com")
                .tokenEndpoint("https://uaa.run.pivotal.io")
                .version(2)
                .build();

        GetInfoResponse actual = Streams.wrap(this.info.get()).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void getError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/info")
                .status(OK)
                .errorResponse());

        Streams.wrap(this.info.get()).next().get();
    }

}
