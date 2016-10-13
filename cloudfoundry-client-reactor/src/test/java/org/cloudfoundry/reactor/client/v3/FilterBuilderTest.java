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

package org.cloudfoundry.reactor.client.v3;

import org.cloudfoundry.client.v3.FilterParameter;
import org.junit.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class FilterBuilderTest {

    @Test
    public void test() {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        FilterBuilder.augment(builder, new StubFilterParamsSubClass());

        MultiValueMap<String, String> queryParams = builder.build().getQueryParams();

        assertThat(queryParams).hasSize(3);
        assertThat(queryParams.getFirst("test-single")).isEqualTo("test-value-1");
        assertThat(queryParams.getFirst("test-collection")).isEqualTo("test-value-2,test-value-3");
        assertThat(queryParams.getFirst("test-subclass")).isEqualTo("test-value-4");
    }

    private static abstract class StubFilterParams {

        @FilterParameter("test-collection")
        List<String> getCollection() {
            return Arrays.asList("test-value-2", "test-value-3");
        }

        @FilterParameter("test-empty")
        List<String> getEmpty() {
            return Collections.emptyList();
        }

        @FilterParameter("test-null")
        String getNull() {
            return null;
        }

        @FilterParameter("test-single")
        String getSingle() {
            return "test-value-1";
        }

    }

    private static final class StubFilterParamsSubClass extends StubFilterParams {

        @FilterParameter("test-subclass")
        String getSubclass() {
            return "test-value-4";
        }

    }

}
