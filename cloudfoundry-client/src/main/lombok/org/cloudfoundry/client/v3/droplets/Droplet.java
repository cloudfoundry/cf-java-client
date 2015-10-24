/*
 * Copyright 2013-2015 the original author or authors.
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
import lombok.Data;
import lombok.Singular;
import org.cloudfoundry.client.v3.Hash;
import org.cloudfoundry.client.v3.Link;

import java.util.Map;

/**
 * Base class for responses that are droplets
 */
@Data
public abstract class Droplet {

    /**
     * The buildpack
     *
     * @param buildpack the buildpack
     * @return the buildpack
     */
    private final String buildpack;

    /**
     * The created at
     *
     * @param createdAt the created at
     * @return the created at
     */
    private final String createdAt;

    /**
     * The environment variables
     *
     * @param environmentVariables the environment variables
     * @return the environment variables
     */
    private final Map<String, Object> environmentVariables;

    /**
     * The error
     *
     * @param error the error
     * @return the error
     */
    private final String error;

    /**
     * The hash
     *
     * @param hash the hash
     * @return the hash
     */
    private final Hash hash;

    /**
     * The id
     *
     * @param id the id
     * @return id
     */
    private final String id;

    /**
     * The links
     *
     * @param links the links
     * @return links
     */
    private final Map<String, Link> links;

    /**
     * The procfile
     *
     * @param procfile the procfile
     * @return the procfile
     */
    private final String procfile;

    /**
     * The state
     *
     * @param state the state
     * @return the state
     */
    private final String state;

    /**
     * The updated at
     *
     * @param updatedAt the updated at
     * @return the updated at
     */
    private final String updatedAt;

    protected Droplet(@JsonProperty("buildpack") String buildpack,
                      @JsonProperty("created_at") String createdAt,
                      @JsonProperty("environment_variables") @Singular Map<String, Object> environmentVariables,
                      @JsonProperty("error") String error,
                      @JsonProperty("hash") Hash hash,
                      @JsonProperty("guid") String id,
                      @JsonProperty("_links") @Singular Map<String, Link> links,
                      @JsonProperty("procfile") String procfile,
                      @JsonProperty("state") String state,
                      @JsonProperty("updated_at") String updatedAt) {
        this.buildpack = buildpack;
        this.createdAt = createdAt;
        this.environmentVariables = environmentVariables;
        this.error = error;
        this.hash = hash;
        this.id = id;
        this.links = links;
        this.procfile = procfile;
        this.state = state;
        this.updatedAt = updatedAt;
    }

}
