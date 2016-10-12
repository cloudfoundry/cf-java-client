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

package org.cloudfoundry.operations;

import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.util.Assert;
import reactor.test.subscriber.ScriptedSubscriber;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractOperationsApiTest<T> extends AbstractOperationsTest {

    @Test
    public final void test() throws Exception {
        ScriptedSubscriber<T> subscriber = expectations();
        invoke().subscribe(subscriber);
        subscriber.verify(Duration.ofSeconds(5));
    }

    protected static <T> ScriptedSubscriber<T> errorExpectation(Class<? extends Throwable> type, String format, Object... args) {
        Assert.notNull(type, "type must not be null");
        Assert.notNull(format, "format must not be null");

        Function<Throwable, Optional<String>> assertion = t -> {
            if (!type.isInstance(t)) {
                return Optional.of(String.format("expected error of type: %s; actual type: %s", type.getSimpleName(), t.getClass().getSimpleName()));
            }

            String expected = String.format(format, args);
            if (!expected.equals(t.getMessage())) {
                return Optional.of(String.format("expected message: %s; actual message: %s", expected, t.getMessage()));
            }

            return Optional.empty();
        };

        return ScriptedSubscriber.<T>create()
            .expectErrorWith(t -> !assertion.apply(t).isPresent(),
                t -> assertion.apply(t).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching error")));
    }

    protected abstract ScriptedSubscriber<T> expectations();

    protected abstract Publisher<T> invoke();

}
