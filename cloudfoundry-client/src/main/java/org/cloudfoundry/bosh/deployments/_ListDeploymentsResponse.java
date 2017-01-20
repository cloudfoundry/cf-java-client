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

package org.cloudfoundry.bosh.deployments;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.immutables.value.Value;

import java.io.IOException;
import java.util.List;

/**
 * The response payload for the List Deployments operation
 */
@JsonDeserialize(using = _ListDeploymentsResponse.ListDeploymentsResponseDeserializer.class)
@Value.Immutable
abstract class _ListDeploymentsResponse {

    /**
     * The deployments
     */
    abstract List<Deployment> getDeployments();

    static final class ListDeploymentsResponseDeserializer extends StdDeserializer<ListDeploymentsResponse> {

        private static final long serialVersionUID = -1469605057307048011L;

        ListDeploymentsResponseDeserializer() {
            super(ListDeploymentsResponse.class);
        }

        @Override
        public ListDeploymentsResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return ListDeploymentsResponse.builder()
                .deployments(p.readValueAs(new TypeReference<List<Deployment>>() {

                }))
                .build();
        }

    }
}
