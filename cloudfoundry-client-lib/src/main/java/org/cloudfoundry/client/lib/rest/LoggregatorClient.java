package org.cloudfoundry.client.lib.rest;

import org.apache.tomcat.websocket.WsWebSocketContainer;
import org.cloudfoundry.client.lib.ApplicationLogListener;
import org.cloudfoundry.client.lib.CloudOperationException;
import org.springframework.web.util.UriTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.UUID;

public class LoggregatorClient {

    private static final UriTemplate loggregatorRecentUriTemplate = new UriTemplate("{scheme}://{host}/recent");

    private static final UriTemplate loggregatorStreamUriTemplate = new UriTemplate("{endpoint}/{kind}/?app={appId}");

    private boolean trustSelfSignedCerts;

    public LoggregatorClient(boolean trustSelfSignedCerts) {
        this.trustSelfSignedCerts = trustSelfSignedCerts;
    }

    public StreamingLogTokenImpl connectToLoggregator(String endpoint, String mode, UUID appId,
                                                      ApplicationLogListener listener,
                                                      ClientEndpointConfig.Configurator configurator) {
        URI loggregatorUri = loggregatorStreamUriTemplate.expand(endpoint, mode, appId);

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

    public String getRecentHttpEndpoint(String endpoint) {
        URI uri = stringToUri(endpoint);

        String scheme = uri.getScheme();
        String host = uri.getHost();

        if ("wss".equals(scheme)) {
            scheme = "https";
        } else {
            scheme = "http";
        }

        return loggregatorRecentUriTemplate.expand(scheme, host).toString();
    }

    private ClientEndpointConfig buildClientConfig(ClientEndpointConfig.Configurator configurator) {
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();

        if (trustSelfSignedCerts) {
            SSLContext sslContext = createSslContext();
            Map<String, Object> userProperties = config.getUserProperties();
            userProperties.put(WsWebSocketContainer.SSL_CONTEXT_PROPERTY, sslContext);
        }

        return config;
    }

    private SSLContext createSslContext() {
        try {
            TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws
                        CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws
                        CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }};

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);

            return sslContext;
        } catch (NoSuchAlgorithmException e) {
            throw new CloudOperationException(e);
        } catch (KeyManagementException e) {
            throw new CloudOperationException(e);
        }
    }

    private URI stringToUri(String endPoint) {
        try {
            return new URI(endPoint);
        } catch (URISyntaxException e) {
            throw new CloudOperationException("Unable to parse Loggregator endpoint " + endPoint);
        }
    }
}
