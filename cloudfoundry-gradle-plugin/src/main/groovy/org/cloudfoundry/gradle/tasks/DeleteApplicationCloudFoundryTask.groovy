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

/**
 * Task used to delete an application. If the application name is '*', then
 * all applications are deleted.
 */
@Mixin(PushCloudFoundryHelper)
class DeleteApplicationCloudFoundryTask extends AbstractCloudFoundryTask {
    DeleteApplicationCloudFoundryTask() {
        super()
        description = 'Deletes an application deployment'
    }

    @TaskAction
    void deleteApplication() {
        withCloudFoundryClient {
            if ('*' == application) {
                log "Deleting all applications"
                client.deleteAllApplications()
                log "Deleted successfully"
            } else {
                if (applicationExists(application)) {
                    log "Deleting application ${application}"
                    client.deleteApplication(application)
                    log "Deleted successfully"
                } else {
                    log "Application ${application} not found"
                }
            }
        }
    }
}
