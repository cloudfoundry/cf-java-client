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

package org.cloudfoundry.client.spring;

import org.cloudfoundry.client.spring.util.SslCertificateTruster;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public final class SpringCloudFoundryClientBuilderTest extends AbstractRestTest {

    private final SslCertificateTruster sslCertificateTruster = mock(SslCertificateTruster.class);

    private final SpringCloudFoundryClientBuilder builder = new SpringCloudFoundryClientBuilder(this.restTemplate,
            this.sslCertificateTruster);

    @Test
    public void test() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/info"))
                .andRespond(withSuccess(new ClassPathResource("info_GET_response.json"), MediaType.APPLICATION_JSON));

        SpringCloudFoundryClient client = this.builder
                .withApi("api.run.pivotal.io")
                .withClient("test-client-id", "test-client-secret")
                .withCredentials("test-username", "test-password")
                .build();

        OAuth2RestTemplate restTemplate = (OAuth2RestTemplate) client.getRestOperations();
        OAuth2ProtectedResourceDetails details = restTemplate.getResource();

        assertEquals("test-client-id", details.getClientId());
        assertEquals("test-client-secret", details.getClientSecret());
        assertEquals("https://uaa.run.pivotal.io/oauth/token", details.getAccessTokenUri());
        this.mockServer.verify();
    }

    @Test
    public void skipSslValidationTrue() throws GeneralSecurityException, IOException {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/info"))
                .andRespond(withSuccess(new ClassPathResource("info_GET_response.json"), MediaType.APPLICATION_JSON));

        this.builder
                .withApi("api.run.pivotal.io")
                .withCredentials("test-username", "test-password")
                .withSkipSslValidation(true)
                .build();

        verify(this.sslCertificateTruster).trust("api.run.pivotal.io", 443, 5, SECONDS);
    }

    @Test
    public void skipSslValidationFalse() throws GeneralSecurityException, IOException {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/info"))
                .andRespond(withSuccess(new ClassPathResource("info_GET_response.json"), MediaType.APPLICATION_JSON));

        this.builder
                .withApi("api.run.pivotal.io")
                .withCredentials("test-username", "test-password")
                .build();

        verifyZeroInteractions(this.sslCertificateTruster);
    }

    @Test
    public void defaultConstructor() {
        new SpringCloudFoundryClientBuilder();
    }

}
