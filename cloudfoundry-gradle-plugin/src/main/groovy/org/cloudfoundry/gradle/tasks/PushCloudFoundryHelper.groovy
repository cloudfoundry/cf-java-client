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
import org.cloudfoundry.client.lib.domain.Staging
import org.gradle.api.GradleException
import org.springframework.http.HttpStatus

class PushCloudFoundryHelper {
    void createApplication() {

        Staging staging = new Staging(command, buildpack, stack, healthCheckTimeout)
        List<String> serviceNames = serviceInfos.collect { it.name }

        if (applicationExists(application)) {
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
        } else {
            log "Creating application ${application}"
            client.createApplication(application, staging, diskQuota, memory, allUris, serviceNames)
        }

        if (env) {
            client.updateApplicationEnv(application, env)
        }

        client.updateApplicationInstances(application, instances)
    }

    boolean applicationExists(String appName) {
        try {
            client.getApplication(appName)
            return true
        } catch (CloudFoundryException e) {
            if (e.statusCode == HttpStatus.NOT_FOUND) {
                return false
            } else {
                throw e
            }
        }
    }

    void uploadApplication() {
        log "Uploading ${file}"

        client.uploadApplication(application, file)
    }

    void validateApplicationConfig() {
        ensureFileExists()
    }

    void ensureFileExists() {
        if (!file || !file.isFile()) {
            throw new GradleException("You must specify a valid file ('${file}' is not valid)")
        }
    }
}
