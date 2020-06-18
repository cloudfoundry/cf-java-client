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

package org.cloudfoundry.reactor.logcache.v1;

import org.cloudfoundry.logcache.v1.Envelope;
import org.cloudfoundry.logcache.v1.EnvelopeBatch;
import org.cloudfoundry.logcache.v1.Gauge;
import org.cloudfoundry.logcache.v1.InfoRequest;
import org.cloudfoundry.logcache.v1.InfoResponse;
import org.cloudfoundry.logcache.v1.Log;
import org.cloudfoundry.logcache.v1.LogType;
import org.cloudfoundry.logcache.v1.MetaRequest;
import org.cloudfoundry.logcache.v1.MetaResponse;
import org.cloudfoundry.logcache.v1.Metadata;
import org.cloudfoundry.logcache.v1.Metric;
import org.cloudfoundry.logcache.v1.ReadRequest;
import org.cloudfoundry.logcache.v1.ReadResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class ReactorLogCacheClientTest extends AbstractLogCacheApiTest {

    private final ReactorLogCacheEndpoints logCacheEndpoints = new ReactorLogCacheEndpoints(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void info() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/api/v1/info")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .contentType("text/plain; charset=utf-8")
                .payload("fixtures/logcache.v1/GET_info_response.json")
                .build())
            .build());

        this.logCacheEndpoints.info(InfoRequest.builder().build())
            .as(StepVerifier::create)
            .expectNext(InfoResponse.builder()
                .version("2.6.1")
                .vmUptime(7166438L)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void meta() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/api/v1/meta")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .contentType("text/plain; charset=utf-8")
                .payload("fixtures/logcache.v1/GET_meta_response.json")
                .build())
            .build());

        this.logCacheEndpoints.meta(MetaRequest.builder().build())
            .as(StepVerifier::create)
            .expectNext(MetaResponse.builder()
                .meta("traffic_controller", Metadata.builder()
                    .count(5490L)
                    .expired(1069110L)
                    .newestTimestamp(1588631926299067790L)
                    .oldestTimestamp(1588595386296937544L)
                    .build())
                .meta("uaa", Metadata.builder()
                    .count(100000L)
                    .expired(79066604L)
                    .newestTimestamp(1588631951858159538L)
                    .oldestTimestamp(1588623478864261934L)
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void read() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/api/v1/read/test-source-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .contentType("application/json")
                .payload("fixtures/logcache.v1/GET_{id}_read_response.json")
                .build())
            .build());

        this.logCacheEndpoints.read(ReadRequest.builder()
                .sourceId("test-source-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(ReadResponse.builder()
                .envelopes(EnvelopeBatch.builder()
                    .batch(
                        Envelope.builder()
                            .timestamp(1588592413697846700L)
                            .sourceId("1a69c137-90f5-4b0a-8526-2ebca84c83a8")
                            .instanceId("0")
                            .tag("app_id", "1a69c137-90f5-4b0a-8526-2ebca84c83a8")
                            .tag("app_name", "test")
                            .tag("deployment", "cf-6a000373a858bcb78f1c")
                            .tag("index", "f237deb0-471f-459a-a18b-de084a8113e7")
                            .tag("instance_id", "0")
                            .tag("ip", "10.194.34.36")
                            .tag("job", "diego_cell")
                            .tag("organization_id", "825ec316-5590-416a-9247-1dd0a5750801")
                            .tag("organization_name", "system")
                            .tag("origin", "rep")
                            .tag("process_id", "1a69c137-90f5-4b0a-8526-2ebca84c83a8")
                            .tag("process_instance_id", "9173f9d0-02b7-4f39-4738-f53b")
                            .tag("process_type", "web")
                            .tag("product", "Pivotal Application Service")
                            .tag("source_id", "1a69c137-90f5-4b0a-8526-2ebca84c83a8")
                            .tag("space_id", "547d902c-0554-4e7c-b886-3926a2e73bdf")
                            .tag("space_name", "test")
                            .tag("system_domain", "cf.red.springapps.io")
                            .gauge(Gauge.builder()
                                .metric("cpu", Metric.builder().unit("percentage").value(0.394234612100979).build())
                                .metric("disk", Metric.builder().unit("bytes").value(433881088D).build())
                                .metric("disk_quota", Metric.builder().unit("bytes").value(2122546345D).build())
                                .metric("memory", Metric.builder().unit("bytes").value(822926477D).build())
                                .metric("memory_quota", Metric.builder().unit("bytes").value(2147483648D).build())
                                .build())
                            .build(),
                        Envelope.builder()
                            .timestamp(1588617404686865694L)
                            .sourceId("1a69c137-90f5-4b0a-8526-2ebca84c83a8")
                            .instanceId("0")
                            .tag("app_id", "1a69c137-90f5-4b0a-8526-2ebca84c83a8")
                            .tag("app_name", "test")
                            .tag("deployment", "cf-6a000373a858bcb78f1c")
                            .tag("index", "f237deb0-471f-459a-a18b-de084a8113e7")
                            .tag("instance_id", "0")
                            .tag("ip", "10.194.34.36")
                            .tag("job", "diego_cell")
                            .tag("organization_id", "825ec316-5590-416a-9247-1dd0a5750801")
                            .tag("organization_name", "system")
                            .tag("origin", "rep")
                            .tag("process_id", "1a69c137-90f5-4b0a-8526-2ebca84c83a8")
                            .tag("process_instance_id", "9173f9d0-02b7-4f39-4738-f53b")
                            .tag("process_type", "web")
                            .tag("product", "Pivotal Application Service")
                            .tag("source_id", "1a69c137-90f5-4b0a-8526-2ebca84c83a8")
                            .tag("source_type", "APP/PROC/WEB")
                            .tag("space_id", "547d902c-0554-4e7c-b886-3926a2e73bdf")
                            .tag("space_name", "test")
                            .tag("system_domain", "cf.red.springapps.io")
                            .log(Log.builder()
                                .payload("MjAyMC0wNS0wNCAxODozNjo0NC42ODYgIElORk8gMTMgLS0tIFstaW5zdGFuY2Uua2V5LTBdIG8uYy5zLkZpbGVXYXRjaGluZ1g1MDlFeHRlbmRlZEtleU1hbmFnZXIgOiBVcGRhdGVkIEtleU1hbmFnZXIgZm9yIC9ldGMvY2YtaW5zdGFuY2UtY3JlZGVudGlhbHMvaW5zdGFuY2Uua2V5IGFuZCAvZXRjL2NmLWluc3RhbmNlLWNyZWRlbnRpYWxzL2luc3RhbmNlLmNydA==")
                                .type(LogType.OUT)
                                .build())
                            .build()
                    )
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
