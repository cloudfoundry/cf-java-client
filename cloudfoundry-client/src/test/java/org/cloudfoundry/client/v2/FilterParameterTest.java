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

import static org.cloudfoundry.client.v2.FilterParameter.Operation.GREATER_THAN;
import static org.cloudfoundry.client.v2.FilterParameter.Operation.GREATER_THAN_OR_EQUAL_TO;
import static org.cloudfoundry.client.v2.FilterParameter.Operation.IN;
import static org.cloudfoundry.client.v2.FilterParameter.Operation.IS;
import static org.cloudfoundry.client.v2.FilterParameter.Operation.LESS_THAN;
import static org.cloudfoundry.client.v2.FilterParameter.Operation.LESS_THAN_OR_EQUAL_TO;
import static org.junit.Assert.assertEquals;

public final class FilterParameterTest {

    @Test
    public void test() {
        assertEquals(">", GREATER_THAN.toString());
        assertEquals(">=", GREATER_THAN_OR_EQUAL_TO.toString());
        assertEquals(" IN ", IN.toString());
        assertEquals(":", IS.toString());
        assertEquals("<", LESS_THAN.toString());
        assertEquals("<=", LESS_THAN_OR_EQUAL_TO.toString());
    }

}
