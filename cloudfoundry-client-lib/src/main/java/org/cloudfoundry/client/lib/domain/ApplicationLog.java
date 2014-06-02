package org.cloudfoundry.client.lib.domain;


import java.util.Date;

public class ApplicationLog implements Comparable<ApplicationLog> {
	public enum MessageType {STDOUT, STDERR}

	private String appId;
	private String message;
	private Date timestamp;
	private MessageType messageType;
	private String sourceName;
	private String sourceId;

	public ApplicationLog(String appId, String message, Date timestamp, MessageType messageType, String sourceName, String sourceId) {
		this.appId = appId;
		this.message = message;
		this.timestamp = timestamp;
		this.messageType = messageType;
		this.sourceName = sourceName;
		this.sourceId = sourceId;
	}

	public String getAppId() {
		return appId;
	}

	public String getMessage() {
		return message;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public String getSourceName() {
		return sourceName;
	}

	public String getSourceId() {
		return sourceId;
	}

	public int compareTo(ApplicationLog o) {
		return timestamp.compareTo(o.timestamp);
	}

	@Override
	public String toString() {
		return String.format("%s [%s] %s (%s, %s)", appId, timestamp, message, messageType, sourceName);
	}
}