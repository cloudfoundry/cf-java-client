/*
 * Copyright 2013-2021 the original author or authors.
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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class PasswordPolicyTest {

    @Test
    void noMaximumLength() {
        assertThrows(IllegalStateException.class, () -> {
            PasswordPolicy.builder()
                .minimumDigit(0)
                .minimumLength(0)
                .minimumLowerCaseCharacter(0)
                .minimumSpecialCharacter(0)
                .minimumUpperCaseCharacter(0)
                .passwordExpirationInMonth(0)
                .build();
        });
    }

    @Test
    void noMinimumDigit() {
        assertThrows(IllegalStateException.class, () -> {
            PasswordPolicy.builder()
                .maximumLength(0)
                .minimumLength(0)
                .minimumLowerCaseCharacter(0)
                .minimumSpecialCharacter(0)
                .minimumUpperCaseCharacter(0)
                .passwordExpirationInMonth(0)
                .build();
        });
    }

    @Test
    void noMinimumLength() {
        assertThrows(IllegalStateException.class, () -> {
            PasswordPolicy.builder()
                .maximumLength(0)
                .minimumDigit(0)
                .minimumLowerCaseCharacter(0)
                .minimumSpecialCharacter(0)
                .minimumUpperCaseCharacter(0)
                .passwordExpirationInMonth(0)
                .build();
        });
    }

    @Test
    void noMinimumLowerCaseCharacter() {
        assertThrows(IllegalStateException.class, () -> {
            PasswordPolicy.builder()
                .maximumLength(0)
                .minimumDigit(0)
                .minimumLength(0)
                .minimumSpecialCharacter(0)
                .minimumUpperCaseCharacter(0)
                .passwordExpirationInMonth(0)
                .build();
        });
    }

    @Test
    void noMinimumSpecialCharacter() {
        assertThrows(IllegalStateException.class, () -> {
            PasswordPolicy.builder()
                .maximumLength(0)
                .minimumDigit(0)
                .minimumLength(0)
                .minimumLowerCaseCharacter(0)
                .minimumUpperCaseCharacter(0)
                .passwordExpirationInMonth(0)
                .build();
        });
    }

    @Test
    void noMinimumUpperCaseCharacter() {
        assertThrows(IllegalStateException.class, () -> {
            PasswordPolicy.builder()
                .maximumLength(0)
                .minimumDigit(0)
                .minimumLength(0)
                .minimumLowerCaseCharacter(0)
                .minimumSpecialCharacter(0)
                .passwordExpirationInMonth(0)
                .build();
        });
    }

    @Test
    void noPasswordExpirationInMonth() {
        assertThrows(IllegalStateException.class, () -> {
            PasswordPolicy.builder()
                .maximumLength(0)
                .minimumDigit(0)
                .minimumLength(0)
                .minimumLowerCaseCharacter(0)
                .minimumSpecialCharacter(0)
                .minimumUpperCaseCharacter(0)
                .build();
        });
    }

    @Test
    void valid() {
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
