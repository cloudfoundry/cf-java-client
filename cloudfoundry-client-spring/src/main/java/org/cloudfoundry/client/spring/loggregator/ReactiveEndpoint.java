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

import org.cloudfoundry.client.LoggregatorException;
import org.reactivestreams.Subscriber;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

/**
 * An implementation of {@link Endpoint} for reactive APIs
 */
public final class ReactiveEndpoint<T> extends Endpoint {

    private final MessageHandler messageHandler;

    private final Subscriber<T> subscriber;

    /**
     * Creates a new instance
     *
     * @param messageHandler the message handler to attach
     * @param subscriber     the subscriber to signal
     */
    public ReactiveEndpoint(MessageHandler messageHandler, Subscriber<T> subscriber) {
        this.messageHandler = messageHandler;
        this.subscriber = subscriber;
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        if (CloseReason.CloseCodes.NORMAL_CLOSURE == closeReason.getCloseCode() || CloseReason.CloseCodes.GOING_AWAY == closeReason.getCloseCode()) {
            this.subscriber.onComplete();
        } else {
            this.subscriber.onError(new LoggregatorException(closeReason.getReasonPhrase()));
        }
    }

    @Override
    public void onError(Session session, Throwable throwable) {
        this.subscriber.onError(new LoggregatorException(throwable));
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        session.addMessageHandler(this.messageHandler);
    }

}
