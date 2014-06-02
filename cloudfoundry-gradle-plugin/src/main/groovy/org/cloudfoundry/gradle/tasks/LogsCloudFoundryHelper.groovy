package org.cloudfoundry.gradle.tasks

import org.cloudfoundry.client.lib.domain.ApplicationLog

class LogsCloudFoundryHelper {
    String formatLogEntry(ApplicationLog logEntry) {
        "${formatTimestamp(logEntry.timestamp)} ${formatSource(logEntry.sourceName, logEntry.sourceId)} ${formatType(logEntry.messageType)} ${logEntry.message}"
    }

    String formatTimestamp(Date timestamp) {
        timestamp.format("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    }

    String formatSource(String sourceName, String sourceId) {
        String source = sourceName
        if (source.equals("App")) {
            source += "/${sourceId}"
        }
        "[${source}]".padRight(10)
    }

    String formatType(def messageType) {
        messageType.toString() - 'STD'
    }
}
