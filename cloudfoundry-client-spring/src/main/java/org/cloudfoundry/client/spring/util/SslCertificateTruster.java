/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.spring.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;

/**
 * A utility that trusts certificates provided by connections
 */
public interface SslCertificateTruster {

    /**
     * Trust the certificate provided by a connection
     *
     * @param host     the host
     * @param port     the port
     * @param timeout  the maximum time to wait
     * @param timeUnit the time unit of the {@code timeout} argument
     */
    void trust(String host, int port, int timeout, TimeUnit timeUnit) throws GeneralSecurityException, IOException;

}
