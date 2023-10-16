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

package org.cloudfoundry.uaa.groups;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class AddMemberRequestTest {

    @Test
    void noGroupId() {
        assertThrows(IllegalStateException.class, () -> {
            AddMemberRequest.builder()
                .origin("test-origin")
                .memberId("test-member-id")
                .type(MemberType.USER)
                .build();
        });
    }

    @Test
    void noMemberId() {
        assertThrows(IllegalStateException.class, () -> {
            AddMemberRequest.builder()
                .groupId("test-group-id")
                .origin("test-origin")
                .type(MemberType.USER)
                .build();
        });
    }

    @Test
    void noOrigin() {
        assertThrows(IllegalStateException.class, () -> {
            AddMemberRequest.builder()
                .groupId("test-group-id")
                .memberId("test-member-id")
                .type(MemberType.USER)
                .build();
        });
    }

    @Test
    void noType() {
        assertThrows(IllegalStateException.class, () -> {
            AddMemberRequest.builder()
                .groupId("test-group-id")
                .memberId("test-member-id")
                .origin("test-origin")
                .build();
        });
    }

    @Test
    void valid() {
        AddMemberRequest.builder()
            .groupId("test-group-id")
            .memberId("test-member-id")
            .origin("test-origin")
            .type(MemberType.USER)
            .build();
    }

}
