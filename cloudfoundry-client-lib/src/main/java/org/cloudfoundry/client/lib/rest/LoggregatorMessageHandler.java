package org.cloudfoundry.client.lib.rest;

import javax.websocket.MessageHandler;

import org.cloudfoundry.client.lib.ApplicationLogListener;

import com.google.protobuf.InvalidProtocolBufferException;

public class LoggregatorMessageHandler implements MessageHandler.Whole<byte[]> {

	private final LoggregatorMessageParser messageParser;
	private final ApplicationLogListener listener;

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
