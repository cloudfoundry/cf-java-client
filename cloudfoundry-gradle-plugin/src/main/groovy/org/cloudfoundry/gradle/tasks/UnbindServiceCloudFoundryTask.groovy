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
import org.cloudfoundry.gradle.CloudFoundryServiceExtension
import org.gradle.api.tasks.TaskAction

/**
 * Task used to unbind a service from an application.
 *
 * @author Cedric Champeau
 */
class UnbindServiceCloudFoundryTask extends AbstractCloudFoundryTask {
    UnbindServiceCloudFoundryTask() {
        super()
        description = 'Unbinds services from an application'
    }

    @TaskAction
    void unbindService() {
        withCloudFoundryClient {
            withApplication {
                CloudApplication app = client.getApplication(application)
                List<String> servicesNames = app.services

                serviceInfos.each { CloudFoundryServiceExtension service ->
                    String serviceName = service.name

                    if (!servicesNames.contains(serviceName)) {
                        log "Service ${serviceName} is not bound to ${application}"
                    } else {
                        log "Unbinding service ${serviceName} from application ${application}"
                        client.unbindService(application, serviceName)
                    }
                }
            }
        }
    }
}
