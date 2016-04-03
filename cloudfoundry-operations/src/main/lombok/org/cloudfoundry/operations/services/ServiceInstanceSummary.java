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

package org.cloudfoundry.operations.services;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * A service instance summary
 */
@Data
public final class ServiceInstanceSummary {

    /**
     * The bound applications
     *
     * @param applications the bound applications
     * @return the bound applications
     */
    private final List<String> applications;

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    private final String id;

    /**
     * The last operation
     *
     * @param lastOperation the last operation
     * @return the last operation
     */
    private final String lastOperation;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The managed service plan
     *
     * @param plan the managed service plan
     * @return the managed service plan
     */
    private final String plan;

    /**
     * The managed service
     *
     * @param service the managed service
     * @return the managed service
     */
    private final String service;

    /**
     * The type of the service instance
     *
     * @param type the type of the service instance
     * @return the type of the service instance
     */
    private final ServiceInstanceType type;

    @Builder
    ServiceInstanceSummary(@Singular List<String> applications,
                           String id,
                           String lastOperation,
                           String name,
                           String plan,
                           String service,
                           ServiceInstanceType type) {
        this.applications = applications;
        this.id = id;
        this.lastOperation = lastOperation;
        this.name = name;
        this.plan = plan;
        this.service = service;
        this.type = type;
    }

}
