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

package org.cloudfoundry.reactor;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import static io.netty.handler.codec.http.HttpResponseStatus.UNPROCESSABLE_ENTITY;

@Data
public final class InteractionContext {

    private static final TestResponse ERROR_RESPONSE = TestResponse.builder()
        .status(UNPROCESSABLE_ENTITY)
        .payload("fixtures/client/v2/error_response.json")
        .build();

    private final TestRequest request;

    private final TestResponse response;

    private volatile boolean done = false;

    @Builder(toBuilder = true)
    InteractionContext(@NonNull TestRequest request, TestResponse response) {
        this.request = request;
        this.response = response;
    }

    InteractionContext getErrorResponse() {
        return this.toBuilder()
            .response(ERROR_RESPONSE)
            .build();
    }

}
