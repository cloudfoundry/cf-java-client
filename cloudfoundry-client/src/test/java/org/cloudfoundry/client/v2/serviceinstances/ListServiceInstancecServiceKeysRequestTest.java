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

package org.cloudfoundry.client.v2.serviceinstances;

import org.cloudfoundry.ValidationResult;
import org.junit.Test;

import static org.cloudfoundry.ValidationResult.Status.INVALID;
import static org.cloudfoundry.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public final class ListServiceInstancecServiceKeysRequestTest {

    @Test
    public void isInValidNoId() {
        ValidationResult result = ListServiceInstanceServiceBindingsRequest.builder()
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("service instance id must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValid() {
        ValidationResult result = ListServiceInstanceServiceKeysRequest.builder()
            .serviceInstanceId("test-service-instance-id")
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidWithName() {
        ValidationResult result = ListServiceInstanceServiceKeysRequest.builder()
            .serviceInstanceId("test-service-instance-id")
            .name("test-service-key")
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

}
