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

import io.netty.handler.ssl.util.SimpleTrustManagerFactory;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import java.security.KeyStore;

public final class StaticTrustManagerFactory extends SimpleTrustManagerFactory {

    private final TrustManager[] trustManagers;

    public StaticTrustManagerFactory(TrustManager... trustManager) {
        this.trustManagers = trustManager;
    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return this.trustManagers;
    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) {
        // Do nothing
    }

    @Override
    protected void engineInit(KeyStore keyStore) {
        // Do nothing
    }

}
