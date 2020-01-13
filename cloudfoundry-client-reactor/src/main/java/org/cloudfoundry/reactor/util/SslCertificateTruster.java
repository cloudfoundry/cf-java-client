/*
 * Copyright 2013-2020 the original author or authors.
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

import reactor.core.publisher.Mono;

import javax.net.ssl.X509TrustManager;
import java.time.Duration;

/**
 * A utility that trusts certificates provided by connections
 */
public interface SslCertificateTruster extends X509TrustManager {

    /**
     * Trust the certificate provided by a connection
     *
     * @param host     the host
     * @param port     the port
     * @param duration the duration to wait
     */
    Mono<Void> trust(String host, int port, Duration duration);

}
