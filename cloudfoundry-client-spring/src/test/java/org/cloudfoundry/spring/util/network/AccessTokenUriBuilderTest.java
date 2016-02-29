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

package org.cloudfoundry.spring.util.network;

import org.junit.Test;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class AccessTokenUriBuilderTest {

    private final RestOperations restOperations = mock(RestOperations.class, RETURNS_SMART_NULLS);

    private final SslCertificateTruster sslCertificateTruster = mock(SslCertificateTruster.class, RETURNS_SMART_NULLS);

    @Test
    public void test() {
        when(this.restOperations.getForObject(URI.create("https://test-host:443/v2/info"), Map.class)).thenReturn(Collections.singletonMap("token_endpoint", "https://test-uaa-host"));

        URI accessTokenUri = new AccessTokenUriBuilder()
            .host("test-host")
            .port(443)
            .restOperations(this.restOperations)
            .sslCertificateTruster(this.sslCertificateTruster)
            .build();

        assertEquals(URI.create("https://test-uaa-host/oauth/token"), accessTokenUri);

        verify(this.sslCertificateTruster).trust("test-host", 443, 5, SECONDS);
        verify(this.sslCertificateTruster).trust("test-uaa-host", -1, 5, SECONDS);
    }

}
