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

package org.cloudfoundry.operations.useradmin;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class CreateUserRequestTest {

    @Test
    void bothOriginAndPassword() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateUserRequest.builder()
                            .origin("test-origin")
                            .password("test-password")
                            .username("test-username")
                            .build();
                });
    }

    @Test
    void noOriginOrPassword() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateUserRequest.builder().username("test-username").build();
                });
    }

    @Test
    void noUsername() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateUserRequest.builder().password("test-password").build();
                });
    }

    @Test
    void valid() {
        CreateUserRequest.builder().password("test-password").username("test-username").build();
    }
}
