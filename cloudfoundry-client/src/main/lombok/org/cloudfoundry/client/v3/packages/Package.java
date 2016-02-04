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

package org.cloudfoundry.client.v3.packages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Singular;
import org.cloudfoundry.client.v3.Link;

import java.util.Map;

/**
 * Base class for responses that are packages
 */
@Data
public abstract class Package {

    /**
     * The created at
     *
     * @param createdAt the created at
     * @return the created at
     */
    private final String createdAt;

    /**
     * The datas
     *
     * @param datas the datas
     * @return the datas
     */
    private final Map<String, Object> datas;

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    private final String id;

    /**
     * The links
     *
     * @param links the links
     * @return the links
     */
    private final Map<String, Link> links;

    /**
     * The state
     *
     * @param state the state
     * @return the state
     */
    private final String state;

    /**
     * The type
     *
     * @param type the type
     * @return the type
     */
    private final String type;

    /**
     * The updated at
     *
     * @param updatedAt the updated at
     * @return the updated at
     */
    private final String updatedAt;


    protected Package(@JsonProperty("created_at") String createdAt,
                      @JsonProperty("data") @Singular Map<String, Object> datas,
                      @JsonProperty("guid") String id,
                      @JsonProperty("links") @Singular Map<String, Link> links,
                      @JsonProperty("state") String state,
                      @JsonProperty("type") String type,
                      @JsonProperty("updated_at") String updatedAt) {
        this.createdAt = createdAt;
        this.datas = datas;
        this.id = id;
        this.links = links;
        this.state = state;
        this.type = type;
        this.updatedAt = updatedAt;
    }

}
