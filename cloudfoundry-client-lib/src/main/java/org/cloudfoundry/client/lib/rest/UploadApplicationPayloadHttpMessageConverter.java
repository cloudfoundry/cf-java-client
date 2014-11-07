/*
 * Copyright 2009-2012 the original author or authors.
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

package org.cloudfoundry.client.lib.rest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.cloudfoundry.client.lib.domain.UploadApplicationPayload;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.FileCopyUtils;

/**
 * Implementation of {@link HttpMessageConverter} that can write {@link org.cloudfoundry.client.lib.domain.UploadApplicationPayload}s. The {@code Content-Type}
 * of written resources is {@code application/octet-stream}.
 *
 * @author Phillip Webb
 */
public class UploadApplicationPayloadHttpMessageConverter implements HttpMessageConverter<UploadApplicationPayload> {

	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return UploadApplicationPayload.class.isAssignableFrom(clazz);
	}

	public List<MediaType> getSupportedMediaTypes() {
		return Collections.singletonList(MediaType.ALL);
	}

	public UploadApplicationPayload read(Class<? extends UploadApplicationPayload> clazz, HttpInputMessage inputMessage)
		throws IOException, HttpMessageNotReadableException {
		throw new UnsupportedOperationException();
	}

	public void write(UploadApplicationPayload t, MediaType contentType, HttpOutputMessage outputMessage)
		throws IOException, HttpMessageNotWritableException {
		setOutputContentType(contentType, outputMessage);

		FileCopyUtils.copy(t.getInputStream(), outputMessage.getBody());
		outputMessage.getBody().flush();

		writeApplicationZipToFile(t.getInputStream());
	}

	private void setOutputContentType(MediaType contentType, HttpOutputMessage outputMessage) {
		HttpHeaders headers = outputMessage.getHeaders();
		if (contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype()) {
			contentType = MediaType.APPLICATION_OCTET_STREAM;
		}
		if (contentType != null) {
			headers.setContentType(contentType);
		}
	}

	private void writeApplicationZipToFile(InputStream inputStream) {
		// for testing/debugging purposes, write the zip file being uploaded to a path specified
		// in the following environment variable
		String uploadFilePath = System.getenv("CF_APP_UPLOAD_FILE");
		if (uploadFilePath != null) {
			try {
				File outputFile = new File(uploadFilePath);
				BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
				FileCopyUtils.copy(inputStream, outputStream);
				outputStream.close();
			} catch (IOException e) {
				System.err.println("Error writing application upload to file: " + e);
			}
		}
	}

}
