package org.cloudfoundry.client.lib;

import org.eclipse.jetty.client.Address;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.servlets.ProxyServlet;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An http proxy implementation that is able to chain request to another proxy. Usefull when starting an InJvm proxy chained to another
 * corporate proxy.
 */
public class ChainedProxyServlet extends ProxyServlet {
    private AtomicInteger nbReceivedRequests;
    /**
     * If ever this junit test needs a proxy to reach the Cf instance, we direct to it.
     */
    private HttpProxyConfiguration httpProxyConfiguration;

    public ChainedProxyServlet(HttpProxyConfiguration httpProxyConfiguration, AtomicInteger nbReceivedRequests) {
        this.httpProxyConfiguration = httpProxyConfiguration;
        this.nbReceivedRequests = nbReceivedRequests;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        nbReceivedRequests.incrementAndGet();
        super.service(req, res);
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
