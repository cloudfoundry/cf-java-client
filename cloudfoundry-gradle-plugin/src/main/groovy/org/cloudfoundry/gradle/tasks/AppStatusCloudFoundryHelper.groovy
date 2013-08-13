package org.cloudfoundry.gradle.tasks

import org.cloudfoundry.client.lib.StartingInfo
import org.cloudfoundry.client.lib.domain.CloudApplication

class AppStatusCloudFoundryHelper {
    String health(CloudApplication app) {
        String state = app.state

        if (state == "STARTED") {
            def running_instances = app.runningInstances
            def expected_instances = app.instances

            if (expected_instances > 0) {
                float ratio = running_instances / expected_instances
                if (ratio == 1.0)
                    "running"
                else
                    "${(ratio * 100).toInteger()}%"
            } else {
                'n/a'
            }
        } else {
            state.toLowerCase()
        }
    }

    void showStagingStatus(StartingInfo startingInfo) {
        if (startingInfo) {
            int offset = 0
            String staging = client.getStagingLogs(startingInfo, offset)
            while (staging != null) {
                println staging
                offset += staging.size()
                staging = client.getStagingLogs(startingInfo, offset)
            }
        }
    }

    void showStartingStatus() {
        while (true) {
            sleep 1000
            CloudApplication app = client.getApplication(application)
            println "${app.runningInstances} of ${app.instances} instances running"

            if (app.runningInstances == app.instances)
                break
        }
    }
}
