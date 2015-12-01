package org.cloudfoundry.client.lib.rest;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.TextFormat;
import loggregator.LogMessages;
import org.cloudfoundry.client.lib.domain.ApplicationLog;

import java.util.Date;

public class LoggregatorMessageParser {

    private static final long NANOSECONDS_IN_MILLISECOND = 1000000;

    public ApplicationLog parseMessage(byte[] rawMessage) throws InvalidProtocolBufferException {
        LogMessages.Message message = LogMessages.Message.parseFrom(rawMessage);

        return createApplicationLog(message);
    }

    public ApplicationLog parseMessage(String messageString) throws InvalidProtocolBufferException, TextFormat
            .ParseException {
        LogMessages.Message.Builder builder = LogMessages.Message.newBuilder();
        TextFormat.merge(messageString, builder);
        LogMessages.Message message = builder.build();

        return createApplicationLog(message);
    }

    private ApplicationLog createApplicationLog(LogMessages.Message message) {
        ApplicationLog.MessageType messageType =
                message.getMessageType() == LogMessages.Message.MessageType.OUT ?
                        ApplicationLog.MessageType.STDOUT :
                        ApplicationLog.MessageType.STDERR;

        return new ApplicationLog(message.getAppId(),
                message.getMessage().toStringUtf8(),
                new Date(message.getTimestamp() / NANOSECONDS_IN_MILLISECOND),
                messageType,
                message.getSourceName(), message.getSourceId());
    }

}
