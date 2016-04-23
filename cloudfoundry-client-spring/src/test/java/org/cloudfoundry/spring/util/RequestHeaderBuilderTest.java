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

package org.cloudfoundry.spring.util;

import org.cloudfoundry.RequestHeader;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class RequestHeaderBuilderTest {

    @Test
    public void test() {
        HttpHeaders headers = new HttpHeaders();

        RequestHeaderBuilder.populate(headers, new StubHeadersSubClass());

        assertThat(headers.size(), is(equalTo(3)));
        assertFalse(headers.containsKey("test-header-null"));

        assertThat(headers.get("test-header-string"), is(notNullValue()));
        assertThat(headers.get("test-header-string").size(), is(equalTo(1)));
        assertThat(headers.getFirst("test-header-string"), is(equalTo("test-value")));

        assertThat(headers.get("test-header-iterable-1"), is(notNullValue()));
        assertThat(headers.get("test-header-iterable-1").size(), is(equalTo(2)));
        assertThat(headers.get("test-header-iterable-1").get(0), is(equalTo("value1")));
        assertThat(headers.get("test-header-iterable-1").get(1), is(equalTo("value2")));

        assertThat(headers.get("test-header-iterable-2"), is(notNullValue()));
        assertThat(headers.get("test-header-iterable-2").size(), is(equalTo(2)));
        assertThat(headers.get("test-header-iterable-2").get(0), is(equalTo("value1")));
        assertThat(headers.get("test-header-iterable-2").get(1), is(equalTo("value2")));

    }

    private static abstract class StubHeaders {

        @RequestHeader("test-header-null")
        final String getNull() {
            return null;
        }

        @RequestHeader("test-header-iterable-1")
        final List<String> getParameterIterable() {
            return Arrays.asList("value1", "value2");
        }

        @RequestHeader("test-header-string")
        final String getParameterString() {
            return "test-value";
        }

    }

    private static final class StubHeadersSubClass extends StubHeaders {

        @RequestHeader("test-header-iterable-2")
        final List<String> getParameterIterable2() {
            return Arrays.asList("value1", "value2");
        }

    }

}
