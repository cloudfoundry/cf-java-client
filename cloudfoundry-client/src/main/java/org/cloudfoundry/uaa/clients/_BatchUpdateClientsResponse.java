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
 * The response from the Batch Update Clients request
 */
@JsonDeserialize(using = _BatchUpdateClientsResponse.BatchUpdateClientsResponseDeserializer.class)
@Value.Immutable
abstract class _BatchUpdateClientsResponse {

    /**
     * The updated clients
     */
    abstract List<Client> getClients();


    static class BatchUpdateClientsResponseDeserializer extends StdDeserializer<BatchUpdateClientsResponse> {

        private static final long serialVersionUID = 7236348519922670190L;

        BatchUpdateClientsResponseDeserializer() {
            super(BatchUpdateClientsResponse.class);
        }

        @Override
        public BatchUpdateClientsResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return BatchUpdateClientsResponse.builder()
                .clients(p.readValueAs(new TypeReference<List<Client>>() {

                }))
                .build();
        }
    }
}
