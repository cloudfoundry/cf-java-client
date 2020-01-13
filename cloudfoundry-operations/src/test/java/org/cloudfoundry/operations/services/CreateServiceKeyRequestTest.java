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

package org.cloudfoundry.operations.services;

import org.junit.Test;

public final class CreateServiceKeyRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noSerivceInstanceName() {
        CreateServiceKeyRequest.builder()
            .serviceKeyName("test-service-key-name")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noServiceKeyName() {
        CreateServiceKeyRequest.builder()
            .serviceInstanceName("test-service-instance-name")
            .build();
    }

    @Test
    public void valid() {
        CreateServiceKeyRequest.builder()
            .serviceInstanceName("test-service-instance-name")
            .serviceKeyName("test-service-key-name")
            .build();
    }

}
