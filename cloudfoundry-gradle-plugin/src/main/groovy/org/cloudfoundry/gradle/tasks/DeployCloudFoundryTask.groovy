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

import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Tasks used to deploy variants of an application to a Cloud Foundry cloud.
 *
 * @author Scott Frederick
 */
@Mixin(ServiceCloudFoundryHelper)
@Mixin(PushCloudFoundryHelper)
@Mixin(DeployCloudFoundryHelper)
@Mixin(StartCloudFoundryHelper)
class DeployCloudFoundryTask extends AbstractCloudFoundryTask {
    DeployCloudFoundryTask() {
        super()
        description = 'Deploys a variants of an application'
    }

    @TaskAction
    void deploy() {
        withCloudFoundryClient {
            validateApplicationConfig()
            validateVariantsForDeploy()

            def apps = client.applications

            String next = findNextVariantToDeploy(application, apps)

            List<String> mappedAppVariants = findMappedVariants(application, apps)

            if (!next) {
                throw new GradleException("All variants are active, none available for deployment")
            }

            applyVariantSuffix(next)

            if (mappedAppVariants)
                log "Currently active variants are ${mappedAppVariants}, deploying ${application}"
            else
                log "No currently active variants, deploying ${application}"

            createServices(serviceInfos)

            createApplication()

            uploadApplication()

            if (startApp) {
                startApplication()
            }

            removeVariantSuffix()
        }
    }
}
