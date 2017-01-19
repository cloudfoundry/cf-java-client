/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.bosh.tasks;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.immutables.value.Value;

import java.io.IOException;
import java.util.List;

/**
 * The response payload for the List Tasks operation
 */
@JsonDeserialize(using = _ListTasksResponse.ListTasksResponseDeserializer.class)
@Value.Immutable
abstract class _ListTasksResponse {

    /**
     * The tasks
     */
    abstract List<Task> getTasks();

    static final class ListTasksResponseDeserializer extends StdDeserializer<ListTasksResponse> {

        private static final long serialVersionUID = -7147649101380547159L;

        ListTasksResponseDeserializer() {
            super(ListTasksResponseDeserializer.class);
        }

        @Override
        public ListTasksResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return ListTasksResponse.builder()
                .tasks(p.readValueAs(new TypeReference<List<Task>>() {

                }))
                .build();
        }
    }

}
