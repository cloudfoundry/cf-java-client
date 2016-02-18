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

package org.cloudfoundry.spring.util;

import com.github.zafarkhaja.semver.Version;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.cloudfoundry.client.v2.info.Info;
import org.cloudfoundry.util.tuple.Consumer2;
import org.cloudfoundry.util.tuple.Function1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.util.tuple.TupleUtils.consumer;

public final class CloudFoundryClientCompatibilityChecker implements CompatibilityChecker {

    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.compatibility");

    private final Info info;

    public CloudFoundryClientCompatibilityChecker(Info info) {
        this.info = info;
    }

    @Override
    public void check() {
        this.info
            .get(GetInfoRequest.builder()
                .build())
            .map(new Function1<GetInfoResponse, Version>() {

                @Override
                public Version apply(GetInfoResponse response) {
                    return Version.valueOf(response.getApiVersion());
                }

            })
            .and(Mono.just(Version.valueOf(CloudFoundryClient.SUPPORTED_API_VERSION)))
            .doOnSuccess(consumer(new Consumer2<Version, Version>() {

                @Override
                public void accept(Version server, Version supported) {
                    logCompatibility(server, supported, logger);
                }

            }))
            .subscribe();
    }

    private static void logCompatibility(Version server, Version supported, Logger logger) {
        String message = "Client supports API version {} and is connected to server with API version {}. Things may not work as expected.";

        if (server.greaterThan(supported)) {
            logger.info(message, supported, server);
        } else if (server.lessThan(supported)) {
            logger.warn(message, supported, server);
        }
    }

}
