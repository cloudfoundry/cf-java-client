package org.cloudfoundry.client.lib;

import java.io.IOException;

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
            if (host != "127.0.0.1") {
                throw new IOException("only proxy threads should go through external hosts, got:host=" + host + " port=" + port);
            }
        }
    }
}
