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
import org.mockito.Mockito;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringCloudFoundryClientBuilderTest extends AbstractRestTest {

    private final SslCertificateTruster sslCertificateTruster = mock(SslCertificateTruster.class);

    private final SpringCloudFoundryClientBuilder builder = new SpringCloudFoundryClientBuilder(this.restTemplate,
            this.sslCertificateTruster);

    @Test
    public void test() {
        mockRequest(new RequestContext()
                .method(GET).path("/info")
                .status(OK)
                .responsePayload("info_GET_response.json"));

        SpringCloudFoundryClient client = this.builder
                .api("api.run.pivotal.io")
                .client("test-client-id", "test-client-secret")
                .credentials("test-username", "test-password")
                .build();

        OAuth2RestTemplate restTemplate = (OAuth2RestTemplate) client.getRestOperations();
        OAuth2ProtectedResourceDetails details = restTemplate.getResource();

        assertEquals("test-client-id", details.getClientId());
        assertEquals("test-client-secret", details.getClientSecret());
        assertEquals("https://uaa.run.pivotal.io/oauth/token", details.getAccessTokenUri());
        verify();
    }

    @Test
    public void skipSslValidationTrue() throws GeneralSecurityException, IOException {
        mockRequest(new RequestContext()
                .method(GET).path("/info")
                .status(OK)
                .responsePayload("info_GET_response.json"));

        this.builder
                .api("api.run.pivotal.io")
                .credentials("test-username", "test-password")
                .skipSslValidation(true)
                .build();

        Mockito.verify(this.sslCertificateTruster).trust("api.run.pivotal.io", 443, 5, SECONDS);
    }

    @Test
    public void skipSslValidationFalse() throws GeneralSecurityException, IOException {
        mockRequest(new RequestContext()
                .method(GET).path("/info")
                .status(OK)
                .responsePayload("info_GET_response.json"));

        this.builder
                .api("api.run.pivotal.io")
                .credentials("test-username", "test-password")
                .build();

        verifyZeroInteractions(this.sslCertificateTruster);
    }

    @Test
    public void defaultConstructor() {
        new SpringCloudFoundryClientBuilder();
    }

}
