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

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * An event of an application
 */
@Data
public final class ApplicationEvent {

    /**
     * The actor
     *
     * @param actor the actor
     * @return the actor
     */
    private final String actor;

    /**
     * The description
     *
     * @param description the description
     * @return the description
     */
    private final String description;

    /**
     * The event
     *
     * @param event the event
     * @return the event
     */
    private final String event;

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    private final String id;

    /**
     * The time
     *
     * @param time the time
     * @return the time
     */
    private final Date time;

    @Builder
    ApplicationEvent(String actor,
                     String description,
                     String event,
                     String id,
                     Date time) {
        this.actor = actor;
        this.description = description;
        this.event = event;
        this.id = id;
        this.time = time;
    }
}
