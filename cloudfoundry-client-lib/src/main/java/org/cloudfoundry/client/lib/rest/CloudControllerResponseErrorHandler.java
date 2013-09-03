package org.cloudfoundry.client.lib.rest;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.NotFinishedStagingException;
import org.cloudfoundry.client.lib.StagingErrorException;
import org.cloudfoundry.client.lib.util.CloudUtil;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.Map;

public class CloudControllerResponseErrorHandler extends DefaultResponseErrorHandler {
	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = response.getStatusCode();
		switch (statusCode.series()) {
			case CLIENT_ERROR:
				throw getException(response);
			case SERVER_ERROR:
				throw new HttpServerErrorException(statusCode, response.getStatusText());
			default:
				throw new RestClientException("Unknown status code [" + statusCode + "]");
		}
	}

	private static CloudFoundryException getException(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = response.getStatusCode();
		CloudFoundryException cloudFoundryException = null;

		String description = "Client error";
		String statusText = response.getStatusText();

		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

		if (response.getBody() != null) {
			try {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = mapper.readValue(response.getBody(), Map.class);
				description = CloudUtil.parse(String.class, map.get("description"));

				int cloudFoundryErrorCode = CloudUtil.parse(Integer.class, map.get("code"));

				if (cloudFoundryErrorCode >= 0) {
					switch (cloudFoundryErrorCode) {
						case StagingErrorException.ERROR_CODE:
							cloudFoundryException = new StagingErrorException(
									statusCode, statusText);
							break;
						case NotFinishedStagingException.ERROR_CODE:
							cloudFoundryException = new NotFinishedStagingException(
									statusCode, statusText);
							break;
					}
				}
			} catch (JsonParseException e) {
				// Fall through. Handled below.
			} catch (IOException e) {
				// Fall through. Handled below.
			}
		}

		if (cloudFoundryException == null) {
			cloudFoundryException = new CloudFoundryException(statusCode,
					statusText);
		}
		cloudFoundryException.setDescription(description);

		return cloudFoundryException;
	}
}
