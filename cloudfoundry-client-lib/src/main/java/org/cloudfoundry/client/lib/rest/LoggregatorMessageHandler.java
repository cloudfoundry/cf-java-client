package org.cloudfoundry.client.lib.rest;

import java.util.Date;

import javax.websocket.MessageHandler;

import loggregator.LogMessages;

import org.cloudfoundry.client.lib.ApplicationLogListener;

import com.google.protobuf.InvalidProtocolBufferException;
import org.cloudfoundry.client.lib.domain.ApplicationLog;

public class LoggregatorMessageHandler implements MessageHandler.Whole<byte[]> {
	private static final long NANOSECONDS_IN_MILLISECOND = 1000000;

	private ApplicationLogListener listener;

	public LoggregatorMessageHandler(ApplicationLogListener listener) {
		this.listener = listener;
	}

	public void onMessage(byte[] rawMessage) {
		try {
			LogMessages.Message message = LogMessages.Message.parseFrom(rawMessage);
			ApplicationLog.MessageType messageType =
					message.getMessageType() == LogMessages.Message.MessageType.OUT ?
							ApplicationLog.MessageType.STDOUT :
							ApplicationLog.MessageType.STDERR;
			listener.onMessage(new ApplicationLog(message.getAppId(),
					message.getMessage().toStringUtf8(),
					new Date(message.getTimestamp() / NANOSECONDS_IN_MILLISECOND),
					messageType,
					message.getSourceName(), message.getSourceId()));
		} catch (InvalidProtocolBufferException e) {
			listener.onError(e);
		}
	}
}
