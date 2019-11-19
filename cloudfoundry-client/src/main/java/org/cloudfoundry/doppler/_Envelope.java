/*
 * Copyright 2013-2019 the original author or authors.
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

package org.cloudfoundry.doppler;

import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.loggregator.v2.LoggregatorEnvelope;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Wraps an Event and adds metadata
 */
@Value.Immutable
abstract class _Envelope {

    public static Envelope from(org.cloudfoundry.dropsonde.events.Envelope dropsonde) {
        Objects.requireNonNull(dropsonde, "dropsonde");

        Envelope.Builder envelope = Envelope.builder()
            .deployment(dropsonde.deployment)
            .eventType(EventType.from(dropsonde.eventType))
            .index(dropsonde.index)
            .ip(dropsonde.ip)
            .job(dropsonde.job)
            .origin(dropsonde.origin)
            .tags(dropsonde.tags)
            .timestamp(dropsonde.timestamp);

        Optional.ofNullable(dropsonde.containerMetric).ifPresent(d -> envelope.containerMetric(ContainerMetric.from(d)));
        Optional.ofNullable(dropsonde.counterEvent).ifPresent(d -> envelope.counterEvent(CounterEvent.from(d)));
        Optional.ofNullable(dropsonde.error).ifPresent(d -> envelope.error(Error.from(d)));
        Optional.ofNullable(dropsonde.httpStartStop).ifPresent(d -> envelope.httpStartStop(HttpStartStop.from(d)));
        Optional.ofNullable(dropsonde.logMessage).ifPresent(d -> envelope.logMessage(LogMessage.from(d)));
        Optional.ofNullable(dropsonde.valueMetric).ifPresent(d -> envelope.valueMetric(ValueMetric.from(d)));

        return envelope.build();
    }

    public static Envelope from(LoggregatorEnvelope.Envelope envelope) {
        Objects.requireNonNull(envelope, "envelope");

        Envelope.Builder envelopeBuilder = Envelope.builder()
            .deployment(envelope.getTagsMap().get("deployment"))
            .index(envelope.getTagsMap().get("index"))
            .ip(envelope.getTagsMap().get("ip"))
            .job(envelope.getTagsMap().get("job"))
            .origin(envelope.getTagsMap().get("origin"))
            .tags(envelope.getTagsMap())
            .timestamp(envelope.getTimestamp());

        if (envelope.hasGauge()) {
            envelopeBuilder.containerMetric(ContainerMetric.from(envelope));
            envelopeBuilder.eventType(EventType.CONTAINER_METRIC);
        }
        if (envelope.hasCounter()) {
            envelopeBuilder.counterEvent(CounterEvent.from(envelope.getCounter()));
            envelopeBuilder.eventType(EventType.COUNTER_EVENT);
        }
        if (envelope.hasLog()) {
            envelopeBuilder.logMessage(LogMessage.from(envelope));
            envelopeBuilder.eventType(EventType.LOG_MESSAGE);
        }
        return envelopeBuilder.build();
    }

    /**
     * The enclosed {@link ContainerMetric}
     */
    @Nullable
    abstract ContainerMetric getContainerMetric();

    /**
     * The enclosed {@link CounterEvent}
     */
    @Nullable
    abstract CounterEvent getCounterEvent();

    /**
     * Deployment name (used to uniquely identify source)
     */
    @Nullable
    abstract String getDeployment();

    /**
     * The enclosed {@link Error}
     */
    @Nullable
    abstract Error getError();

    /**
     * Type of wrapped event. Only the optional field corresponding to the value of eventType should be set.
     */
    abstract EventType getEventType();

    /**
     * The enclosed {@link HttpStartStop}
     */
    @Nullable
    abstract HttpStartStop getHttpStartStop();

    /**
     * Index of job (used to uniquely identify source)
     */
    @Nullable
    abstract String getIndex();

    /**
     * IP address (used to uniquely identify source)
     */
    @Nullable
    abstract String getIp();

    /**
     * Job name (used to uniquely identify source)
     */
    @Nullable
    abstract String getJob();

    /**
     * The enclosed {@link LogMessage}
     */
    @Nullable
    abstract LogMessage getLogMessage();

    /**
     * Unique description of the origin of this event
     */
    abstract String getOrigin();

    /**
     * key/value tags to include additional identifying information
     */
    @AllowNulls
    abstract Map<String, String> getTags();

    /**
     * UNIX timestamp (in nanoseconds) event was wrapped in this Envelope.
     */
    @Nullable
    abstract Long getTimestamp();

    /**
     * The enclosed {@link ValueMetric}
     */
    @Nullable
    abstract ValueMetric getValueMetric();

}
