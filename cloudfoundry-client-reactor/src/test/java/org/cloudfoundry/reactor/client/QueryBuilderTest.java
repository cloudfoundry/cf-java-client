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

package org.cloudfoundry.reactor.client;

import org.cloudfoundry.QueryParameter;
import org.junit.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class QueryBuilderTest {

    @Test
    public void test() {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        QueryBuilder.augment(builder, new StubQueryParamsSubClass());

        MultiValueMap<String, String> queryParams = builder.build().getQueryParams();
        assertThat(queryParams).hasSize(3);

        assertThat(queryParams.getFirst("test-parameter-1")).isEqualTo("test-value-1,test-value-2");
        assertThat(queryParams.getFirst("test-parameter-3")).isEqualTo("test-value-3");
        assertThat(queryParams.getFirst("test-parameter-4")).isEqualTo("test-value-4 test-value-5");
    }

    private static abstract class StubQueryParams {

        @QueryParameter("test-parameter-2")
        final String getNull() {
            return null;
        }

        @QueryParameter("test-parameter-1")
        final List<String> getParameter1() {
            return Arrays.asList("test-value-1", "test-value-2");
        }

    }

    private static final class StubQueryParamsSubClass extends StubQueryParams {

        @QueryParameter("test-parameter-3")
        String getParameter2() {
            return "test-value-3";
        }

        @QueryParameter(value = "test-parameter-4", delimiter = " ")
        List<String> getParameter4() {
            return Arrays.asList("test-value-4", "test-value-5");
        }
    }

}
