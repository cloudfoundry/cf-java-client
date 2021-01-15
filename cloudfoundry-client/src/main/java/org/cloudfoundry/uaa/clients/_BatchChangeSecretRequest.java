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

package org.cloudfoundry.uaa.clients;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cloudfoundry.uaa.IdentityZoned;
import org.immutables.value.Value;

import java.io.IOException;
import java.util.List;

/**
 * The request payload for the Batch Create Clients operation
 */
@Value.Immutable
@JsonSerialize(using = _BatchChangeSecretRequest.BatchChangeSecretSerializer.class)
abstract class _BatchChangeSecretRequest implements IdentityZoned {

    @Value.Check
    void checkClients() {
        if (this.getChangeSecrets().isEmpty()) {
            throw new IllegalStateException("Cannot build BatchChangeSecretsRequest, required attribute changeSecret is not set");
        }
    }

    /**
     * A list of secrets to change
     */
    @JsonIgnore
    abstract List<ChangeSecret> getChangeSecrets();

    static class BatchChangeSecretSerializer extends StdSerializer<BatchChangeSecretRequest> {

        private static final long serialVersionUID = 8880285813370852371L;

        BatchChangeSecretSerializer() {
            super(BatchChangeSecretRequest.class);
        }

        @Override
        public void serialize(BatchChangeSecretRequest request, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObject(request.getChangeSecrets());
        }

    }

}
