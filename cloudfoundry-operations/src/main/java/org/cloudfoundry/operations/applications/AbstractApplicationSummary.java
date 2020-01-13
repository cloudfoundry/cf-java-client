/*
 * Copyright 2013-2020 the original author or authors.
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

import java.util.List;

/**
 * A Cloud Foundry Application summary base class
 */
public abstract class AbstractApplicationSummary {

    /**
     * The disk quota in bytes
     */
    public abstract Integer getDiskQuota();

    /**
     * The id
     */
    public abstract String getId();

    /**
     * The number of instances
     */
    public abstract Integer getInstances();

    /**
     * The memory limit in bytes
     */
    public abstract Integer getMemoryLimit();

    /**
     * The name
     */
    public abstract String getName();

    /**
     * The requested state
     */
    public abstract String getRequestedState();

    /**
     * The number of running instances
     */
    public abstract Integer getRunningInstances();

    /**
     * The list of bound urls
     */
    public abstract List<String> getUrls();

}
