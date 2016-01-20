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

package org.cloudfoundry.client.spring.util;

import lombok.ToString;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.spring.v2.CloudFoundryExceptionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ProcessorGroup;
import reactor.core.subscription.ReactiveSession;
import reactor.fn.Consumer;
import reactor.fn.Function;
import reactor.fn.Supplier;
import reactor.rx.Stream;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.PUT;

@ToString
public abstract class AbstractSpringOperations {

    private static final int BYTE_ARRAY_BUFFER_LENGTH = 8192;

    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client-spring");

    protected final RestOperations restOperations;

    protected final URI root;

    private final ProcessorGroup<?> processorGroup;

    protected AbstractSpringOperations(RestOperations restOperations, URI root, ProcessorGroup<?> processorGroup) {
        this.restOperations = restOperations;
        this.root = root;
        this.processorGroup = processorGroup;
    }

    protected final Mono<Void> delete(final Validatable request, final Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, new Function<ReactiveSession<Void>, Void>() {

            @Override
            public Void apply(ReactiveSession<Void> session) {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUri(AbstractSpringOperations.this.root);
                builderCallback.accept(builder);
                URI uri = builder.build().encode().toUri();

                AbstractSpringOperations.this.logger.debug("DELETE {}", uri);
                AbstractSpringOperations.this.restOperations.exchange(new RequestEntity<>(request, DELETE, uri), Void.class);
                return null;
            }

        })
                .next();
    }

    protected final <T, V extends Validatable> Stream<T> exchange(V request, final Function<ReactiveSession<T>, T> exchange) {
        return Stream
                .from(Validators
                        .validate(request)
                        .flatMap(new Function<V, Stream<T>>() {

                            @Override
                            public Stream<T> apply(V request) {
                                return Stream
                                        .yield(new Consumer<ReactiveSession<T>>() {

                                            @Override
                                            public void accept(ReactiveSession<T> session) {
                                                try {
                                                    T result = exchange.apply(session);
                                                    if (result != null) {
                                                        session.onNext(result);
                                                    }

                                                    session.onComplete();
                                                } catch (HttpStatusCodeException e) {
                                                    session.onError(CloudFoundryExceptionBuilder.build(e));
                                                }
                                            }

                                        });
                            }

                        }))
                .publishOn(this.processorGroup)
                .onBackpressureBlock();
    }

    protected final <T> Mono<T> get(Validatable request, final Class<T> responseType, final Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, new Function<ReactiveSession<T>, T>() {

            @Override
            public T apply(ReactiveSession<T> session) {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUri(AbstractSpringOperations.this.root);
                builderCallback.accept(builder);
                URI uri = builder.build().encode().toUri();

                AbstractSpringOperations.this.logger.debug("GET {}", uri);
                return AbstractSpringOperations.this.restOperations.getForObject(uri, responseType);
            }

        })
                .next();
    }

    protected final Stream<byte[]> getStream(final Validatable request, final Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, new Function<ReactiveSession<byte[]>, byte[]>() {

            @Override
            public byte[] apply(final ReactiveSession<byte[]> session) {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUri(AbstractSpringOperations.this.root);
                builderCallback.accept(builder);
                URI uri = builder.build().encode().toUri();

                AbstractSpringOperations.this.logger.debug("GET {}", uri);
                return AbstractSpringOperations.this.restOperations.execute(uri, HttpMethod.GET, null, new ResponseExtractor<byte[]>() {

                    @Override
                    public byte[] extractData(ClientHttpResponse response) throws IOException {
                        try (InputStream in = response.getBody()) {
                            int len;
                            byte[] buffer = new byte[BYTE_ARRAY_BUFFER_LENGTH];

                            ReactiveSession.Emission emission = ReactiveSession.Emission.OK;
                            while (emission.isOk() && (len = in.read(buffer)) != -1) {
                                emission = session.emit(Arrays.copyOf(buffer, len));
                            }

                            return null;
                        }
                    }

                });
            }

        });
    }

    protected final <T> Mono<T> patch(final Validatable request, final Class<T> responseType, final Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, new Function<ReactiveSession<T>, T>() {

            @Override
            public T apply(ReactiveSession<T> session) {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUri(AbstractSpringOperations.this.root);
                builderCallback.accept(builder);
                URI uri = builder.build().encode().toUri();

                AbstractSpringOperations.this.logger.debug("PATCH {}", uri);
                return AbstractSpringOperations.this.restOperations.exchange(new RequestEntity<>(request, PATCH, uri), responseType).getBody();
            }

        })
                .next();
    }

    protected final <T> Mono<T> post(final Validatable request, Class<T> responseType, Consumer<UriComponentsBuilder> builderCallback) {
        return postWithBody(request, new Supplier<Validatable>() {

            @Override
            public Validatable get() {
                return request;
            }

        }, responseType, builderCallback);
    }

    protected final <T, B> Mono<T> postWithBody(Validatable request, final Supplier<B> bodySupplier, final Class<T> responseType, final Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, new Function<ReactiveSession<T>, T>() {

            @Override
            public T apply(ReactiveSession<T> session) {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUri(AbstractSpringOperations.this.root);
                builderCallback.accept(builder);
                URI uri = builder.build().encode().toUri();

                AbstractSpringOperations.this.logger.debug("POST {}", uri);
                return AbstractSpringOperations.this.restOperations.postForObject(uri, bodySupplier.get(), responseType);
            }

        })
                .next();
    }

    protected final <T> Mono<T> put(final Validatable request, Class<T> responseType, Consumer<UriComponentsBuilder> builderCallback) {
        return putWithBody(request, new Supplier<Validatable>() {

            @Override
            public Validatable get() {
                return request;
            }

        }, responseType, builderCallback);
    }

    protected final <T, B> Mono<T> putWithBody(Validatable request, final Supplier<B> bodySupplier, final Class<T> responseType, final Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, new Function<ReactiveSession<T>, T>() {

            @Override
            public T apply(ReactiveSession<T> session) {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUri(AbstractSpringOperations.this.root);
                builderCallback.accept(builder);
                URI uri = builder.build().encode().toUri();

                AbstractSpringOperations.this.logger.debug("PUT {}", uri);
                return AbstractSpringOperations.this.restOperations.exchange(new RequestEntity<>(bodySupplier.get(), null, PUT, uri), responseType).getBody();
            }

        })
                .next();
    }

}
