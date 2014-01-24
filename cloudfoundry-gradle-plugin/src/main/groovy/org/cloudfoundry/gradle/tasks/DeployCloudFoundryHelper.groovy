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

import org.cloudfoundry.client.lib.domain.CloudApplication
import org.gradle.api.GradleException

class DeployCloudFoundryHelper {
    void validateVersionsForDeploy() {
        if (!versions || versions.size() < 2) {
            throw new GradleException("At least two 'versions' suffixes must be specified.")
        }
    }

    String findNextVersionToDeploy(String appName, List<CloudApplication> apps) {
        versions.reverse().find { String version ->
            boolean notUsed = !apps.any { app -> appHasDecoratedName(app, appName, version) }
            boolean usedButUnmapped = apps.any { app -> appVersionExistsAndUnmapped(app, appName, version) }
            notUsed || usedButUnmapped
        }
    }

    List<String> findMappedVersions(String appName, List<CloudApplication> apps) {
        def appNames = []

        versions.each { String version ->
            appNames += apps.findAll { app ->
                appVersionExistsAndMapped(app, appName, version)
            }.collect { app ->
                app.name
            }
        }

        appNames
    }

    List<String> findUnmappedVersions(String appName, List<CloudApplication> apps) {
        def appNames = []

        versions.each { String version ->
            appNames += apps.findAll { app ->
                appVersionExistsAndUnmapped(app, appName, version)
            }.collect { app ->
                app.name
            }
        }

        appNames
    }

    boolean appVersionExistsAndMapped(CloudApplication app, String appName, String version) {
        appHasDecoratedName(app, appName, version) && app.uris.containsAll(allUris)
    }

    boolean appVersionExistsAndUnmapped(CloudApplication app, String appName, String version) {
        appHasDecoratedName(app, appName, version) && app.uris.disjoint(allUris)
    }

    boolean appHasDecoratedName(CloudApplication app, String appName, String version) {
        app.name == appName + version
    }
}
