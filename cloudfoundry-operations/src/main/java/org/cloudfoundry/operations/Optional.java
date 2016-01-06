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

package org.cloudfoundry.operations;

import reactor.fn.Consumer;
import reactor.fn.Function;
import reactor.fn.Predicate;
import reactor.fn.Supplier;

import java.util.NoSuchElementException;
import java.util.Objects;

final class Optional<T> {

    private static final Optional<?> EMPTY = new Optional<>();

    private final T value;

    private Optional() {
        this.value = null;
    }

    private Optional(T value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Optional)) {
            return false;
        }

        Optional<?> other = (Optional<?>) obj;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    @Override
    public String toString() {
        return this.value != null ? String.format("Optional[%s]", this.value) : "Optional.empty";
    }

    @SuppressWarnings("unchecked")
    static <T> Optional<T> empty() {
        return (Optional<T>) EMPTY;
    }

    static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }

    @SuppressWarnings("unchecked")
    static <T> Optional<T> ofNullable(T value) {
        return value == null ? (Optional<T>) empty() : of(value);
    }

    @SuppressWarnings("unchecked")
    Optional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);

        if (!isPresent())
            return this;
        else
            return predicate.test(this.value) ? this : (Optional<T>) empty();
    }

    <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
        Objects.requireNonNull(mapper);

        if (!isPresent())
            return empty();
        else {
            return Objects.requireNonNull(mapper.apply(this.value));
        }
    }

    T get() {
        if (this.value == null) {
            throw new NoSuchElementException("No value present");
        }
        return this.value;
    }

    void ifPresent(Consumer<? super T> consumer) {
        if (this.value != null)
            consumer.accept(this.value);
    }

    boolean isPresent() {
        return this.value != null;
    }

    <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);

        if (!isPresent())
            return empty();
        else {
            return Optional.ofNullable(mapper.apply(this.value));
        }
    }

    T orElse(T other) {
        return this.value != null ? this.value : other;
    }

    T orElseGet(Supplier<? extends T> other) {
        return this.value != null ? this.value : other.get();
    }

    <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (this.value != null) {
            return this.value;
        } else {
            throw exceptionSupplier.get();
        }
    }

}
