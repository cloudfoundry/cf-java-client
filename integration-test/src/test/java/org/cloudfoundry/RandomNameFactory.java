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

package org.cloudfoundry;

import reactor.core.Exceptions;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

final class RandomNameFactory implements NameFactory {

    private static final int PORT_MAXIMUM = 61_099;

    private static final int PORT_MINIMUM = 61_001;

    private final Random random;

    private AtomicInteger port = new AtomicInteger(PORT_MINIMUM);

    RandomNameFactory(Random random) {
        this.random = random;
    }

    @Override
    public String getIpAddress() {
        try {
            return InetAddress.getByName(String.format("169.254.%d.%d", 1 + this.random.nextInt(254), this.random.nextInt(256))).getHostAddress();
        } catch (UnknownHostException e) {
            throw Exceptions.propagate(e);
        }
    }

    @Override
    public String getName(String prefix) {
        return String.format("%s%s", prefix, new BigInteger(25, this.random).toString(32));
    }

    @Override
    public int getPort() {
        int candidate = this.port.getAndIncrement();

        if (candidate <= PORT_MAXIMUM) {
            return candidate;
        } else {
            throw new IllegalStateException("All suitable ports have been allocated");
        }
    }

    @Override
    public boolean isIpAddress(String candidate) {
        try {
            return InetAddress.getByName(candidate).isLinkLocalAddress();
        } catch (UnknownHostException e) {
            return false;
        }
    }

    @Override
    public boolean isName(String prefix, String candidate) {
        return candidate != null && candidate.startsWith(prefix);
    }

    @Override
    public boolean isPort(int candidate) {
        return candidate >= PORT_MINIMUM && candidate <= PORT_MAXIMUM;
    }

}
