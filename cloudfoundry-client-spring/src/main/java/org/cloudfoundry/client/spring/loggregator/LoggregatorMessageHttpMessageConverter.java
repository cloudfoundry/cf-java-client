/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.client.spring.loggregator;

import com.google.protobuf.InvalidProtocolBufferException;
import org.cloudfoundry.client.loggregator.LoggregatorMessage;
import org.cloudfoundry.client.loggregator.LoggregatorProtocolBuffers.LogMessage;
import org.cloudfoundry.client.spring.util.Multipart;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.util.TypeUtils;
import reactor.fn.Function;

import java.io.IOException;
import java.util.List;

public final class LoggregatorMessageHttpMessageConverter extends AbstractHttpMessageConverter<List<LoggregatorMessage>> {

    public LoggregatorMessageHttpMessageConverter() {
        super(MediaType.parseMediaType("multipart/x-protobuf"));
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return false;
    }

    @Override
    protected List<LoggregatorMessage> readInternal(Class<? extends List<LoggregatorMessage>> clazz, HttpInputMessage inputMessage) throws IOException {
        String boundary = inputMessage.getHeaders().getContentType().getParameter("boundary");

        return Multipart
            .from(inputMessage.getBody(), boundary)
            .map(toLoggregatorMessage())
            .toList()
            .get();
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return TypeUtils.isAssignable(List.class, clazz);
    }

    @Override
    protected void writeInternal(List<LoggregatorMessage> loggregatorMessage, HttpOutputMessage outputMessage) {
        throw new UnsupportedOperationException();
    }

    private static Function<byte[], LoggregatorMessage> toLoggregatorMessage() {
        return new Function<byte[], LoggregatorMessage>() {

            @Override
            public LoggregatorMessage apply(byte[] part) {
                try {
                    LogMessage logMessage = LogMessage.parseFrom(part);
                    return LoggregatorMessage.from(logMessage);
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e);
                }
            }

        };
    }

}
