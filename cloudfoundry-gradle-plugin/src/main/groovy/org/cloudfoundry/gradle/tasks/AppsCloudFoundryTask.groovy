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

import org.cloudfoundry.gradle.text.FlexibleTableOutput
import org.gradle.api.tasks.TaskAction
import org.cloudfoundry.client.lib.domain.CloudApplication

/**
 * Task which lists applications deployed on CloudFoundry.
 */
@Mixin(AppStatusCloudFoundryHelper)
class AppsCloudFoundryTask extends AbstractCloudFoundryTask {
    AppsCloudFoundryTask() {
        super()
        description = 'Lists applications running on the targeted Cloud Foundry platform'
    }

    @TaskAction
    void showApps() {
        withCloudFoundryClient {
            List<CloudApplication> apps = client.applications

            if (apps.isEmpty()) {
                log 'No applications'
            } else {
                FlexibleTableOutput output = new FlexibleTableOutput()

                apps.sort { it.name }
                apps.each { CloudApplication app ->
                    output.addRow(name: app.name,
                            status: health(app),
                            usage: "${app.instances} x ${app.memory}M",
                            uris: "${app.uris ? app.uris.join(', ') : 'none'}")
                }

                log 'Applications\n' + output.toString()
            }
        }
    }
}
