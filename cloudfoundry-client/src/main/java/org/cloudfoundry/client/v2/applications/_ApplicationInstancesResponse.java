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

package org.cloudfoundry.client.v2.applications;

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
 * The response payload for the Get the instance information operation.
 */
@JsonDeserialize(using = _ApplicationInstancesResponse.ApplicationInstancesResponseDeserializer.class)
@Value.Immutable
abstract class _ApplicationInstancesResponse {

    /**
     * The instances
     */
    @AllowNulls
    abstract Map<String, ApplicationInstanceInfo> getInstances();

    static final class ApplicationInstancesResponseDeserializer extends StdDeserializer<ApplicationInstancesResponse> {

        private static final long serialVersionUID = -3557583833091104581L;

        ApplicationInstancesResponseDeserializer() {
            super(ApplicationInstancesResponse.class);
        }

        @Override
        public ApplicationInstancesResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return ApplicationInstancesResponse.builder()
                .instances(p.readValueAs(new TypeReference<Map<String, ApplicationInstanceInfo>>() {

                }))
                .build();
        }
    }

}
