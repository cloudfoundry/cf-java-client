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
 * A Basic task which can be used to map the application to the url on Cloud Foundry platform.
 *
 * @author Scott Frederick
 */
@Mixin(DeployCloudFoundryHelper)
class SwapDeployedCloudFoundryTask extends AbstractMapCloudFoundryTask {

    SwapDeployedCloudFoundryTask() {
        super()
        description = 'Swaps the URIs for deployed variants of an application'
    }

    @TaskAction
    void swapDeployedUris() {
        withCloudFoundryClient {
            validateVariantsForDeploy()

            List<CloudApplication> apps = client.applications

            List<String> mappedAppVariants = findMappedVariants(application, apps)
            List<String> unmappedAppVariants = findUnmappedVariants(application, apps)

            if (unmappedAppVariants) {
                log "Mapping URIs ${allUris} for ${unmappedAppVariants}"
            }
            if (mappedAppVariants) {
                log "Unmapping URIs ${allUris} for ${mappedAppVariants}"
            }

            withApplication {
                unmappedAppVariants.each { appName ->
                    project.cloudfoundry.application = appName
                    mapUrisToApplication()
                }

                mappedAppVariants.each { appName ->
                    project.cloudfoundry.application = appName
                    unmapUrisFromApplication()
                }
            }
        }
    }
}
