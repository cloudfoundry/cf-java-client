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

package org.cloudfoundry.uaa.clients;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cloudfoundry.uaa.IdentityZoned;
import org.immutables.value.Value;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * The request payload for the Batch Delete Clients operation
 */
@Value.Immutable
@JsonSerialize(using = _BatchDeleteClientsRequest.BatchDeleteClientsSerializer.class)
abstract class _BatchDeleteClientsRequest implements IdentityZoned {

    @Value.Check
    void checkClientIds() {
        if (this.getClientIds().isEmpty()) {
            throw new IllegalStateException("Cannot build BatchDeleteClientsRequest, required attribute clientIds is not set");
        }
    }

    /**
     * A list of identifiers of clients to delete
     */
    @JsonIgnore
    abstract List<String> getClientIds();

    static class BatchDeleteClientsSerializer extends StdSerializer<BatchDeleteClientsRequest> {

        private static final long serialVersionUID = 621601835573250942L;

        BatchDeleteClientsSerializer() {
            super(BatchDeleteClientsRequest.class);
        }

        @Override
        public void serialize(BatchDeleteClientsRequest request, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObject(request.getClientIds().stream()
                .map(clientId -> Collections.singletonMap("client_id", clientId))
                .toArray());
        }
    }

}
