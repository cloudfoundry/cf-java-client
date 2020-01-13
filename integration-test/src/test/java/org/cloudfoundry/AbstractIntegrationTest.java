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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = IntegrationTestConfiguration.class)
public abstract class AbstractIntegrationTest {

    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.test");

    @Rule
    public final TestName testName = new TestName();

    @Autowired
    @Rule
    public CloudFoundryVersionConditionalRule cloudFoundryVersion;

    @Autowired
    protected NameFactory nameFactory;

    @Before
    public void testEntry() {
        this.logger.debug(">> {} <<", getTestName());
    }

    @After
    public final void testExit() {
        this.logger.debug("<< {} >>", getTestName());
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

    protected static <T> Consumer<Tuple2<T, T>> tupleEquality() {
        return consumer((expected, actual) -> assertThat(actual).isEqualTo(expected));
    }

    private String getTestName() {
        return String.format("%s.%s", this.getClass().getSimpleName(), this.testName.getMethodName());
    }

}
