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

package org.cloudfoundry.client.v2.appusageevents;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * The entity response payload for the Application Usage Event resource
 */
@Data
public final class ApplicationUsageEventEntity {

    /**
     * The application id
     *
     * @param applicationId the application id
     * @return the application id
     */
    private final String applicationId;

    /**
     * The application name
     *
     * @param applicationName the application name
     * @return the application name
     */
    private final String applicationName;

    /**
     * The buildpack id
     *
     * @param buildpackId the buildpack id
     * @return the buildpack id
     */
    private final String buildpackId;

    /**
     * The buildpack name
     *
     * @param buildpackName the build pack name
     * @return the buildpack name
     */
    private final String buildpackName;

    /**
     * The instance count
     *
     * @param instanceCount the number of instances of the application
     * @return the instance count
     */
    private final Integer instanceCount;

    /**
     * The memory in mb by instances
     *
     * @param memoryInMbPerInstances the memory per application instance
     * @return the memory in mb by instances
     */
    private final Integer memoryInMbPerInstances;

    /**
     * The organization id
     *
     * @param organizationId the organization id
     * @return the organization id
     */
    private final String organizationId;

    /**
     * The state of the package
     *
     * @param packageState the state of the package
     * @return the state of the package
     */
    private final String packageState;

    /**
     * The parent application id if one exists (experimental)
     *
     * @param parentApplicationId the parent application id
     * @return the parent application id
     */
    private final String parentApplicationId;

    /**
     * The parent application name if one exists (experimental)
     *
     * @param parentApplicationName the parent application name
     * @return the parent application name
     */
    private final String parentApplicationName;

    /**
     * The process type (experimental)
     *
     * @param processType the process type
     * @return the process type
     */
    private final String processType;

    /**
     * The space id
     *
     * @param spaceId the space id
     * @return the space id
     */
    private final String spaceId;

    /**
     * The space name
     *
     * @param spaceName the space name
     * @return the space name
     */
    private final String spaceName;

    /**
     * The desired state of the application
     *
     * @param state the desired state
     * @return the desired state
     */
    private final String state;

    /**
     * The task id if one exists (experimental)
     *
     * @param taskId the task id
     * @return the task id
     */
    private final String taskId;

    /**
     * The task name if one exists (experimental)
     *
     * @param taskName the task name
     * @return the task taskName
     */
    private final String taskName;

    @Builder
    ApplicationUsageEventEntity(@JsonProperty("app_guid") String applicationId,
                                @JsonProperty("app_name") String applicationName,
                                @JsonProperty("buildpack_guid") String buildpackId,
                                @JsonProperty("buildpack_name") String buildpackName,
                                @JsonProperty("instance_count") Integer instanceCount,
                                @JsonProperty("memory_in_mb_per_instance") Integer memoryInMbPerInstances,
                                @JsonProperty("org_guid") String organizationId,
                                @JsonProperty("package_state") String packageState,
                                @JsonProperty("parent_app_guid") String parentApplicationId,
                                @JsonProperty("parent_app_name") String parentApplicationName,
                                @JsonProperty("process_type") String processType,
                                @JsonProperty("space_guid") String spaceId,
                                @JsonProperty("space_name") String spaceName,
                                @JsonProperty("state") String state,
                                @JsonProperty("task_guid") String taskId,
                                @JsonProperty("task_name") String taskName) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.buildpackId = buildpackId;
        this.buildpackName = buildpackName;
        this.instanceCount = instanceCount;
        this.memoryInMbPerInstances = memoryInMbPerInstances;
        this.packageState = packageState;
        this.organizationId = organizationId;
        this.parentApplicationId = parentApplicationId;
        this.parentApplicationName = parentApplicationName;
        this.processType = processType;
        this.spaceId = spaceId;
        this.spaceName = spaceName;
        this.state = state;
        this.taskId = taskId;
        this.taskName = taskName;
    }

}
