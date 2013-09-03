package org.cloudfoundry.gradle.tasks

import org.cloudfoundry.client.lib.StartingInfo
import org.cloudfoundry.client.lib.domain.CloudApplication
import org.cloudfoundry.client.lib.domain.InstanceInfo
import org.cloudfoundry.client.lib.domain.InstancesInfo

class AppStatusCloudFoundryHelper {
    static final int MAX_STATUS_CHECKS = 60

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

    void showAppStartup(startingInfo) {
        CloudApplication app = client.getApplication(application)

        client.responseErrorHandler = new AppStagingStatusRestErrorHandler()

        showStagingStatus(startingInfo)
        showStartingStatus(app)
        showStartResults(app)
    }

    void showStagingStatus(StartingInfo startingInfo) {
        if (startingInfo) {
            int offset = 0
            String staging = client.getStagingLogs(startingInfo, offset)
            while (staging != null) {
                log staging
                offset += staging.size()
                staging = client.getStagingLogs(startingInfo, offset)
            }
        }
    }

    void showStartingStatus(CloudApplication app) {
        log "Checking status of ${app.name}"

        def statusChecks = 0

        while (true) {
            List<InstanceInfo> instances = getApplicationInstances(app)

            if (instances) {
                def expectedInstances = getExpectedInstances(instances)
                def runningInstances = getRunningInstances(instances)
                def flappingInstances = getFlappingInstances(instances)

                showInstancesStatus(instances, runningInstances, expectedInstances)

                if (flappingInstances > 0)
                    break

                if (runningInstances == expectedInstances)
                    break
            }

            if (statusChecks > MAX_STATUS_CHECKS)
                break

            statusChecks++
            sleep 1000
        }
    }

    void showInstancesStatus(List<InstanceInfo> instances, runningInstances, expectedInstances) {
        def stateCounts = [:].withDefault { 0 }
        instances.each { instance ->
            stateCounts[instance.state] += 1
        }

        def stateStrings = []
        stateCounts.each { state, count ->
            stateStrings << "${count} ${state.toString().toLowerCase()}"
        }

        def expectedString = "${expectedInstances}"
        def runningString = "${runningInstances}".padLeft(expectedString.length())
        log "  ${runningString} of ${expectedString} instances running (${stateStrings.join(", ")})"
    }

    void showStartResults(CloudApplication app) {
        List<InstanceInfo> instances = getApplicationInstances(app)

        def expectedInstances = getExpectedInstances(instances)
        def runningInstances = getRunningInstances(instances)
        def flappingInstances = getFlappingInstances(instances)

        if (flappingInstances > 0 || runningInstances == 0) {
            log "Application start unsuccessful"
        } else if (runningInstances > 0) {
            List<String> uris = allUris
            if (uris.empty) {
                log "Application ${application} is available"
            } else {
                log "Application ${application} is available at ${uris.collect{"http://$it"}.join(',')}"
            }
        }

        if (expectedInstances != runningInstances) {
            log "TIP: The system will continue to start all requested app instances. Use the 'cf-app' task to monitor app status."
        }
    }

    List<InstanceInfo> getApplicationInstances(CloudApplication app) {
        InstancesInfo instancesInfo = client.getApplicationInstances(app)
        instancesInfo?.instances
    }

    def getExpectedInstances(List<InstanceInfo> instances) {
        instances.size()
    }

    def getRunningInstances(List<InstanceInfo> instances) {
        instances.count { instance -> instance.state.toString() == "RUNNING" }
    }

    def getFlappingInstances(List<InstanceInfo> instances) {
        instances.count { instance -> instance.state.toString() == "FLAPPING" }
    }
}
