package org.cloudfoundry.client.lib.rest;

import org.cloudfoundry.client.lib.ApplicationLogListener;
import org.cloudfoundry.client.lib.CloudOperationException;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

public class LoggregatorEndpoint extends Endpoint {

    private ApplicationLogListener listener;

    public LoggregatorEndpoint(ApplicationLogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        if (closeReason.getCloseCode() == CloseReason.CloseCodes.NORMAL_CLOSURE
                || closeReason.getCloseCode() == CloseReason.CloseCodes.GOING_AWAY) {
            listener.onComplete();
        } else {
            listener.onError(new CloudOperationException("Loggregrator connection closed unexpectedly " + closeReason));
        }
    }

    public void onError(Session session, Throwable throwable) {
        listener.onError(throwable);
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        session.addMessageHandler(new LoggregatorMessageHandler(listener));
    }
}
