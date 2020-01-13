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

package org.cloudfoundry.uaa.serverinformation;

import org.junit.Test;

public final class GetAutoLoginAuthenticationCodeRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noClientId() {
        GetAutoLoginAuthenticationCodeRequest.builder()
            .clientSecret("test-client-secret")
            .password("test-password")
            .username("test-username")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noClientSecret() {
        GetAutoLoginAuthenticationCodeRequest.builder()
            .clientId("test-client-id")
            .password("test-password")
            .username("test-username")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noPassword() {
        GetAutoLoginAuthenticationCodeRequest.builder()
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .username("test-username")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noUsername() {
        GetAutoLoginAuthenticationCodeRequest.builder()
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .password("test-password")
            .build();
    }

    @Test
    public void valid() {
        GetAutoLoginAuthenticationCodeRequest.builder()
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .password("test-password")
            .username("test-username")
            .build();
    }

}
