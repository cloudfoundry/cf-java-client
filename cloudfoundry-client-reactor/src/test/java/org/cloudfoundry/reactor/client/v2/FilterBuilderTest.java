/*
 * Copyright 2013-2019 the original author or authors.
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

import org.cloudfoundry.client.v2.FilterParameter;
import org.junit.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.client.v2.FilterParameter.Operation.GREATER_THAN;
import static org.cloudfoundry.client.v2.FilterParameter.Operation.GREATER_THAN_OR_EQUAL_TO;
import static org.cloudfoundry.client.v2.FilterParameter.Operation.LESS_THAN;
import static org.cloudfoundry.client.v2.FilterParameter.Operation.LESS_THAN_OR_EQUAL_TO;

public final class FilterBuilderTest {

    @Test
    public void test() {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        FilterBuilder.augment(builder, new StubFilterParamsSubClass());

        MultiValueMap<String, String> queryParams = builder.build().encode().getQueryParams();
        List<String> q = queryParams.get("q");

        assertThat(q)
            .hasSize(9)
            .containsOnly("test-empty-value:",
                "test-greater-than%3Etest-value-1",
                "test-greater-than-or-equal-to%3E%3Dtest-value-2",
                "test-in%20IN%20test-value-3,test-value-4",
                "test-is:test-value-5",
                "test-less-than%3Ctest-value-6",
                "test-less-than-or-equal-to%3C%3Dtest-value-7",
                "test-default%20IN%20test-value-8,test-value-9",
                "test-override:test-value-10");
    }

    public static abstract class StubFilterParams {

        @FilterParameter("test-empty")
        public final List<String> getEmpty() {
            return Collections.emptyList();
        }

        @FilterParameter("test-empty-value")
        public final String getEmptyValue() {
            return "";
        }

        @FilterParameter(value = "test-greater-than", operation = GREATER_THAN)
        public final String getGreaterThan() {
            return "test-value-1";
        }

        @FilterParameter(value = "test-greater-than-or-equal-to", operation = GREATER_THAN_OR_EQUAL_TO)
        public final String getGreaterThanOrEqualTo() {
            return "test-value-2";
        }

        @FilterParameter("test-in")
        public final List<String> getIn() {
            return Arrays.asList("test-value-3", "test-value-4");
        }

        @FilterParameter("test-is")
        public final String getIs() {
            return "test-value-5";
        }

        @FilterParameter(value = "test-less-than", operation = LESS_THAN)
        public final String getLessThan() {
            return "test-value-6";
        }

        @FilterParameter(value = "test-less-than-or-equal-to", operation = LESS_THAN_OR_EQUAL_TO)
        public final String getLessThanOrEqualTo() {
            return "test-value-7";
        }

        @FilterParameter("test-null")
        public final String getNull() {
            return null;
        }

        @FilterParameter("test-override")
        abstract String getOverride();

    }

    public static final class StubFilterParamsSubClass extends StubFilterParams {

        @FilterParameter("test-default")
        public List<String> getDefault() {
            return Arrays.asList("test-value-8", "test-value-9");
        }

        @Override
        public String getOverride() {
            return "test-value-10";
        }

    }

}
