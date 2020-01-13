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

package org.cloudfoundry.util;

import org.cloudfoundry.client.v2.ClientV2Exception;
import org.cloudfoundry.client.v2.serviceinstances.LastOperation;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Utilities for {@link LastOperation}s
 */
public final class LastOperationUtils {

    private static final String IN_PROGRESS = "in progress";

    private LastOperationUtils() {
    }

    public static Mono<Void> waitForCompletion(Duration completionTimeout, Supplier<Mono<LastOperation>> lastOperationSupplier) {
        return lastOperationSupplier.get()
            .map(LastOperation::getState)
            .filter(state -> !IN_PROGRESS.equals(state))
            .repeatWhenEmpty(DelayUtils.exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), completionTimeout))
            .onErrorResume(t -> t instanceof ClientV2Exception && ((ClientV2Exception) t).getStatusCode() == 404, t -> Mono.empty())
            .then();
    }

}
