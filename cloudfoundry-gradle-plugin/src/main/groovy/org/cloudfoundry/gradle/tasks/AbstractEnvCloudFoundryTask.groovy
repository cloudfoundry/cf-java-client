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

abstract class AbstractEnvCloudFoundryTask extends AbstractCloudFoundryTask {
    AbstractEnvCloudFoundryTask() {
        super()
    }

    protected void listEnvironmentVariables(def env) {
        StringBuilder sb = new StringBuilder("Environment variables for ${application}\n")

        if (env.isEmpty()) {
            sb << 'No environment variables set\n'
        }
        env.each { key, value ->
            sb << "  $key=$value\n"
        }

        log sb.toString()
    }

    protected def modifyAppEnv(CloudApplication app, Closure c) {
        def newEnv = c.call(app.getEnvAsMap(), env)

        client.updateApplicationEnv(application, newEnv)

        newEnv
    }
}
