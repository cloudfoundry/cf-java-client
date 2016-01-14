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
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * A Cloud Foundry Application as returned by get
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ApplicationDetail extends AbstractApplicationSummary {

    /**
     * The buildpack, if any, used to stage this application
     *
     * @param buildpack the buildpack
     * @return the buildpack
     */
    private final String buildpack;

    /**
     * The list of instances
     *
     * @param instanceDetails the list of instances
     * @return the list of instances
     */
    private final List<InstanceDetail> instanceDetails;

    /**
     * The time the application was last updated
     *
     * @param lastUploaded the time of the last application update
     * @return the time of the last application update
     */
    private final Date lastUploaded;

    /**
     * The requested state
     *
     * @param requestedState the requestedState
     * @return the requested state
     */
    private final String requestedState;

    /**
     * The name of the stack running the application
     *
     * @param stack the name of the application's stack
     * @return the name of the application's stack
     */
    private final String stack;

    @Builder
    private ApplicationDetail(String buildpack,
                              Integer diskQuota,
                              String id,
                              @Singular List<InstanceDetail> instanceDetails,
                              Integer instances,
                              Date lastUploaded,
                              Integer memoryLimit,
                              String name,
                              String requestedState,
                              Integer runningInstances,
                              String stack,
                              @Singular List<String> urls) {
        super(diskQuota, id, instances, memoryLimit, name, requestedState, runningInstances, urls);
        this.buildpack = buildpack;
        this.instanceDetails = instanceDetails;
        this.lastUploaded = lastUploaded;
        this.requestedState = requestedState;
        this.stack = stack;
    }

    /**
     * Information about an instance of an application
     */
    @Data
    public static final class InstanceDetail {

        /**
         * The CPU consumption of this instance
         *
         * @param cpu the CPU consumption
         * @return the CPU consumption
         */
        private final Double cpu;

        /**
         * The diskUsage quota, in bytes, of this instance
         *
         * @param diskQuota the diskUsage quota
         * @return the diskUsage quota
         */
        private final Long diskQuota;

        /**
         * The disk usage, in bytes, of this instance
         *
         * @param diskUsage the disk usage
         * @return the disk usage
         */
        private final Long diskUsage;

        /**
         * The memoryUsage quota, in bytes, of this instance
         *
         * @param memoryQuota the memoryUsage quota
         * @return the memoryUsage quota
         */
        private final Long memoryQuota;

        /**
         * The memory usage, in bytes, of this instance
         *
         * @param memoryUsage the memory usage
         * @return the memory usage
         */
        private final Long memoryUsage;

        /**
         * The time this instance was created
         *
         * @param since the creation time
         * @return the creation time
         */
        private final Date since;

        /**
         * The state of the instance
         *
         * @param state the state
         * @return the state
         */
        private final String state;

        @Builder
        private InstanceDetail(Double cpu, Long diskQuota, Long diskUsage, Long memoryQuota, Long memoryUsage, Date since, String state) {
            this.cpu = cpu;
            this.diskQuota = diskQuota;
            this.diskUsage = diskUsage;
            this.memoryQuota = memoryQuota;
            this.memoryUsage = memoryUsage;
            this.since = since;
            this.state = state;
        }
    }

}
