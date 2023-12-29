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

import static org.junit.jupiter.api.Assertions.assertThrows;

final class BatchChangeSecretRequestTest {

    @Test
    void emptyChangeSecrets() {
        assertThrows(IllegalStateException.class, () -> {
            BatchChangeSecretRequest.builder()
                .changeSecrets()
                .build();
        });
    }

    @Test
    void noChangeSecrets() {
        assertThrows(IllegalStateException.class, () -> {
            BatchChangeSecretRequest.builder()
                .build();
        });
    }

    @Test
    void noClientId() {
        assertThrows(IllegalStateException.class, () -> {
            BatchChangeSecretRequest.builder()
                .changeSecret(ChangeSecret.builder()
                    .secret("test-secret")
                    .build())
                .build();
        });
    }

    @Test
    void noSecret() {
        assertThrows(IllegalStateException.class, () -> {
            BatchChangeSecretRequest.builder()
                .changeSecret(ChangeSecret.builder()
                    .clientId("test-client-id")
                    .build())
                .build();
        });
    }

    @Test
    void valid() {
        BatchChangeSecretRequest.builder()
                .changeSecret(
                        ChangeSecret.builder()
                                .clientId("test-client-id")
                                .oldSecret("test-old-secret")
                                .secret("test-secret")
                                .build())
                .build();
    }
}
