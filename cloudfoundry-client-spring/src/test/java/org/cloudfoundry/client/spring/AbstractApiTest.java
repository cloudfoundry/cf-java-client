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

package org.cloudfoundry.client.spring;

import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.core.io.Resource;
import reactor.Mono;
import reactor.core.error.ReactorFatalException;
import reactor.fn.BiFunction;
import reactor.fn.Consumer;
import reactor.fn.Function;
import reactor.rx.Streams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertArrayEquals;

public abstract class AbstractApiTest<REQ, RSP> extends AbstractRestTest {

    private final TestSubscriber<RSP> testSubscriber = new TestSubscriber<>();

    @Test
    public final void error() throws Exception {
        mockRequest(getRequestContext().errorResponse());
        this.testSubscriber.assertError(CloudFoundryException.class);
        invoke(getValidRequest()).subscribe(this.testSubscriber);

        this.testSubscriber.verify(5, SECONDS);
        verify();
    }

    @Test
    public final void invalidRequest() throws Exception {
        REQ request = getInvalidRequest();
        if (request == null) {
            return;
        }

        this.testSubscriber.assertError(RequestValidationException.class);
        invoke(request).subscribe(this.testSubscriber);

        this.testSubscriber.verify(5, SECONDS);
        verify();
    }

    @Test
    public final void success() throws Exception {
        RSP response = getResponse();
        assertions(this.testSubscriber, response);

        mockRequest(getRequestContext());
        invoke(getValidRequest()).subscribe(this.testSubscriber);

        this.testSubscriber.verify(5, SECONDS);
        verify();
    }

    protected static Consumer<byte[]> arrayEqualsExpectation(final byte[] expected) {
        return new Consumer<byte[]>() {

            @Override
            public void accept(byte[] actual) {
                assertArrayEquals(expected, actual);
            }

        };
    }

    protected void assertions(TestSubscriber<RSP> testSubscriber, RSP expected) {
        if (expected != null) {
            testSubscriber.assertEquals(expected);
        }
    }

    protected final Mono<byte[]> getContents(Publisher<byte[]> publisher) {
        return Streams
                .from(publisher)
                .reduce(new ByteArrayOutputStream(), collectIntoByteArrayInputStream())
                .map(toByteArray());
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

    protected abstract REQ getInvalidRequest();

    protected abstract RequestContext getRequestContext();

    protected abstract RSP getResponse();

    protected abstract REQ getValidRequest() throws Exception;

    protected abstract Publisher<RSP> invoke(REQ request);

    private static BiFunction<ByteArrayOutputStream, byte[], ByteArrayOutputStream> collectIntoByteArrayInputStream() {
        return new BiFunction<ByteArrayOutputStream, byte[], ByteArrayOutputStream>() {

            @Override
            public ByteArrayOutputStream apply(ByteArrayOutputStream out, byte[] bytes) {

                try {
                    out.write(bytes);
                    return out;
                } catch (IOException e) {
                    throw ReactorFatalException.create(e);
                }
            }
        };
    }

    private static Function<ByteArrayOutputStream, byte[]> toByteArray() {
        return new Function<ByteArrayOutputStream, byte[]>() {

            @Override
            public byte[] apply(ByteArrayOutputStream out) {
                return out.toByteArray();
            }

        };
    }

}
