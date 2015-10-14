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
 * The request payload for the Scale Process operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class ScaleProcessRequest implements Validatable {

    private volatile Integer diskInMb;

    private volatile String id;

    private volatile Integer instances;

    private volatile Integer memoryInMb;

    /**
     * Returns the diskInMb
     *
     * @return the diskInMb
     */
    @JsonProperty("disk_in_mb")
    public Integer getDiskInMb() {
        return this.diskInMb;
    }

    /**
     * Configure the diskInMb
     *
     * @param diskInMb the diskInMb
     * @return {@code this}
     */
    public ScaleProcessRequest withDiskInMb(Integer diskInMb) {
        this.diskInMb = diskInMb;
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
    public ScaleProcessRequest withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns the number of instances
     *
     * @return the instances
     */
    @JsonProperty("instances")
    public Integer getInstances() {
        return this.instances;
    }

    /**
     * Configure the number of instances
     *
     * @param instances the number of instances
     * @return {@code this}
     */
    public ScaleProcessRequest withInstances(Integer instances) {
        this.instances = instances;
        return this;
    }

    /**
     * Returns the memoryInMb
     *
     * @return the memoryInMb
     */
    @JsonProperty("memory_in_mb")
    public Integer getMemoryInMb() {
        return this.memoryInMb;
    }

    /**
     * Configure the memoryInMb
     *
     * @param memoryInMb the memoryInMb
     * @return {@code this}
     */
    public ScaleProcessRequest withMemoryInMb(Integer memoryInMb) {
        this.memoryInMb = memoryInMb;
        return this;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult result = new ValidationResult();

        if (this.id == null) {
            result.invalid("id must be specified");
        }

        return result;
    }
}
