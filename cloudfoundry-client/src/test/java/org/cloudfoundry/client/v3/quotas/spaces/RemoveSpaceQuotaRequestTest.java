/*
 * Copyright 2013-2025 the original author or authors.
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

package org.cloudfoundry.client.v3.quotas.spaces;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class RemoveSpaceQuotaRequestTest {

    @Test
    void noSpaceId() {
        assertThrows(
                IllegalStateException.class,
                () -> RemoveSpaceQuotaRequest.builder().spaceQuotaId("quota-id").build());
    }

    @Test
    void noSpaceQuotaId() {
        assertThrows(
                IllegalStateException.class,
                () -> RemoveSpaceQuotaRequest.builder().spaceId("space-guid").build());
    }

    @Test
    void valid() {
        RemoveSpaceQuotaRequest.builder().spaceQuotaId("quota-id").spaceId("space-guid").build();
    }
}
