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

package org.cloudfoundry.client.v3;

import lombok.Builder;
import org.cloudfoundry.client.ValidationResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class PaginatedRequestTest {

    @Test
    public void isPaginationRequestValid() {
        ValidationResult result = StubPaginatedRequest.builder()
            .page(10)
            .perPage(10)
            .build()
            .isPaginatedRequestValid()
            .build();

        assertEquals(ValidationResult.Status.VALID, result.getStatus());
    }

    @Test
    public void isPaginationRequestValidExcessivePerPage() {
        ValidationResult result = StubPaginatedRequest.builder()
            .perPage(10_000)
            .build()
            .isPaginatedRequestValid()
            .build();

        assertEquals(ValidationResult.Status.INVALID, result.getStatus());
        assertEquals("perPage must be between 1 and 5000 inclusive", result.getMessages().get(0));
    }

    @Test
    public void isPaginationRequestValidNull() {
        ValidationResult result = StubPaginatedRequest.builder()
            .build()
            .isPaginatedRequestValid()
            .build();

        assertEquals(ValidationResult.Status.VALID, result.getStatus());
    }

    @Test
    public void isPaginationRequestValidZeroPage() {
        ValidationResult result = StubPaginatedRequest.builder()
            .page(0)
            .build()
            .isPaginatedRequestValid()
            .build();

        assertEquals(ValidationResult.Status.INVALID, result.getStatus());
        assertEquals("page must be greater than or equal to 1", result.getMessages().get(0));
    }

    @Test
    public void isPaginationRequestValidZeroPerPage() {
        ValidationResult result = StubPaginatedRequest.builder()
            .perPage(0)
            .build()
            .isPaginatedRequestValid()
            .build();

        assertEquals(ValidationResult.Status.INVALID, result.getStatus());
        assertEquals("perPage must be between 1 and 5000 inclusive", result.getMessages().get(0));
    }

    private static final class StubPaginatedRequest extends PaginatedRequest {

        @Builder
        private StubPaginatedRequest(Integer page, Integer perPage) {
            super(page, perPage);
        }

    }

}
