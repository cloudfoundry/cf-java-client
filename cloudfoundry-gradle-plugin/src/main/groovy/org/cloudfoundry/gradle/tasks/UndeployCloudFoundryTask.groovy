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
 * Tasks used to undeploy unused variants of an application to a Cloud Foundry cloud.
 *
 * @author Scott Frederick
 */
@Mixin(DeployCloudFoundryHelper)
class UndeployCloudFoundryTask extends AbstractCloudFoundryTask {
    UndeployCloudFoundryTask() {
        super()
        description = 'Undeploys variants of an application'
    }

    @TaskAction
    void undeploy() {
        withCloudFoundryClient {
            validateVariantsForDeploy()

            List<CloudApplication> runningApps = client.applications

            List<String> unmappedAppVariants = findUnmappedVariants(application, runningApps)

            if (unmappedAppVariants.isEmpty()) {
                log "No candidates for undeploying found"
            }

            unmappedAppVariants.each { String appName ->
                project.cloudfoundry.application = appName
                log "Deleting application ${appName}"
                withApplication {
                    client.deleteApplication(appName)
                }
            }
        }
    }
}
