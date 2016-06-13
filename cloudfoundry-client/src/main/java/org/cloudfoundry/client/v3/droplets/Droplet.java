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

package org.cloudfoundry.client.v3.droplets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.Link;
import reactor.core.util.Exceptions;

import java.io.IOException;
import java.util.Map;

/**
 * Base class for responses that are droplets
 */
public abstract class Droplet {

    /**
     * The created at
     */
    @JsonProperty("created_at")
    @Nullable
    public abstract String getCreatedAt();

    /**
     * The environment variables
     */
    @JsonProperty("environment_variables")
    @Nullable
    public abstract Map<String, Object> getEnvironmentVariables();

    /**
     * The error
     */
    @JsonProperty("error")
    @Nullable
    public abstract String getError();

    /**
     * The id
     */
    @JsonProperty("guid")
    @Nullable
    public abstract String getId();

    /**
     * The lifecycle
     */
    @JsonProperty("lifecycle")
    @Nullable
    public abstract Lifecycle getLifecycle();

    /**
     * The links
     */
    @JsonProperty("links")
    @Nullable
    public abstract Map<String, Link> getLinks();

    /**
     * The results
     */
    @JsonDeserialize(using = ResultDeserializer.class)
    @JsonProperty("result")
    @Nullable
    public abstract Result getResult();

    /**
     * The staging disk in MB
     */
    @JsonProperty("staging_disk_in_mb")
    @Nullable
    public abstract Integer getStagingDiskInMb();

    /**
     * The staging memory in MB
     */
    @JsonProperty("staging_memory_in_mb")
    @Nullable
    public abstract Integer getStagingMemoryInMb();

    /**
     * The state
     */
    @JsonProperty("state")
    @Nullable
    public abstract State getState();

    /**
     * The updated at
     */
    @JsonProperty("updated_at")
    @Nullable
    public abstract String getUpdatedAt();

    public static final class ResultDeserializer extends StdDeserializer<Result> {

        private static final long serialVersionUID = 4299183263982676771L;

        ResultDeserializer() {
            super(Result.class);
        }

        @Override
        public Result deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            State state = getState(p.getParsingContext().getParent().getCurrentValue());

            switch (state) {
                case EXPIRED:
                    return p.readValueAs(ExpiredResult.class);
                case FAILED:
                    return p.readValueAs(FailedResult.class);
                case STAGED:
                    return p.readValueAs(StagedResult.class);
                default:
                    return null;
            }
        }

        private State getState(Object droplet) {
            try {
                return (State) droplet.getClass().getDeclaredField("state").get(droplet);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw Exceptions.propagate(e);
            }
        }
    }

}
