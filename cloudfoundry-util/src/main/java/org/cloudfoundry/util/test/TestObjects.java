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

package org.cloudfoundry.util.test;

import org.junit.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@code TestObjects} provides a utility which calls setters of an object of {@code *Builder} type and returns the resulting builder object.
 * <p>
 * The exported static methods are {@link #fill &lt;T&gt;fill(T,String)} The {@code T} argument is a builder object, and the {@code String} is a <i>modifier</i> which is used to augment the {@code
 * String} values set. {@code fill(b)} is equivalent to {@code fill(b,"")} and {@code fillPage(b)} is equivalent to {@code fillPage(b,"")}.
 * <p>
 * {@code TestObjects} is designed to populate builder objects with test values. Object setters are called with standard values based upon the parameter type and the name of the setter method. Setters
 * which take collections are ignored.
 * <p>
 * <ul> <li>{@link String} types are set to {@code "test-"+modifier+settername}, where {@code modifier} is supplied on the call {@code fill(b,modifier)} or {@code fillPage(b,modifier)}.</li>
 * <li>{@link Boolean} types are set to {@code true}.</li> <li>{@link Integer} or {@link Long} types are set to {@code 1}.</li> <li>{@link Float} or {@link Double} types are set to {@code 1.0}.</li>
 * <li>Types with names ending in {@code Entity} or {@code Metadata}<sup>1</sup> are recursively filled, using {@link #fill fill(builder-of-type, modifier)}, if their builder types can be found.</li>
 * </ul>
 * <p>
 * <sup>1</sup>These special cases make the {@code TestObjects} class specific to v2 CloudFoundry REST api interfaces.
 */
public abstract class TestObjects {

    private TestObjects() { // do not instantiate this class
    }

    /**
     * Fill a builder by calling its configuration methods with default values.
     *
     * @param builder the builder to fill
     * @param <T>     The type of the builder
     * @return the filled builder
     */
    public static <T> T fill(T builder) {
        return fill(builder, Optional.empty());
    }

    /**
     * Fill a builder by calling its configuration methods with default values.
     *
     * @param builder  the builder to fill
     * @param modifier a modifier for the values of {@link String} types
     * @param <T>      The type of the builder
     * @return the filled builder
     */
    public static <T> T fill(T builder, String modifier) {
        return fill(builder, Optional.of(modifier));
    }

    private static boolean buildsRequestType(Class<?> builderType) {
        return getBuiltType(builderType).getName().endsWith("Request");
    }

    private static <T> T fill(T builder, Optional<String> modifier) {
        Class<?> builderType = builder.getClass();
        Assert.assertFalse("Do not fill Request types", buildsRequestType(builderType));

        List<Method> builderMethods = getBuilderMethods(builderType);
        Set<String> builtGetters = getBuiltGetters(builderType);

        return getConfigurationMethods(builderType, builderMethods, builtGetters).stream()
            .collect(() -> builder, (b, method) -> ReflectionUtils.invokeMethod(method, b, getConfiguredValue(method, modifier)), (a, b) -> {
            });
    }

    private static Method getBuildMethod(Class<?> builderType) {
        return ReflectionUtils.findMethod(builderType, "build");
    }

    private static Method getBuilderMethod(Class<?> builderType) {
        return ReflectionUtils.findMethod(builderType, "builder");
    }

    private static List<Method> getBuilderMethods(Class<?> builderType) {
        return Arrays.asList(ReflectionUtils.getUniqueDeclaredMethods(builderType));
    }

    private static Set<String> getBuiltGetters(Class<?> builderType) {
        Class<?> builtType = getBuiltType(builderType);
        return Arrays.stream(ReflectionUtils.getUniqueDeclaredMethods(builtType))
            .map(Method::getName)
            .filter(s -> s.startsWith("get"))
            .collect(Collectors.toSet());
    }

    private static Class<?> getBuiltType(Class<?> builderType) {
        return getBuildMethod(builderType).getReturnType();
    }

    private static List<Method> getConfigurationMethods(Class<?> builderType, List<Method> builderMethods, Set<String> builtGetters) {
        return builderMethods.stream()
            .filter(TestObjects::isPublic)
            .filter(method -> returnsBuilder(method, builderType))
            .filter(TestObjects::hasSingleParameter)
            .filter(method -> hasMatchingGetter(method, builtGetters))
            .collect(Collectors.toList());
    }

    private static Object getConfiguredBuilder(Method builderMethod, Optional<String> modifier) {
        Object builder = ReflectionUtils.invokeMethod(builderMethod, null);
        Method buildMethod = getBuildMethod(builder.getClass());

        return ReflectionUtils.invokeMethod(buildMethod, fill(builder, modifier));
    }

    private static Object getConfiguredEnum(Class<?> parameterType) {
        return parameterType.getEnumConstants()[0];
    }

    private static String getConfiguredString(Method method, Optional<String> modifier) {
        return modifier
            .map(m -> String.format("test-%s%s", m, method.getName()))
            .orElse(String.format("test-%s", method.getName()));
    }

    @SuppressWarnings("unchecked")
    private static Object getConfiguredValue(Method configurationMethod, Optional<String> modifier) {
        Class<?> parameterType = getParameter(configurationMethod).getType();
        Method builderMethod = getBuilderMethod(parameterType);

        if (builderMethod != null) {
            return getConfiguredBuilder(builderMethod, modifier);
        } else if (Enum.class.isAssignableFrom(parameterType)) {
            return getConfiguredEnum(parameterType);
        } else if (parameterType == Boolean.class) {
            return Boolean.TRUE;
        } else if (parameterType == Collection.class) {  // TODO: Remove once Lombok is gone
            return Collections.emptyList();
        } else if (parameterType == Date.class) {
            return new Date(0);
        } else if (parameterType == Double.class) {
            return 1D;
        } else if (parameterType == Duration.class) {
            return Duration.ofSeconds(15);
        } else if (parameterType == Integer.class) {
            return 1;
        } else if (parameterType == Iterable.class) {
            return Collections.emptyList();
        } else if (parameterType == List.class) {  // TODO: Remove once Lombok is gone
            return Collections.emptyList();
        } else if (parameterType == Long.class) {
            return 1L;
        } else if (parameterType == Map.class) {
            return Collections.emptyMap();
        } else if (parameterType == String.class) {
            return getConfiguredString(configurationMethod, modifier);
        } else {
            throw new IllegalStateException(String.format("Unable to configure %s", configurationMethod));
        }
    }

    private static Parameter getParameter(Method method) {
        return method.getParameters()[0];
    }

    private static boolean hasMatchingGetter(Method method, Set<String> builtGetters) {
        String propertyName = method.getName();
        String candidate = String.format("get%s%s", propertyName.substring(0, 1).toUpperCase(), propertyName.substring(1));
        return builtGetters.contains(candidate);
    }

    private static boolean hasSingleParameter(Method method) {
        return 1 == method.getParameterCount();
    }

    private static boolean isPublic(Method method) {
        return Modifier.isPublic(method.getModifiers());
    }

    private static boolean returnsBuilder(Method method, Class<?> builderType) {
        return builderType == method.getReturnType();
    }

}
