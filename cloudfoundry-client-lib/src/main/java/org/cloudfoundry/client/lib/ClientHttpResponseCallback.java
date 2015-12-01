package org.cloudfoundry.client.lib;

import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Callback class while the client receives the server response.
 */
public interface ClientHttpResponseCallback {

    public void onClientHttpResponse(ClientHttpResponse clientHttpResponse) throws IOException;
}
