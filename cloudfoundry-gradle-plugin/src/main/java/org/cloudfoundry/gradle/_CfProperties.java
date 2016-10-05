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

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

/**
 * Holds the set of properties that will drive the various Gradle tasks
 *
 * @author Biju Kunjummen
 */
@Value.Immutable(copy = true)
abstract class _CfProperties {
	abstract String ccHost();
    abstract String ccUser();
    abstract String ccPassword();

    abstract String org();
    abstract String space();

    abstract String name();

    @Nullable
    abstract String filePath();

    @Nullable
    abstract String hostName();
    @Nullable
    abstract String domain();

    @Nullable
    abstract String path();

    @Nullable
    abstract String state();

    @Nullable
    abstract String buildpack();

    @Nullable
    abstract String command();

    @Nullable
    abstract Boolean console();

    @Nullable
    abstract Boolean debug();

    @Nullable
    abstract String detectedStartCommand();

    @Nullable
    abstract Integer diskQuota();

    @Nullable
    abstract Boolean enableSsh();

    @Nullable
    abstract Map<String, String> environment();

    @Nullable
    abstract Integer timeout();

    @Nullable
    abstract String healthCheckType();

    @Nullable
    abstract Integer instances();

    @Nullable
    abstract Integer memory();

    @Nullable
    abstract List<Integer> ports();

    @Nullable
    abstract List<String> services();

    @Nullable
    abstract Integer stagingTimeout();

    @Nullable
    abstract Integer startupTimeout();
}
