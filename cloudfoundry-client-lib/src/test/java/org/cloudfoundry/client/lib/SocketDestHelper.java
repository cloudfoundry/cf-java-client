package org.cloudfoundry.client.lib;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Byteman helper which rejects Sockets on non jetty threads that do not target the local http proxy.
 * Calls to this class are dynamically injected into JDK java.net.Socket bytecode by byteman.
 */
public class SocketDestHelper {

    private static final ThreadLocal<Boolean> isSocketRestrictingOnlyLocalHost = new ThreadLocal<Boolean>();

    private static final Set<String> installedRules = Collections.synchronizedSet(new HashSet<String>());
    private static final AtomicBoolean isActivated = new AtomicBoolean(false);


    //Byteman API

    public static void activated() {
        logDebugTrace("SocketDestHelper activated");
        isActivated.set(true);
    }
    public static void installed(String ruleName) {
        logDebugTrace("SocketDestHelper installed:" + ruleName);
        installedRules.add(ruleName);

    }
    public static void uninstalled(String ruleName) {
        logDebugTrace("SocketDestHelper uninstalled:" + ruleName);
        installedRules.remove(ruleName);
    }

    public static void deactivated() {
        logDebugTrace("SocketDestHelper deactivated");
        isActivated.set(false);
    }

    // accessors the checking rules are activates

    public static Set<String> getInstalledRules() {
        return installedRules;
    }

    public static boolean isActivated() {
        return isActivated.get();
    }

    public static boolean isSocketRestrictionFlagActive() {
        return isSocketRestrictingOnlyLocalHost.get();
    }



    public  void setForbiddenOnCurrentThread() {
        isSocketRestrictingOnlyLocalHost.set(true);
    }

    //helper methods for byteman rules

    public void alwaysThrowException() throws IOException {
        IOException ioe = new IOException("always throws IOE");
        printStackTrace(ioe);
        throw ioe;
    }

    public void throwExceptionIfForbidden(String host, int port) throws IOException {
        logDebugTrace("throwExceptionIfForbidden(host=" + host + " port=" + port + ") with isSocketRestrictingOnlyLocalHost=" + isSocketRestrictingOnlyLocalHost.get());
        Boolean flag = isSocketRestrictingOnlyLocalHost.get();
        if (flag != null && flag.booleanValue()) {
            if (! host.equals("127.0.0.1") && ! host.equals("localhost")) {
                IOException ioe = new IOException("detected direct socket connect while tests expect them to go through proxy instead: Only jetty proxy threads should go through external hosts, got:host=" + host + " port=" + port);
                printStackTrace(ioe);
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

    public void throwExceptionIfForbidden(InetAddress inetAddress, int port) throws IOException {
        throwExceptionIfForbidden(inetAddress.getHostName(), port);
    }

    public void throwExceptionIfForbidden(InetSocketAddress inetAddress) throws IOException {
        throwExceptionIfForbidden(inetAddress.getHostName(), inetAddress.getPort());
    }
    public void throwExceptionIfForbidden(InetSocketAddress inetAddress, int port) throws IOException {
        throwExceptionIfForbidden(inetAddress.getHostName(), port);
    }
    private static void logDebugTrace(String msg) {
        //until cloud-foundry-client-lib embed a logging library (slf4j ?), for debugging tests just change this flag
        if (false) {
            System.out.println(msg);
            System.out.flush();
        }
    }

    private static void printStackTrace(IOException ioe) {
        if (false) {
            ioe.printStackTrace();
        }
    }


}
