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

public final class UpdateUserRequestTest {

    @Test(expected = IllegalStateException.class)
    public void incompleteName() {
        UpdateUserRequest.builder()
            .active(true)
            .email(Email.builder()
                .primary(true)
                .value("john.doe@pivotal.io")
                .build())
            .id("user-id")
            .version("*")
            .name(Name.builder()
                .givenName("John")
                .build())
            .userName("jdoe")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noId() {
        UpdateUserRequest.builder()
            .active(true)
            .email(Email.builder()
                .primary(true)
                .value("john.doe@pivotal.io")
                .build())
            .version("*")
            .name(Name.builder()
                .familyName("Doe")
                .givenName("John")
                .build())
            .userName("jdoe")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noName() {
        UpdateUserRequest.builder()
            .active(true)
            .email(Email.builder()
                .primary(true)
                .value("john.doe@pivotal.io")
                .build())
            .id("user-id")
            .version("*")
            .userName("jdoe")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noUsername() {
        UpdateUserRequest.builder()
            .active(true)
            .email(Email.builder()
                .primary(true)
                .value("john.doe@pivotal.io")
                .build())
            .id("user-id")
            .version("*")
            .name(Name.builder()
                .familyName("Doe")
                .givenName("John")
                .build())
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noVersion() {
        UpdateUserRequest.builder()
            .active(true)
            .email(Email.builder()
                .primary(true)
                .value("john.doe@pivotal.io")
                .build())
            .id("user-id")
            .name(Name.builder()
                .familyName("Doe")
                .givenName("John")
                .build())
            .userName("jdoe")
            .build();
    }

    @Test
    public void valid() {
        UpdateUserRequest.builder()
            .active(true)
            .email(Email.builder()
                .primary(true)
                .value("john.doe@pivotal.io")
                .build())
            .id("user-id")
            .version("*")
            .name(Name.builder()
                .familyName("Doe")
                .givenName("John")
                .build())
            .userName("jdoe")
            .build();
    }

}
