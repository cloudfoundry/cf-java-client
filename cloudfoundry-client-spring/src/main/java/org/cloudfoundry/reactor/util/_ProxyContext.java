/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.reactor.util;

import io.netty.channel.ChannelHandler;
import io.netty.handler.proxy.HttpProxyHandler;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.Optional;

@Value.Immutable
abstract class _ProxyContext {

    @Nullable
    abstract String getHost();

    @Value.Derived
    Optional<ChannelHandler> getHttpProxyHandler() {
        if (StringUtils.hasText(getHost())) {
            InetSocketAddress proxyAddress = new InetSocketAddress(getHost(), Optional.ofNullable(getPort()).orElse(8080));

            HttpProxyHandler httpProxyHandler;
            if (getUsername() != null) {
                httpProxyHandler = new HttpProxyHandler(proxyAddress, getUsername(), getPassword());
            } else {
                httpProxyHandler = new HttpProxyHandler(proxyAddress);
            }

            return Optional.of(httpProxyHandler);
        }

        return Optional.empty();
    }

    @Nullable
    abstract String getPassword();

    @Nullable
    abstract Integer getPort();

    @Nullable
    abstract String getUsername();
}
