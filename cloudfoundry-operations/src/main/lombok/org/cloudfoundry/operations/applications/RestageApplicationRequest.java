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
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.time.Duration;

/**
 * The request options for the restage application operation
 */
@Data
public final class RestageApplicationRequest implements Validatable {

    /**
     * The name of the application
     *
     * @param name the name of the application
     * @return the name of the application
     */
    private final String name;

    /**
     * How long to wait for staging
     *
     * @param stagingTimeout how long to wait for staging
     * @return how long to wait for staging
     */
    private final Duration stagingTimeout;

    /**
     * How long to wait for startup
     *
     * @param startupTimeout how long to wait for startup
     * @return how long to wait for startup
     */
    private final Duration startupTimeout;

    @Builder
    RestageApplicationRequest(String name, Duration stagingTimeout, Duration startupTimeout) {
        this.name = name;
        this.stagingTimeout = stagingTimeout;
        this.startupTimeout = startupTimeout;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("name must be specified");
        }

        return builder.build();
    }

}
