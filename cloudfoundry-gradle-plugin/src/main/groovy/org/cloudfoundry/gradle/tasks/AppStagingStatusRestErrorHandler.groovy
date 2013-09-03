package org.cloudfoundry.gradle.tasks

import org.cloudfoundry.client.lib.CloudFoundryException
import org.cloudfoundry.client.lib.rest.CloudControllerResponseErrorHandler
import org.springframework.http.HttpStatus

/**
 * This class is used to override the default error handling for REST responses in RestTemplate. The main
 * goal is to keep RestTemplate from logging warnings in the specific cases mentioned below.
 */
class AppStagingStatusRestErrorHandler extends CloudControllerResponseErrorHandler {
    @Override
    protected boolean hasError(HttpStatus statusCode) {
        if (statusCode.equals(HttpStatus.NOT_FOUND)) {
            // NOT_FOUND is expected when the last staging log response has been received
            throw new CloudFoundryException(statusCode);
        } else if (statusCode.equals(HttpStatus.BAD_REQUEST)) {
            // BAD_REQUEST is expected when getting app instances before staging has completed
            throw new CloudFoundryException(statusCode)
        }
        return super.hasError(statusCode)
    }
}
