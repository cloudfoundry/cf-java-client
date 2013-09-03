package org.cloudfoundry.gradle.tasks

import org.cloudfoundry.client.lib.CloudFoundryException
import org.cloudfoundry.client.lib.rest.CloudControllerResponseErrorHandler
import org.springframework.http.HttpStatus

/**
 * This class is used to override the default error handling for REST responses in RestTemplate. The main
 * goal is to keep RestTemplate from logging warnings for expected HTTP "errors".
 */
class WarningBypassingResponseErrorHandler extends CloudControllerResponseErrorHandler {
    private List<HttpStatus> expectedStatusCodes = []

    public void addExpectedStatus(HttpStatus status) {
        expectedStatusCodes << status
    }

    public void clearExpectedStatus() {
        expectedStatusCodes.clear()
    }

    @Override
    protected boolean hasError(HttpStatus statusCode) {
        if (expectedStatusCodes.contains(statusCode)) {
            throw new CloudFoundryException(statusCode)
        }
        return super.hasError(statusCode)
    }
}
