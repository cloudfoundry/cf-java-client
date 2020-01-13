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

package org.cloudfoundry.uaa.identityzones;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.immutables.value.Value;

import java.io.IOException;
import java.util.List;

/**
 * The response from the list identity zones request
 */
@JsonDeserialize(using = _ListIdentityZonesResponse.ListIdentityZonesResponseDeserializer.class)
@Value.Immutable
abstract class _ListIdentityZonesResponse {

    /**
     * The identity zones
     */
    abstract List<IdentityZone> getIdentityZones();

    static final class ListIdentityZonesResponseDeserializer extends StdDeserializer<ListIdentityZonesResponse> {

        private static final long serialVersionUID = 3381296378901733925L;

        ListIdentityZonesResponseDeserializer() {
            super(ListIdentityZonesResponse.class);
        }

        @Override
        @SuppressWarnings("unchecked")
        public ListIdentityZonesResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return ListIdentityZonesResponse.builder()
                .identityZones((List<IdentityZone>) p.readValueAs(new TypeReference<List<IdentityZone>>() {

                }))
                .build();
        }

    }

}
