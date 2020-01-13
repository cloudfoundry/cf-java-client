/*
 * Copyright 2013-2020 the original author or authors.
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

import org.junit.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;


public final class MethodNameComparatorTest {

    private final MethodNameComparator comparator = MethodNameComparator.INSTANCE;

    @Test
    public void test() throws NoSuchMethodException {
        Method alpha = this.getClass().getDeclaredMethod("alpha");
        Method bravo = this.getClass().getDeclaredMethod("bravo");

        assertThat(this.comparator.compare(alpha, bravo)).isLessThan(0);
        assertThat(this.comparator.compare(bravo, alpha)).isGreaterThan(0);
        assertThat(this.comparator.compare(alpha, alpha)).isZero();
    }

    private void alpha() {
        // test fixture
    }

    private void bravo() {
        // text fixture
    }

}
