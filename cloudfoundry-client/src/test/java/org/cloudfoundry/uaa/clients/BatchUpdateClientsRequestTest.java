/*
 * Copyright 2013-2021 the original author or authors.
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

import org.junit.jupiter.api.Test;

import static org.cloudfoundry.uaa.tokens.GrantType.CLIENT_CREDENTIALS;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class BatchUpdateClientsRequestTest {

    @Test
    void emptyClient() {
        assertThrows(IllegalStateException.class, () -> {
            BatchUpdateClientsRequest.builder()
                .clients()
                .build();
        });
    }

    @Test
    void noClient() {
        assertThrows(IllegalStateException.class, () -> {
            BatchUpdateClientsRequest.builder()
                .build();
        });
    }

    @Test
    void valid() {
        BatchUpdateClientsRequest.builder()
                .client(
                        UpdateClient.builder()
                                .clientId("test-client-id")
                                .authorizedGrantType(CLIENT_CREDENTIALS)
                                .build())
                .build();
    }
}
