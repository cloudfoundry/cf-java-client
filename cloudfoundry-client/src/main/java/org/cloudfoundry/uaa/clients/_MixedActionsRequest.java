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
import java.util.List;

/**
 * The request payload for the Batch Update Clients operation
 */
@JsonSerialize(using = _MixedActionsRequest.MixedActionsSerializer.class)
@Value.Immutable
abstract class _MixedActionsRequest implements IdentityZoned {

    @Value.Check
    void checkActions() {
        if (this.getActions().isEmpty()) {
            throw new IllegalStateException("Cannot build MixedActionsRequest, required attribute actions is not set");
        }
    }

    /**
     * A list of actions to perform
     */
    @JsonIgnore
    abstract List<Action> getActions();

    static class MixedActionsSerializer extends StdSerializer<MixedActionsRequest> {

        private static final long serialVersionUID = 8507863382046380145L;

        MixedActionsSerializer() {
            super(MixedActionsRequest.class);
        }

        @Override
        public void serialize(MixedActionsRequest request, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObject(request.getActions());
        }

    }

}
