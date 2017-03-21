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

package org.cloudfoundry.reactor.uaa;

import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.util.DelayUtils;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

public final class TestUsernameProviderRefresh {

    public static void main(String[] args) throws InterruptedException {
        DefaultConnectionContext connectionContext = DefaultConnectionContext.builder()
            .apiHost("api.run.pivotal.io")
            .build();

        PasswordGrantTokenProvider tokenProvider = PasswordGrantTokenProvider.builder()
            .password("rNgdFPs{NgYYVW{PXCAJMqGuokEtVpBgRDu2zjQgJ7nNNFnokA")
            .username("bhale@pivotal.io")
            .build();

        UaaClient uaaClient = ReactorUaaClient.builder()
            .connectionContext(connectionContext)
            .tokenProvider(tokenProvider)
            .build();

        CountDownLatch latch = new CountDownLatch(1);

        uaaClient.getUsername()
            .doOnNext(System.out::println)
            .filter(s -> false)
            .repeatWhenEmpty(DelayUtils.fixed(Duration.ofSeconds(30)))
            .subscribe(System.out::println, t -> {
                t.printStackTrace();
                latch.countDown();
            }, latch::countDown);

        latch.await();
    }

}
