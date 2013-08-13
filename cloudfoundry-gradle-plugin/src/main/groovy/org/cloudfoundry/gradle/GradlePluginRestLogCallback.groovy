package org.cloudfoundry.gradle

import org.cloudfoundry.client.lib.RestLogCallback
import org.cloudfoundry.client.lib.RestLogEntry

class GradlePluginRestLogCallback implements RestLogCallback {
    @Override
    void onNewLogEntry(RestLogEntry logEntry) {
        println "REQUEST: ${logEntry.method} ${logEntry.uri}"
        println "RESPONSE: ${logEntry.httpStatus} ${logEntry.status} ${logEntry.message}"
    }
}
