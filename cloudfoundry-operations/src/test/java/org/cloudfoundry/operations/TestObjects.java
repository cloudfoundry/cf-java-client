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

package org.cloudfoundry.operations;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@code TestObjects} provides a generic utility which transforms a builder object of type {@code T}, by calling its configuration methods with default values.
 * <p>
 * A <i>builder object</i> of type <b>T</b> is an object with a {@code build()} method returning a <i>built object</i> whose type has a {@code builder} of type <b>T</b>.
 * <p>
 * A <i>built object</i> of type <b>B</b> is an object with a {@code builder()} method returning a <i>builder object</i> which builds type <b>B</b>.
 * <p>
 * The exported static methods are {@link #fill T fill(T, String)} and {@link #fill T fill(T)}. The {@code T} argument must be an object of builder type (which is returned as result),
 * and the {@code String} is a <i>modifier</i> which is used to augment the {@code String} values set.  The modifier must not be {@code null}.
 * <p>
 * {@code fill(b)} is equivalent to {@code fill(b, "")}.
 * <p>
 * {@code TestObjects} populates builder objects with test values. Builder setter methods are called with standard values based upon the parameter type and the name of the setter method.
 * <ul>
 * <li>{@code enum} types are set to the first enumerated constant value.</li>
 * <li>{@link Boolean} types are set to {@code true}.</li>
 * <li>{@link Date} types are set to {@code new Date(0)}.</li>
 * <li>{@link Double} types are set to {@code 1.0}.</li>
 * <li>{@link Duration} types are set to a duration of 15 seconds.</li>
 * <li>{@link Integer} or {@link Long} types are set to {@code 1}.</li>
 * <li>{@link Iterable} types are set to empty.</li>
 * <li>{@link Map} types are set to empty.</li>
 * <li>{@link String} types are set to {@code "test-"+modifier+settername}.</li>
 * <li>Types of <i>built objects</i> are set to a value built from a (recursively) {@code fill()}ed builder instance.</li>
 * </ul>
 * <p>
 * Only public, chainable, single-parameter setter methods which have a corresponding getter (on the type built) are configured.
 * <p>
 * Non-builder objects, or builder objects that build {@code *Request} types, are rejected (by assertion failure).
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
        assertThat(isBuilderType(builderType)).as("Cannot fill type %s", builderType.getName()).isTrue();
        assertThat(buildsRequestType(builderType)).as("Do not fill Request types").isFalse();

        List<Method> builderMethods = getMethods(builderType);
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
            .filter(returnsThisType(builderType))
            .filter(TestObjects::hasSingleParameter)
            .filter(method -> hasMatchingGetter(method, builtGetters))
            .collect(Collectors.toList());
    }

    private static Object getConfiguredBuilder(Class<?> parameterType, Optional<String> modifier) {
        Object builder = ReflectionUtils.invokeMethod(getBuilderMethod(parameterType), null);
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

        if (isBuiltType(parameterType)) {
            return getConfiguredBuilder(parameterType, modifier);
        } else if (Enum.class.isAssignableFrom(parameterType)) {
            return getConfiguredEnum(parameterType);
        } else if (parameterType == Boolean.class) {
            return Boolean.TRUE;
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
        } else if (parameterType == Long.class) {
            return 1L;
        } else if (parameterType == Map.class) {
            return Collections.emptyMap();
        } else if (parameterType == String.class) {
            return getConfiguredString(configurationMethod, modifier);
        } else if (parameterType.isArray()) {
            return Array.newInstance(parameterType.getComponentType(), 0);
        } else {
            throw new IllegalStateException(String.format("Unable to configure %s", configurationMethod));
        }
    }

    private static List<Method> getMethods(Class<?> builderType) {
        return Arrays.asList(ReflectionUtils.getUniqueDeclaredMethods(builderType));
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

    private static boolean isBuilderType(Class<?> aType) {
        return Optional.ofNullable(getBuildMethod(aType))
            .map(Method::getReturnType)
            .map(TestObjects::getBuilderMethod)
            .map(Method::getReturnType)
            .map(aType::equals)
            .orElse(false);
    }

    private static boolean isBuiltType(Class<?> aType) {
        return Optional.ofNullable(getBuilderMethod(aType))
            .map(Method::getReturnType)
            .map(TestObjects::getBuildMethod)
            .map(Method::getReturnType)
            .map(aType::equals)
            .orElse(false);
    }

    private static boolean isPublic(Method method) {
        return Modifier.isPublic(method.getModifiers());
    }

    private static Predicate<Method> returnsThisType(Class<?> aType) {
        return method -> aType == method.getReturnType();
    }

}
