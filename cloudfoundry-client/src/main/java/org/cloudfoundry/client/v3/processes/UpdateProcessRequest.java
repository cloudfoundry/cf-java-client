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

package org.cloudfoundry.client.v3.processes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request payload for the Update Process operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class UpdateProcessRequest implements Validatable {

    private volatile String command;

    private volatile String id;

    /**
     * Returns the command
     *
     * @return the command
     */
    @JsonProperty("command")
    public String getCommand() {
        return command;
    }

    /**
     * Configure the command
     *
     * @param command the command
     * @return {@code this}
     */
    public UpdateProcessRequest withCommand(String command) {
        this.command = command;
        return this;
    }

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
    public UpdateProcessRequest withId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult result = new ValidationResult();

        if (this.id == null) {
            result.invalid("id must be specified");
        }

        if (this.command == null) {
            result.invalid("command must be specified");
        }

        return result;
    }
}
