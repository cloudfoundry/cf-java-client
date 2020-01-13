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

package org.cloudfoundry.uaa.users;

import org.junit.Test;

public final class CreateUserRequestTest {

    @Test(expected = IllegalStateException.class)
    public void incompleteName() {
        CreateUserRequest.builder()
            .email(Email.builder()
                .primary(true)
                .value("test-email")
                .build())
            .name(Name.builder()
                .familyName("test-family-name")
                .build())
            .userName("test-userName")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noEmail() {
        CreateUserRequest.builder()
            .userName("test-userName")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noUserName() {
        CreateUserRequest.builder()
            .email(Email.builder()
                .primary(true)
                .value("test-email")
                .build())
            .build();
    }

    @Test
    public void valid() {
        CreateUserRequest.builder()
            .email(Email.builder()
                .primary(true)
                .value("test-email")
                .build())
            .userName("test-userName")
            .build();
    }

}
