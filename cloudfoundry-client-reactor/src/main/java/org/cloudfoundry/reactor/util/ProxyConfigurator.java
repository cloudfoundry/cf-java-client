package org.cloudfoundry.reactor.util;

import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.function.Supplier;

import org.cloudfoundry.reactor.ProxyConfiguration;

import reactor.ipc.netty.options.ClientOptions;
import reactor.ipc.netty.options.ClientOptions.Proxy;

/**
 * Configures proxy in {@link ClientOptions}.
 */
public class ProxyConfigurator {

    /**
     * Configure the given options to use the proxy as defined in the provided proxy configuration.
     */
    public static void configure(ClientOptions opts, ProxyConfiguration proxyConf) {
        Supplier<InetSocketAddress> hostAddr = () -> {
            try {
                InetAddress addr = InetAddress.getByName(proxyConf.getHost());
                return new InetSocketAddress(addr, proxyConf.getPort().orElse(0));
            } catch (UnknownHostException e) {
                throw new UncheckedIOException("failed to resolve host " + proxyConf.getHost(), e);
            }
        };
        opts.proxy(Proxy.HTTP, hostAddr, proxyConf.getUsername().orElse(null),
                u -> proxyConf.getPassword().orElse(null));
    }

}
