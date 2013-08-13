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
        int NAME_PAD = 15
        int VERSION_PAD = 10
        int PROVIDER_PAD = 15
        int PLANS_PAD = 12

        withCloudFoundryClient {
            List<CloudServiceOffering> services = client.serviceOfferings

            if (services.isEmpty()) {
                log 'No services available'
            } else {
                StringBuilder sb = new StringBuilder("Services\n")
                sb << "service".padRight(NAME_PAD)
                sb << "version".padRight(VERSION_PAD)
                sb << "provider".padRight(PROVIDER_PAD)
                sb << "plans".padRight(PLANS_PAD)
                sb << "description\n"
                services.each { CloudServiceOffering service ->
                    def plans = service.cloudServicePlans.collect { it.name }

                    sb << service.name.padRight(NAME_PAD)
                    sb << service.version.padRight(VERSION_PAD)
                    sb << service.provider.padRight(PROVIDER_PAD)
                    sb << plans.join(', ').padRight(PLANS_PAD)
                    sb << service.description
                    sb << '\n'
                }
                log sb.toString()
            }
        }
    }
}
