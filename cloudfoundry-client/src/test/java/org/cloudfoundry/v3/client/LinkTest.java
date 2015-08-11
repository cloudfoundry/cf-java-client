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

package org.cloudfoundry.v3.client;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class LinkTest {

    @Test
    public void test() {
        Link link = new Link()
                .withHref("test-href")
                .withMethod("test-method");

        assertEquals("test-href", link.getHref());
        assertEquals("test-method", link.getMethod());
    }

    @Test
    public void isValid() {
        ValidationResult result1 = new Link().isValid();
        assertEquals(ValidationResult.Status.INVALID, result1.getStatus());
        assertEquals(1, result1.getMessages().size());

        ValidationResult result2 = new Link()
                .withHref("test-href")
                .withMethod("test-method").isValid();
        assertEquals(ValidationResult.Status.VALID, result2.getStatus());
    }

}
