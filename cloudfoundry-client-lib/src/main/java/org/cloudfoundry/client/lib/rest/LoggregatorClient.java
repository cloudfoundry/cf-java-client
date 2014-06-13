package org.cloudfoundry.client.lib.rest;

import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.tomcat.websocket.WsWebSocketContainer;
import org.cloudfoundry.client.lib.ApplicationLogListener;
import org.cloudfoundry.client.lib.CloudOperationException;
import org.springframework.web.util.UriTemplate;

import javax.net.ssl.SSLContext;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.UUID;

public class LoggregatorClient {
	private static final UriTemplate loggregatorUriTemplate = new UriTemplate("{endpoint}/{kind}/?app={appId}");

	private boolean trustSelfSignedCerts;

	public LoggregatorClient(boolean trustSelfSignedCerts) {
		this.trustSelfSignedCerts = trustSelfSignedCerts;
	}

	public StreamingLogTokenImpl connectToLoggregator(String endpoint, String mode, UUID appId,
	                                                  ApplicationLogListener listener,
	                                                  ClientEndpointConfig.Configurator configurator) {
		URI loggregatorUri = loggregatorUriTemplate.expand(endpoint, mode, appId);

		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			ClientEndpointConfig config = buildClientConfig(configurator);
			Session session = container.connectToServer(new LoggregatorEndpoint(listener), config, loggregatorUri);
			return new StreamingLogTokenImpl(session);
		} catch (DeploymentException e) {
			throw new CloudOperationException(e);
		} catch (IOException e) {
			throw new CloudOperationException(e);
		}
	}

	private ClientEndpointConfig buildClientConfig(ClientEndpointConfig.Configurator configurator) {
		ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();

		if (trustSelfSignedCerts) {
			SSLContext sslContext = buildSslContext();
			Map<String, Object> userProperties = config.getUserProperties();
			userProperties.put(WsWebSocketContainer.SSL_CONTEXT_PROPERTY, sslContext);
		}

		return config;
	}

	private SSLContext buildSslContext() {
		try {
			SSLContextBuilder contextBuilder = new SSLContextBuilder().
					useTLS().
					loadTrustMaterial(null, new TrustSelfSignedStrategy());

			return contextBuilder.build();
		} catch (GeneralSecurityException e) {
			throw new CloudOperationException(e);
		}
	}
}
