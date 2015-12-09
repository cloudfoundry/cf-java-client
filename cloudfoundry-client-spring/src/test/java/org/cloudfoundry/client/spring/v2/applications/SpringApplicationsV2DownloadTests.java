/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.spring.v2.applications;

import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.v2.applications.DownloadApplicationRequest;
import org.cloudfoundry.client.v2.applications.DownloadDropletRequest;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.rx.Streams;

import java.io.IOException;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

/**
 * Download tests with artificially small internal buffering
 */
public final class SpringApplicationsV2DownloadTests extends AbstractRestTest {

    private final int testByteArrayBufferLength = 64;

    private final SpringApplicationsV2 applications = new SpringApplicationsV2(testByteArrayBufferLength,
            this.restTemplate, this.root);

    @Test
    public void download() throws IOException {
        mockRequest(new RequestContext()
                .method(GET).path("v2/apps/test-id/download")
                .status(OK)
                .responsePayload("v2/apps/GET_{id}_download_response.bin"));

        DownloadApplicationRequest request = DownloadApplicationRequest.builder()
                .id("test-id")
                .build();

        assertBytesEqual(new ClassPathResource("v2/apps/GET_{id}_download_response.bin").getInputStream(),
                Streams.wrap(this.applications.download(request)));

        verify();
    }

    @Test
    public void downloadDroplet() throws IOException {
        mockRequest(new RequestContext()
                .method(GET).path("v2/apps/test-id/droplet/download")
                .status(OK)
                .responsePayload("v2/apps/GET_{id}_download_response.bin"));

        DownloadDropletRequest request = DownloadDropletRequest.builder()
                .id("test-id")
                .build();

        assertBytesEqual(new ClassPathResource("v2/apps/GET_{id}_download_response.bin").getInputStream(),
                Streams.wrap(this.applications.downloadDroplet(request)));

        verify();
    }
}
