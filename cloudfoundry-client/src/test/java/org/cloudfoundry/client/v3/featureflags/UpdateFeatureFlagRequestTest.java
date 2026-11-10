/*
 * Copyright 2026 the original author or authors.
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

package org.cloudfoundry.client.v3.featureflags;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class UpdateFeatureFlagRequestTest {

    @Test
    void noName() {
        assertThrows(
                IllegalStateException.class,
                () -> UpdateFeatureFlagRequest.builder().enabled(true).build());
    }

    @Test
    void valid() {
        UpdateFeatureFlagRequest.builder().name("app_scaling").enabled(true).build();
    }

    @Test
    void validWithCustomErrorMessage() {
        UpdateFeatureFlagRequest.builder()
                .name("app_scaling")
                .enabled(false)
                .customErrorMessage("This feature is disabled")
                .build();
    }

    @Test
    void validNoFields() {
        UpdateFeatureFlagRequest.builder().name("app_scaling").build();
    }
}
