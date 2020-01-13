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

import org.cloudfoundry.doppler.LogMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Applications Operations API
 */
public interface Applications {

    /**
     * Copy the source code from this application to another.
     *
     * @param request the copy source application request
     * @return a completion indicator
     */
    Mono<Void> copySource(CopySourceApplicationRequest request);

    /**
     * Deletes a specific application and, optionally, all routes mapped to the application.
     * <p>
     * Warning: deleting routes mapped to the application deletes them even if they are mapped to other applications.
     *
     * @param request the delete application request
     * @return a completion indicator
     */
    Mono<Void> delete(DeleteApplicationRequest request);

    /**
     * Disable SSH for a specific application
     *
     * @param request the disable application ssh request
     * @return a completion indicator
     */
    Mono<Void> disableSsh(DisableApplicationSshRequest request);

    /**
     * Enable SSH for a specific application
     *
     * @param request the enable application ssh request
     * @return a completion indicator
     */
    Mono<Void> enableSsh(EnableApplicationSshRequest request);

    /**
     * Gets information for a specific application
     *
     * @param request the get application request
     * @return the application
     */
    Mono<ApplicationDetail> get(GetApplicationRequest request);

    /**
     * Gets the manifest for a specific application
     *
     * @param request the get application manifest request
     * @return the application manifest
     */
    Mono<ApplicationManifest> getApplicationManifest(GetApplicationManifestRequest request);

    /**
     * Gets the environment variables for an application
     *
     * @param request the get application environments request
     * @return the application environments
     */
    Mono<ApplicationEnvironments> getEnvironments(GetApplicationEnvironmentsRequest request);

    /**
     * Gets recent events of an application.
     *
     * @param request the get application events request
     * @return the events
     */
    Flux<ApplicationEvent> getEvents(GetApplicationEventsRequest request);

    /**
     * Retrieve the Health Check Type of an application
     *
     * @param request the get health check request
     * @return the health check
     */
    Mono<ApplicationHealthCheck> getHealthCheck(GetApplicationHealthCheckRequest request);

    /**
     * Lists the applications
     *
     * @return the applications
     */
    Flux<ApplicationSummary> list();

    /**
     * Lists the tasks for an application
     *
     * @param request the list tasks request
     * @return the tasks
     */
    Flux<Task> listTasks(ListApplicationTasksRequest request);

    /**
     * List the applications logs
     *
     * @param request the application logs request
     * @return the applications logs
     */
    Flux<LogMessage> logs(LogsRequest request);

    /**
     * Push a specific application
     *
     * @param request the push application request
     * @return a completion indicator
     */
    Mono<Void> push(PushApplicationRequest request);

    /**
     * Push a manifest
     *
     * @param request the push manifest request
     * @return a completion indicator
     */
    Mono<Void> pushManifest(PushApplicationManifestRequest request);

    /**
     * Rename a specific application
     *
     * @param request the rename application request
     * @return a completion indicator
     */
    Mono<Void> rename(RenameApplicationRequest request);

    /**
     * Restarts a specific application
     *
     * @param request the restart application request
     * @return a completion indicator
     */
    Mono<Void> restage(RestageApplicationRequest request);

    /**
     * Restarts a specific application
     *
     * @param request the restart application request
     * @return a completion indicator
     */
    Mono<Void> restart(RestartApplicationRequest request);

    /**
     * Restart a specific application instance
     *
     * @param request the restart application instance request
     * @return a completion indicator
     */
    Mono<Void> restartInstance(RestartApplicationInstanceRequest request);

    /**
     * Run a one-off task on an application
     *
     * @param request the run task request
     * @return the task
     */
    Mono<Task> runTask(RunApplicationTaskRequest request);

    /**
     * Terminate a running task of an application
     *
     * @param request the terminate task request
     * @return a completion indicator
     */
    Mono<Void> terminateTask(TerminateApplicationTaskRequest request);

    /**
     * Scales a specific application
     *
     * @param request the scale application request
     * @return a completion indicator
     */
    Mono<Void> scale(ScaleApplicationRequest request);

    /**
     * Set an environment variable of an application
     *
     * @param request the set environment variable request
     * @return a completion indicator
     */
    Mono<Void> setEnvironmentVariable(SetEnvironmentVariableApplicationRequest request);

    /**
     * Set the Health Check Type of an application
     *
     * @param request the set health check request
     * @return a completion indicator
     */
    Mono<Void> setHealthCheck(SetApplicationHealthCheckRequest request);

    /**
     * Check if SSH is enabled for a specific application
     *
     * @param request the check application ssh enabled request
     * @return Boolean is ssh enabled on the application
     */
    Mono<Boolean> sshEnabled(ApplicationSshEnabledRequest request);

    /**
     * Starts a specific application or, if the application is already started, simply returns.
     *
     * @param request the start application request
     * @return a completion indicator
     */
    Mono<Void> start(StartApplicationRequest request);

    /**
     * Stops a specific application or, if the application is already stopped, simply returns.
     *
     * @param request the stop application request
     * @return a completion indicator
     */
    Mono<Void> stop(StopApplicationRequest request);

    /**
     * Unset an environment variable of an application
     *
     * @param request the unset environment variable request
     * @return a completion indicator
     */
    Mono<Void> unsetEnvironmentVariable(UnsetEnvironmentVariableApplicationRequest request);

}
