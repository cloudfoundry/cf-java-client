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

import org.cloudfoundry.client.lib.domain.CloudRoute
import org.gradle.api.tasks.TaskAction

/**
 * Task used to delete orphaned routes.
 */
class DeleteOrphanedRoutesCloudFoundryTask extends AbstractCloudFoundryTask {
    DeleteOrphanedRoutesCloudFoundryTask() {
        super()
        description = 'Deletes all orphaned routes'
    }

    @TaskAction
    void deleteOrphanedRoutes() {
        withCloudFoundryClient {
            log "Getting routes"
            List<CloudRoute> routes = client.deleteOrphanedRoutes()
            routes.each { route ->
                log "Deleted route ${route.name}"
            }
        }
    }
}
