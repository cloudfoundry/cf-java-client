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
    void validateVariantsForDeploy() {
        if (!variants || variants.size() < 2) {
            throw new GradleException("At least two 'variants' suffixes must be specified.")
        }
    }

    String findNextVariantToDeploy(String appName, List<CloudApplication> apps) {
        variants.reverse().find { String variant ->
            boolean notUsed = !apps.any { app -> appHasDecoratedName(app, appName, variant) }
            boolean usedButUnmapped = apps.any { app -> appVariantExistsAndUnmapped(app, appName, variant) }
            notUsed || usedButUnmapped
        }
    }

    List<String> findMappedVariants(String appName, List<CloudApplication> apps) {
        def appNames = []

        variants.each { String variant ->
            appNames += apps.findAll { app ->
                appVariantExistsAndMapped(app, appName, variant)
            }.collect { app ->
                app.name
            }
        }

        appNames
    }

    List<String> findUnmappedVariants(String appName, List<CloudApplication> apps) {
        def appNames = []

        variants.each { String variant ->
            appNames += apps.findAll { app ->
                appVariantExistsAndUnmapped(app, appName, variant)
            }.collect { app ->
                app.name
            }
        }

        appNames
    }

    boolean appVariantExistsAndMapped(CloudApplication app, String appName, String variant) {
        appHasDecoratedName(app, appName, variant) && app.uris.containsAll(allUris)
    }

    boolean appVariantExistsAndUnmapped(CloudApplication app, String appName, String variant) {
        appHasDecoratedName(app, appName, variant) && app.uris.disjoint(allUris)
    }

    boolean appHasDecoratedName(CloudApplication app, String appName, String variant) {
        app.name == appName + variant
    }
}
