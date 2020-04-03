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

package org.cloudfoundry.reactor.util;

import org.cloudfoundry.reactor.client.MethodNameComparator;
import reactor.core.Exceptions;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public final class AnnotationUtils {

    private AnnotationUtils() {
    }

    public static class AnnotatedValue<T extends Annotation> {

        private final T annotation;

        private final Object value;

        public AnnotatedValue(T annotation, Object value) {
            this.annotation = annotation;
            this.value = value;
        }

        public T getAnnotation() {
            return this.annotation;
        }

        public Object getValue() {
            return this.value;
        }

    }

    public static <T extends Annotation> Optional<T> findAnnotation(Class<?> type, Class<T> annotationType) {
        Class<?> clazz = type;
        T annotation = clazz.getAnnotation(annotationType);

        while (annotation == null) {
            clazz = clazz.getSuperclass();

            if (clazz == null || Object.class == clazz) {
                break;
            }

            annotation = clazz.getAnnotation(annotationType);
        }

        return Optional.ofNullable(annotation);
    }

    public static <T extends Annotation> Stream<AnnotatedValue<T>> streamAnnotatedValues(Object instance, Class<T> annotationClass) {
        Class<?> instanceClass = instance.getClass();
        return Arrays.stream(instanceClass.getMethods())
            .sorted(MethodNameComparator.INSTANCE)
            .map(processMethod(instance, annotationClass))
            .filter(Objects::nonNull);
    }

    private static <T extends Annotation> Optional<T> findAnnotation(Method method, Class<T> annotationType) {
        Class<?> clazz = method.getDeclaringClass();
        T annotation = method.getAnnotation(annotationType);

        while (annotation == null) {
            clazz = clazz.getSuperclass();

            if (clazz == null || Object.class == clazz) {
                break;
            }

            try {
                annotation = clazz.getDeclaredMethod(method.getName(), method.getParameterTypes())
                    .getAnnotation(annotationType);
            } catch (NoSuchMethodException e) {
                // No equivalent method found
            }
        }

        return Optional.ofNullable(annotation);
    }

    private static Optional<Object> getValue(Method method, Object instance) {
        try {
            return Optional.ofNullable(method.invoke(instance));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw Exceptions.propagate(e);
        }
    }

    private static <T extends Annotation> Function<T, Optional<AnnotatedValue<T>>> processAnnotation(Method method, Object instance) {
        return annotation -> getValue(method, instance).map(value -> new AnnotatedValue<T>(annotation, value));
    }

    private static <T extends Annotation> Function<Method, AnnotatedValue<T>> processMethod(Object instance, Class<T> annotationClass) {
        return method -> findAnnotation(method, annotationClass).flatMap(processAnnotation(method, instance))
            .orElse(null);
    }

}
