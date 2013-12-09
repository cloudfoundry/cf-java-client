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
import org.cloudfoundry.gradle.tasks.LogsCloudFoundryTask
import org.cloudfoundry.gradle.tasks.LoginCloudFoundryTask
import org.cloudfoundry.gradle.tasks.LogoutCloudFoundryTask
import org.cloudfoundry.gradle.tasks.MapCloudFoundryTask
import org.cloudfoundry.gradle.tasks.PushApplicationCloudFoundryTask
import org.cloudfoundry.gradle.tasks.RestartApplicationCloudFoundryTask
import org.cloudfoundry.gradle.tasks.ScaleCloudFoundryTask
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

    @Override
    void apply(Project project) {
        project.extensions.create("cloudfoundry", CloudFoundryExtension, project)
        project.cloudfoundry.extensions.services = project.container(CloudFoundryServiceExtension)
        // alias "services" to "serviceInfos" for backward compatibility
        project.cloudfoundry.extensions.serviceInfos = project.cloudfoundry.extensions.services

        // register tasks
        project.tasks.create('cf-target', InfoCloudFoundryTask)
        project.tasks.create('cf-login', LoginCloudFoundryTask)
        project.tasks.create('cf-logout', LogoutCloudFoundryTask)
        project.tasks.create('cf-spaces', SpacesCloudFoundryTask)
        project.tasks.create('cf-push', PushApplicationCloudFoundryTask)
        project.tasks.create('cf-delete', DeleteApplicationCloudFoundryTask)
        project.tasks.create('cf-start', StartApplicationCloudFoundryTask)
        project.tasks.create('cf-restart', RestartApplicationCloudFoundryTask)
        project.tasks.create('cf-stop', StopApplicationCloudFoundryTask)
        project.tasks.create('cf-scale', ScaleCloudFoundryTask)
        project.tasks.create('cf-apps', AppsCloudFoundryTask)
        project.tasks.create('cf-app', AppCloudFoundryTask)
        project.tasks.create('cf-logs', LogsCloudFoundryTask)
        project.tasks.create('cf-services', ServicesCloudFoundryTask)
        project.tasks.create('cf-service-plans', ServiceOfferingsCloudFoundryTask)
        project.tasks.create('cf-create-service', CreateServiceCloudFoundryTask)
        project.tasks.create('cf-delete-service', DeleteServiceCloudFoundryTask)
        project.tasks.create('cf-bind', BindServiceCloudFoundryTask)
        project.tasks.create('cf-unbind', UnbindServiceCloudFoundryTask)
        project.tasks.create('cf-env', EnvCloudFoundryTask)
        project.tasks.create('cf-set-env', SetEnvCloudFoundryTask)
        project.tasks.create('cf-unset-env', UnsetEnvCloudFoundryTask)
        project.tasks.create('cf-map', MapCloudFoundryTask)
        project.tasks.create('cf-unmap', UnmapCloudFoundryTask)
    }

}
