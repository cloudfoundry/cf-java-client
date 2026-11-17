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

package org.cloudfoundry.reactor.logcache.v1;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.cloudfoundry.AbstractCloudFoundryException;
import org.cloudfoundry.logcache.v1.Envelope;
import org.cloudfoundry.logcache.v1.EnvelopeType;
import org.cloudfoundry.logcache.v1.InfoRequest;
import org.cloudfoundry.logcache.v1.InfoResponse;
import org.cloudfoundry.logcache.v1.MetaRequest;
import org.cloudfoundry.logcache.v1.MetaResponse;
import org.cloudfoundry.logcache.v1.ReadRequest;
import org.cloudfoundry.logcache.v1.ReadResponse;
import org.cloudfoundry.logcache.v1.TailLogsRequest;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

final class ReactorLogCacheEndpoints extends AbstractLogCacheOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactorLogCacheEndpoints.class);

    private static final int MAX_RETRIES = 5;

    private static final Duration MAX_BACKOFF = Duration.ofSeconds(10);

    ReactorLogCacheEndpoints(
            ConnectionContext connectionContext,
            Mono<String> root,
            TokenProvider tokenProvider,
            Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    Mono<InfoResponse> info(InfoRequest request) {
        return get(request, InfoResponse.class, "info").checkpoint();
    }

    Mono<MetaResponse> meta(MetaRequest request) {
        return get(request, MetaResponse.class, "meta").checkpoint();
    }

    Mono<ReadResponse> read(ReadRequest request) {
        return get(request, ReadResponse.class, "read", request.getSourceId()).checkpoint();
    }

    /**
     * Continuously polls Log Cache and emits new {@link Envelope}s as they arrive.
     *
     * <p>Mirrors the Go {@code logcache.Walk()} / {@code WalkWithDelay} semantics:
     * <ol>
     *   <li>On every subscription, start a cursor at {@code startTime} (defaults to now minus 5 s
     *       in nanoseconds). The cursor is per subscription, so subscribers never interfere.</li>
     *   <li>Issue {@code GET /api/v1/read/{sourceId}?start_time=cursor}.</li>
     *   <li>Log Cache is eventually consistent, so envelopes younger than {@code delay} are
     *       withheld and picked up by a later poll. The rest are emitted in ascending timestamp
     *       order and the cursor advances to {@code lastTimestamp + 1}.</li>
     *   <li>When nothing can be emitted, wait {@code pollInterval} before polling again.</li>
     *   <li>Transient failures (I/O errors, HTTP 429 and 5xx) are retried with exponential
     *       backoff, up to {@value #MAX_RETRIES} times in a row; other errors terminate the
     *       stream.</li>
     * </ol>
     * The stream never completes on its own – the caller cancels the subscription to stop.
     */
    Flux<Envelope> logsTail(TailLogsRequest request) {
        List<EnvelopeType> envelopeTypes =
                request.getEnvelopeTypes() != null
                        ? request.getEnvelopeTypes()
                        : Collections.emptyList();

        return Mono.fromSupplier(
                        () ->
                                request.getStartTime() != null
                                        ? request.getStartTime()
                                        : toNanos(Instant.now().minusSeconds(5)))
                .flatMap(cursor -> poll(request, envelopeTypes, cursor))
                .expand(
                        page ->
                                page.envelopes.isEmpty()
                                        ? Mono.delay(request.getPollInterval())
                                                .then(poll(request, envelopeTypes, page.cursor))
                                        : poll(request, envelopeTypes, page.nextCursor()))
                .concatMapIterable(page -> page.envelopes);
    }

    private Mono<Page> poll(
            TailLogsRequest request, List<EnvelopeType> envelopeTypes, long cursor) {
        ReadRequest.Builder builder =
                ReadRequest.builder().sourceId(request.getSourceId()).startTime(cursor);
        if (!envelopeTypes.isEmpty()) {
            builder.envelopeTypes(envelopeTypes);
        }
        if (request.getNameFilter() != null && !request.getNameFilter().isEmpty()) {
            builder.nameFilter(request.getNameFilter());
        }

        return read(builder.build())
                .retryWhen(
                        Retry.backoff(MAX_RETRIES, request.getPollInterval())
                                .maxBackoff(MAX_BACKOFF)
                                .filter(ReactorLogCacheEndpoints::isTransient)
                                .doBeforeRetry(
                                        signal ->
                                                LOGGER.warn(
                                                        "Transient error polling Log Cache, retry"
                                                                + " {}/{}",
                                                        signal.totalRetries() + 1,
                                                        MAX_RETRIES,
                                                        signal.failure()))
                                .onRetryExhaustedThrow((spec, signal) -> signal.failure()))
                .map(response -> toPage(response, cursor, request.getDelay()));
    }

    private static Page toPage(ReadResponse response, long cursor, Duration delay) {
        List<Envelope> raw =
                response.getEnvelopes() != null
                        ? response.getEnvelopes().getBatch()
                        : Collections.emptyList();
        long newestAllowed = toNanos(Instant.now().minus(delay));

        List<Envelope> ready = new ArrayList<>();
        for (Envelope envelope : raw) {
            if (envelope.getTimestamp() != null && envelope.getTimestamp() <= newestAllowed) {
                ready.add(envelope);
            }
        }
        ready.sort(Comparator.comparingLong(Envelope::getTimestamp));
        return new Page(cursor, ready);
    }

    private static boolean isTransient(Throwable t) {
        if (t instanceof AbstractCloudFoundryException) {
            int status = ((AbstractCloudFoundryException) t).getStatusCode();
            return status == 429 || status >= 500;
        }
        return true;
    }

    private static long toNanos(Instant instant) {
        return instant.getEpochSecond() * 1_000_000_000L + instant.getNano();
    }

    private static final class Page {

        private final long cursor;

        private final List<Envelope> envelopes;

        private Page(long cursor, List<Envelope> envelopes) {
            this.cursor = cursor;
            this.envelopes = envelopes;
        }

        private long nextCursor() {
            return this.envelopes.get(this.envelopes.size() - 1).getTimestamp() + 1;
        }
    }
}
