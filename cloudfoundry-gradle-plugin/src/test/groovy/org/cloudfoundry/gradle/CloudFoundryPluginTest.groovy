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

package org.cloudfoundry.gradle

import org.cloudfoundry.client.lib.CloudFoundryOperations
import org.cloudfoundry.client.lib.domain.CloudEntity
import org.cloudfoundry.client.lib.domain.CloudInfo
import org.cloudfoundry.client.lib.domain.CloudOrganization
import org.cloudfoundry.client.lib.domain.CloudSpace
import org.cloudfoundry.gradle.tasks.AbstractCloudFoundryTask
import org.gradle.api.Task
import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*
import org.junit.Before
import static org.hamcrest.CoreMatchers.*

class CloudFoundryPluginTest {
    private Project project

    private StringBuilder logOutput
    private def mockClient = [:]

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'cloudfoundry'

        project.cloudfoundry.with {
            target = 'https://api.run.pivotal.io'
            username = 'test-user'
            password = 'test-password'
            space = 'test-space'
        }

        mockClient << [
                login: { "login" },
                logout: { "logout" },
        ]

        logOutput = new StringBuilder()

        AbstractCloudFoundryTask.metaClass.connectToCloudFoundry = {
            client = mockClient as CloudFoundryOperations
        }
    }

    @Test
    public void configuration() {
        assertTrue(project.cloudfoundry instanceof CloudFoundryExtension)
    }

    @Test
    public void target() {
        def meta = new CloudEntity.Meta(UUID.randomUUID(), new Date(), new Date())
        def cloudInfo = { new CloudInfo(version: 2) }
        def spaces = { [new CloudSpace(meta, 'test-space', new CloudOrganization(meta, 'test-org'))] as List<CloudSpace> }
        mockClient << [
                getCloudInfo: cloudInfo,
                getSpaces: spaces
        ]

        withTask('cf-target').execute()

        assertThat(logOutput.toString(), containsString(project.cloudfoundry.target))
        assertThat(logOutput.toString(), containsString(project.cloudfoundry.username))
        assertThat(logOutput.toString(), containsString(project.cloudfoundry.space))
    }

    private Task withTask(String taskName) {
        Task task = project.tasks[taskName]

        task.metaClass.log = { message ->
            println message
            logOutput << message
        }

        task
    }
}
