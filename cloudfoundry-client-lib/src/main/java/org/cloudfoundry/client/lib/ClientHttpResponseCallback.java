package org.cloudfoundry.client.lib;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;

/**
 * Callback class while the client receives the server response.
 * 
 */
public interface ClientHttpResponseCallback {

	public void onClientHttpResponse(ClientHttpResponse clientHttpResponse) throws IOException;
}