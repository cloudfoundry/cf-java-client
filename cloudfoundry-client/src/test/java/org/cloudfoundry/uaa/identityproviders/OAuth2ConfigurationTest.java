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

package org.cloudfoundry.uaa.identityproviders;

import org.junit.Test;

public final class OAuth2ConfigurationTest {

    @Test(expected = IllegalStateException.class)
    public void noAuthUrl() {
        OAuth2Configuration.builder()
            .tokenUrl("test-token-url")
            .relyingPartyId("test-relying-party-id")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noRelyingPartyId() {
        OAuth2Configuration.builder()
            .authUrl("test-auth-url")
            .tokenUrl("test-token-url")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noTokenUrl() {
        OAuth2Configuration.builder()
            .authUrl("test-auth-url")
            .relyingPartyId("test-relying-party-id")
            .build();
    }

    @Test
    public void valid() {
        OAuth2Configuration.builder()
            .authUrl("test-auth-url")
            .tokenUrl("test-token-url")
            .relyingPartyId("test-relying-party-id")
            .build();
    }

}
