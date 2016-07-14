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

package org.cloudfoundry.reactor.util;

import reactor.util.Exceptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

final class JsonParsingException extends RuntimeException {

    private static final long serialVersionUID = 689280281752742553L;

    private final String payload;

    JsonParsingException(String message, Throwable cause, InputStream in) {
        super(message, cause);
        this.payload = getPayload(in);
    }

    public String getPayload() {
        return this.payload;
    }

    private static String getPayload(InputStream in) {
        StringBuilder sb = new StringBuilder();

        try {
            in.reset();

            try (Reader reader = new InputStreamReader(in, Charset.forName("UTF-8"))) {

                int length;
                char[] buffer = new char[8192];
                while ((length = reader.read(buffer)) != -1) {
                    sb.append(buffer, 0, length);
                }
            }
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }

        return sb.toString();
    }

}
