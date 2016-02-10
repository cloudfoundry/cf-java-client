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

import java.util.Map;

/**
 * An event of an application
 */
@Data
public final class ApplicationEnvironments {

    /**
     * The running environment variables
     *
     * @param running the running environment variables
     * @return the running environment variables
     */
    private final Map<String, Object> running;

    /**
     * The staging environment variables
     *
     * @param staging the staging environment variables
     * @return the staging environment variables
     */
    private final Map<String, Object> staging;

    /**
     * The system provided environment variables
     *
     * @param systemProvided the system provided environment variables
     * @return the system provided environment variables
     */
    private final Map<String, Object> systemProvided;

    /**
     * The user defined environment variables
     *
     * @param userProvided the user defined environment variables
     * @return the user defined environment variables
     */
    private final Map<String, Object> userProvided;

    @Builder
    ApplicationEnvironments(Map<String, Object> running,
                            Map<String, Object> staging,
                            Map<String, Object> systemProvided,
                            Map<String, Object> userProvided) {
        this.running = running;
        this.staging = staging;
        this.systemProvided = systemProvided;
        this.userProvided = userProvided;
    }
}
