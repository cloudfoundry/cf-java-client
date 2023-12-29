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

package org.cloudfoundry.client.v3;

import org.immutables.value.Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class PaginatedRequestTest {

    @Test
    void excessivePerPage() {
        assertThrows(IllegalStateException.class, () -> {
            StubPaginatedRequest.builder()
                .perPage(10_000)
                .build();
        });
    }

    @Test
    void validNoValues() {
        StubPaginatedRequest.builder()
            .build();
    }

    @Test
    void validValues() {
        StubPaginatedRequest.builder()
            .page(10)
            .perPage(10)
            .orderBy("name")
            .build();
    }

    @Test
    void zeroPage() {
        assertThrows(IllegalStateException.class, () -> {
            StubPaginatedRequest.builder()
                .page(0)
                .build();
        });
    }

    @Test
    void zeroPerPage() {
        assertThrows(IllegalStateException.class, () -> {
            StubPaginatedRequest.builder()
                .perPage(0)
                .build();
        });
    }

    @Value.Immutable
    abstract static class _StubPaginatedRequest extends PaginatedRequest {}
}
