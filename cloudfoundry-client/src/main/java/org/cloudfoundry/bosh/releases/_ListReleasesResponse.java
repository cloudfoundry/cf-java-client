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

package org.cloudfoundry.bosh.releases;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.immutables.value.Value;

import java.io.IOException;
import java.util.List;

/**
 * The response payload for the List Releases operation
 */
@JsonDeserialize(using = _ListReleasesResponse.ListReleasesResponseDeserializer.class)
@Value.Immutable
abstract class _ListReleasesResponse {

    /**
     * The releases
     */
    abstract List<Release> getReleases();

    static final class ListReleasesResponseDeserializer extends StdDeserializer<ListReleasesResponse> {

        private static final long serialVersionUID = -5015096857014617894L;

        ListReleasesResponseDeserializer() {
            super(ListReleasesResponse.class);
        }

        @Override
        public ListReleasesResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return ListReleasesResponse.builder()
                .releases(p.readValueAs(new TypeReference<List<Release>>() {

                }))
                .build();
        }

    }

}
