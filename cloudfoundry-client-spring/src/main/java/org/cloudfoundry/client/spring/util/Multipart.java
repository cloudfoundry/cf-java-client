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

package org.cloudfoundry.client.spring.util;

import org.reactivestreams.Subscriber;
import reactor.core.subscriber.SubscriberWithContext;
import reactor.fn.Consumer;
import reactor.fn.Function;
import reactor.rx.Stream;
import reactor.rx.Streams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public final class Multipart {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private static final String CRLF = "\r\n";

    private static final String DASHES = "--";

    public static Stream<byte[]> from(final InputStream inputStream, final String boundary) {
        return Streams.create(new Consumer<SubscriberWithContext<byte[], Void>>() {

            @Override
            public void accept(SubscriberWithContext<byte[], Void> subscriber) {
                try {
                    byte[] part = getPart(inputStream, boundary);

                    if (part == null) {
                        subscriber.onComplete();
                    } else {
                        subscriber.onNext(part);
                    }
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }

        }, new Function<Subscriber<? super byte[]>, Void>() {

            @Override
            public Void apply(Subscriber<? super byte[]> subscriber) {
                try {
                    primeStream(inputStream, boundary);
                } catch (IOException e) {
                    subscriber.onError(e);
                }

                return null;
            }

        });
    }

    private static void discardHeader(InputStream in) throws IOException {
        readLine(in);
    }

    private static void drain(InputStream in) throws IOException {
        in.skip(in.available());
    }

    private static byte[] getPart(InputStream in, String boundary) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        discardHeader(in);

        byte[] line;
        while (!isBoundary(boundary, (line = readLine(in)))) {
            out.write(line);
        }

        if (isEomBoundary(boundary, line)) {
            drain(in);
        }

        byte[] part = trim(out.toByteArray());
        return part.length > 0 ? part : null;
    }

    private static boolean isBoundary(String boundary, byte[] bytes) {
        if (bytes.length == 0) {
            return true;
        }

        String line = new String(bytes, CHARSET);
        return line.equals(DASHES + boundary + CRLF) || isEomBoundary(boundary, line);
    }

    private static boolean isEomBoundary(String boundary, byte[] bytes) {
        if (bytes.length == 0) {
            return true;
        }

        String line = new String(bytes, CHARSET);
        return isEomBoundary(boundary, line);
    }

    private static boolean isEomBoundary(String boundary, String line) {
        return line.startsWith(DASHES + boundary + DASHES);
    }

    private static void primeStream(InputStream in, String boundary) throws IOException {
        byte[] line;
        while (!isBoundary(boundary, (line = readLine(in)))) {
            // discard content
        }

        if (isEomBoundary(boundary, line)) {
            drain(in);
        }
    }

    private static byte[] readLine(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int value;
        while ((value = in.read()) != -1) {
            out.write(value);

            if (value == '\n') {
                break;
            }
        }

        return out.toByteArray();
    }

    private static byte[] trim(byte[] bytes) {
        return bytes.length < 2 ? bytes : Arrays.copyOf(bytes, bytes.length - 2);
    }

}
