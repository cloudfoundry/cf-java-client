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

import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.tuple.Tuple2;

import java.time.Duration;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(IntegrationTestConfiguration.class)
public abstract class AbstractIntegrationTest {

    @Rule
    public final TestName testName = new TestName();

    private final TestSubscriber<?> testSubscriber = new TestSubscriber<>()
        .setScanningLoggerName(() -> String.format("%s.%s", this.getClass().getSimpleName(), this.testName.getMethodName()))
        .setPerformanceLoggerName(() -> String.format("%s.%s", this.getClass().getSimpleName(), this.testName.getMethodName()));

    @Autowired
    private NameFactory nameFactory;

    @After
    public final void verify() throws InterruptedException {
        this.testSubscriber.verify(Duration.ofMinutes(5));
    }

    protected final <T> void assertTupleEquality(Tuple2<T, T> tuple) {
        T actual = tuple.t1;
        T expected = tuple.t2;

        assertEquals(expected, actual);
    }

    protected final String getApplicationName() {
        return this.nameFactory.getName("test-application-");
    }

    protected final String getBuildpackName() {
        return this.nameFactory.getName("test-buildpack-");
    }

    protected final String getDomainName() {
        return this.nameFactory.getName("test.domain.");
    }

    protected final String getHostName() {
        return this.nameFactory.getName("test-host-");
    }

    protected final String getOrganizationName() {
        return this.nameFactory.getName("test-organization-");
    }

    protected final String getPath() {
        return this.nameFactory.getName("/test-path-");
    }

    protected final String getServiceInstanceName() {
        return this.nameFactory.getName("test-service-instance-");
    }

    protected final String getSpaceName() {
        return this.nameFactory.getName("test-space-");
    }

    @SuppressWarnings("unchecked")
    protected final <T> TestSubscriber<T> testSubscriber() {
        return (TestSubscriber<T>) this.testSubscriber;
    }

}
