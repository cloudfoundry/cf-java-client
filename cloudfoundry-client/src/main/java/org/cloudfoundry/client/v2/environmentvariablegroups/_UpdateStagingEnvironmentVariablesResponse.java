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

package org.cloudfoundry.client.v2.environmentvariablegroups;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cloudfoundry.AllowNulls;
import org.immutables.value.Value;

import java.io.IOException;
import java.util.Map;

/**
 * The request payload for the update staging environment variable group
 */
@JsonDeserialize(using = _UpdateStagingEnvironmentVariablesResponse.UpdateStagingEnvironmentVariablesResponseDeserializer.class)
@Value.Immutable
abstract class _UpdateStagingEnvironmentVariablesResponse {

    /**
     * The environment variables
     */
    @AllowNulls
    abstract Map<String, Object> getEnvironmentVariables();

    static final class UpdateStagingEnvironmentVariablesResponseDeserializer extends StdDeserializer<UpdateStagingEnvironmentVariablesResponse> {

        private static final long serialVersionUID = 8275824722309754398L;

        UpdateStagingEnvironmentVariablesResponseDeserializer() {
            super(UpdateStagingEnvironmentVariablesResponse.class);
        }

        @Override
        public UpdateStagingEnvironmentVariablesResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return UpdateStagingEnvironmentVariablesResponse.builder()
                .environmentVariables(p.readValueAs(new TypeReference<Map<String, Object>>() {

                }))
                .build();
        }
    }

}
