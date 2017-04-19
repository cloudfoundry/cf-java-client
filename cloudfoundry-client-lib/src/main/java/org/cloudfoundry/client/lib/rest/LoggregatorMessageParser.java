package org.cloudfoundry.client.lib.rest;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.TextFormat;
import loggregator.LogMessages;
import org.cloudfoundry.dropsonde.events.LogFactory;
import org.cloudfoundry.client.lib.domain.ApplicationLog;

import java.util.Date;

public class LoggregatorMessageParser {
	private static final long NANOSECONDS_IN_MILLISECOND = 1000000;
	private final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory.getLog(getClass().getName());

	public ApplicationLog parseMessage(byte[] rawMessage) throws InvalidProtocolBufferException {
		try {
			// First tries to parse the message with the new protobuf of dropsonde protocol
			// https://github.com/cloudfoundry/cf-release/releases/v244
			LogFactory.LogMessage logMessage = LogFactory.LogMessage.parseFrom(rawMessage);
			if (logMessage.hasSourceType() && !logMessage.getSourceType().isEmpty()) {
				return createApplicationLog(logMessage);
			}
		} catch (InvalidProtocolBufferException ipbe) {
			logger.error(ipbe.getMessage());
			logger.error("ApplicationLog was unable to be parsed with the new protobuf dropsonde protocol, falling back to old version!");
		}
		// Drop to old style message which support is cut after - cf release v244
		LogMessages.Message message = LogMessages.Message.parseFrom(rawMessage);

		return createApplicationLog(message);
	}

	public ApplicationLog parseMessage(String messageString) throws InvalidProtocolBufferException, TextFormat.ParseException {
		try {
			// First tries to parse the message with the new protobuf of dropsonde protocol
			// https://github.com/cloudfoundry/cf-release/releases/v244
			LogFactory.LogMessage.Builder newProtoLogMessageBuilder = LogFactory.LogMessage.newBuilder();
			TextFormat.merge(messageString, newProtoLogMessageBuilder);
			LogFactory.LogMessage newLogMessage = newProtoLogMessageBuilder.build();
			if (newLogMessage.hasSourceType() && !newLogMessage.getSourceType().isEmpty()) {
				return createApplicationLog(newLogMessage);
			}
			return createApplicationLog(newLogMessage);
		} catch (Exception e) {
			logger.error("ApplicationLog was unable to be parsed with the new protobuf dropsonde protocol, falling back to old version!");
		}
		// Drop to old style message which support is cut after - cf release v244
		LogMessages.Message.Builder builder = LogMessages.Message.newBuilder();
		TextFormat.merge(messageString, builder);
		LogMessages.Message message = builder.build();

		return createApplicationLog(message);
	}

	// Parses the old log message coming from loggregator
	// you can find the protobuf for this type at
	// cloudfoundry-client-lib/src/main/protobuf/log_message.proto
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

	// Parses the new log message coming from loggregator
	// you can find the protobuf for this type at
	// cloudfoundry-client-lib/src/main/protobuf/new_log.proto
	private ApplicationLog createApplicationLog(LogFactory.LogMessage message) {
		ApplicationLog.MessageType messageType =
				message.getMessageType() == LogFactory.LogMessage.MessageType.OUT ?
						ApplicationLog.MessageType.STDOUT :
						ApplicationLog.MessageType.STDERR;

		return new ApplicationLog(message.getAppId(),
				message.getMessage().toStringUtf8(),
				new Date(message.getTimestamp() / NANOSECONDS_IN_MILLISECOND),
				messageType,
				message.getSourceType(), message.getSourceInstance());
	}

}
