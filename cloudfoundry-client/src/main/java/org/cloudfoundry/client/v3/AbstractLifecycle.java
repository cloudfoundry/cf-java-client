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

package org.cloudfoundry.client.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.immutables.value.Value;

import java.io.IOException;

/**
 * Represents the lifecycle of an application
 */
@JsonDeserialize
@Value.Immutable
abstract class AbstractLifecycle {

    /**
     * The datas
     */
    @JsonDeserialize(using = DataDeserializer.class)
    @JsonProperty("data")
    abstract Data getData();

    /**
     * The type
     */
    @JsonProperty("type")
    abstract String getType();

    static final class DataDeserializer extends StdDeserializer<Data> {

        private static final long serialVersionUID = -8299189647244106624L;

        DataDeserializer() {
            super(Data.class);
        }

        @SuppressWarnings("deprecation")
        @Override
        public Data deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            Lifecycle.Json lifecycle = (Lifecycle.Json) p.getParsingContext().getParent().getCurrentValue();

            switch (lifecycle.type.toLowerCase()) {
                case "buildpack":
                    return p.readValueAs(BuildpackData.class);
                case "docker":
                    return p.readValueAs(DockerData.class);
                default:
                    throw new IllegalArgumentException(String.format("Unknown lifecycle type: %s", lifecycle.type));
            }
        }
    }

}
