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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

final class UpdateFeatureFlagRequestSerializer extends StdSerializer<UpdateFeatureFlagRequest> {

    UpdateFeatureFlagRequestSerializer() {
        super(UpdateFeatureFlagRequest.class);
    }

    @Override
    public void serialize(
            UpdateFeatureFlagRequest value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        if (value.getEnabled() != null) {
            gen.writeBooleanField("enabled", value.getEnabled());
        }
        if (Boolean.TRUE.equals(value.clearCustomErrorMessage())) {
            gen.writeNullField("custom_error_message");
        } else if (value.getCustomErrorMessage() != null) {
            gen.writeStringField("custom_error_message", value.getCustomErrorMessage());
        }
        gen.writeEndObject();
    }
}
