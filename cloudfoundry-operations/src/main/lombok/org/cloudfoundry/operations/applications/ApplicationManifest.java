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
import lombok.Singular;

import java.util.List;
import java.util.Map;

/**
 * An application manifest which captures some of the details of how an application is deployed
 */
@Data
public final class ApplicationManifest {

    private final String buildpack;

    private final String command;

    private final int diskQuotaMB;

    private final List<String> domains;

    private final Map<String, Object> envs;

    private final List<String> hosts;

    private final int instances;

    private final int memoryMB;

    private final String name;

    private final List<String> services;

    private final String stack;

    private final int timeout;

    /*
     * This representation of an application manifest cannot preserve the correct relationship between domains and hosts.
     * See https://github.com/cloudfoundry/cli/issues/765 for more information.
     */
    @Builder
    ApplicationManifest(String buildpack,
                        String command,
                        int diskQuotaMB,
                        @Singular List<String> domains,
                        @Singular Map<String, Object> envs,
                        @Singular List<String> hosts,
                        int instances,
                        int memoryMB,
                        String name,
                        @Singular List<String> services,
                        String stack,
                        int timeout) {
        this.buildpack = buildpack;
        this.command = command;
        this.diskQuotaMB = diskQuotaMB;
        this.domains = domains;
        this.envs = envs;
        this.hosts = hosts;
        this.instances = instances;
        this.memoryMB = memoryMB;
        this.name = name;
        this.services = services;
        this.stack = stack;
        this.timeout = timeout;
    }

}
