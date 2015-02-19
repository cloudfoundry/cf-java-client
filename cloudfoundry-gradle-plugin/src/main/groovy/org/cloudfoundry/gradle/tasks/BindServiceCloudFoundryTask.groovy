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
import org.gradle.api.tasks.TaskAction

/**
 * Task used to bind a service to an application.
 *
 * @author Cedric Champeau
 */
class BindServiceCloudFoundryTask extends AbstractCloudFoundryTask {
    BindServiceCloudFoundryTask() {
        super()
        description = 'Binds services to an application'
    }

    @TaskAction
    void bindService() {
        withCloudFoundryClient {
            withApplication {
                CloudApplication app = client.getApplication(application)
                List<String> servicesNames = app.services

                serviceInfos.each { serviceInfo ->
                    String serviceName = serviceInfo.name

                    if (!client.getService(serviceName)) {
                        log "Service ${serviceName} does not exist"
                    } else if (servicesNames.contains(serviceName)) {
                        log "Service ${serviceName} is already bound to ${application}"
                    } else {
                        log "Binding service ${serviceName} to application ${application}"
                        client.bindService(application, serviceName)
                    }
                }
            }
        }
    }
}
