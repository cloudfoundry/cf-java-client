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
import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.junit.Test;
import reactor.rx.Streams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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

        GetInfoResponse response = Streams.wrap(this.info.get()).next().get();

        assertEquals("2.33.0", response.getApiVersion());
        assertEquals("ssh.run.pivotal.io:2222", response.getAppSshEndpoint());
        assertNull(response.getAppSshHostKeyFingerprint());
        assertEquals("https://login.run.pivotal.io", response.getAuthorizationEndpoint());
        assertEquals("2222", response.getBuild());
        assertEquals("Cloud Foundry sponsored by Pivotal", response.getDescription());
        assertEquals("wss://doppler.run.pivotal.io:443", response.getDopplerLoggingEndpoint());
        assertEquals("wss://loggregator.run.pivotal.io:4443", response.getLoggingEndpoint());
        assertEquals("vcap", response.getName());
        assertNull(response.getMinCliVersion());
        assertNull(response.getMinRecommendedCliVersion());
        assertEquals("http://support.cloudfoundry.com", response.getSupport());
        assertEquals("https://uaa.run.pivotal.io", response.getTokenEndpoint());
        assertNull(response.getUser());
        assertEquals(Integer.valueOf(2), response.getVersion());

        verify();
    }

}
