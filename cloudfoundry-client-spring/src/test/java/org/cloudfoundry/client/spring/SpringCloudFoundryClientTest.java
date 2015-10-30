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
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringCloudFoundryClientTest extends AbstractRestTest {

    private final SpringCloudFoundryClient client = new SpringCloudFoundryClient(this.restTemplate, this.root);

    private final SslCertificateTruster sslCertificateTruster = mock(SslCertificateTruster.class);

    @Test
    public void applicationsV2() {
        assertNotNull(this.client.applicationsV2());
    }

    @Test
    public void applicationsV3() {
        assertNotNull(this.client.applicationsV3());
    }

    @Test
    public void builder() throws GeneralSecurityException, IOException {
        mockRequest(new RequestContext()
                .method(GET).path("/info")
                .status(OK)
                .responsePayload("info_GET_response.json"));

        SpringCloudFoundryClient client = new SpringCloudFoundryClient("api.run.pivotal.io", false, "test-client-id",
                "test-client-secret", "test-username", "test-password", this.restTemplate, this.sslCertificateTruster);

        OAuth2RestOperations restOperations = client.getRestOperations();
        OAuth2ProtectedResourceDetails details = restOperations.getResource();

        assertEquals("test-client-id", details.getClientId());
        assertEquals("test-client-secret", details.getClientSecret());
        assertEquals("https://uaa.run.pivotal.io/oauth/token", details.getAccessTokenUri());

        Mockito.verify(this.sslCertificateTruster).trust("api.run.pivotal.io", 443, 5, SECONDS);
        verify();
    }

    @Test
    public void builderSkipSslVerification() throws GeneralSecurityException, IOException {
        mockRequest(new RequestContext()
                .method(GET).path("/info")
                .status(OK)
                .responsePayload("info_GET_response.json"));

        new SpringCloudFoundryClient("api.run.pivotal.io", true, "test-client-id", "test-client-secret",
                "test-username", "test-password", this.restTemplate, this.sslCertificateTruster);

        verifyZeroInteractions(this.sslCertificateTruster);
        verify();
    }

    @Test
    public void droplets() {
        assertNotNull(this.client.droplets());
    }

    @Test
    public void events() {
        assertNotNull(this.client.events());
    }

    @Test
    public void info() {
        assertNotNull(this.client.info());
    }

    @Test
    public void organizations() {
        assertNotNull(this.client.organizations());
    }

    @Test
    public void packages() {
        assertNotNull(this.client.packages());
    }

    @Test
    public void serviceInstances() {
        assertNotNull(this.client.serviceInstances());
    }

    @Test
    public void space() {
        assertNotNull(this.client.spaces());
    }
}
