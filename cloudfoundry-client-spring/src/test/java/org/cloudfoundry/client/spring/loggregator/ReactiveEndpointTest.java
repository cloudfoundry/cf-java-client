/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.spring.loggregator;

import org.cloudfoundry.client.spring.TestSubscriber;
import org.junit.Test;

import javax.websocket.CloseReason;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public final class ReactiveEndpointTest {

    private final MessageHandler messageHandler = mock(MessageHandler.class);

    private final Session session = mock(Session.class);

    private final TestSubscriber<String> subscriber = new TestSubscriber<>();

    private final ReactiveEndpoint<String> reactiveEndpoint = new ReactiveEndpoint<>(this.messageHandler,
            this.subscriber);

    @Test
    public void onCloseNormalClosure() {
        this.reactiveEndpoint.onClose(this.session, new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                "test-reason-phrase"));

        assertFalse(this.subscriber.getOnCompleteEvents().isEmpty());
    }

    @Test
    public void onCloseGoingAway() {
        this.reactiveEndpoint.onClose(this.session, new CloseReason(CloseReason.CloseCodes.GOING_AWAY,
                "test-reason-phrase"));

        assertFalse(this.subscriber.getOnCompleteEvents().isEmpty());
    }

    @Test
    public void onCloseOther() {
        this.reactiveEndpoint.onClose(this.session, new CloseReason(CloseReason.CloseCodes.NO_STATUS_CODE,
                "test-reason-phrase"));

        assertFalse(this.subscriber.getOnErrorEvents().isEmpty());
    }

    @Test
    public void onError() {
        this.reactiveEndpoint.onError(this.session, new RuntimeException());

        assertFalse(this.subscriber.getOnErrorEvents().isEmpty());
    }

    @Test
    public void onOpen() {
        this.reactiveEndpoint.onOpen(this.session, null);
        verify(this.session).addMessageHandler(this.messageHandler);
    }

}
