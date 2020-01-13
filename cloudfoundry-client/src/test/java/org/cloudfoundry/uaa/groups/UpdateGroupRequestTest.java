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

package org.cloudfoundry.uaa.groups;

import org.junit.Test;

public final class UpdateGroupRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noDisplayName() {
        UpdateGroupRequest.builder()
            .groupId("test-group-id")
            .version("*")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noId() {
        UpdateGroupRequest.builder()
            .identityZoneId("test-identity-zone-id")
            .displayName("group-test")
            .version("*")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noIdentityZoneId() {
        UpdateGroupRequest.builder()
            .displayName("group-test")
            .version("*")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noVersion() {
        UpdateGroupRequest.builder()
            .identityZoneId("test-identity-zone-id")
            .displayName("group-test")
            .groupId("test-group-id")
            .build();
    }

    @Test
    public void valid() {
        UpdateGroupRequest.builder()
            .identityZoneId("test-identity-zone-id")
            .displayName("group-test")
            .groupId("test-group-id")
            .version("*")
            .build();
    }

}
