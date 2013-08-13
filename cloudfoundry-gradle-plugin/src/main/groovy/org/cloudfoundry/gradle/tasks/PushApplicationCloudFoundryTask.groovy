/*
 * Copyright 2012 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.gradle.tasks

import org.cloudfoundry.client.lib.CloudFoundryException
import org.cloudfoundry.client.lib.StartingInfo
import org.cloudfoundry.client.lib.domain.Staging
import org.gradle.api.tasks.TaskAction
import org.gradle.api.GradleException
import org.springframework.http.HttpStatus

/**
 * Tasks used to push an application on a Cloud Foundry cloud.
 *
 * @author Cedric Champeau
 * @author Scott Frederick
 */
@Mixin(ServiceCloudFoundryHelper)
@Mixin(AppStatusCloudFoundryHelper)
class PushApplicationCloudFoundryTask extends AbstractCloudFoundryTask {
    PushApplicationCloudFoundryTask() {
        super()
        description = 'Pushes an application'
    }

    @TaskAction
    void push() {
        ensureFileExists()
        withCloudFoundryClient {
            checkValidMemory()

            serviceInfos.each { service ->
                createService(service)
            }

            log "Creating application ${application}"
            createApplication()

            if (env) {
                client.updateApplicationEnv(application, env)
            }

            if (instances > 0) {
                client.updateApplicationInstances(application, instances)
            }

            log "Uploading '${file}'"
            client.uploadApplication(application, file)

            if (startApp) {
                log "Starting ${application}"
                StartingInfo startingInfo = client.startApplication(application)
                // showStagingStatus(startingInfo) todo: configure RestTemplate to disable warnings
                showStartingStatus()
            } else {
                log "Application ${application} has been uploaded but not started (disabled by config)"
            }
        }
    }

    protected void createApplication() {
        Staging staging = new Staging(command, buildpack)
        List<String> serviceNames = serviceInfos.collect { it.name }

        boolean exists
        try {
            client.getApplication(application)
            exists = true
        } catch (CloudFoundryException e) {
            if (e.statusCode == HttpStatus.NOT_FOUND) {
                exists = false
            } else {
                throw e
            }
        }

        if (exists) {
            client.stopApplication(application)
            client.updateApplicationStaging(application, staging)
            client.updateApplicationMemory(application, memory)
            client.updateApplicationUris(application, allUris)
            client.updateApplicationServices(application, serviceNames)
        } else {
            client.createApplication(application, staging, memory, allUris, serviceNames)
        }
    }

    protected void ensureFileExists() {
        if (!file || !file.isFile()) {
            throw new GradleException("You must specify a valid file ('${file}' is not valid)")
        }
    }

    protected void checkValidMemory() {
        int requestedMemory = memory
        int[] memoryChoices = client.getApplicationMemoryChoices()

        if (!(requestedMemory in memoryChoices)) {
            throw new GradleException("Memory size $requestedMemory not allowed. Available memory sizes are: $memoryChoices")
        }
    }

}
