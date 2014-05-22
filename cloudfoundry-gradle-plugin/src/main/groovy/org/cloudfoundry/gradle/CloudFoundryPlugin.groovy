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

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project

import org.cloudfoundry.gradle.tasks.AppCloudFoundryTask
import org.cloudfoundry.gradle.tasks.AppsCloudFoundryTask
import org.cloudfoundry.gradle.tasks.BindServiceCloudFoundryTask
import org.cloudfoundry.gradle.tasks.CreateServiceCloudFoundryTask
import org.cloudfoundry.gradle.tasks.DeleteApplicationCloudFoundryTask
import org.cloudfoundry.gradle.tasks.DeleteServiceCloudFoundryTask
import org.cloudfoundry.gradle.tasks.DeployCloudFoundryTask
import org.cloudfoundry.gradle.tasks.EnvCloudFoundryTask
import org.cloudfoundry.gradle.tasks.InfoCloudFoundryTask
import org.cloudfoundry.gradle.tasks.LogsCloudFoundryTask
import org.cloudfoundry.gradle.tasks.LoginCloudFoundryTask
import org.cloudfoundry.gradle.tasks.LogoutCloudFoundryTask
import org.cloudfoundry.gradle.tasks.MapCloudFoundryTask
import org.cloudfoundry.gradle.tasks.PushCloudFoundryTask
import org.cloudfoundry.gradle.tasks.RestartApplicationCloudFoundryTask
import org.cloudfoundry.gradle.tasks.ScaleCloudFoundryTask
import org.cloudfoundry.gradle.tasks.ServicesCloudFoundryTask
import org.cloudfoundry.gradle.tasks.ServiceOfferingsCloudFoundryTask
import org.cloudfoundry.gradle.tasks.SetEnvCloudFoundryTask
import org.cloudfoundry.gradle.tasks.SpacesCloudFoundryTask
import org.cloudfoundry.gradle.tasks.StartApplicationCloudFoundryTask
import org.cloudfoundry.gradle.tasks.StopApplicationCloudFoundryTask
import org.cloudfoundry.gradle.tasks.SwapDeployedCloudFoundryTask
import org.cloudfoundry.gradle.tasks.UnbindServiceCloudFoundryTask
import org.cloudfoundry.gradle.tasks.UndeployCloudFoundryTask
import org.cloudfoundry.gradle.tasks.UnmapCloudFoundryTask
import org.cloudfoundry.gradle.tasks.UnsetEnvCloudFoundryTask

/**
 * The main Cloud Foundry plugin class.
 *
 * @author Cedric Champeau
 * @author Scott Frederick
 */
class CloudFoundryPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("cloudfoundry", CloudFoundryExtension, project)
        project.cloudfoundry.extensions.services = project.container(CloudFoundryServiceExtension)

        // register tasks
        createTask(project, 'Target', InfoCloudFoundryTask)
        createTask(project, 'Login', LoginCloudFoundryTask)
        createTask(project, 'Logout', LogoutCloudFoundryTask)
        createTask(project, 'Spaces', SpacesCloudFoundryTask)
        createTask(project, 'Push', PushCloudFoundryTask)
        createTask(project, 'Delete', DeleteApplicationCloudFoundryTask)
        createTask(project, 'Start', StartApplicationCloudFoundryTask)
        createTask(project, 'Restart', RestartApplicationCloudFoundryTask)
        createTask(project, 'Stop', StopApplicationCloudFoundryTask)
        createTask(project, 'Deploy', DeployCloudFoundryTask)
        createTask(project, 'Undeploy', UndeployCloudFoundryTask)
        createTask(project, 'SwapDeployed', SwapDeployedCloudFoundryTask)
        createTask(project, 'Scale', ScaleCloudFoundryTask)
        createTask(project, 'Apps', AppsCloudFoundryTask)
        createTask(project, 'App', AppCloudFoundryTask)
        createTask(project, 'Logs', LogsCloudFoundryTask)
        createTask(project, 'Services', ServicesCloudFoundryTask)
        createTask(project, 'ServicePlans', ServiceOfferingsCloudFoundryTask)
        createTask(project, 'CreateService', CreateServiceCloudFoundryTask)
        createTask(project, 'DeleteService', DeleteServiceCloudFoundryTask)
        createTask(project, 'Bind', BindServiceCloudFoundryTask)
        createTask(project, 'Unbind', UnbindServiceCloudFoundryTask)
        createTask(project, 'Env', EnvCloudFoundryTask)
        createTask(project, 'SetEnv', SetEnvCloudFoundryTask)
        createTask(project, 'UnsetEnv', UnsetEnvCloudFoundryTask)
        createTask(project, 'Map', MapCloudFoundryTask)
        createTask(project, 'Unmap', UnmapCloudFoundryTask)
    }

    private void createTask(Project project, String name, Class<? extends DefaultTask> clazz) {
        project.tasks.create("cf${name}", clazz)
    }
}
