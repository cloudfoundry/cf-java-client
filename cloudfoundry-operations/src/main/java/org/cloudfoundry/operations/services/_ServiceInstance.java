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

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.List;

/**
 * A service instance
 */
@Value.Immutable
abstract class _ServiceInstance {

    /**
     * The bound applications
     */
    abstract List<String> getApplications();

    /**
     * The dashboard URL
     */
    @Nullable
    abstract String getDashboardUrl();

    /**
     * The service description
     */
    @Nullable
    abstract String getDescription();

    /**
     * The documentation URL
     */
    @Nullable
    abstract String getDocumentationUrl();

    /**
     * The service instance id
     */
    abstract String getId();

    /**
     * The last operation
     */
    @Nullable
    abstract String getLastOperation();

    /**
     * The message
     */
    @Nullable
    abstract String getMessage();

    /**
     * The service instance name
     */
    abstract String getName();

    /**
     * The managed service plan
     */
    @Nullable
    abstract String getPlan();

    /**
     * The name of the managed service
     */
    @Nullable
    abstract String getService();

    /**
     * When the service was last started
     */
    @Nullable
    abstract String getStartedAt();

    /**
     * The status of the last operation
     */
    @Nullable
    abstract String getStatus();

    /**
     * The tags for the service
     */
    abstract List<String> getTags();

    /**
     * The type of the service instance
     */
    abstract ServiceInstanceType getType();

    /**
     * When the service was last updated
     */
    @Nullable
    abstract String getUpdatedAt();

}
