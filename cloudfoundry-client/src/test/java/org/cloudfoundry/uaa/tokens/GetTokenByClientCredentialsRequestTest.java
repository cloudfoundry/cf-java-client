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

package org.cloudfoundry.uaa.tokens;

import org.cloudfoundry.ValidationResult;
import org.junit.Test;

import static org.cloudfoundry.ValidationResult.Status.INVALID;
import static org.cloudfoundry.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public final class GetTokenByClientCredentialsRequestTest {

    @Test
    public void isNotValidNoClientId() {
        ValidationResult result = GetTokenByClientCredentialsRequest.builder()
            .clientSecret("test-client-secret")
            .tokenFormat(TokenFormat.OPAQUE)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("client id must be specified", result.getMessages().get(0));
    }

    @Test
    public void isNotValidNoClientSecret() {
        ValidationResult result = GetTokenByClientCredentialsRequest.builder()
            .clientId("test-client-id")
            .tokenFormat(TokenFormat.OPAQUE)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("client secret must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidMax() {
        ValidationResult result = GetTokenByClientCredentialsRequest.builder()
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .tokenFormat(TokenFormat.OPAQUE)
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidMin() {
        ValidationResult result = GetTokenByClientCredentialsRequest.builder()
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

}
