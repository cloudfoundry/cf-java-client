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

package org.cloudfoundry.uaa.identityproviders;


import org.junit.Test;

public final class LockoutPolicyTest {

    @Test(expected = IllegalStateException.class)
    public void noLockAccountPeriodInSecond() {
        LockoutPolicy.builder()
            .lockoutPeriodInSecond(0)
            .numberOfAllowedFailures(0)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noLockoutPeriodInSecond() {
        LockoutPolicy.builder()
            .lockAccountPeriodInSecond(0)
            .numberOfAllowedFailures(0)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noNumberOfAllowedFailures() {
        LockoutPolicy.builder()
            .lockAccountPeriodInSecond(0)
            .lockoutPeriodInSecond(0)
            .build();
    }

    @Test
    public void valid() {
        LockoutPolicy.builder()
            .lockAccountPeriodInSecond(0)
            .lockoutPeriodInSecond(0)
            .numberOfAllowedFailures(0)
            .build();
    }

}
