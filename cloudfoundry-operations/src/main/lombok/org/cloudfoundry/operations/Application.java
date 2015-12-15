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

package org.cloudfoundry.operations;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * A Cloud Foundry Application
 */
@Data
public final class Application {

    /**
     * The disk
     *
     * @param disk the disk
     * @return the disk
     */
    private final Integer disk;

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
     * @param name the number of instances
     * @return the number of instances
     */
    private final Integer instances;

    /**
     * The memory
     *
     * @param memory the memory
     * @return the memory
     */
    private final Integer memory;

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

    @Builder
    private Application(Integer disk,
                        String id,
                        Integer instances,
                        Integer memory,
                        String name,
                        String requestedState,
                        Integer runningInstances,
                        @Singular List<String> urls) {

        this.disk = disk;
        this.id = id;
        this.instances = instances;
        this.memory = memory;
        this.name = name;
        this.requestedState = requestedState;
        this.runningInstances = runningInstances;
        this.urls = urls;
    }

}
