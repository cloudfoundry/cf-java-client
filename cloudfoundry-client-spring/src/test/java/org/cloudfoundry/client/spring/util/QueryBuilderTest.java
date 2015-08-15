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

package org.cloudfoundry.client.spring.util;

import org.cloudfoundry.client.QueryParameter;
import org.junit.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.Assert.assertEquals;

public final class QueryBuilderTest {

    @Test
    public void test() {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        QueryBuilder.augment(builder, new StubQueryParamsSubClass());

        MultiValueMap<String, String> queryParams = builder.build().getQueryParams();
        assertEquals(2, queryParams.size());
        assertEquals("test-value-1", queryParams.getFirst("test-parameter-1"));
        assertEquals("test-value-3", queryParams.getFirst("test-parameter-3"));
    }

    private static abstract class StubQueryParams {

        @QueryParameter("test-parameter-1")
        final String getParameter1() {
            return "test-value-1";
        }

        @QueryParameter("test-parameter-2")
        final String getNull() {
            return null;
        }

    }

    private static final class StubQueryParamsSubClass extends StubQueryParams {

        @QueryParameter("test-parameter-3")
        String getParameter2() {
            return "test-value-3";
        }

    }

}
