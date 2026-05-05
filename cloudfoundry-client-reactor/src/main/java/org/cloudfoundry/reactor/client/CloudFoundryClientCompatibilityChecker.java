/*
 * Copyright 2013-2021 the original author or authors.
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

import static org.cloudfoundry.util.tuple.TupleUtils.consumer;

import com.github.zafarkhaja.semver.Version;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.GetRootRequest;
import org.cloudfoundry.client.Root;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.client.v2.info.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

final class CloudFoundryClientCompatibilityChecker {

    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.compatibility");

    private final Info info;
    private final Root root;

    CloudFoundryClientCompatibilityChecker(Info info, Root root) {
        this.info = info;
        this.root = root;
    }

    void check() {
        Mono<Tuple2<Version, Version>> v2 = checkV2();
        v2.switchIfEmpty(checkV3())
                .subscribe(
                        consumer(
                                (server, supported) ->
                                        logCompatibility(server, supported, this.logger)),
                        t ->
                                this.logger.error(
                                        "An error occurred while checking version compatibility:",
                                        t));
    }

    private Mono<Tuple2<Version, Version>> checkV2() {
        return this.info
                .get(GetInfoRequest.builder().build())
                .flatMap(
                        response -> {
                            String version = response.getApiVersion();
                            if (version == null || version.isEmpty()) {
                                if ("CF API v2 is disabled".equals(response.getSupport())) {
                                    this.logger.warn(
                                            "calling v2 info endpoint but CF API v2 is disabled",
                                            response);
                                }
                                return Mono.empty();
                            } else {
                                return Mono.just(Version.valueOf(version));
                            }
                        })
                .zipWith(Mono.just(Version.valueOf(CloudFoundryClient.SUPPORTED_API_VERSION)));
    }

    private Mono<Tuple2<Version, Version>> checkV3() {
        return this.root
                .get(GetRootRequest.builder().build())
                .map(
                        response -> {
                            String versionV3 = response.getApiVersionV3();
                            return Version.valueOf(versionV3);
                        })
                .zipWith(Mono.just(Version.valueOf(CloudFoundryClient.SUPPORTED_API_VERSION_V3)));
    }

    private static void logCompatibility(Version server, Version supported, Logger logger) {
        String message =
                "Client supports API version {} and is connected to server with API version {}."
                        + " Things may not work as expected.";
        if (server.greaterThan(supported)) {
            logger.info(message, supported, server);
        } else if (server.lessThan(supported)) {
            logger.warn(message, supported, server);
        }
    }
}
