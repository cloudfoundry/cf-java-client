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

package org.cloudfoundry.reactor.client;

import okhttp3.Headers;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.reactor.AbstractApiTest;
import org.junit.Test;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public abstract class AbstractClientApiTest<REQ, RSP> extends AbstractApiTest<REQ, RSP> {

    private static final Pattern BOUNDARY = Pattern.compile("multipart/form-data; boundary=(.+)");

    @Test
    public final void error() throws Exception {
        mockRequest(getInteractionContext().getErrorResponse());
        this.testSubscriber.assertError(CloudFoundryException.class, "CF-UnprocessableEntity(10008): The request is semantically invalid: space_guid and name unique");
        invoke(getValidRequest()).subscribe(this.testSubscriber);

        this.testSubscriber.verify(Duration.ofSeconds(5));
        verify();
    }

    protected static Mono<byte[]> collectByteArray(Flux<byte[]> bytes) {
        return bytes
            .reduceWith(ByteArrayOutputStream::new, (prev, next) -> {
                try {
                    prev.write(next);
                } catch (IOException e) {
                    throw Exceptions.propagate(e);
                }
                return prev;
            })
            .map(ByteArrayOutputStream::toByteArray);
    }

    protected static String extractBoundary(Headers headers) {
        String contentType = headers.get("Content-Type");
        assertNotNull(contentType);

        Matcher matcher = BOUNDARY.matcher(contentType);
        assertTrue(matcher.find());
        return matcher.group(1);
    }

    protected static byte[] getBytes(String path) {
        try (InputStream in = new FileInputStream(new File("src/test/resources", path)); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;

            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
