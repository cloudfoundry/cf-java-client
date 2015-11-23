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
import reactor.rx.Stream;
import reactor.rx.Streams;

import java.net.URI;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    protected final Stream<Void> delete(Validatable request, Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, () -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(this.root);
            builderCallback.accept(builder);
            URI uri = builder.build().toUri();

            this.logger.debug("DELETE {}", uri);
            this.restOperations.exchange(new RequestEntity<>(request, DELETE, uri), Void.class).getBody();
            return null;
        });
    }

    protected final <T> Stream<T> exchange(Validatable request, Supplier<T> exchange) {
        return Streams.wrap(Publishers.create(subscriber -> {
            if (request != null) {
                ValidationResult validationResult = request.isValid();
                if (validationResult.getStatus() == ValidationResult.Status.INVALID) {
                    subscriber.onError(new RequestValidationException(validationResult));
                    return;
                }
            }

            try {
                subscriber.onNext(exchange.get());
                subscriber.onComplete();
            } catch (HttpStatusCodeException e) {
                subscriber.onError(CloudFoundryExceptionBuilder.build(e));
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }));
    }

    protected final <T> Stream<T> get(Validatable request, Class<T> responseType,
                                      Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, () -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(this.root);
            builderCallback.accept(builder);
            URI uri = builder.build().toUri();

            this.logger.debug("GET {}", uri);
            return this.restOperations.getForObject(uri, responseType);
        });
    }

    protected final <T> Stream<T> patch(Validatable request, Class<T> responseType,
                                        Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, () -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(this.root);
            builderCallback.accept(builder);
            URI uri = builder.build().toUri();

            this.logger.debug("PATCH {}", uri);
            return this.restOperations.exchange(new RequestEntity<>(request, PATCH, uri), responseType).getBody();
        });
    }

    protected final <T> Stream<T> post(Validatable request, Class<T> responseType,
                                       Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, () -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(this.root);
            builderCallback.accept(builder);
            URI uri = builder.build().toUri();

            this.logger.debug("POST {}", uri);
            return this.restOperations.postForObject(uri, request, responseType);
        });
    }

    protected final <T> Stream<T> put(Validatable request, Class<T> responseType,
                                      Consumer<UriComponentsBuilder> builderCallback) {
        return put(request, ()-> request, responseType, builderCallback);
    }

    protected final <T, B> Stream<T> put(Validatable request,
                                         Supplier<B> bodySupplier,
                                         Class<T> responseType,
                                         Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, () -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(this.root);
            builderCallback.accept(builder);
            URI uri = builder.build().toUri();

            this.logger.debug("PUT {}", uri);
            return this.restOperations.exchange(new RequestEntity<B>(bodySupplier.get(), null, PUT, uri),
                    responseType).getBody();
        });

    }
}
