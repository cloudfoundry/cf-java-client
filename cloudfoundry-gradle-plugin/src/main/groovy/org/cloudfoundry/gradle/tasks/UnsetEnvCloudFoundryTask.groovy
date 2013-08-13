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
 * A basic task which can be used to delete an environment variable to an application on CloudFoundry platform.
 */
class UnsetEnvCloudFoundryTask extends AbstractEnvCloudFoundryTask {
    UnsetEnvCloudFoundryTask() {
        super()
        description = 'Deletes environment variables from an application'
    }

    @TaskAction
    void deleteEnvironmentVariables() {
        withCloudFoundryClient {
            log "Deleting environment variables for ${application}"

            withApplication {
                CloudApplication app = client.getApplication(application)

                def newEnv = modifyAppEnv(app) { existingEnv, passedEnv ->
                    existingEnv - passedEnv
                }

                if (verbose) {
                    listEnvironmentVariables(newEnv)
                }
            }
        }
    }
}
