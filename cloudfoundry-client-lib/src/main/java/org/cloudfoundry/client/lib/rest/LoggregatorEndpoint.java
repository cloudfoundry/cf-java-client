package org.cloudfoundry.client.lib.rest;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import org.cloudfoundry.client.lib.ApplicationLogListener;
import org.cloudfoundry.client.lib.CloudOperationException;

public class LoggregatorEndpoint extends Endpoint {
    private ApplicationLogListener listener;
    
    public LoggregatorEndpoint(ApplicationLogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        if (closeReason.getCloseCode() == CloseReason.CloseCodes.NORMAL_CLOSURE) {
            listener.onComplete();
        } else {
            listener.onError(new CloudOperationException("Loggregrator connection closed unexpectedly " + closeReason));
        }
    }

    public void onError(Session session, Throwable throwable) {
        listener.onError(throwable);
    }    
}
