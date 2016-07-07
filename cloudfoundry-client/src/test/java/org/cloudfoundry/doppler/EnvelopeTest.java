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

package org.cloudfoundry.doppler;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EnvelopeTest {

    @Test
    public void validEnvelope() {
        Envelope<? extends Event> envelope = sampleEnvelope();

        assertEquals("index", envelope.getIndex());
        assertEquals(EventType.CounterEvent, envelope.getEventType());
        assertEquals("deployment", envelope.getDeployment());
        assertEquals("origin", envelope.getOrigin());
        assertEquals("job", envelope.getJob());
        assertEquals("ip", envelope.getIp());
        assertTrue(envelope.getTimestamp() == 123L);
        Event event = envelope.getEvent();
        CounterEvent counterEvent = (CounterEvent)event;
        assertTrue(counterEvent.getDelta() == 1L);
        assertEquals("counter", counterEvent.getName());

    }

    @Test
    public void retrieveCorrectEventTypeFromEnvelope() {
        Envelope<? extends Event> envelope = sampleEnvelope();
        CounterEvent resEvent = envelope.getEvent(CounterEvent.class);

        assertTrue(resEvent.getDelta() == 1L);
        assertEquals("counter", resEvent.getName());
    }

    @Test(expected = ClassCastException.class)
    public void retrieveWrongEventTypeFromEnvelopeShouldThrowCastingException() {
        Envelope<? extends Event> envelope = sampleEnvelope();
        HttpStart httpStartEvent = envelope.getEvent(HttpStart.class);
        assertFalse(true);//should not reach this point
    }

    @Test(expected = IllegalStateException.class)
    public void missingOriginShouldTriggerException() {
        org.cloudfoundry.dropsonde.events.CounterEvent cfCounterEvent =
            new org.cloudfoundry.dropsonde.events.CounterEvent.Builder()
                .name("counter")
                .delta(1L)
                .build();

        org.cloudfoundry.dropsonde.events.Envelope cfEnvelope = new org.cloudfoundry.dropsonde.events.Envelope.Builder()
            .index("index")
            .eventType(org.cloudfoundry.dropsonde.events.Envelope.EventType.CounterEvent)
            .deployment("deployment")
            .job("job")
            .ip("ip")
            .timestamp(123L)
            .counterEvent(cfCounterEvent)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void missingEventTypeShouldTriggerException() {
        org.cloudfoundry.dropsonde.events.CounterEvent cfCounterEvent =
            new org.cloudfoundry.dropsonde.events.CounterEvent.Builder()
                .name("counter")
                .delta(1L)
                .build();

        org.cloudfoundry.dropsonde.events.Envelope cfEnvelope = new org.cloudfoundry.dropsonde.events.Envelope.Builder()
            .index("index")
            .deployment("deployment")
            .origin("origin")
            .job("job")
            .ip("ip")
            .timestamp(123L)
            .counterEvent(cfCounterEvent)
            .build();
    }

    private Envelope<? extends Event> sampleEnvelope() {
        org.cloudfoundry.dropsonde.events.CounterEvent cfCounterEvent =
            new org.cloudfoundry.dropsonde.events.CounterEvent.Builder()
                .name("counter")
                .delta(1L)
                .build();

        org.cloudfoundry.dropsonde.events.Envelope cfEnvelope = new org.cloudfoundry.dropsonde.events.Envelope.Builder()
            .index("index")
            .eventType(org.cloudfoundry.dropsonde.events.Envelope.EventType.CounterEvent)
            .deployment("deployment")
            .origin("origin")
            .job("job")
            .ip("ip")
            .timestamp(123L)
            .counterEvent(cfCounterEvent)
            .build();

        return Envelope.from(cfEnvelope);
    }
}
