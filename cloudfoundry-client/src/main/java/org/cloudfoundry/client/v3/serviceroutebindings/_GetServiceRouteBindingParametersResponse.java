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

package org.cloudfoundry.client.v3.serviceroutebindings;

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
 * The response payload for the Get Service Route Binding Parameters operation
 * 
 * see https://v3-apidocs.cloudfoundry.org/index.html#get-parameters-for-a-route-binding
 */
@JsonDeserialize(
        using =
                _GetServiceRouteBindingParametersResponse
                        .ServiceRouteBindingParametersResponseDeserializer.class)
@Value.Immutable
abstract class _GetServiceRouteBindingParametersResponse {

    /**
     * The parameters
     */
    @AllowNulls
    abstract Map<String, Object> getParameters();

    static final class ServiceRouteBindingParametersResponseDeserializer
            extends StdDeserializer<GetServiceRouteBindingParametersResponse> {

        private static final long serialVersionUID = 1L;

        ServiceRouteBindingParametersResponseDeserializer() {
            super(GetServiceRouteBindingParametersResponse.class);
        }

        @Override
        public GetServiceRouteBindingParametersResponse deserialize(
                JsonParser p, DeserializationContext ctxt) throws IOException {
            return GetServiceRouteBindingParametersResponse.builder()
                    .parameters(p.readValueAs(new TypeReference<Map<String, Object>>() {}))
                    .build();
        }
    }
}
