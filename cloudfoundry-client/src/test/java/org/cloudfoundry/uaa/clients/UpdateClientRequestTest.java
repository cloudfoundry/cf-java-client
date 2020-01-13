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

package org.cloudfoundry.uaa.clients;

import org.junit.Test;

import static org.cloudfoundry.uaa.tokens.GrantType.CLIENT_CREDENTIALS;

public final class UpdateClientRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noAuthorizedGrantType() {
        UpdateClientRequest.builder()
            .clientId("test-client-id")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noClientId() {
        UpdateClientRequest.builder()
            .authorizedGrantType(CLIENT_CREDENTIALS)
            .build();
    }

    @Test
    public void valid() {
        UpdateClientRequest.builder()
            .authorizedGrantType(CLIENT_CREDENTIALS)
            .clientId("test-client-id")
            .build();
    }

}
