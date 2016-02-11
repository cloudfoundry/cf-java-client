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

package org.cloudfoundry.client.spring.util.network;

import org.junit.Test;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public final class ResourceOwnerPasswordResourceDetailsBuilderTest {

    @Test
    public void test() {
        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetailsBuilder()
            .accessTokenUri(URI.create("https://test-host"))
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .password("test-password")
            .username("test-username")
            .build();

        assertEquals("https://test-host", resourceDetails.getAccessTokenUri());
        assertEquals("test-client-id", resourceDetails.getClientId());
        assertEquals("test-client-secret", resourceDetails.getClientSecret());
        assertEquals("test-password", resourceDetails.getPassword());
        assertEquals("test-username", resourceDetails.getUsername());
    }

    @Test
    public void testDefaults() {
        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetailsBuilder()
            .accessTokenUri(URI.create("https://test-host"))
            .password("test-password")
            .username("test-username")
            .build();

        assertEquals("https://test-host", resourceDetails.getAccessTokenUri());
        assertEquals("cf", resourceDetails.getClientId());
        assertEquals("", resourceDetails.getClientSecret());
        assertEquals("test-password", resourceDetails.getPassword());
        assertEquals("test-username", resourceDetails.getUsername());
    }

}
