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

package org.cloudfoundry.doppler;

import org.junit.Test;

public final class ErrorTest {

    @Test
    public void dropsonde() {
        Error.from(new org.cloudfoundry.dropsonde.events.Error.Builder()
            .code(0)
            .message("test-message")
            .source("test-source")
            .build());
    }

    @Test(expected = IllegalStateException.class)
    public void noCode() {
        Error.builder()
            .message("test-message")
            .source("test-source")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noMessage() {
        Error.builder()
            .code(0)
            .source("test-source")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noSource() {
        Error.builder()
            .code(0)
            .message("test-message")
            .build();
    }

    @Test
    public void valid() {
        Error.builder()
            .code(0)
            .message("test-message")
            .source("test-source")
            .build();
    }

}
