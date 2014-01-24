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

import org.gradle.api.tasks.TaskAction

/**
 * Task used to add a service.
 *
 * @author Cedric Champeau
 * @author Scott Frederick
 */
@Mixin(ServiceCloudFoundryHelper)
class CreateServiceCloudFoundryTask extends AbstractCloudFoundryTask {
    CreateServiceCloudFoundryTask() {
        super()
        description = 'Creates a service, optionally binding it to an application'
    }

    @TaskAction
    void createService() {
        withCloudFoundryClient {
            createServices(serviceInfos)

            withApplicationIfExists {
                client.getApplication(application)

                serviceInfos.each { service ->
                    if (service.bind) {
                        log "Binding service ${service.name} to application ${application}"
                        client.bindService(application, service.name)
                    }
                }
            }
        }
    }
}
