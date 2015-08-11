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
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

import static org.junit.Assert.assertEquals;

public final class ResourceOwnerPasswordResourceDetailsBuilderTest {

    private final ResourceOwnerPasswordResourceDetailsBuilder builder =
            new ResourceOwnerPasswordResourceDetailsBuilder();

    @Test
    public void withAccessTokenUri() {
        ResourceOwnerPasswordResourceDetails details = this.builder.withAccessTokenUri("test-access-token-uri").build();
        assertEquals("test-access-token-uri", details.getAccessTokenUri());
    }

    @Test
    public void withClientId() {
        ResourceOwnerPasswordResourceDetails details = this.builder.withClientId("test-client-id").build();
        assertEquals("test-client-id", details.getClientId());
    }

    @Test
    public void withClientSecret() {
        ResourceOwnerPasswordResourceDetails details = this.builder.withClientSecret("test-client-secret").build();
        assertEquals("test-client-secret", details.getClientSecret());
    }

    @Test
    public void withCredentials() {
        ResourceOwnerPasswordResourceDetails details = this.builder.withPassword("test-password").build();
        assertEquals("test-password", details.getPassword());
    }

    @Test
    public void withUsername() {
        ResourceOwnerPasswordResourceDetails details = this.builder.withUsername("test-username").build();
        assertEquals("test-username", details.getUsername());
    }

}
