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

package org.cloudfoundry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.test.subscriber.ScriptedSubscriber;
import reactor.util.function.Tuple2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(IntegrationTestConfiguration.class)
public abstract class AbstractIntegrationTest {

    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.test");

    @Rule
    public final TestName testName = new TestName();

    @Autowired
    protected NameFactory nameFactory;

    @Before
    public void testEntry() {
        this.logger.debug(">> {} <<", getTestName());
    }

    @After
    public final void testExit() throws InterruptedException {
        this.logger.debug("<< {} >>", getTestName());
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

    protected static <T> ScriptedSubscriber<T> errorExpectation(Class<? extends Throwable> type, String pattern) {
        Assert.notNull(type, "type must not be null");
        Assert.notNull(pattern, "pattern must not be null");

        Function<Throwable, Optional<String>> assertion = t -> {
            if (!type.isInstance(t)) {
                return Optional.of(String.format("expected error of type: %s; actual type: %s", type.getSimpleName(), t.getClass().getSimpleName()));
            }

            if (!Pattern.compile(pattern).matcher(t.getMessage()).matches()) {
                return Optional.of(String.format("expected message pattern: %s; actual message: %s", pattern, t.getMessage()));
            }

            return Optional.empty();
        };

        return ScriptedSubscriber.<T>create()
            .expectErrorWith(t -> !assertion.apply(t).isPresent(),
                t -> assertion.apply(t).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching error")));
    }

    protected static Mono<byte[]> getBytes(String path) {
        try (InputStream in = new FileInputStream(new File("src/test/resources", path)); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;

            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            return Mono.just(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static <T> ScriptedSubscriber<Tuple2<T, T>> tupleEquality() {
        Function<Tuple2<T, T>, Optional<String>> assertion = function((expected, actual) -> {

            boolean matches;
            if (expected.getClass().isArray()) {
                if (expected.getClass().getComponentType().equals(byte.class)) {
                    matches = Arrays.equals((byte[]) expected, (byte[]) actual);
                } else {
                    matches = Arrays.equals((Object[]) expected, (Object[]) actual);
                }
            } else {
                matches = expected.equals(actual);
            }

            return matches ? Optional.empty() : Optional.of(String.format("expected value: %s; actual value: %s", expected, actual));
        });

        return ScriptedSubscriber.<Tuple2<T, T>>create()
            .expectValueWith(tuple -> !assertion.apply(tuple).isPresent(),
                tuple -> assertion.apply(tuple).orElseThrow(() -> new IllegalStateException("Cannot generate assertion message for matching result")))
            .expectComplete();
    }

    private String getTestName() {
        return String.format("%s.%s", this.getClass().getSimpleName(), this.testName.getMethodName());
    }

}
