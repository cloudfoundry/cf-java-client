/*
 * Copyright 2013-2020 the original author or authors.
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
import org.cloudfoundry.reactor.AbstractRestTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractClientApiTest extends AbstractRestTest {

    private static final Pattern BOUNDARY = Pattern.compile("multipart/form-data; boundary=(.+)");

    protected static String extractBoundary(Headers headers) {
        String contentType = headers.get("Content-Type");
        assertThat(contentType).as("Has Content-Type header").isNotNull();

        Matcher matcher = BOUNDARY.matcher(contentType);
        assertThat(matcher.find()).as("Has Content-Type with boundary").isTrue();
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
