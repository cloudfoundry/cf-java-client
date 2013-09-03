package org.cloudfoundry.maven.common;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.rest.CloudControllerResponseErrorHandler;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class WarningBypassingResponseErrorHandler extends CloudControllerResponseErrorHandler {
	private List<HttpStatus> expectedStatusCodes = new ArrayList<HttpStatus>();

	public void addExpectedStatus(HttpStatus status) {
		expectedStatusCodes.add(status);
	}

	public void clearExpectedStatus() {
		expectedStatusCodes.clear();
	}

	@Override
	protected boolean hasError(HttpStatus statusCode) {
		if (expectedStatusCodes.contains(statusCode)) {
			throw new CloudFoundryException(statusCode);
		}
		return super.hasError(statusCode);
	}
}
