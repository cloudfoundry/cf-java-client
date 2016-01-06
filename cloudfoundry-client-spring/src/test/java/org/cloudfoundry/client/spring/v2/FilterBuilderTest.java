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

package org.cloudfoundry.client.spring.v2;

import org.cloudfoundry.client.v2.FilterParameter;
import org.junit.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.cloudfoundry.client.v2.FilterParameter.Operation.GREATER_THAN;
import static org.cloudfoundry.client.v2.FilterParameter.Operation.GREATER_THAN_OR_EQUAL_TO;
import static org.cloudfoundry.client.v2.FilterParameter.Operation.IN;
import static org.cloudfoundry.client.v2.FilterParameter.Operation.IS;
import static org.cloudfoundry.client.v2.FilterParameter.Operation.LESS_THAN;
import static org.cloudfoundry.client.v2.FilterParameter.Operation.LESS_THAN_OR_EQUAL_TO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class FilterBuilderTest {

    @Test
    public void test() {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        FilterBuilder.augment(builder, new StubFilterParamsSubClass());

        MultiValueMap<String, String> queryParams = builder.build().getQueryParams();
        List<String> q = queryParams.get("q");
        assertEquals(7, q.size());
        assertTrue(q.contains("test-greater-than>test-value-1"));
        assertTrue(q.contains("test-greater-than-or-equal-to>=test-value-2"));
        assertTrue(q.contains("test-in IN test-value-3,test-value-4"));
        assertTrue(q.contains("test-is:test-value-5"));
        assertTrue(q.contains("test-less-than<test-value-6"));
        assertTrue(q.contains("test-less-than-or-equal-to<=test-value-7"));
        assertTrue(q.contains("test-default IN test-value-8,test-value-9"));
    }

    private static abstract class StubFilterParams {

        @FilterParameter(name = "test-empty")
        final List<String> getEmpty() {
            return Collections.emptyList();
        }

        @FilterParameter(name = "test-greater-than", operation = GREATER_THAN)
        final String getGreaterThan() {
            return "test-value-1";
        }

        @FilterParameter(name = "test-greater-than-or-equal-to", operation = GREATER_THAN_OR_EQUAL_TO)
        final String getGreaterThanOrEqualTo() {
            return "test-value-2";
        }

        @FilterParameter(name = "test-in", operation = IN)
        final List<String> getIn() {
            return Arrays.asList("test-value-3", "test-value-4");
        }

        @FilterParameter(name = "test-is", operation = IS)
        final String getIs() {
            return "test-value-5";
        }

        @FilterParameter(name = "test-less-than", operation = LESS_THAN)
        final String getLessThan() {
            return "test-value-6";
        }

        @FilterParameter(name = "test-less-than-or-equal-to", operation = LESS_THAN_OR_EQUAL_TO)
        final String getLessThanOrEqualTo() {
            return "test-value-7";
        }

        @FilterParameter(name = "test-null")
        final String getNull() {
            return null;
        }

    }

    private static final class StubFilterParamsSubClass extends StubFilterParams {

        @FilterParameter("test-default")
        List<String> getDefault() {
            return Arrays.asList("test-value-8", "test-value-9");
        }

    }

}
