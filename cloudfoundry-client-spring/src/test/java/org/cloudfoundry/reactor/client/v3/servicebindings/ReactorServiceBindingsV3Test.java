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

package org.cloudfoundry.reactor.client.v3.servicebindings;

import org.cloudfoundry.client.v3.servicebindings.DeleteServiceBindingRequest;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;

public final class ReactorServiceBindingsV3Test {

    public static final class Delete extends AbstractClientApiTest<DeleteServiceBindingRequest, Void> {

        private final ReactorServiceBindingsV3 serviceBindings = new ReactorServiceBindingsV3(this.authorizationProvider, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v3/service_bindings/test-service-binding-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected DeleteServiceBindingRequest getInvalidRequest() {
            return DeleteServiceBindingRequest.builder()
                .build();
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected DeleteServiceBindingRequest getValidRequest() throws Exception {
            return DeleteServiceBindingRequest.builder()
                .serviceBindingId("test-service-binding-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(DeleteServiceBindingRequest request) {
            return this.serviceBindings.delete(request);
        }

    }

}
