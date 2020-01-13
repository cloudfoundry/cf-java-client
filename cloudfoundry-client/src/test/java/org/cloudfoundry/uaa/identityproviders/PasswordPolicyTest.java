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

public final class PasswordPolicyTest {

    @Test(expected = IllegalStateException.class)
    public void noMaximumLength() {
        PasswordPolicy.builder()
            .minimumDigit(0)
            .minimumLength(0)
            .minimumLowerCaseCharacter(0)
            .minimumSpecialCharacter(0)
            .minimumUpperCaseCharacter(0)
            .passwordExpirationInMonth(0)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noMinimumDigit() {
        PasswordPolicy.builder()
            .maximumLength(0)
            .minimumLength(0)
            .minimumLowerCaseCharacter(0)
            .minimumSpecialCharacter(0)
            .minimumUpperCaseCharacter(0)
            .passwordExpirationInMonth(0)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noMinimumLength() {
        PasswordPolicy.builder()
            .maximumLength(0)
            .minimumDigit(0)
            .minimumLowerCaseCharacter(0)
            .minimumSpecialCharacter(0)
            .minimumUpperCaseCharacter(0)
            .passwordExpirationInMonth(0)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noMinimumLowerCaseCharacter() {
        PasswordPolicy.builder()
            .maximumLength(0)
            .minimumDigit(0)
            .minimumLength(0)
            .minimumSpecialCharacter(0)
            .minimumUpperCaseCharacter(0)
            .passwordExpirationInMonth(0)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noMinimumSpecialCharacter() {
        PasswordPolicy.builder()
            .maximumLength(0)
            .minimumDigit(0)
            .minimumLength(0)
            .minimumLowerCaseCharacter(0)
            .minimumUpperCaseCharacter(0)
            .passwordExpirationInMonth(0)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noMinimumUpperCaseCharacter() {
        PasswordPolicy.builder()
            .maximumLength(0)
            .minimumDigit(0)
            .minimumLength(0)
            .minimumLowerCaseCharacter(0)
            .minimumSpecialCharacter(0)
            .passwordExpirationInMonth(0)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noPasswordExpirationInMonth() {
        PasswordPolicy.builder()
            .maximumLength(0)
            .minimumDigit(0)
            .minimumLength(0)
            .minimumLowerCaseCharacter(0)
            .minimumSpecialCharacter(0)
            .minimumUpperCaseCharacter(0)
            .build();
    }

    @Test
    public void valid() {
        PasswordPolicy.builder()
            .maximumLength(0)
            .minimumDigit(0)
            .minimumLength(0)
            .minimumLowerCaseCharacter(0)
            .minimumSpecialCharacter(0)
            .minimumUpperCaseCharacter(0)
            .passwordExpirationInMonth(0)
            .build();
    }

}
