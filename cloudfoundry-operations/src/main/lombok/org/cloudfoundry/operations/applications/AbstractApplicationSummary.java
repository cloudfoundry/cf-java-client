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

package org.cloudfoundry.operations.applications;

import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * A Cloud Foundry Application summary base class
 */
@Data
abstract class AbstractApplicationSummary {

    /**
     * The disk quota in bytes
     *
     * @param diskQuota the disk quota in bytes
     * @return the disk quota in bytes
     */
    private final Integer diskQuota;

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    private final String id;

    /**
     * The number of instances
     *
     * @param instances the number of instances
     * @return the number of instances
     */
    private final Integer instances;

    /**
     * The memory limit in bytes
     *
     * @param memoryLimit the memory limit in bytes
     * @return the memory limit in bytes
     */
    private final Integer memoryLimit;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The requested state
     *
     * @param name the requestedState
     * @return the requested state
     */
    private final String requestedState;

    /**
     * The number of running instances
     *
     * @param name the number of running instances
     * @return the number of running instances
     */
    private final Integer runningInstances;

    /**
     * The list of bound urls
     *
     * @param urls list of bound urls
     * @return the urls
     */
    private final List<String> urls;

    protected AbstractApplicationSummary(Integer diskQuota,
                                         String id,
                                         Integer instances,
                                         Integer memoryLimit,
                                         String name,
                                         String requestedState,
                                         Integer runningInstances,
                                         @Singular List<String> urls) {
        this.diskQuota = diskQuota;
        this.id = id;
        this.instances = instances;
        this.memoryLimit = memoryLimit;
        this.name = name;
        this.requestedState = requestedState;
        this.runningInstances = runningInstances;
        this.urls = urls;
    }

}
