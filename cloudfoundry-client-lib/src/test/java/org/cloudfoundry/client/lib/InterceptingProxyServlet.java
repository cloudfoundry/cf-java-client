package org.cloudfoundry.client.lib;

import org.eclipse.jetty.client.Address;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlets.ProxyServlet;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An http proxy implementation that corrects fake host name to ensure some request indeed go through the proxy
 * as expected
 */
public class InterceptingProxyServlet extends ProxyServlet {
    private AtomicInteger nbReceivedRequests;
    /**
     * If ever this junit test needs a proxy to reach the Cf instance, we direct to it.
     */
    private HttpProxyConfiguration httpProxyConfiguration;
    private String fakeFdqnProxiedSuffix;

    public InterceptingProxyServlet(HttpProxyConfiguration httpProxyConfiguration, String fakeFdqnProxiedSuffix, AtomicInteger nbReceivedRequests) {
        this.httpProxyConfiguration = httpProxyConfiguration;
        this.fakeFdqnProxiedSuffix = fakeFdqnProxiedSuffix;
        this.nbReceivedRequests = nbReceivedRequests;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        final Request request = (Request) req;

        //Setting the host header in the proxy servlet is not sufficient, we do also need to rewrite the uri and the servername.

        String host = request.getServerName();
        if (host.endsWith(fakeFdqnProxiedSuffix)) {
            host = host.substring(0, host.length() - fakeFdqnProxiedSuffix.length());
            setHostHeader(host);
            request.setServerName(host);
        }

        String requestURI = request.getUri().toString();
        int fakeIndex = requestURI.indexOf(fakeFdqnProxiedSuffix);
        if (fakeIndex >0) {
            requestURI = removeFakeSuffixFromUri(requestURI, fakeIndex);
            request.setUri(new HttpURI(requestURI));
        }

        nbReceivedRequests.incrementAndGet();

        super.service(req, res);
    }

    /**
    @Override
    protected HttpURI proxyHttpURI(final String scheme, String serverName, int serverPort, String uri) throws MalformedURLException {
        int fakeIndex = serverName.indexOf(fakeFdqnProxiedSuffix);
        if (fakeIndex >0) {
            serverName = removeFakeSuffixFromUri(serverName, fakeIndex);
        }
        return super.proxyHttpURI(scheme, serverName, serverPort, uri);
    }

    */

    /**
    @Override
    protected void customizeExchange(HttpExchange exchange, HttpServletRequest request) {
        String requestURI = exchange.getRequestURI();
        int fakeIndex = requestURI.indexOf(fakeFdqnProxiedSuffix);
        if (fakeIndex >0) {
            requestURI = removeFakeSuffixFromUri(requestURI, fakeIndex);
            exchange.setRequestURI(requestURI);
        }
    }

    */
    private String removeFakeSuffixFromUri(String requestURI, int fakeIndex) {
        requestURI = requestURI.substring(0, fakeIndex) + requestURI.substring(fakeIndex+fakeFdqnProxiedSuffix.length());
        return requestURI;
    }

    @Override
    protected HttpClient createHttpClientInstance() {
        HttpClient httpClient = super.createHttpClientInstance();
        if (httpProxyConfiguration != null) {
            httpClient.setProxy(new Address(httpProxyConfiguration.getProxyHost(), httpProxyConfiguration.getProxyPort()));
        }
        return httpClient;
    }

    /** jetty 9 impl variant which imposes upgrade to java 7


    @Override
    protected HttpClient createHttpClient() throws ServletException {
        HttpClient httpClient = super.createHttpClient();
        if (httpProxyConfiguration != null) {
            ProxyConfiguration proxyConfiguration = new ProxyConfiguration(httpProxyConfiguration.getProxyHost(), httpProxyConfiguration.getProxyPort());
            httpClient.setProxyConfiguration(proxyConfiguration);
        }
        return httpClient;
    }
     */
}
