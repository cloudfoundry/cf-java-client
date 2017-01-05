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

package org.cloudfoundry.client.v3.packages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Link;
import reactor.core.Exceptions;

import java.io.IOException;
import java.util.Map;

/**
 * Base class for responses that are packages
 */
public abstract class Package {

    /**
     * The created at
     */
    @JsonProperty("created_at")
    @Nullable
    public abstract String getCreatedAt();

    /**
     * The datas
     */
    @JsonDeserialize(using = DataDeserializer.class)
    @JsonProperty("data")
    @Nullable
    public abstract Data getData();

    /**
     * The id
     */
    @JsonProperty("guid")
    @Nullable
    public abstract String getId();

    /**
     * The links
     */
    @AllowNulls
    @JsonProperty("links")
    @Nullable
    public abstract Map<String, Link> getLinks();

    /**
     * The state
     */
    @JsonProperty("state")
    @Nullable
    public abstract State getState();

    /**
     * The type
     */
    @JsonProperty("type")
    @Nullable
    public abstract PackageType getType();

    /**
     * The updated at
     */
    @JsonProperty("updated_at")
    @Nullable
    public abstract String getUpdatedAt();

    public static final class DataDeserializer extends StdDeserializer<Data> {

        private static final long serialVersionUID = 8880263435485035323L;

        DataDeserializer() {
            super(Data.class);
        }

        @Override
        public Data deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            PackageType packageType = getPackageType(p.getParsingContext().getParent().getCurrentValue());

            switch (packageType) {
                case BITS:
                    return p.readValueAs(BitsData.class);
                case DOCKER:
                    return p.readValueAs(DockerData.class);
                default:
                    throw new IllegalArgumentException(String.format("Unknown package type: %s", packageType));
            }
        }

        private PackageType getPackageType(Object packag) {
            try {
                return (PackageType) packag.getClass().getDeclaredField("type").get(packag);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw Exceptions.propagate(e);
            }
        }

    }

}
