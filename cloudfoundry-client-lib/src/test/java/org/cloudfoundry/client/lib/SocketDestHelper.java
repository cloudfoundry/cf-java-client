package org.cloudfoundry.client.lib;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.*;

/**
 * Rejects Sockets on non jetty threads that do not target the local http proxy
 */
public class SocketDestHelper {

    static ThreadLocal<Boolean> isSocketRestrictingOnlyLocalHost = new ThreadLocal<Boolean>();

    public static void setForbiddenOnCurrentThread() {
        isSocketRestrictingOnlyLocalHost.set(true);
    }

    public void alwaysThrowException() throws IOException {
        throw new IOException("only proxy threads should go through external hosts");
    }
    public void throwExceptionIfForbidden(String host, int port) throws IOException {
        System.out.println("throwExceptionIfForbidden(host=" + host + " port=" + port + ") with isSocketRestrictingOnlyLocalHost=" + isSocketRestrictingOnlyLocalHost.get());
        if (isSocketRestrictingOnlyLocalHost.get()) {
            if (! host.equals("127.0.0.1") && ! host.equals("localhost")) {
                IOException ioe = new IOException("only proxy threads should go through external hosts, got:host=" + host + " port=" + port);
                ioe.printStackTrace();
                throw ioe;
            }
        }
    }
    public void throwExceptionIfForbidden(SocketAddress address) throws IOException {
        if (address instanceof InetSocketAddress) {
            InetSocketAddress inetAddress = (InetSocketAddress) address;
            throwExceptionIfForbidden(inetAddress);
        }
    }

    public void throwExceptionIfForbidden(InetSocketAddress inetAddress) throws IOException {
        throwExceptionIfForbidden(inetAddress.getHostName(), inetAddress.getPort());
    }
    public void throwExceptionIfForbidden(InetSocketAddress inetAddress, int port) throws IOException {
        throwExceptionIfForbidden(inetAddress.getHostName(), port);
    }


    public SocketFactory getDefaultSslSocketFactory() {
        return new SocketFactoryInterceptor();
    }

    /**
     * Wraps default SSLSocketFactory to reject SSL sockets opened directly from non-jetty threads
     */
    public static class SocketFactoryInterceptor extends SocketFactory {

        private SocketFactory impl;

        SocketFactoryInterceptor() {
            this.impl = SSLSocketFactory.getDefault();
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            new SocketDestHelper().throwExceptionIfForbidden(host, port);
            return impl.createSocket(host, port);
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
            new SocketDestHelper().throwExceptionIfForbidden(host, port);
            return impl.createSocket(host, port, localHost, localPort);
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            new SocketDestHelper().throwExceptionIfForbidden(host.getHostName(), port);
            return impl.createSocket(host, port);
        }

        @Override
        public Socket createSocket(InetAddress host, int port, InetAddress localHost, int localPort) throws IOException {
            return impl.createSocket(host, port, localHost, localPort);
        }

    }


}
