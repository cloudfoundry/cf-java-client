package org.cloudfoundry.gradle.tasks

import org.cloudfoundry.client.lib.domain.CloudApplication

import java.text.DecimalFormat

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

    String formatPercentage(double value) {
        DecimalFormat dec = new DecimalFormat("0.00")
        return dec.format(value)
    }

    String formatMBytes(int size) {
        double g = size / 1024

        DecimalFormat dec = new DecimalFormat("0")

        if (g > 1) {
            return dec.format(g).concat("G")
        } else {
            return dec.format(size).concat("M")
        }
    }

    String formatBytes(double size) {
        double k = size / 1024.0
        double m = k / 1024.0
        double g = m / 1024.0

        DecimalFormat dec = new DecimalFormat("0")

        if (g > 1) {
            return dec.format(g).concat("G")
        } else if (m > 1) {
            return dec.format(m).concat("M")
        } else if (k > 1) {
            return dec.format(k).concat("K")
        } else {
            return dec.format(size).concat("B")
        }
    }
}
