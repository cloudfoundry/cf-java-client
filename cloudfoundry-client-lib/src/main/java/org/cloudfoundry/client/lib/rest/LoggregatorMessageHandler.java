package org.cloudfoundry.client.lib.rest;

import java.util.Date;

import javax.websocket.MessageHandler;

import loggregator.LogMessages;

import org.cloudfoundry.client.lib.ApplicationLogListener;
import org.cloudfoundry.client.lib.ApplicationLogListener.ApplicationLog;

import com.google.protobuf.InvalidProtocolBufferException;

public class LoggregatorMessageHandler implements MessageHandler.Whole<byte[]> {
    private ApplicationLogListener listener;
    
    public LoggregatorMessageHandler(ApplicationLogListener listener) {
        this.listener = listener;
    }

    public void onMessage(byte[] rawMessage) {
        try {
            LogMessages.Message message = LogMessages.Message.parseFrom(rawMessage);
            ApplicationLogListener.MessageType messageType = 
                    message.getMessageType() == LogMessages.Message.MessageType.OUT ?
                            ApplicationLogListener.MessageType.STDOUT :
                            ApplicationLogListener.MessageType.STDERR;    
            listener.onMessage(new ApplicationLog(message.getAppId(), 
                                                  message.getMessage().toStringUtf8(), 
                                                  new Date(message.getTimestamp()), 
                                                  messageType, 
                                                  message.getSourceName()));
        } catch (InvalidProtocolBufferException e) {
            listener.onError(e);
        }
    }
}
