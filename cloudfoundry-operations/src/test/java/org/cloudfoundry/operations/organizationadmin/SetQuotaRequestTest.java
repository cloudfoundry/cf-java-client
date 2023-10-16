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

package org.cloudfoundry.operations.organizationadmin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class SetQuotaRequestTest {

    @Test
    void noOrganizationName() {
        assertThrows(IllegalStateException.class, () -> {
            SetQuotaRequest.builder()
                .quotaName("test-quota")
                .build();
        });
    }

    @Test
    void noQuotaName() {
        assertThrows(IllegalStateException.class, () -> {
            SetQuotaRequest.builder()
                .organizationName("test-organization")
                .build();
        });
    }

    @Test
    void valid() {
        SetQuotaRequest.builder()
            .organizationName("test-organization")
            .quotaName("test-quota")
            .build();
    }

}
