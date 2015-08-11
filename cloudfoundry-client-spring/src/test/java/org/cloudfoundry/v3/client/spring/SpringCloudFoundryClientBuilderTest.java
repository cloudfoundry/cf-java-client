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

package org.cloudfoundry.v3.client.spring;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public final class SpringCloudFoundryClientBuilderTest {

    private final RestTemplate restTemplate = new RestTemplate();

    private final MockRestServiceServer mockServer = MockRestServiceServer.createServer(this.restTemplate);

    private final SpringCloudFoundryClientBuilder builder = new SpringCloudFoundryClientBuilder(this.restTemplate);

    @Test
    public void test() {
        this.mockServer
                .expect(requestTo("https://api.test.host/info"))
                .andRespond(withSuccess(new ClassPathResource("info.json"), MediaType.APPLICATION_JSON));

        SpringCloudFoundryClient client = (SpringCloudFoundryClient) this.builder
                .withApi("api.test.host")
                .withCredentials("test-username", "test-password")
                .build();

        OAuth2RestTemplate restTemplate = (OAuth2RestTemplate) client.getRestOperations();
        OAuth2ProtectedResourceDetails details = restTemplate.getResource();

        assertEquals("cf", details.getClientId());
        assertEquals("", details.getClientSecret());
        assertEquals("https://uaa.test.host/oauth/token", details.getAccessTokenUri());
        this.mockServer.verify();
    }
}
