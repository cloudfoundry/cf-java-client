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

package org.cloudfoundry.client.v2.resourcematch;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.immutables.value.Value;

import java.io.IOException;
import java.util.List;

/**
 * The request payload for the List Matching Resources operation
 */
@JsonSerialize(using = _ListMatchingResourcesRequest.ListMatchingResourcesRequestSerializer.class)
@Value.Immutable
abstract class _ListMatchingResourcesRequest {

    /**
     * The resources
     */
    abstract List<Resource> getResources();

    static final class ListMatchingResourcesRequestSerializer extends StdSerializer<ListMatchingResourcesRequest> {

        private static final long serialVersionUID = 9014636223275241673L;

        ListMatchingResourcesRequestSerializer() {
            super(ListMatchingResourcesRequest.class);
        }

        @Override
        public void serialize(ListMatchingResourcesRequest value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObject(value.getResources());
        }

    }

}
