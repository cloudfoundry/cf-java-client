/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.uaa.clients;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.immutables.value.Value;

import java.io.IOException;
import java.util.List;

/**
 * The response from the list clients request
 */
@JsonDeserialize(using = _ListMetadatasResponse.ListMetadatasResponseDeserializer.class)
@Value.Immutable
abstract class _ListMetadatasResponse {

    /**
     * The metadatas
     */
    abstract List<Metadata> getMetadatas();


    static final class ListMetadatasResponseDeserializer extends StdDeserializer<ListMetadatasResponse> {

        ListMetadatasResponseDeserializer() {
            super(ListMetadatasResponse.class);
        }

        @Override
        public ListMetadatasResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return ListMetadatasResponse.builder()
                .metadatas(p.readValueAs(new TypeReference<List<Metadata>>() {

                }))
                .build();
        }
    }
}
