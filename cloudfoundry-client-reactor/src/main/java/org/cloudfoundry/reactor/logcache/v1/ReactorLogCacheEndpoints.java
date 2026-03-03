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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

final class ReactorLogCacheEndpoints extends AbstractLogCacheOperations {

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

    Mono<ReadResponse> recentLogs(ReadRequest request) {
        return read(request);
    }

    /**
     * Continuously polls Log Cache and emits new {@link Envelope}s as they arrive.
     *
     * <p>Mirrors the Go {@code logcache.Walk()} / {@code cf tail --follow} semantics:
     * <ol>
     *   <li>Start the cursor at {@code startTime} (defaults to now&nbsp;&minus;&nbsp;5&nbsp;s in
     *       nanoseconds).</li>
     *   <li>Issue {@code GET /api/v1/read/{sourceId}?start_time=cursor}.</li>
     *   <li>Emit every returned envelope in ascending timestamp order and advance
     *       the cursor to {@code lastTimestamp + 1}.</li>
     *   <li>When the batch is empty, wait {@code pollInterval} before the next poll.</li>
     *   <li>Repeat forever – the caller cancels the subscription to stop.</li>
     * </ol>
     * Fully non-blocking: no {@code Thread.sleep}.
     */
    Flux<Envelope> logsTail(TailLogsRequest request) {
        long defaultStartNanos = (System.currentTimeMillis() - 5_000L) * 1_000_000L;
        AtomicLong cursor =
                new AtomicLong(
                        request.getStartTime() != null
                                ? request.getStartTime()
                                : defaultStartNanos);

        List<EnvelopeType> envelopeTypes =
                request.getEnvelopeTypes() != null
                        ? request.getEnvelopeTypes()
                        : Collections.emptyList();
        String nameFilter = request.getNameFilter();

        /*
         * Strategy (mirrors Go's logcache.Walk):
         *  – Mono.defer builds a fresh ReadRequest from the mutable cursor on every repetition.
         *  – The Mono returns either the sorted batch (non-empty) or an empty list.
         *  – flatMapMany turns each batch into a stream of individual Envelope items.
         *  – repeat() subscribes again after each completion.
         *  – When the batch was empty we insert a delay via Mono.delay before the next
         *    repetition so we do not hammer the server. We signal "empty" by returning
         *    a sentinel Mono<Boolean> (false = was empty, true = had data) and use
         *    repeatWhen to conditionally delay.
         */
        return Flux.defer(
                        () -> {
                            // Build the read request from the current cursor position.
                            ReadRequest.Builder builder =
                                    ReadRequest.builder()
                                            .sourceId(request.getSourceId())
                                            .startTime(cursor.get());
                            if (!envelopeTypes.isEmpty()) {
                                builder.envelopeTypes(envelopeTypes);
                            }
                            if (nameFilter != null && !nameFilter.isEmpty()) {
                                builder.nameFilter(nameFilter);
                            }

                            return read(builder.build())
                                    .onErrorReturn(ReadResponse.builder().build())
                                    .flatMapMany(
                                            resp -> {
                                                List<Envelope> raw =
                                                        resp.getEnvelopes() != null
                                                                ? resp.getEnvelopes().getBatch()
                                                                : Collections.emptyList();

                                                if (raw.isEmpty()) {
                                                    // Signal "no data" so repeatWhen can insert the
                                                    // back-off delay.
                                                    return Flux.empty();
                                                }

                                                // Sort ascending by timestamp and advance the
                                                // cursor.
                                                List<Envelope> sorted = new ArrayList<>(raw);
                                                sorted.sort(
                                                        (a, b) ->
                                                                Long.compare(
                                                                        a.getTimestamp() != null
                                                                                ? a.getTimestamp()
                                                                                : 0L,
                                                                        b.getTimestamp() != null
                                                                                ? b.getTimestamp()
                                                                                : 0L));

                                                Envelope last = sorted.get(sorted.size() - 1);
                                                cursor.set(
                                                        (last.getTimestamp() != null
                                                                        ? last.getTimestamp()
                                                                        : cursor.get())
                                                                + 1);

                                                return Flux.fromIterable(sorted);
                                            });
                        })
                // repeatWhen receives a Flux<Long> where each element is the count of items
                // emitted in the previous cycle (0 = empty batch → insert delay).
                .repeatWhen(
                        companion ->
                                companion.flatMap(
                                        count ->
                                                count == 0
                                                        ? Mono.delay(request.getPollInterval())
                                                        : Mono.just(count)));
    }
}
