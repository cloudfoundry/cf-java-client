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

package org.cloudfoundry.logcache.v1;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.ApplicationUtils;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.junit.Test;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;
import java.time.Duration;
import java.util.Random;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.cloudfoundry.util.DelayUtils.exponentialBackOff;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
public class LogCacheTest extends AbstractIntegrationTest implements InitializingBean {

    @Autowired
    LogCacheClient logCacheClient;

    @Autowired
    Random random;

    @Autowired
    private Mono<ApplicationUtils.ApplicationMetadata> testLogCacheApp;

    private ApplicationUtils.ApplicationMetadata testLogCacheAppMetadata;

    @Autowired
    private TestLogCacheEndpoints testLogCacheEndpoints;

    @Override
    public void afterPropertiesSet() {
        this.testLogCacheAppMetadata = this.testLogCacheApp.block();
    }

    @Test
    public void info() {
        this.logCacheClient.info(InfoRequest.builder().build())
            .as(StepVerifier::create)
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.getVersion())
                    .isNotBlank();
                assertThat(response.vmUptime())
                    .isNotNull()
                    .isGreaterThan(1);
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void meta() {
        this.logCacheClient.meta(MetaRequest.builder().build())
            .as(StepVerifier::create)
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.getMeta())
                    .containsKey(this.testLogCacheAppMetadata.applicationId);
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void readCounter() {
        final String name = this.nameFactory.getName("counter-");
        final int delta = this.random.nextInt(1000);

        this.testLogCacheEndpoints.counter(name, delta)
            .then(read(EnvelopeType.COUNTER, envelope -> hasCounter(envelope) && name.equals(envelope.getCounter().getName())))
            .as(StepVerifier::create)
            .assertNext(response -> assertCounter(response, name, delta))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void readEvent() {
        final String title = this.nameFactory.getName("event-");
        final String body = "This is the body. " + new BigInteger(1024, this.random).toString(32);

        this.testLogCacheEndpoints.event(title, body)
            .then(read(EnvelopeType.EVENT, this::hasEvent))
            .as(StepVerifier::create)
            .assertNext(response -> assertEvent(response, title, body))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void readGauge() {
        final String gaugeName = this.nameFactory.getName("gauge-");
        final Double value = this.random.nextDouble() % 100;

        this.testLogCacheEndpoints.gauge(gaugeName, value)
            .then(read(EnvelopeType.GAUGE, envelope -> hasGauge(envelope) && envelope.getGauge().getMetrics().containsKey(gaugeName)))
            .as(StepVerifier::create)
            .assertNext(response -> assertGauge(response, gaugeName, value))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void readLogs() {
        final String logMessage = this.nameFactory.getName("log-");

        this.testLogCacheEndpoints.log(logMessage)
            .then(read(EnvelopeType.LOG, envelope -> hasLog(envelope) && logMessage.equals(envelope.getLog().getPayloadAsText())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private void assertCounter(Envelope envelope, String name, int delta) {
        assertThat(envelope.getCounter().getName()).isEqualTo(name);
        assertThat(envelope.getCounter().getDelta()).isEqualTo(delta);
        assertThat(envelope.getCounter().getTotal()).isEqualTo(delta);
    }

    private void assertEnvelope(Envelope envelope) {
        assertThat(envelope).isNotNull();
        assertThat(envelope.getInstanceId()).isEqualTo("0");
        assertThat(envelope.getSourceId()).isEqualTo(this.testLogCacheAppMetadata.applicationId);
        assertThat(envelope.getTimestamp()).isGreaterThan(0);
    }

    private void assertEvent(Envelope envelope, String title, String body) {
        assertThat(envelope.getEvent().getBody()).isEqualTo(body);
        assertThat(envelope.getEvent().getTitle()).isEqualTo(title);
    }

    private void assertGauge(Envelope envelope, String gaugeName, Double value) {
        assertThat(envelope.getGauge().getMetrics().get(gaugeName).getValue())
            .isCloseTo(value, within(0.001));
    }

    private void assertResponse(ReadResponse readResponse) {
        assertThat(readResponse).isNotNull();
        assertThat(readResponse.getEnvelopes()).isNotNull();
        assertThat(readResponse.getEnvelopes().getBatch()).isNotNull();
    }

    private boolean hasCounter(Envelope envelope) {
        return envelope.getCounter() != null;
    }

    private boolean hasEvent(Envelope envelope) {
        return envelope.getEvent() != null;
    }

    private boolean hasGauge(Envelope envelope) {
        return envelope.getGauge() != null;
    }

    private boolean hasLog(Envelope envelope) {
        return envelope.getLog() != null;
    }

    private Mono<Envelope> read(EnvelopeType envelopeType, Predicate<Envelope> filter) {
        return this.logCacheClient.read(ReadRequest.builder()
            .sourceId(this.testLogCacheAppMetadata.applicationId)
            .envelopeType(envelopeType)
            .limit(1000)
            .build())
            .doOnNext(this::assertResponse)
            .flatMap(response -> Mono.justOrEmpty(response.getEnvelopes().getBatch().stream().filter(filter).findFirst()))
            .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(5), Duration.ofMinutes(1)))
            .doOnNext(this::assertEnvelope);
    }

}
