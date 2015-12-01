package org.cloudfoundry.client.lib.rest;

import com.google.protobuf.InvalidProtocolBufferException;
import org.cloudfoundry.client.lib.ApplicationLogListener;

import javax.websocket.MessageHandler;

public class LoggregatorMessageHandler implements MessageHandler.Whole<byte[]> {

    private final ApplicationLogListener listener;

    private final LoggregatorMessageParser messageParser;

    public LoggregatorMessageHandler(ApplicationLogListener listener) {
        this.listener = listener;
        this.messageParser = new LoggregatorMessageParser();
    }

    public void onMessage(byte[] rawMessage) {
        try {
            listener.onMessage(messageParser.parseMessage(rawMessage));
        } catch (InvalidProtocolBufferException e) {
            listener.onError(e);
        }
    }
}
