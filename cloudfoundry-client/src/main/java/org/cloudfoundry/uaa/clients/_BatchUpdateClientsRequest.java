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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.uaa.IdentityZoned;
import org.immutables.value.Value;

import java.io.IOException;
import java.util.List;

/**
 * The request payload for the Batch Update Clients operation
 */
@Value.Immutable
@JsonSerialize(using = _BatchUpdateClientsRequest.BatchUpdateClientsSerializer.class)
abstract class _BatchUpdateClientsRequest implements IdentityZoned {

    @Value.Check
    void checkClients() {
        if (this.getClients() == null) {
            throw new IllegalStateException("Cannot build BatchUpdateClientsRequest, required attribute clients is not set");
        }
    }

    /**
     * A list of clients to update
     */
    @Nullable
    @JsonIgnore
    abstract List<UpdateClient> getClients();


    static class BatchUpdateClientsSerializer extends StdSerializer<BatchUpdateClientsRequest> {

        private static final long serialVersionUID = -4801741701674524760L;

        BatchUpdateClientsSerializer() {
            super(BatchUpdateClientsRequest.class);
        }

        @Override
        public void serialize(BatchUpdateClientsRequest request, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObject(request.getClients());
        }
    }

}
