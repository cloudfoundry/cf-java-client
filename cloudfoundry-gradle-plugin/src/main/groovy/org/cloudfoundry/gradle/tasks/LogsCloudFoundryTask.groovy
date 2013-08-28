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

import org.gradle.api.tasks.TaskAction

/**
 * Task which displays log files.
 */
class LogsCloudFoundryTask extends AbstractCloudFoundryTask {
    LogsCloudFoundryTask() {
        super()
        description = 'Shows the contents of log files'
    }

    @TaskAction
    void showLogs() {
        withCloudFoundryClient {
            log "Getting logs for ${application}"

            withApplication {
                Map<String, String> logs = client.getLogs(application)
                logs.each { name, content ->
                    StringBuilder sb = new StringBuilder()
                    sb.append("Reading ${name}\n")
                    sb.append(content)

                    log sb.toString()
                }
            }
        }
    }
}
