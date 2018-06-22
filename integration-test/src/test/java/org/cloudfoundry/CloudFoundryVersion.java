/*
 * Copyright 2013-2018 the original author or authors.
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

package org.cloudfoundry;

import com.github.zafarkhaja.semver.Version;

public enum CloudFoundryVersion {

    PCF_1_9(Version.forIntegers(2, 65, 0)),

    PCF_1_10(Version.forIntegers(2, 75, 0)),

    PCF_1_11(Version.forIntegers(2, 82, 0)),

    PCF_1_12(Version.forIntegers(2, 94, 0)),

    PCF_2_1(Version.forIntegers(2, 102, 0)),

    UNSPECIFIED(Version.forIntegers(0));

    private final Version version;

    CloudFoundryVersion(Version version) {
        this.version = version;
    }

    Version getVersion() {
        return this.version;
    }

}
