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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public final class CloudFoundryExceptionTest {

    @Test
    public void test() {
        Exception cause = new Exception();
        CloudFoundryException exception = new CloudFoundryException(-1, "test-description", "test-error-code", cause);

        assertEquals(Integer.valueOf(-1), exception.getCode());
        assertEquals("test-description", exception.getDescription());
        assertEquals("test-error-code", exception.getErrorCode());
        assertEquals("test-error-code(-1): test-description", exception.getMessage());
        assertEquals(cause, exception.getCause());

        assertNull(new CloudFoundryException(-1, "test-description", "test-error-code").getCause());
    }

}
