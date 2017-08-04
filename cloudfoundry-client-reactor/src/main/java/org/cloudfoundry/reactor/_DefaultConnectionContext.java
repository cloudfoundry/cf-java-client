/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.reactor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.reactor.util.DefaultSslCertificateTruster;
import org.cloudfoundry.reactor.util.SslCertificateTruster;
import org.cloudfoundry.reactor.util.StaticTrustManagerFactory;
import org.immutables.value.Value;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.resources.LoopResources;
import reactor.ipc.netty.resources.PoolResources;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static io.netty.channel.ChannelOption.SO_KEEPALIVE;
import static io.netty.channel.ChannelOption.SO_RCVBUF;
import static io.netty.channel.ChannelOption.SO_SNDBUF;

/**
 * The default implementation of the {@link ConnectionContext} interface.  This is the implementation that should be used for most non-testing cases.
 */
@Value.Immutable
abstract class _DefaultConnectionContext implements ConnectionContext {

    private static final int RECEIVE_BUFFER_SIZE = 10 * 1024 * 1024;

    private static final int SEND_BUFFER_SIZE = 10 * 1024 * 1024;

    /**
     * Disposes resources created to service this connection context
     */
    @PreDestroy
    public final void dispose() {
        getConnectionPool().ifPresent(PoolResources::dispose);
        getThreadPool().dispose();
    }

    /**
     * The number of connections to use when processing requests and responses.  Setting this to `null` disables connection pooling.
     */
    @Nullable
    @Value.Default
    public Integer getConnectionPoolSize() {
        return PoolResources.DEFAULT_POOL_MAX_CONNECTION;
    }

    @Override
    @Value.Default
    public HttpClient getHttpClient() {
        return HttpClient.create(options -> {
            options
                .compression(true)
                .loopResources(getThreadPool())
                .option(SO_SNDBUF, SEND_BUFFER_SIZE)
                .option(SO_RCVBUF, RECEIVE_BUFFER_SIZE)
                .disablePool();

            options.sslSupport(ssl -> getSslCertificateTruster().ifPresent(trustManager -> ssl.trustManager(new StaticTrustManagerFactory(trustManager))));

            getConnectionPool().ifPresent(options::poolResources);
            getConnectTimeout().ifPresent(socketTimeout -> options.option(CONNECT_TIMEOUT_MILLIS, (int) socketTimeout.toMillis()));
            getKeepAlive().ifPresent(keepAlive -> options.option(SO_KEEPALIVE, keepAlive));
            getSslHandshakeTimeout().ifPresent(options::sslHandshakeTimeout);
            getProxyConfiguration().ifPresent(c -> c.configure(options));
        });
    }

    @Override
    @Value.Default
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper()
            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .registerModule(new Jdk8Module())
            .setSerializationInclusion(NON_NULL);

        getProblemHandlers().forEach(objectMapper::addHandler);

        return objectMapper;
    }

    @Override
    @Value.Default
    public RootProvider getRootProvider() {
        return InfoPayloadRootProvider.builder()
            .apiHost(getApiHost())
            .objectMapper(getObjectMapper())
            .port(getPort())
            .secure(getSecure())
            .build();
    }

    /**
     * The number of worker threads to use when processing requests and responses
     */
    @Value.Default
    public Integer getThreadPoolSize() {
        return LoopResources.DEFAULT_IO_WORKER_COUNT;
    }

    @Override
    public void trust(String host, int port) {
        getSslCertificateTruster().ifPresent(t -> t.trust(host, port, Duration.ofSeconds(30)));
    }

    /**
     * The hostname of the API root.  Typically something like {@code api.run.pivotal.io}.
     */
    @Nullable
    abstract String getApiHost();

    /**
     * The {@code CONNECT_TIMEOUT_MILLIS} value
     */
    abstract Optional<Duration> getConnectTimeout();

    /**
     * The {@code SO_KEEPALIVE} value
     */
    abstract Optional<Boolean> getKeepAlive();

    /**
     * The port for the Cloud Foundry instance. Defaults to {@code 443}.
     */
    abstract Optional<Integer> getPort();

    /**
     * Jackson deserialization problem handlers.  Typically only used for testing.
     */
    abstract List<DeserializationProblemHandler> getProblemHandlers();

    /**
     * The (optional) proxy configuration
     */
    abstract Optional<ProxyConfiguration> getProxyConfiguration();

    /**
     * Whether the connection to the root API should be secure (i.e. using HTTPS).  Defaults to {@code true}.
     */
    abstract Optional<Boolean> getSecure();

    /**
     * Whether to skip SSL certificate validation for all hosts reachable from the API host.  Defaults to {@code false}.
     */
    abstract Optional<Boolean> getSkipSslValidation();

    @Value.Derived
    Optional<SslCertificateTruster> getSslCertificateTruster() {
        if (getSkipSslValidation().orElse(false)) {
            return Optional.of(new DefaultSslCertificateTruster(getProxyConfiguration()));
        } else {
            return Optional.empty();
        }
    }

    /**
     * The timeout for the SSL handshake negotiation
     */
    abstract Optional<Duration> getSslHandshakeTimeout();

    private Optional<PoolResources> getConnectionPool() {
        return Optional.ofNullable(getConnectionPoolSize())
            .map(connectionPoolSize -> PoolResources.fixed("cloudfoundry-client", connectionPoolSize));
    }

    private LoopResources getThreadPool() {
        return LoopResources.create("cloudfoundry-client", getThreadPoolSize(), true);
    }

}
