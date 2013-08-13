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
        int NAME_PAD = 30
        int STATUS_PAD = 10
        int USAGE_PAD = 11

        withCloudFoundryClient {
            List<CloudApplication> apps = client.applications

            if (apps.isEmpty()) {
                log 'No applications'
            } else {
                apps.sort { it.name }
                StringBuilder sb = new StringBuilder('Applications\n')
                sb << "name".padRight(NAME_PAD)
                sb << "status".padRight(STATUS_PAD)
                sb << "usage".padRight(USAGE_PAD)
                sb << "uris\n"
                apps.each { CloudApplication app ->
                    sb << app.name.padRight(NAME_PAD)
                    sb << health(app).padRight(STATUS_PAD)
                    sb << "${app.instances} x ${app.memory}M".padRight(USAGE_PAD)
                    sb << "${app.uris ? app.uris.join(', ') : 'none'}\n"
                }
                log sb.toString()
            }
            client.logout()
        }
    }
}
