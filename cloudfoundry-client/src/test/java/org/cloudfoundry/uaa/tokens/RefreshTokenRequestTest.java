/*
 * Copyright 2013-2020 the original author or authors.
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

import org.junit.Test;

public final class RefreshTokenRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noClientId() {
        RefreshTokenRequest.builder()
            .clientSecret("test-client-secret")
            .refreshToken("test-refresh-token")
            .tokenFormat(TokenFormat.OPAQUE)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noClientSecret() {
        RefreshTokenRequest.builder()
            .clientId("test-client-id")
            .refreshToken("test-refresh-token")
            .tokenFormat(TokenFormat.OPAQUE)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noRefreshToken() {
        RefreshTokenRequest.builder()
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .tokenFormat(TokenFormat.OPAQUE)
            .build();
    }

    @Test
    public void validMax() {
        RefreshTokenRequest.builder()
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .refreshToken("test-refresh-token")
            .tokenFormat(TokenFormat.OPAQUE)
            .build();
    }

    @Test
    public void validMin() {
        RefreshTokenRequest.builder()
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .refreshToken("test-refresh-token")
            .build();
    }

}
