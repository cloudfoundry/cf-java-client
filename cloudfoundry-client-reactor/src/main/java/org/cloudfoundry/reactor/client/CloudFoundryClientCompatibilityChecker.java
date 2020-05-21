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

package org.cloudfoundry.reactor.client;

import com.github.zafarkhaja.semver.Version;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.client.v2.info.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.util.tuple.TupleUtils.consumer;

final class CloudFoundryClientCompatibilityChecker {

    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.compatibility");

    private final Info info;

    CloudFoundryClientCompatibilityChecker(Info info) {
        this.info = info;
    }

    void check() {
        this.info
            .get(GetInfoRequest.builder()
                .build())
            .map(response -> Version.valueOf(response.getApiVersion()))
            .zipWith(Mono.just(Version.valueOf(CloudFoundryClient.SUPPORTED_API_VERSION)))
            .subscribe(consumer((server, supported) -> logCompatibility(server, supported, this.logger)), t -> this.logger.error("An error occurred while checking version compatibility:", t));
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
