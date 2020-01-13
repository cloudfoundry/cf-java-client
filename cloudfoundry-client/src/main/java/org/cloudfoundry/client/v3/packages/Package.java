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

package org.cloudfoundry.client.v3.packages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.cloudfoundry.client.v3.Resource;

/**
 * Base class for responses that are packages
 */
public abstract class Package extends Resource {

    /**
     * The data for the package
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonSubTypes({
        @JsonSubTypes.Type(name = "bits", value = BitsData.class),
        @JsonSubTypes.Type(name = "docker", value = DockerData.class)
    })
    @JsonProperty("data")
    public abstract PackageData getData();

    /**
     * The state
     */
    @JsonProperty("state")
    public abstract PackageState getState();

    /**
     * The type
     */
    @JsonProperty("type")
    public abstract PackageType getType();

}
