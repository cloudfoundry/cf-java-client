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

package org.cloudfoundry.client.v3;

import org.immutables.value.Value;
import org.junit.Test;

public final class ResourceTest {

    @Test(expected = IllegalStateException.class)
    public void noCreatedAt() {
        StubResource.builder()
            .id("test-id")
            .updatedAt("test-updated-at")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noId() {
        StubResource.builder()
            .createdAt("test-created-at")
            .updatedAt("test-updated-at")
            .build();
    }

    @Test
    public void valid() {
        StubResource.builder()
            .createdAt("test-created-at")
            .id("test-id")
            .updatedAt("test-updated-at")
            .build();
    }

    @Value.Immutable
    static abstract class _StubResource extends Resource {

    }

}
