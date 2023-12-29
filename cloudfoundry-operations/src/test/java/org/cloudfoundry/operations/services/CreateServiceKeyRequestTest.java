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

package org.cloudfoundry.operations.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class CreateServiceKeyRequestTest {

    @Test
    void noSerivceInstanceName() {
        assertThrows(IllegalStateException.class, () -> {
            CreateServiceKeyRequest.builder()
                .serviceKeyName("test-service-key-name")
                .build();
        });
    }

    @Test
    void noServiceKeyName() {
        assertThrows(IllegalStateException.class, () -> {
            CreateServiceKeyRequest.builder()
                .serviceInstanceName("test-service-instance-name")
                .build();
        });
    }

    @Test
    void valid() {
        CreateServiceKeyRequest.builder()
                .serviceInstanceName("test-service-instance-name")
                .serviceKeyName("test-service-key-name")
                .build();
    }
}
