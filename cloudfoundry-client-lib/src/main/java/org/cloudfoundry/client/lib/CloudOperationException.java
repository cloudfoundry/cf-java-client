package org.cloudfoundry.client.lib;

@SuppressWarnings("serial")
public class CloudOperationException extends RuntimeException {
    public CloudOperationException(Throwable cause) {
        super(cause);
    }

    public CloudOperationException(String message) {
        super(message);
    }
}
