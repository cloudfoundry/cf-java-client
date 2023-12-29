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

package org.cloudfoundry.logcache.v1;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

class LogTest {

    @Test
    void valid() {
        Log.builder().build();
    }

    @Test
    void getPayloadAsText() {
        final String payload = "This is a test.";
        final String encodedPayload =
                Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));

        assertThat(Log.builder().payload(encodedPayload).build().getPayloadAsText())
                .isEqualTo(payload);
    }
}
