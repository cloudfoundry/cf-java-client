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

package org.cloudfoundry.uaa.identityzones;

import org.cloudfoundry.ValidationResult;
import org.junit.Test;

import static org.cloudfoundry.ValidationResult.Status.INVALID;
import static org.cloudfoundry.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public final class CreateIdentityZoneClientRequestTest {

    @Test
    public void isNotValidNoAllowedProvider() {
        ValidationResult result = CreateIdentityZoneClientRequest.builder()
            .authorizedGrantType("test-authorized-grant-type")
            .authority("test-authority")
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .identityZoneId("test-identity-zone-id")
            .scope("test-scope")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("allowed providers must be specified", result.getMessages().get(0));
    }

    @Test
    public void isNotValidNoAuthority() {
        ValidationResult result = CreateIdentityZoneClientRequest.builder()
            .allowedProvider("test-allow-provider")
            .authorizedGrantType("test-authorized-grant-type")
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .identityZoneId("test-identity-zone-id")
            .scope("test-scope")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("authorities must be specified", result.getMessages().get(0));
    }

    @Test
    public void isNotValidNoAuthorizedGrantType() {
        ValidationResult result = CreateIdentityZoneClientRequest.builder()
            .allowedProvider("test-allow-provider")
            .authority("test-authority")
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .identityZoneId("test-identity-zone-id")
            .scope("test-scope")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("authorized grant types must be specified", result.getMessages().get(0));
    }

    @Test
    public void isNotValidNoClientId() {
        ValidationResult result = CreateIdentityZoneClientRequest.builder()
            .allowedProvider("test-allow-provider")
            .authority("test-authority")
            .authorizedGrantType("test-authorized-grant-type")
            .clientSecret("test-client-secret")
            .identityZoneId("test-identity-zone-id")
            .scope("test-scope")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("client id must be specified", result.getMessages().get(0));
    }

    @Test
    public void isNotValidNoClientSecret() {
        ValidationResult result = CreateIdentityZoneClientRequest.builder()
            .allowedProvider("test-allow-provider")
            .authority("test-authority")
            .authorizedGrantType("test-authorized-grant-type")
            .clientId("test-client-id")
            .identityZoneId("test-identity-zone-id")
            .scope("test-scope")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("client secret must be specified", result.getMessages().get(0));
    }

    @Test
    public void isNotValidNoIdentityZoneId() {
        ValidationResult result = CreateIdentityZoneClientRequest.builder()
            .allowedProvider("test-allow-provider")
            .authority("test-authority")
            .authorizedGrantType("test-authorized-grant-type")
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .scope("test-scope")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("identity zone id must be specified", result.getMessages().get(0));
    }

    @Test
    public void isNotValidNoScope() {
        ValidationResult result = CreateIdentityZoneClientRequest.builder()
            .allowedProvider("test-allow-provider")
            .authority("test-authority")
            .authorizedGrantType("test-authorized-grant-type")
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .identityZoneId("test-identity-zone-id")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("scopes must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValid() {
        ValidationResult result = CreateIdentityZoneClientRequest.builder()
            .allowedProvider("test-allow-provider")
            .authority("test-authority")
            .authorizedGrantType("test-authorized-grant-type")
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .identityZoneId("test-identity-zone-id")
            .scope("test-scope")
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

}
