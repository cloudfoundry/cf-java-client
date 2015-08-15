/*
 * Copyright 2013-2015 the original author or authors.
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

import org.cloudfoundry.client.v3.PaginatedResponse.Pagination;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public final class PaginatedResponseTest {

    @Test
    public void test() {
        Link first = new Link();
        Link last = new Link();
        Link next = new Link();
        Link previous = new Link();

        Pagination pagination = new Pagination()
                .withFirst(first)
                .withLast(last)
                .withNext(next)
                .withPrevious(previous)
                .withTotalResults(-1);

        assertEquals(first, pagination.getFirst());
        assertEquals(last, pagination.getLast());
        assertEquals(next, pagination.getNext());
        assertEquals(previous, pagination.getPrevious());
        assertEquals(Integer.valueOf(-1), pagination.getTotalResults());

        StubPaginatedResponse response = new StubPaginatedResponse()
                .withPagination(pagination)
                .withResource("test-resource-1")
                .withResources(Collections.singletonList("test-resource-2"));

        assertEquals(pagination, response.getPagination());
        assertEquals(Arrays.asList("test-resource-1", "test-resource-2"), response.getResources());
    }

    private static final class StubPaginatedResponse extends PaginatedResponse<StubPaginatedResponse, String> {
    }

}
