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

package org.cloudfoundry.client.spring.v2;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public final class FilterBuilderTest {

    private final FilterBuilder builder = new FilterBuilder("test-key");

    @Test
    public void greaterThan() {
        assertEquals("test-key>-1", this.builder.greaterThan(-1).build());
    }

    @Test
    public void greaterThanOrEqualTo() {
        assertEquals("test-key>=-1", this.builder.greaterThanOrEqualTo(-1).build());
    }

    @Test
    public void in() {
        this.builder
                .in("test-value-1")
                .in(Collections.singletonList("test-value-2"));

        assertEquals("test-key IN test-value-1,test-value-2", this.builder.build());
    }

    @Test
    public void is() {
        assertEquals("test-key:test-value", this.builder.is("test-value").build());
    }

    @Test
    public void lessThan() {
        assertEquals("test-key<-1", this.builder.lessThan(-1).build());
    }

    @Test
    public void lessThanOrEqualTo() {
        assertEquals("test-key<=-1", this.builder.lessThanOrEqualTo(-1).build());
    }

}
