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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link org.cloudfoundry.client.lib.JsonStringHttpMessageConverter}.
 *
 * @author Thomas
 */
public class JsonStringHttpMessageConverterTest {

    private static final String CONTENT = "{ 'id': 1, 'name': 'Mårten' }";
	private static final MediaType MEDIA_TYPE = new MediaType(
			MediaType.APPLICATION_OCTET_STREAM.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("UTF-8"));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private StringHttpMessageConverter converter = new JsonStringHttpMessageConverter();

    @Test
    public void canWrite() throws Exception {
        assertThat(converter.canWrite(String.class, MediaType.APPLICATION_JSON), is(true));
    }

    @Test
    public void shouldSupportRequiredMediaType() throws Exception {
        List<MediaType> supportedMediaTypes = converter.getSupportedMediaTypes();
        assertThat(supportedMediaTypes.size(), is(1));
        assertThat(supportedMediaTypes.get(0), is(MEDIA_TYPE));
    }

    @Test
    public void shouldWrite() throws Exception {
        String payload = new String(CONTENT.getBytes(), Charset.forName("UTF-8"));
		HttpHeaders headers = mock(HttpHeaders.class);
		given(headers.getContentType()).willReturn(MEDIA_TYPE);
        HttpOutputMessage outputMessage = mock(HttpOutputMessage.class);
        given(outputMessage.getHeaders()).willReturn(headers);
		OutputStream out = new ByteArrayOutputStream();
		given(outputMessage.getBody()).willReturn(out);
        converter.write(payload, null, outputMessage);
        assertThat(out.toString(), is(equalTo(CONTENT)));
    }

}
