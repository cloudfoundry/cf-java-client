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

package org.cloudfoundry.client.v3.applications;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request payload for the Delete Application Process operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class DeleteApplicationProcessRequest implements Validatable {

    private volatile String id;

    private volatile String index;

    private volatile String type;

    /**
     * Returns the id
     *
     * @return the id
     */
    @JsonIgnore
    public String getId() {
        return this.id;
    }

    /**
     * Configure the id
     *
     * @param id the id
     * @return {@code this}
     */
    public DeleteApplicationProcessRequest withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns the index
     *
     * @return the index
     */
    @JsonIgnore
    public String getIndex() {
        return this.index;
    }

    /**
     * Configure the index
     *
     * @param index the index
     * @return {@code this}
     */
    public DeleteApplicationProcessRequest withIndex(String index) {
        this.index = index;
        return this;
    }

    /**
     * Returns the type
     *
     * @return the type
     */
    @JsonIgnore
    public String getType() {
        return this.type;
    }

    /**
     * Configure the type
     *
     * @param type the type
     * @return {@code this}
     */
    public DeleteApplicationProcessRequest withType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult result = new ValidationResult();

        if (this.id == null) {
            result.invalid("id must be specified");
        }

        if (this.index == null) {
            result.invalid("index must be specified");
        }

        if (this.type == null) {
            result.invalid("type must be specified");
        }

        return result;
    }

}
