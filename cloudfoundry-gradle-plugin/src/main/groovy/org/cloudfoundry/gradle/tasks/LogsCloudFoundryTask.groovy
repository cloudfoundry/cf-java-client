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

import org.cloudfoundry.client.lib.ApplicationLogListener
import org.cloudfoundry.client.lib.domain.ApplicationLog
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Task which displays log files.
 */
@Mixin(LogsCloudFoundryHelper)
class LogsCloudFoundryTask extends AbstractCloudFoundryTask {
    LogsCloudFoundryTask() {
        super()
        description = 'Shows a tail of log entries'
    }

    @TaskAction
    void showLogs() {
        withCloudFoundryClient {
            log "Getting logs for ${application}\n"

            withApplication {
                def listener = new LoggingListener()
                client.streamLogs(application, listener)
                synchronized (listener) {
                    listener.wait()
                }
            }
        }
    }

    private class LoggingListener implements ApplicationLogListener {
        public void onMessage(ApplicationLog logEntry) {
            log formatLogEntry(logEntry)
        }

        public void onError(Throwable e) {
            synchronized (this) {
                this.notify()
            }
            throw new GradleException("Error streaming logs", e)
        }

        public void onComplete() {
            synchronized (this) {
                this.notify()
            }
        }
    }
}
