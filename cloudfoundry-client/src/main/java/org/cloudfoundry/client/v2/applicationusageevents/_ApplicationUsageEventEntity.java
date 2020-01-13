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

package org.cloudfoundry.client.v2.applicationusageevents;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The entity response payload for the Application Usage Event resource
 */
@JsonDeserialize
@Value.Immutable
abstract class _ApplicationUsageEventEntity {

    /**
     * The application id
     */
    @JsonProperty("app_guid")
    @Nullable
    abstract String getApplicationId();

    /**
     * The application name
     */
    @JsonProperty("app_name")
    @Nullable
    abstract String getApplicationName();

    /**
     * The buildpack id
     */
    @JsonProperty("buildpack_guid")
    @Nullable
    abstract String getBuildpackId();

    /**
     * The buildpack name
     */
    @JsonProperty("buildpack_name")
    @Nullable
    abstract String getBuildpackName();

    /**
     * The instance count
     */
    @JsonProperty("instance_count")
    @Nullable
    abstract Integer getInstanceCount();

    /**
     * The memory in mb by instance
     */
    @JsonProperty("memory_in_mb_per_instance")
    @Nullable
    abstract Integer getMemoryInMbPerInstance();

    /**
     * The organization id
     */
    @JsonProperty("org_guid")
    @Nullable
    abstract String getOrganizationId();

    /**
     * The state of the package
     */
    @JsonProperty("package_state")
    @Nullable
    abstract String getPackageState();

    /**
     * The parent application id if one exists (experimental)
     */
    @JsonProperty("parent_app_guid")
    @Nullable
    abstract String getParentApplicationId();

    /**
     * The parent application name if one exists (experimental)
     */
    @JsonProperty("parent_app_name")
    @Nullable
    abstract String getParentApplicationName();

    /**
     * The process type (experimental)
     */
    @JsonProperty("process_type")
    @Nullable
    abstract String getProcessType();

    /**
     * The space id
     */
    @JsonProperty("space_guid")
    @Nullable
    abstract String getSpaceId();

    /**
     * The space name
     */
    @JsonProperty("space_name")
    @Nullable
    abstract String getSpaceName();

    /**
     * The desired state of the application
     */
    @JsonProperty("state")
    @Nullable
    abstract String getState();

    /**
     * The task id if one exists (experimental)
     */
    @JsonProperty("task_guid")
    @Nullable
    abstract String getTaskId();

    /**
     * The task name if one exists (experimental)
     */
    @JsonProperty("task_name")
    @Nullable
    abstract String getTaskName();

}
