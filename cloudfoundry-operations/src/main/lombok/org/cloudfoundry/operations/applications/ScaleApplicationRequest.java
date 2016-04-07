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

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.time.Duration;

/**
 * The request options for the scale application operation
 */
@Data
public final class ScaleApplicationRequest implements Validatable {

    /**
     * The disk limit in MB
     *
     * @param diskLimit the disk limit in MB
     * @return the disk limit in MB
     */
    private final Integer diskLimit;

    @Getter(AccessLevel.NONE)
    private final String diskLimitInput;

    @Getter(AccessLevel.NONE)
    private final boolean diskLimitInvalid;

    /**
     * The number of instances
     *
     * @param instances the number of instances
     * @return the number of instances
     */
    private final Integer instances;

    /**
     * The memory limit in MB
     *
     * @param memoryLimit the memory limit in MB
     * @return the memory limit in MB
     */
    private final Integer memoryLimit;

    @Getter(AccessLevel.NONE)
    private final String memoryLimitInput;

    @Getter(AccessLevel.NONE)
    private final boolean memoryLimitInvalid;

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
    ScaleApplicationRequest(String diskLimit,
                            Integer instances,
                            String memoryLimit,
                            String name,
                            Duration stagingTimeout,
                            Duration startupTimeout) {
        this.diskLimitInput = diskLimit;
        this.diskLimit = parseForMb(diskLimit);
        this.diskLimitInvalid = null == this.diskLimit;

        this.instances = instances;

        this.memoryLimitInput = memoryLimit;
        this.memoryLimit = parseForMb(memoryLimit);
        this.memoryLimitInvalid = null == this.memoryLimit;

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

        if (this.diskLimitInput != null && this.diskLimitInvalid) {
            builder.message(String.format("disk limit (%s) specified incorrectly", this.diskLimitInput));
        }

        if (this.memoryLimitInput != null && this.memoryLimitInvalid) {
            builder.message(String.format("memory limit (%s) specified incorrectly", this.memoryLimitInput));
        }

        return builder.build();
    }

    private static Integer getPositiveValueTimes(int factor, String intString) {
        try {
            int result = factor * Integer.valueOf(intString);
            return (result <= 0) ? null : result;
        } catch (Exception x) {
            return null;
        }
    }

    private static Integer parseForMb(String limit) {
        if (null == limit || limit.length() < 2) {
            return null;
        }

        int lastIndex = limit.length() - 1;
        String numberPart = limit.substring(0, lastIndex);

        switch (limit.charAt(lastIndex)) {
            case 'm':
            case 'M':
                return getPositiveValueTimes(1, numberPart);
            case 'g':
            case 'G':
                return getPositiveValueTimes(1024, numberPart);
            default:
                return null;
        }
    }

}
