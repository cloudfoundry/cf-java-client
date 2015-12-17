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

package org.cloudfoundry.operations.v2;

import org.cloudfoundry.client.v2.PaginatedRequest;
import org.cloudfoundry.client.v2.PaginatedResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.reflect.Modifier.isPublic;
import static org.junit.Assert.fail;

/**
 * {@code TestObjects} provides a utility which calls setters of an object of {@code *Builder} type and returns the resulting builder object.
 *
 * <p>The exported static methods are {@link #fill &LT;T>fill(T,String)} and {@link #fill &LT;T>fill(T)}. The {@code T} argument is a builder object, and the {@code String} is a <i>modifier</i> which
 * is used to augment the {@code String} values set. {@code fill(b)} is equivalent to {@code fill(b,"")}.</p>
 *
 * <p>{@code TestObjects} is designed to populate builder objects with test values. Object setters are called with standard values based upon the parameter type. Setters which are collections or
 * belong to {@link lombok.Singular} versions of collections are ignored.</p>
 *
 * <ul> <li>{@link String} types are set to {@code "test-"+modifier+settername}, where {@code modifier} is supplied on the call {@code fill(b,modifier)}.</li>
 *
 * <li>{@link Boolean} types are set to {@code true}.</li>
 *
 * <li>{@link Integer} types are set to 1.</li>
 *
 * <li>Types with names ending in {@code Entity} or {@code Metadata}<sup>1</sup> are recursively filled, with the same {@code modifier}, if their builder types can be found.</li> </ul>
 *
 * <p>Paginated builder objects<sup>1</sup> (subclassing {@link PaginatedRequest} or {@link PaginatedResponse}) are treated specially, to set page request fields consistently with the operations
 * implementations: their setters {@code resultsPerPage} and {@code orderDirection} are ignored.</p>
 *
 * <p><sup>1</sup>These special cases make the {@code TestObjects} class specific to v2 CloudFoundry REST api interfaces. </p>
 */
public abstract class TestObjects {

    private TestObjects() { // do not instantiate this class
    }

    /**
     * Fill the builder "fields" by calling their setters with default values.
     *
     * @param builder an object of type T which is a builder type
     * @param <T>     the type of the builder object
     * @return builder with setter fields "filled in"
     */
    public static <T> T fill(T builder) {
        return fill(builder, "");
    }

    /**
     * Fill the builder "fields" by calling their setters with default values.
     *
     * @param builder  an object of type T which is a builder type
     * @param modifier a modifier for {@code String} types which are set
     * @param <T>      the type of the builder object
     * @return builder with setter fields "filled in"
     */
    public static <T> T fill(T builder, String modifier) {
        Class<?> builderClass = builder.getClass();
        Class<?> builtType = getBuiltType(builderClass, true);

        boolean isPaginated = isPaginatedType(builtType);

        for (Method m : builderClass.getDeclaredMethods()) {
            if (isPublic(m.getModifiers())) {
                Class<?>[] parmTypes = m.getParameterTypes();
                Class<?> returnType = m.getReturnType();
                if (parmTypes.length == 1 && returnType == builderClass) { // single-value, chainable, setter
                    Object parmValue = buildTestValue(m, parmTypes[0], modifier, isPaginated, hasGetterFor(m.getName(), builtType));
                    if (parmValue != null) {
                        builder = invokeSetter(builder, m, parmValue);
                    }
                }
            }
        }
        return builder;
    }

    private static <O> O buildFilled(Class<O> builtClass, String modifier) {
        try {
            Method builderMethod = builtClass.getMethod("builder");
            Class<?> builderClass = builderMethod.getReturnType();
            Object builder = fill(builderMethod.invoke(null), modifier);
            @SuppressWarnings("unchecked")
            O built = (O) builderClass.getMethod("build").invoke(builder);
            return built;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            fail("Cannot get builder for sub-object of type " + builtClass);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <O> O buildTestValue(Method m, Class<O> clazz, String modifier, boolean isPaginated, boolean hasGetterOnBuiltType) {

        if (clazz.getSimpleName().endsWith("Entity")) return buildFilled(clazz, modifier);
        if (clazz.getSimpleName().endsWith("Metadata")) return buildFilled(clazz, modifier);

        if (isPaginated) {
            if (m.getName().equals("resultsPerPage")) return null;
            if (m.getName().equals("orderDirection")) return null;
        }

        if (!hasGetterOnBuiltType) return null;

        if (clazz == Boolean.class) return (O) Boolean.valueOf(true);
        if (clazz == Integer.class) return (O) Integer.valueOf(1);
        if (clazz == String.class) return (O) String.valueOf("test-" + modifier + m.getName());
        if (clazz == Map.class) return (O) Collections.emptyMap();
        if (clazz == List.class) return (O) Collections.emptyList();
        if (clazz == Collection.class) {
            return null;
        }

        return null;
    }

    private static Class<?> getBuiltType(Class<?> builderClass, boolean failIfNotBuilder) {
        if (builderClass.getSimpleName().endsWith("Builder")) {
            try {
                Class<?> builtType = builderClass.getMethod("build").getReturnType();
                if (builtType.getMethod("builder").getReturnType() == builderClass) {
                    return builtType;
                }
            } catch (NoSuchMethodException e) {
            }
        }
        if (failIfNotBuilder) {
            fail("Unrecognized builder type " + builderClass);
        }
        return null;
    }

    private static String getterName(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private static boolean hasGetterFor(String name, Class<?> clazz) {
        try {
            clazz.getMethod(getterName(name));
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T, V> T invokeSetter(T builder, Method m, V parmValue) {
        try {
            builder = (T) m.invoke(builder, parmValue);
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail("test object cannot be filled at method " + m + ". Exception " + e);
        }
        return builder;
    }

    private static boolean isBuilderType(Class<?> clazz) {
        return (null != getBuiltType(clazz, false));
    }

    private static boolean isPaginatedType(Class<?> builtType) {
        return PaginatedRequest.class.isAssignableFrom(builtType) ||
                PaginatedResponse.class.isAssignableFrom(builtType);
    }

}
