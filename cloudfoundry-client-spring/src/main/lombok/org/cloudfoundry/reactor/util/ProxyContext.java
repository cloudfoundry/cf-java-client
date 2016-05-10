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
import lombok.Builder;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.Optional;

@Builder
final class ProxyContext {

    private final String host;

    private final String password;

    private final Integer port;

    private final String username;

    Optional<ChannelHandler> getHttpProxyHandler() {
        if (StringUtils.hasText(this.host)) {
            InetSocketAddress proxyAddress = new InetSocketAddress(this.host, Optional.ofNullable(this.port).orElse(8080));

            HttpProxyHandler httpProxyHandler;
            if (this.username != null) {
                httpProxyHandler = new HttpProxyHandler(proxyAddress, this.username, this.password);
            } else {
                httpProxyHandler = new HttpProxyHandler(proxyAddress);
            }

            return Optional.of(httpProxyHandler);
        }

        return Optional.empty();
    }
}
