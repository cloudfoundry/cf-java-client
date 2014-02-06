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

import org.cloudfoundry.client.lib.domain.CloudServiceOffering
import org.cloudfoundry.gradle.text.FlexibleTableOutput
import org.gradle.api.tasks.TaskAction

/**
 * Task used to display service offerings.
 */
class ServiceOfferingsCloudFoundryTask extends AbstractCloudFoundryTask {
    ServiceOfferingsCloudFoundryTask() {
        super()
        description = 'Displays information about service offerings'
    }

    @TaskAction
    void showServiceOfferings() {
        withCloudFoundryClient {
            List<CloudServiceOffering> services = client.serviceOfferings

            if (services.isEmpty()) {
                log 'No services available'
            } else {
                FlexibleTableOutput output = new FlexibleTableOutput()

                services.each { CloudServiceOffering service ->
                    def plans = service.cloudServicePlans.collect { it.name }

                    output.addRow(service: service.name,
                            plans: plans.join(', '),
                            description: service.description)
                }

                log 'Services\n' + output.toString()
            }
        }
    }
}
