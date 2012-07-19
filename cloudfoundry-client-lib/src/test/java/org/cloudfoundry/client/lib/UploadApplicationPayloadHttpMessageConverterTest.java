/*
 * Copyright 2009-2011 the original author or authors.
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

package org.cloudfoundry.client.lib;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.cloudfoundry.client.lib.domain.UploadApplicationPayload;
import org.cloudfoundry.client.lib.util.UploadApplicationPayloadHttpMessageConverter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

/**
 * Tests for {@link org.cloudfoundry.client.lib.util.UploadApplicationPayloadHttpMessageConverter}.
 *
 * @author Phillip Webb
 */
public class UploadApplicationPayloadHttpMessageConverterTest {

    private static final byte[] CONTENT = new byte[] { 0x00, 0x01 };

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private UploadApplicationPayloadHttpMessageConverter converter = new UploadApplicationPayloadHttpMessageConverter();

    @Test
    public void cannotRead() throws Exception {
        assertThat(converter.canRead(UploadApplicationPayload.class, MediaType.APPLICATION_OCTET_STREAM), is(false));
    }

    @Test
    public void canWrite() throws Exception {
        assertThat(converter.canWrite(Resource.class, MediaType.APPLICATION_OCTET_STREAM), is(false));
        assertThat(converter.canWrite(UploadApplicationPayload.class, MediaType.APPLICATION_OCTET_STREAM), is(true));
    }

    @Test
    public void shouldSupportAllMediaTypes() throws Exception {
        List<MediaType> supportedMediaTypes = converter.getSupportedMediaTypes();
        assertThat(supportedMediaTypes.size(), is(1));
        assertThat(supportedMediaTypes.get(0), is(MediaType.ALL));
    }

    @Test
    public void shouldNotRead() throws Exception {
        thrown.expect(UnsupportedOperationException.class);
        converter.read(UploadApplicationPayload.class, mock(HttpInputMessage.class));
    }

    @Test
    public void shouldWrite() throws Exception {
        UploadApplicationPayload payload = mock(UploadApplicationPayload.class);
        given(payload.getInputStream()).willReturn(new ByteArrayInputStream(CONTENT));
        HttpOutputMessage outputMessage = mock(HttpOutputMessage.class);
        HttpHeaders headers = mock(HttpHeaders.class);
        given(outputMessage.getHeaders()).willReturn(headers);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        given(outputMessage.getBody()).willReturn(out);
        converter.write(payload, null, outputMessage);
        verify(headers).setContentType(MediaType.APPLICATION_OCTET_STREAM);
        assertThat(out.toByteArray(), is(equalTo(CONTENT)));
    }

}
