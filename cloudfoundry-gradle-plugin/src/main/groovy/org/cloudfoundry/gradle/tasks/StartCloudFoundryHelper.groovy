package org.cloudfoundry.gradle.tasks

import groovy.time.TimeCategory
import org.cloudfoundry.client.lib.StartingInfo
import org.cloudfoundry.client.lib.domain.CloudApplication
import org.cloudfoundry.client.lib.domain.InstanceInfo
import org.cloudfoundry.client.lib.domain.InstanceState
import org.cloudfoundry.client.lib.domain.InstancesInfo
import org.gradle.api.GradleException
import org.springframework.http.HttpStatus

class StartCloudFoundryHelper {
    void startApplication() {
        log "Starting ${application}"
        StartingInfo startingInfo = client.startApplication(application)
        showAppStartup(startingInfo)
    }

    void showAppStartup(StartingInfo startingInfo) {
        CloudApplication app = client.getApplication(application)

        showStagingStatus(startingInfo)
        showStartingStatus(app)
        showStartResults(app)
    }

    void showStagingStatus(StartingInfo startingInfo) {
        if (startingInfo) {
            errorHandler.addExpectedStatus(HttpStatus.NOT_FOUND)

            int offset = 0
            String staging = client.getStagingLogs(startingInfo, offset)
            while (staging != null) {
                log staging
                offset += staging.size()
                staging = client.getStagingLogs(startingInfo, offset)
            }

            errorHandler.clearExpectedStatus()
        }
    }

    void showStartingStatus(CloudApplication app) {
        log "Checking status of ${app.name}"

        errorHandler.addExpectedStatus(HttpStatus.BAD_REQUEST)

        Date startTimeout = calcStartTimeout()

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

            Date now = new Date()
            if (now.after(startTimeout)) {
                throw new GradleException("Application ${application} start timed out")
            }

            sleep 1000
        }

        errorHandler.clearExpectedStatus()
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

        if (!instances) {
            throw new GradleException("Application ${application} start unsuccessful")
        }

        def runningInstances = getRunningInstances(instances)
        def flappingInstances = getFlappingInstances(instances)

        if (flappingInstances > 0 || runningInstances == 0) {
            throw new GradleException("Application ${application} start unsuccessful")
        } else if (runningInstances > 0) {
            List<String> uris = allUris
            if (uris.empty) {
                log "Application ${application} is available"
            } else {
                log "Application ${application} is available at ${uris.collect{"http://$it"}.join(',')}"
            }
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
        instances.count { instance -> instance.state == InstanceState.RUNNING }
    }

    def getFlappingInstances(List<InstanceInfo> instances) {
        instances.count { instance -> instance.state == InstanceState.FLAPPING }
    }

    def calcStartTimeout() {
        def startTimeout = new Date()
        use(TimeCategory) {
            if (appStartupTimeout)
                startTimeout += appStartupTimeout.minutes
            else if (healthCheckTimeout)
                startTimeout += healthCheckTimeout.seconds
            else
                startTimeout += 5.minutes
        }
        startTimeout
    }
}
