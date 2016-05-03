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

package org.cloudfoundry.reactor.client.v2;

import org.cloudfoundry.client.v2.GreaterThanFilterParameter;
import org.cloudfoundry.client.v2.GreaterThanOrEqualToFilterParameter;
import org.cloudfoundry.client.v2.InFilterParameter;
import org.cloudfoundry.client.v2.IsFilterParameter;
import org.cloudfoundry.client.v2.LessThanFilterParameter;
import org.cloudfoundry.client.v2.LessThanOrEqualToFilterParameter;
import org.junit.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

        @InFilterParameter("test-empty")
        final List<String> getEmpty() {
            return Collections.emptyList();
        }

        @GreaterThanFilterParameter("test-greater-than")
        final String getGreaterThan() {
            return "test-value-1";
        }

        @GreaterThanOrEqualToFilterParameter("test-greater-than-or-equal-to")
        final String getGreaterThanOrEqualTo() {
            return "test-value-2";
        }

        @InFilterParameter("test-in")
        final List<String> getIn() {
            return Arrays.asList("test-value-3", "test-value-4");
        }

        @IsFilterParameter("test-is")
        final String getIs() {
            return "test-value-5";
        }

        @LessThanFilterParameter("test-less-than")
        final String getLessThan() {
            return "test-value-6";
        }

        @LessThanOrEqualToFilterParameter("test-less-than-or-equal-to")
        final String getLessThanOrEqualTo() {
            return "test-value-7";
        }

        @InFilterParameter("test-null")
        final String getNull() {
            return null;
        }

    }

    private static final class StubFilterParamsSubClass extends StubFilterParams {

        @InFilterParameter("test-default")
        List<String> getDefault() {
            return Arrays.asList("test-value-8", "test-value-9");
        }

    }

}
