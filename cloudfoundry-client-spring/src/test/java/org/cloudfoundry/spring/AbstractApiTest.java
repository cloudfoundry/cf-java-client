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

package org.cloudfoundry.spring;

import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.util.RequestValidationException;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.util.Exceptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static org.junit.Assert.assertArrayEquals;

public abstract class AbstractApiTest<REQ, RSP> extends AbstractRestTest {

    private final TestSubscriber<RSP> testSubscriber = new TestSubscriber<>();

    @Test
    public final void error() throws Exception {
        mockRequest(getRequestContext().errorResponse());
        this.testSubscriber.assertError(CloudFoundryException.class, "CF-UnprocessableEntity(10008): The request is semantically invalid: space_guid and name unique");
        invoke(getValidRequest()).subscribe(this.testSubscriber);

        this.testSubscriber.verify(Duration.ofSeconds(5));
        verify();
    }

    @Test
    public final void invalidRequest() throws Exception {
        REQ request = getInvalidRequest();
        if (request == null) {
            return;
        }

        this.testSubscriber.assertError(RequestValidationException.class, null); // ignore message
        invoke(request).subscribe(this.testSubscriber);

        this.testSubscriber.verify(Duration.ofSeconds(5));
        verify();
    }

    @Test
    public final void success() throws Exception {
        RSP response = getResponse();
        assertions(this.testSubscriber, response);

        mockRequest(getRequestContext());
        invoke(getValidRequest()).subscribe(this.testSubscriber);

        this.testSubscriber.verify(Duration.ofSeconds(5));
        verify();
    }

    protected static Consumer<byte[]> arrayEqualsExpectation(byte[] expected) {
        return actual -> assertArrayEquals(expected, actual);
    }

    protected void assertions(TestSubscriber<RSP> testSubscriber, RSP expected) {
        if (expected != null) {
            testSubscriber.assertEquals(expected);
        }
    }

    protected final Mono<byte[]> getContents(Flux<byte[]> flux) {
        return flux
            .reduce(new ByteArrayOutputStream(), collectIntoByteArrayInputStream())
            .map(ByteArrayOutputStream::toByteArray);
    }

    protected final byte[] getContents(Resource resource) {
        try (InputStream in = resource.getInputStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
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

    protected REQ getInvalidRequest() {
        return null;
    }

    protected abstract RequestContext getRequestContext();

    protected abstract RSP getResponse();

    protected abstract REQ getValidRequest() throws Exception;

    protected abstract Publisher<RSP> invoke(REQ request);

    private static BiFunction<ByteArrayOutputStream, byte[], ByteArrayOutputStream> collectIntoByteArrayInputStream() {
        return (out, bytes) -> {
            try {
                out.write(bytes);
                return out;
            } catch (IOException e) {
                throw new Exceptions.UpstreamException(e);
            }
        };
    }

}
