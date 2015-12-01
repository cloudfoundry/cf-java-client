package org.cloudfoundry.client.lib.rest;

import org.cloudfoundry.client.lib.domain.ApplicationLog;
import org.cloudfoundry.client.lib.domain.ApplicationLogs;
import org.cloudfoundry.client.lib.util.Multipart;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.util.Map;

/**
 * An HttpMessageConverter for parsing and constructing ApplicationLog entries from a Loggregator response.
 *
 * @author Scott Frederick
 */
public class LoggregatorHttpMessageConverter extends AbstractHttpMessageConverter<ApplicationLogs> {

    private LoggregatorMessageParser messageParser = new LoggregatorMessageParser();

    public LoggregatorHttpMessageConverter() {
        super(new MediaType("multipart", "x-protobuf"));
    }

    @Override
    public boolean canWrite(MediaType mediaType) {
        return false;
    }

    @Override
    protected ApplicationLogs readInternal(Class<? extends ApplicationLogs> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        String boundary = getMessageBoundary(inputMessage);

        Multipart multipart = new Multipart(inputMessage.getBody(), boundary);

        ApplicationLogs logs = new ApplicationLogs();

        Multipart.Part part;
        while ((part = multipart.nextPart()) != null) {
            ApplicationLog log = messageParser.parseMessage(part.getContent());
            logs.add(log);
        }

        return logs;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return ApplicationLogs.class.equals(clazz);
    }

    @Override
    protected void writeInternal(ApplicationLogs logs, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        throw new UnsupportedOperationException("Writing to LoggregatorHttpMessageConverter is not supported");
    }

    private String getMessageBoundary(HttpInputMessage inputMessage) {
        MediaType mediaType = inputMessage.getHeaders().getContentType();
        Map<String, String> parameters = mediaType.getParameters();
        return parameters.get("boundary");
    }
}
