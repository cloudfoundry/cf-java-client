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

package org.cloudfoundry.client.v2;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public final class PaginatedResponseTest {

    @Test
    public void test() {
        Resource<StubEntity> resource1 = new Resource<StubEntity>().withEntity(new StubEntity());
        Resource<StubEntity> resource2 = new Resource<StubEntity>().withEntity(new StubEntity());

        StubPaginatedResponse response = new StubPaginatedResponse()
                .withNextUrl("test-next-url")
                .withPreviousUrl("test-previous-url")
                .withResource(resource1)
                .withResources(Collections.singletonList(resource2))
                .withTotalPages(-1)
                .withTotalResults(-2);

        assertEquals("test-next-url", response.getNextUrl());
        assertEquals("test-previous-url", response.getPreviousUrl());
        assertEquals(Arrays.asList(resource1, resource2), response.getResources());
        assertEquals(Integer.valueOf(-1), response.getTotalPages());
        assertEquals(Integer.valueOf(-2), response.getTotalResults());
    }

    private static final class StubPaginatedResponse
            extends PaginatedResponse<StubPaginatedResponse, StubEntity> {
    }

    private static final class StubEntity {
    }

}
