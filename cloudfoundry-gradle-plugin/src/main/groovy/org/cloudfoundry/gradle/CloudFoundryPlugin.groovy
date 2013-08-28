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

import org.gradle.api.Plugin
import org.gradle.api.Project

import org.cloudfoundry.gradle.tasks.AppCloudFoundryTask
import org.cloudfoundry.gradle.tasks.AppsCloudFoundryTask
import org.cloudfoundry.gradle.tasks.BindServiceCloudFoundryTask
import org.cloudfoundry.gradle.tasks.CreateServiceCloudFoundryTask
import org.cloudfoundry.gradle.tasks.DeleteApplicationCloudFoundryTask
import org.cloudfoundry.gradle.tasks.DeleteServiceCloudFoundryTask
import org.cloudfoundry.gradle.tasks.EnvCloudFoundryTask
import org.cloudfoundry.gradle.tasks.InfoCloudFoundryTask
import org.cloudfoundry.gradle.tasks.LoginCloudFoundryTask
import org.cloudfoundry.gradle.tasks.LogoutCloudFoundryTask
import org.cloudfoundry.gradle.tasks.MapCloudFoundryTask
import org.cloudfoundry.gradle.tasks.PushApplicationCloudFoundryTask
import org.cloudfoundry.gradle.tasks.RestartApplicationCloudFoundryTask
import org.cloudfoundry.gradle.tasks.ServicesCloudFoundryTask
import org.cloudfoundry.gradle.tasks.ServiceOfferingsCloudFoundryTask
import org.cloudfoundry.gradle.tasks.SetEnvCloudFoundryTask
import org.cloudfoundry.gradle.tasks.SpacesCloudFoundryTask
import org.cloudfoundry.gradle.tasks.StartApplicationCloudFoundryTask
import org.cloudfoundry.gradle.tasks.StopApplicationCloudFoundryTask
import org.cloudfoundry.gradle.tasks.UnbindServiceCloudFoundryTask
import org.cloudfoundry.gradle.tasks.UnmapCloudFoundryTask
import org.cloudfoundry.gradle.tasks.UnsetEnvCloudFoundryTask

/**
 * The main Cloud Foundry plugin class.
 *
 * @author Cedric Champeau
 * @author Scott Frederick
 */
class CloudFoundryPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create("cloudfoundry", CloudFoundry, project.name)

        def serviceInfos = project.container(CloudFoundryServices)
        project.extensions.serviceInfos = serviceInfos

        // register tasks
        project.task('cf-target', type: InfoCloudFoundryTask)
        project.task('cf-login', type: LoginCloudFoundryTask)
        project.task('cf-logout', type: LogoutCloudFoundryTask)
        project.task('cf-spaces', type: SpacesCloudFoundryTask)
        project.task('cf-push', type: PushApplicationCloudFoundryTask)
        project.task('cf-start', type: StartApplicationCloudFoundryTask)
        project.task('cf-restart', type: RestartApplicationCloudFoundryTask)
        project.task('cf-stop', type: StopApplicationCloudFoundryTask)
        project.task('cf-apps', type: AppsCloudFoundryTask)
        project.task('cf-app', type: AppCloudFoundryTask)
        project.task('cf-delete', type: DeleteApplicationCloudFoundryTask)
        project.task('cf-services', type: ServicesCloudFoundryTask)
        project.task('cf-service-plans', type: ServiceOfferingsCloudFoundryTask)
        project.task('cf-create-service', type: CreateServiceCloudFoundryTask)
        project.task('cf-delete-service', type: DeleteServiceCloudFoundryTask)
        project.task('cf-bind', type: BindServiceCloudFoundryTask)
        project.task('cf-unbind', type: UnbindServiceCloudFoundryTask)
        project.task('cf-env', type: EnvCloudFoundryTask)
        project.task('cf-set-env', type: SetEnvCloudFoundryTask)
        project.task('cf-unset-env', type: UnsetEnvCloudFoundryTask)
        project.task('cf-map', type: MapCloudFoundryTask)
        project.task('cf-unmap', type: UnmapCloudFoundryTask)
    }
}
