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

import org.cloudfoundry.client.lib.domain.CloudSpace
import org.cloudfoundry.gradle.text.FlexibleTableOutput
import org.gradle.api.tasks.TaskAction

/**
 * A task used to display available spaces.
 *
 * @author Scott Frederick
 */
@Mixin(AppStatusCloudFoundryHelper)
class SpacesCloudFoundryTask extends AbstractCloudFoundryTask {
    SpacesCloudFoundryTask() {
        super()
        description = 'Displays available spaces'
    }

    @TaskAction
    void showSpaces() {
        withCloudFoundryClient {
            List<CloudSpace> spaces = client.spaces
            CloudSpace current = currentSpace

            FlexibleTableOutput output = new FlexibleTableOutput()

            spaces.findAll { space ->
                space.organization.name == current.organization.name
            }
            .each { space ->
                output.addRow(name: space.name)
            }

            log "Spaces in org ${current.organization.name}\n\n" + output.toString()
        }
    }
}

