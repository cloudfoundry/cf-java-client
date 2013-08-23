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
 * A basic task which can be used to display information about an application.
 */
@Mixin(AppStatusCloudFoundryHelper)
class AppCloudFoundryTask extends AbstractCloudFoundryTask {
    AppCloudFoundryTask() {
        super()
        description = 'Displays information about the application deployment'
    }

    @TaskAction
    void status() {
        withCloudFoundryClient {
            withApplication {
                CloudApplication app = client.getApplication(application)

                StringBuilder sb = new StringBuilder("Application\n")
                sb << "${app.name}: ${health(app)}\n"
                sb << "  usage: ${app.instances} x ${app.memory}M\n"
                sb << "  uris: ${app.uris ? app.uris.join(', ') : 'none'}\n"

                List<String> services = app.services
                if (!services.isEmpty()) {
                    sb << "  services: ${services.join(', ')}"
                }

                log sb.toString()
            }
        }
    }
}
