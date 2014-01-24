package org.cloudfoundry.gradle.tasks

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
}
