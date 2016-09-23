/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.gradle;

import org.cloudfoundry.gradle.tasks.CfAppDetailsTask;
import org.cloudfoundry.gradle.tasks.CfAppRestartTask;
import org.cloudfoundry.gradle.tasks.CfAppStartTask;
import org.cloudfoundry.gradle.tasks.CfAppStopTask;
import org.cloudfoundry.gradle.tasks.CfDeleteAppTask;
import org.cloudfoundry.gradle.tasks.CfDeleteRouteTask;
import org.cloudfoundry.gradle.tasks.CfMapRouteTask;
import org.cloudfoundry.gradle.tasks.CfPushTask;
import org.cloudfoundry.gradle.tasks.CfRenameAppTask;
import org.cloudfoundry.gradle.tasks.CfRestageTask;
import org.cloudfoundry.gradle.tasks.CfUnMapRouteTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Responsible for registering the tasks for the plugin
 *
 * @author Biju Kunjummen
 */
public class CfPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getTasks().create("cf-push", CfPushTask.class);
        project.getTasks().create("cf-map-route", CfMapRouteTask.class);
        project.getTasks().create("cf-unmap-route", CfUnMapRouteTask.class);
        project.getTasks().create("cf-delete-app", CfDeleteAppTask.class);
        project.getTasks().create("cf-delete-route", CfDeleteRouteTask.class);
        project.getTasks().create("cf-rename-app", CfRenameAppTask.class);
        project.getTasks().create("cf-get-app-detail", CfAppDetailsTask.class);
        project.getTasks().create("cf-start-app", CfAppStartTask.class);
        project.getTasks().create("cf-stop-app", CfAppStopTask.class);
        project.getTasks().create("cf-restart-app", CfAppRestartTask.class);
        project.getTasks().create("cf-restage-app", CfRestageTask.class);

        project.getExtensions().create("cfConfig", CfPluginExtension.class);
    }
}
