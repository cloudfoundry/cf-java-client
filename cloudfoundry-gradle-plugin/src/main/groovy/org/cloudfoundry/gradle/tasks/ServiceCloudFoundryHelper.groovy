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

import org.cloudfoundry.client.lib.domain.CloudService
import org.cloudfoundry.client.lib.domain.CloudServiceOffering
import org.gradle.api.GradleException
import org.cloudfoundry.gradle.CloudFoundryServiceExtension

class ServiceCloudFoundryHelper {
    void createService(CloudFoundryServiceExtension service) {
        List<CloudService> services = client.services
        CloudService foundService = services.find {
            it.name.equals(service.name)
        }
        if (foundService) {
            if (verboseEnabled) {
                logVerbose "Service ${service.name} already exists, not creating"
            }
            return
        }

        if (service.label == 'user-provided') {
            log "Creating service ${service.name}"
            CloudService cloudService = new CloudService(name: service.name)
            client.createUserProvidedService(cloudService, service.userProvidedCredentials as Map<String, Object>)
        } else {
            List<CloudServiceOffering> offerings = client.serviceOfferings
            CloudServiceOffering offering = offerings.find {
                it.label == service.label
            }
            if (!offering) {
                throw new GradleException("No matching service with label '${service.label}' found")
            }

            String ver = service.version ?: offering.version

            log "Creating service ${service.name}"
            CloudService cloudService = new CloudService(name: service.name, label: service.label,
                    provider: service.provider, version: ver, plan: service.plan)
            client.createService(cloudService)
        }
    }
}
