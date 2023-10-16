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

package org.cloudfoundry.doppler;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class UuidUtilsTest {

    @Test
    void from() {
        org.cloudfoundry.dropsonde.events.UUID dropsondeUuid = new org.cloudfoundry.dropsonde.events.UUID.Builder()
            .high(0x79d4c3b2020e67a5L)
            .low(0x7243cc580bc17af4L)
            .build();

        assertEquals(UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"), UuidUtils.from(dropsondeUuid));
    }

}
