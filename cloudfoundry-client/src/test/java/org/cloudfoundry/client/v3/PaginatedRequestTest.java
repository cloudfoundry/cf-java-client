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

package org.cloudfoundry.client.v3;

import org.immutables.value.Value;
import org.junit.Test;

public final class PaginatedRequestTest {

    @Test(expected = IllegalStateException.class)
    public void excessivePerPage() {
        StubPaginatedRequest.builder()
            .perPage(10_000)
            .build();
    }

    @Test
    public void validNoValues() {
        StubPaginatedRequest.builder()
            .build();
    }

    @Test
    public void validValues() {
        StubPaginatedRequest.builder()
            .page(10)
            .perPage(10)
            .orderBy("name")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void zeroPage() {
        StubPaginatedRequest.builder()
            .page(0)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void zeroPerPage() {
        StubPaginatedRequest.builder()
            .perPage(0)
            .build();
    }

    @Value.Immutable
    static abstract class _StubPaginatedRequest extends PaginatedRequest {

    }

}
