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

package org.cloudfoundry.client.spring.v2;

import org.cloudfoundry.client.v2.CloudFoundryException;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public final class CloudFoundryExceptionBuilderTest {

    @Test
    public void build() throws IOException {
        byte[] body = Files.readAllBytes(Paths.get(new ClassPathResource("v2/error_response.json").getURI()));
        HttpStatusCodeException cause = new HttpClientErrorException(UNPROCESSABLE_ENTITY, UNPROCESSABLE_ENTITY.getReasonPhrase(), body, Charset.defaultCharset());

        CloudFoundryException exception = CloudFoundryExceptionBuilder.build(cause);
        assertEquals(Integer.valueOf(10008), exception.getCode());
        assertEquals("The request is semantically invalid: space_guid and name unique", exception.getDescription());
        assertEquals("CF-UnprocessableEntity", exception.getErrorCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildInvalidPayload() {
        HttpStatusCodeException cause = new HttpClientErrorException(UNPROCESSABLE_ENTITY, UNPROCESSABLE_ENTITY.getReasonPhrase(), "{".getBytes(Charset.defaultCharset()), Charset.defaultCharset());
        CloudFoundryExceptionBuilder.build(cause);
    }

}
