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

import org.cloudfoundry.client.lib.domain.CloudApplication
import org.cloudfoundry.client.lib.domain.CloudService
import org.gradle.api.tasks.TaskAction

/**
 * Task used to display service instances.
 */
class ServicesCloudFoundryTask extends AbstractCloudFoundryTask {
    ServicesCloudFoundryTask() {
        super()
        description = 'Displays information about service instances'
    }

    @TaskAction
    void showServices() {
        int NAME_PAD = 17
        int SERVICE_PAD = 14
        int PROVIDER_PAD = 14
        int VERSION_PAD = 10
        int PLAN_PAD = 11

        withCloudFoundryClient {
            List<CloudService> services = client.services

            if (services.isEmpty()) {
                log 'No services'
            } else {
                def servicesToApps = mapServicesToApps(services)

                StringBuilder sb = new StringBuilder("Services\n")
                sb << "name".padRight(NAME_PAD)
                sb << "service".padRight(SERVICE_PAD)
                sb << "provider".padRight(PROVIDER_PAD)
                sb << "version".padRight(VERSION_PAD)
                sb << "plan".padRight(PLAN_PAD)
                sb << "bound apps\n"
                services.each { CloudService service ->
                    sb << service.name.padRight(NAME_PAD)
                    sb << service.label.padRight(SERVICE_PAD)
                    sb << service.provider.padRight(PROVIDER_PAD)
                    sb << service.version.padRight(VERSION_PAD)
                    sb << service.plan.padRight(PLAN_PAD)
                    sb << servicesToApps[service.name].join(', ')
                    sb << '\n'
                }
                log sb.toString()
            }
        }
    }

    protected def mapServicesToApps(def services) {
        def servicesToApps = [:]
        services.each { servicesToApps[it.name] = []}

        List<CloudApplication> apps = client.applications
        apps.each {
            CloudApplication app = client.getApplication(it.name)
            app.services.each { serviceName -> servicesToApps[serviceName] << app.name }
        }
        servicesToApps
    }
}
