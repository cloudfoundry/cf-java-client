/*
 * Copyright 2013-2018 the original author or authors.
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

public final class AnnotationUtils {

    private AnnotationUtils() {
    }

    public static <T extends Annotation> Optional<T> findAnnotation(Method method, Class<T> annotationType) {
        Class<?> clazz = method.getDeclaringClass();
        T annotation = method.getAnnotation(annotationType);

        while (annotation == null) {
            clazz = clazz.getSuperclass();

            if (clazz == null || Object.class == clazz) {
                break;
            }

            try {
                annotation = clazz.getDeclaredMethod(method.getName(), method.getParameterTypes()).getAnnotation(annotationType);
            } catch (NoSuchMethodException e) {
                // No equivalent method found
            }
        }

        return Optional.ofNullable(annotation);
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

}
