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

package org.cloudfoundry.client.spring.logging;

import org.cloudfoundry.client.LoggingException;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Test;

import javax.websocket.CloseReason;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public final class ReactiveEndpointTest {

    private final MessageHandler messageHandler = mock(MessageHandler.class);

    private final Session session = mock(Session.class);

    private final TestSubscriber<String> testSubscriber = new TestSubscriber<>();

    private final ReactiveEndpoint<String> reactiveEndpoint = new ReactiveEndpoint<>(this.messageHandler, this.testSubscriber);

    @Test
    public void onCloseGoingAway() {
        this.reactiveEndpoint.onClose(this.session, new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "test-reason-phrase"));
    }

    @Test
    public void onCloseNormalClosure() {
        this.reactiveEndpoint.onClose(this.session, new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "test-reason-phrase"));
    }

    @Test
    public void onCloseOther() throws InterruptedException {
        this.reactiveEndpoint.onClose(this.session, new CloseReason(CloseReason.CloseCodes.NO_STATUS_CODE, "test-reason-phrase"));

        this.testSubscriber
            .assertError(LoggingException.class)
            .verify(1, SECONDS);
    }

    @Test
    public void onError() throws InterruptedException {
        this.reactiveEndpoint.onError(this.session, new RuntimeException());

        this.testSubscriber
            .assertError(LoggingException.class)
            .verify(5, SECONDS);
    }

    @Test
    public void onOpen() {
        this.reactiveEndpoint.onOpen(this.session, null);
        verify(this.session).addMessageHandler(this.messageHandler);
    }

}
