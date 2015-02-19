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

import org.cloudfoundry.client.lib.domain.CloudInfo
import org.cloudfoundry.client.lib.domain.CloudSpace
import org.gradle.api.tasks.TaskAction

/**
 * A basic task which can be used to get info from Cloud Foundry platform.
 */
class InfoCloudFoundryTask extends AbstractCloudFoundryTask {

    InfoCloudFoundryTask() {
        super()
        description = 'Displays information about the target Cloud Foundry platform'
    }

    @TaskAction
    void info() {
        withCloudFoundryClient {
            CloudInfo info = client.cloudInfo
            CloudSpace space = currentSpace
            log """
API Endpoint: $target (API version: $info.version)
User: $info.user
Org: ${space?.organization?.name ?: 'n/a'}
Space: ${space?.name ?: 'n/a'}
"""
        }
    }
}
