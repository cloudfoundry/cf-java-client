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

import lombok.ToString;
import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.spring.v2.CloudFoundryExceptionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.Publishers;
import reactor.core.subscriber.SubscriberWithContext;
import reactor.fn.Consumer;
import reactor.fn.Supplier;
import reactor.rx.Stream;
import reactor.rx.Streams;

import java.net.URI;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.PUT;

@ToString
public abstract class AbstractSpringOperations {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final RestOperations restOperations;

    protected final URI root;

    protected AbstractSpringOperations(RestOperations restOperations, URI root) {
        this.restOperations = restOperations;
        this.root = root;
    }

    protected final Stream<Void> delete(final Validatable request,
                                        final Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, new Supplier<Void>() {

            @Override
            public Void get() {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUri(AbstractSpringOperations.this.root);
                builderCallback.accept(builder);
                URI uri = builder.build().toUri();

                AbstractSpringOperations.this.logger.debug("DELETE {}", uri);
                AbstractSpringOperations.this.restOperations.exchange(new RequestEntity<>(request, DELETE, uri),
                        Void.class);
                return null;
            }

        });
    }

    protected final <T> Stream<T> exchange(final Validatable request, final Supplier<T> exchange) {
        return Streams.wrap(Publishers.create(new Consumer<SubscriberWithContext<T, Void>>() {

            @Override
            public void accept(SubscriberWithContext<T, Void> subscriber) {
                if (request != null) {
                    ValidationResult validationResult = request.isValid();
                    if (validationResult.getStatus() == ValidationResult.Status.INVALID) {
                        subscriber.onError(new RequestValidationException(validationResult));
                        return;
                    }
                }

                try {
                    T result = exchange.get();
                    if (result != null) {
                        subscriber.onNext(result);
                    }

                    subscriber.onComplete();
                } catch (HttpStatusCodeException e) {
                    subscriber.onError(CloudFoundryExceptionBuilder.build(e));
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }

        }));
    }

    protected final <T> Stream<T> get(Validatable request, final Class<T> responseType,
                                      final Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, new Supplier<T>() {

            @Override
            public T get() {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUri(AbstractSpringOperations.this.root);
                builderCallback.accept(builder);
                URI uri = builder.build().toUri();

                AbstractSpringOperations.this.logger.debug("GET {}", uri);
                return AbstractSpringOperations.this.restOperations.getForObject(uri, responseType);
            }

        });
    }

    protected final <T> Stream<T> patch(final Validatable request, final Class<T> responseType,
                                        final Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, new Supplier<T>() {

            @Override
            public T get() {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUri(AbstractSpringOperations.this.root);
                builderCallback.accept(builder);
                URI uri = builder.build().toUri();

                AbstractSpringOperations.this.logger.debug("PATCH {}", uri);
                return AbstractSpringOperations.this.restOperations.exchange(
                        new RequestEntity<>(request, PATCH, uri), responseType).getBody();
            }

        });
    }

    protected final <T> Stream<T> post(final Validatable request, Class<T> responseType,
                                       Consumer<UriComponentsBuilder> builderCallback) {
        return postWithBody(request, new Supplier<Validatable>() {

            @Override
            public Validatable get() {
                return request;
            }

        }, responseType, builderCallback);
    }

    protected final <T, B> Stream<T> postWithBody(Validatable request,
                                                  final Supplier<B> bodySupplier,
                                                  final Class<T> responseType,
                                                  final Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, new Supplier<T>() {

            @Override
            public T get() {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUri(AbstractSpringOperations.this.root);
                builderCallback.accept(builder);
                URI uri = builder.build().toUri();

                AbstractSpringOperations.this.logger.debug("POST {}", uri);
                return AbstractSpringOperations.this.restOperations.postForObject(
                        uri, bodySupplier.get(), responseType);
            }

        });
    }

    protected final <T> Stream<T> put(final Validatable request, Class<T> responseType,
                                      Consumer<UriComponentsBuilder> builderCallback) {
        return putWithBody(request, new Supplier<Validatable>() {

            @Override
            public Validatable get() {
                return request;
            }

        }, responseType, builderCallback);
    }

    protected final <T, B> Stream<T> putWithBody(Validatable request,
                                                 final Supplier<B> bodySupplier,
                                                 final Class<T> responseType,
                                                 final Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, new Supplier<T>() {

            @Override
            public T get() {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUri(AbstractSpringOperations.this.root);
                builderCallback.accept(builder);
                URI uri = builder.build().toUri();

                AbstractSpringOperations.this.logger.debug("PUT {}", uri);
                return AbstractSpringOperations.this.restOperations.exchange(
                        new RequestEntity<>(bodySupplier.get(), null, PUT, uri), responseType).getBody();
            }

        });
    }

}
