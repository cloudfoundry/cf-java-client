package org.cloudfoundry.client.lib;

import java.util.Date;

public interface ApplicationLogListener {
    void onMessage(ApplicationLog log);
    
    void onComplete();

    void onError(Throwable exception);
    
    public static class ApplicationLog {
        private String appId;
        private String message;
        private Date timestamp;
        private MessageType messageType;
        private String sourceName;
        
        public ApplicationLog(String appId, String message, Date timestamp, MessageType messageType, String sourceName) {
            this.appId = appId;
            this.message = message;
            this.timestamp = timestamp;
            this.messageType = messageType;
            this.sourceName = sourceName;
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
        
        @Override
        public String toString() {
            return String.format("%s [%s] %s (%s, %s)", appId, timestamp, message, messageType, sourceName);
        }
    }
    
    public enum MessageType { STDOUT, STDERR }
}
