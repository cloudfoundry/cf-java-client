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

package org.cloudfoundry.spring.util;

import lombok.ToString;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.reactor.client.CloudFoundryExceptionBuilder;
import org.cloudfoundry.util.ValidationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.subscriber.SignalEmitter;
import reactor.core.util.Exceptions;

import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.PUT;

@ToString
public abstract class AbstractSpringOperations {

    private static final int BYTE_ARRAY_BUFFER_LENGTH = 8192;

    private final RestOperations restOperations;

    private final URI root;

    private final Scheduler schedulerGroup;

    protected AbstractSpringOperations(RestOperations restOperations, URI root, Scheduler schedulerGroup) {
        this.restOperations = restOperations;
        this.root = root;
        this.schedulerGroup = schedulerGroup;
    }

    protected final <T> Mono<T> delete(Validatable request, Class<T> responseType, Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, (Function<SignalEmitter<T>, T>) signalEmitter -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(this.root);
            builderCallback.accept(builder);
            URI uri = builder.build().encode().toUri();

            return this.restOperations.exchange(new RequestEntity<>(request, DELETE, uri), responseType).getBody();
        })
            .next();
    }

    protected final <T, V extends Validatable> Flux<T> exchange(V request, Function<SignalEmitter<T>, T> exchange) {
        return ValidationUtils
            .validate(request)
            .flatMap(validRequest -> Flux
                .create((Consumer<SignalEmitter<T>>) signalEmitter -> {
                    try {
                        T result = exchange.apply(signalEmitter);
                        if (result != null) {
                            signalEmitter.tryEmit(result);
                        }

                        signalEmitter.complete();
                    } catch (HttpStatusCodeException e) {
                        signalEmitter.fail(CloudFoundryExceptionBuilder.build(e));
                    } catch (Throwable t) {
                        Exceptions.throwIfFatal(t);
                        signalEmitter.fail(t);
                    }
                }))
            .subscribeOn(this.schedulerGroup)
            .onBackpressureBuffer();
    }

    protected final <T> Mono<T> get(Validatable request, Class<T> responseType, Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, (Function<SignalEmitter<T>, T>) signalEmitter -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(this.root);
            builderCallback.accept(builder);
            URI uri = builder.build().encode().toUri();

            return this.restOperations.getForObject(uri, responseType);
        })
            .next();
    }

    protected final Flux<byte[]> getStream(Validatable request, Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, signalEmitter -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(this.root);
            builderCallback.accept(builder);
            URI uri = builder.build().encode().toUri();

            return this.restOperations.execute(uri, HttpMethod.GET, null, response -> {
                try (InputStream in = response.getBody()) {
                    int len;
                    byte[] buffer = new byte[BYTE_ARRAY_BUFFER_LENGTH];

                    SignalEmitter.Emission emission = SignalEmitter.Emission.OK;
                    while (emission.isOk() && (len = in.read(buffer)) != -1) {
                        emission = signalEmitter.emit(Arrays.copyOf(buffer, len));
                    }

                    return (byte[]) null;
                }
            });
        });
    }

    protected final <T> Mono<T> patch(Validatable request, Class<T> responseType, Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, (Function<SignalEmitter<T>, T>) signalEmitter -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(this.root);
            builderCallback.accept(builder);
            URI uri = builder.build().encode().toUri();

            return this.restOperations.exchange(new RequestEntity<>(request, PATCH, uri), responseType).getBody();
        })
            .next();
    }

    protected final <T> Mono<T> post(Validatable request, Class<T> responseType, Consumer<UriComponentsBuilder> builderCallback) {
        return postWithBody(request, () -> request, responseType, builderCallback);
    }

    protected final <T, B> Mono<T> postWithBody(Validatable request, Supplier<B> bodySupplier, Class<T> responseType, Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, (Function<SignalEmitter<T>, T>) signalEmitter -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(this.root);
            builderCallback.accept(builder);
            URI uri = builder.build().encode().toUri();

            return this.restOperations.postForObject(uri, bodySupplier.get(), responseType);
        })
            .next();
    }

    protected final <T> Mono<T> put(Validatable request, Class<T> responseType, Consumer<UriComponentsBuilder> builderCallback) {
        return putWithBody(request, () -> request, responseType, builderCallback);
    }

    protected final <T, B> Mono<T> putWithBody(Validatable request, Supplier<B> bodySupplier, Class<T> responseType, Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, (Function<SignalEmitter<T>, T>) signalEmitter -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(this.root);
            builderCallback.accept(builder);
            URI uri = builder.build().encode().toUri();

            return this.restOperations.exchange(new RequestEntity<>(bodySupplier.get(), null, PUT, uri), responseType).getBody();
        })
            .next();
    }

}
