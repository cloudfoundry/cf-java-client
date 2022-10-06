/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.operations.applications;


import org.immutables.value.Value;

import java.util.List;

/**
 * An application manifest that captures some of the details of how an application is deployed.  See <a href="https://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html">the manifest
 * definition</a> for more details.
 */
@Value.Immutable
abstract class _ManifestV3 {

    public abstract List<ManifestV3Application> getApplications();

    @Value.Default
    public Integer getVersion() {
        return 1;
    }

    @Value.Check
    void check() {
        if (getVersion() != 1) {
            throw new IllegalStateException("Only version 1 is allowed");
        }
        if (getApplications().isEmpty()) {
            throw new IllegalStateException("At least one application is required");
        }
    }
}
