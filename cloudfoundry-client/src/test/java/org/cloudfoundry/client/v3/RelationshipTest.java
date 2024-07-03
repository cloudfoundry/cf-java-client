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

package org.cloudfoundry.client.v3;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class RelationshipTest {

    @Test
    void noId() {
        assertThat(Relationship.builder().build().getId()).isNull();
    }

    @Test
    void nullId() {
        assertThat(Relationship.builder().id(null).build().getId()).isNull();
    }

    @Test
    void nonNullId() {
        assertThat(Relationship.builder().id("test-id").build().getId()).isEqualTo("test-id");
    }
}
