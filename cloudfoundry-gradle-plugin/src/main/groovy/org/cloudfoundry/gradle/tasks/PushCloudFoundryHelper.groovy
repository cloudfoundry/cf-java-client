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
import org.cloudfoundry.client.lib.domain.CloudApplication
import org.cloudfoundry.client.lib.domain.Staging
import org.gradle.api.GradleException
import org.springframework.http.HttpStatus

class PushCloudFoundryHelper {
    void createApplication() {

        Staging staging = new Staging(command, buildpack, stack, healthCheckTimeout)
        List<String> serviceNames = serviceInfos.collect { it.name }

        CloudApplication app = getApplication(application)
        if (app) {
            log "Updating application ${application}"
            client.stopApplication(application)
            client.updateApplicationStaging(application, staging)
            if (memory) {
                client.updateApplicationMemory(application, memory)
            }
            if (diskQuota) {
                client.updateApplicationDiskQuota(application, diskQuota)
            }
            client.updateApplicationUris(application, allUris)
            client.updateApplicationServices(application, serviceNames)

            if (env) {
                client.updateApplicationEnv(application, getMergedEnv(app))
            }
        } else {
            log "Creating application ${application}"
            client.createApplication(application, staging, diskQuota, memory, allUris, serviceNames)

            if (env) {
                client.updateApplicationEnv(application, env)
            }
        }

        client.updateApplicationInstances(application, instances)
    }

    CloudApplication getApplication(String appName) {
        try {
            return client.getApplication(appName)
        } catch (CloudFoundryException e) {
            if (e.statusCode == HttpStatus.NOT_FOUND) {
                return null
            } else {
                throw e
            }
        }
    }

    def getMergedEnv(CloudApplication app) {
        if (!mergeEnv) {
            return env
        }

        def mergedEnv = app.getEnvAsMap()
        mergedEnv << env

        mergedEnv
    }

    void uploadApplication() {
        if (file.isDirectory()) {
            log "Uploading from ${file}"
        } else {
            log "Uploading file ${file}"
        }

        client.uploadApplication(application, file)
    }

    void validateApplicationConfig() {
        ensureFileExists()
    }

    void ensureFileExists() {
        if (!file || !file.exists()) {
            throw new GradleException("You must specify a valid file ('${file}' is not valid)")
        }
    }
}
