package org.cloudfoundry.client.lib.domain;


import java.util.Date;

public class ApplicationLog implements Comparable<ApplicationLog> {

    private String appId;

    private String message;

    private MessageType messageType;

    private String sourceId;

    private String sourceName;

    private Date timestamp;

    public ApplicationLog(String appId, String message, Date timestamp, MessageType messageType, String sourceName,
                          String sourceId) {
        this.appId = appId;
        this.message = message;
        this.timestamp = timestamp;
        this.messageType = messageType;
        this.sourceName = sourceName;
        this.sourceId = sourceId;
    }

    public int compareTo(ApplicationLog o) {
        return timestamp.compareTo(o.timestamp);
    }

    public String getAppId() {
        return appId;
    }

    public String getMessage() {
        return message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] %s (%s, %s)", appId, timestamp, message, messageType, sourceName);
    }

    public enum MessageType {STDOUT, STDERR}
}
