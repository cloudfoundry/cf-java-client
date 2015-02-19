package org.cloudfoundry.gradle

import org.cloudfoundry.client.lib.RestLogCallback
import org.cloudfoundry.client.lib.RestLogEntry

class GradlePluginRestLogCallback implements RestLogCallback {
    private def logger

    GradlePluginRestLogCallback(def logger) {
        this.logger = logger
    }

    @Override
    void onNewLogEntry(RestLogEntry logEntry) {
        logger.debug "REQUEST: ${logEntry.method} ${logEntry.uri}"
        logger.debug "RESPONSE: ${logEntry.httpStatus} ${logEntry.status} ${logEntry.message}"
    }
}
