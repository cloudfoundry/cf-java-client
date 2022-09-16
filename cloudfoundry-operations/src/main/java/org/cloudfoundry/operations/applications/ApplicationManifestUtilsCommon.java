/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.operations.applications;

import org.cloudfoundry.util.tuple.Consumer2;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import reactor.core.Exceptions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

/**
 * Common base class for dealing with manifests
 */
abstract class ApplicationManifestUtilsCommon {

    static final int GIBI = 1_024;

    static final Yaml YAML;

    static {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setExplicitStart(true);

        YAML = new Yaml(dumperOptions);
    }

    static final Pattern FIND_VARIABLE_REGEX = Pattern.compile("\\(\\(([a-zA-Z]\\w+)\\)\\)");

    @SuppressWarnings("unchecked")
    static <T extends _ApplicationManifestCommon.Builder> T toApplicationManifestCommon(Map<String, Object> application, Map<String, String> variables, T builder, Path root) {
        asListOfString(application, "buildpacks", variables, builder::buildpacks);
        asString(application, "buildpack", variables, builder::buildpacks);
        asString(application, "command", variables, builder::command);
        asMemoryInteger(application, "disk_quota", variables, builder::disk);
        asDocker(application, "docker", variables, builder::docker);
        asString(application, "domain", variables, builder::domain);
        asListOfString(application, "domains", variables, builder::domain);
        asMapOfStringString(application, "env", variables, builder::environmentVariable);
        asString(application, "health-check-http-endpoint", variables, builder::healthCheckHttpEndpoint);
        asString(application, "health-check-type", variables, healthCheckType -> builder.healthCheckType(ApplicationHealthCheck.from(healthCheckType)));
        asString(application, "host", variables, builder::host);
        asListOfString(application, "hosts", variables, builder::host);
        asInteger(application, "instances", variables, builder::instances);
        asMemoryInteger(application, "memory", variables, builder::memory);
        asString(application, "name", variables, builder::name);
        asBoolean(application, "no-hostname", variables, builder::noHostname);
        asBoolean(application, "no-route", variables, builder::noRoute);
        asString(application, "path", variables, path -> builder.path(root.getParent().resolve(path)));
        asBoolean(application, "random-route", variables, builder::randomRoute);
        asList(application, "routes", variables, raw -> getRoute((Map<String, Object>) raw, variables), builder::route);
        asString(application, "stack", variables, builder::stack);
        asInteger(application, "timeout", variables, builder::timeout);

        return builder;
    }

    static <T> void as(Map<String, Object> payload, String key, Map<String, String> variables, Function<Object, T> mapper, Consumer<T> consumer) {
        Optional.ofNullable(payload.get(key))
            .map(o -> {
                if(o instanceof String) {
                    Matcher m = FIND_VARIABLE_REGEX.matcher((String) o);
                    StringBuffer stringBuffer = new StringBuffer();
                    while(m.find()){
                        m.appendReplacement(stringBuffer, Optional.ofNullable(variables.get(m.group(1)))
                            .orElseThrow(() -> new NoSuchElementException("Expected to find variable: "+m.group(1))));
                    }
                    m.appendTail(stringBuffer);
                    return stringBuffer.toString();
                }
                return o;
            })
            .map(mapper)
            .ifPresent(consumer);
    }

    static void asBoolean(Map<String, Object> payload, String key, Map<String, String> variables, Consumer<Boolean> consumer) {
        as(payload, key, variables, Boolean.class::cast, consumer);
    }

    @SuppressWarnings("unchecked")
    static void asDocker(Map<String, Object> payload, String key, Map<String, String> variables, Consumer<Docker> consumer) {
        as(payload, key, variables, value -> {
            Map<String, String> docker = ((Map<String, String>) value);
            return Docker.builder()
                .image(docker.get("image"))
                .password(docker.get("password"))
                .username(docker.get("username"))
                .build();
        }, consumer);
    }

    static void asInteger(Map<String, Object> payload, String key, Map<String, String> variables, Consumer<Integer> consumer) {
        as(payload, key, variables, (e) -> {
            if(e instanceof String) {
                return Integer.parseInt((String)e);
            }
            return (Integer) e;
        }, consumer);
    }

    @SuppressWarnings("unchecked")
    static <T> void asList(Map<String, Object> payload, String key, Map<String, String> variables, Function<Object, T> mapper, Consumer<T> consumer) {
        as(payload, key, variables, value -> ((List<Object>) value).stream(),
            values -> values
                .map(mapper)
                .forEach(consumer));
    }

    static void asListOfString(Map<String, Object> payload, String key, Map<String, String> variables, Consumer<String> consumer) {
        asList(payload, key, variables, String.class::cast, consumer);
    }

    @SuppressWarnings("unchecked")
    static <T> void asMap(Map<String, Object> payload, String key, Map<String, String> variables, Function<Object, T> valueMapper, Consumer2<String, T> consumer) {
        as(payload, key, variables, value -> ((Map<String, Object>) value),
            values -> values.forEach((k, v) -> consumer.accept(k, valueMapper.apply(v))));
    }

    static void asMapOfStringString(Map<String, Object> payload, String key, Map<String, String> variables, Consumer2<String, String> consumer) {
        asMap(payload, key, variables, String::valueOf, consumer);
    }

    @SuppressWarnings("unchecked")
    static void asMemoryInteger(Map<String, Object> payload, String key, Map<String, String> variables, Consumer<Integer> consumer) {
        as(payload, key, variables, raw -> {
            if (raw instanceof Integer) {
                return (Integer) raw;
            } else if (raw instanceof String) {
                String text = ((String) raw).toUpperCase();

                if (text.endsWith("G")) {
                    return Integer.parseInt(text.substring(0, text.length() - 1)) * GIBI;
                } else if (text.endsWith("GB")) {
                    return Integer.parseInt(text.substring(0, text.length() - 2)) * GIBI;
                } else if (text.endsWith("M")) {
                    return Integer.parseInt(text.substring(0, text.length() - 1));
                } else if (text.endsWith("MB")) {
                    return Integer.parseInt(text.substring(0, text.length() - 2));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        }, consumer);
    }

    static void asString(Map<String, Object> payload, String key, Map<String, String> variables, Consumer<String> consumer) {
        as(payload, key, variables, String.class::cast, consumer);
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> deserialize(Path path) {
        AtomicReference<Map<String, Object>> root = new AtomicReference<>();

        try (InputStream in = Files.newInputStream(path, StandardOpenOption.READ)) {
            root.set((Map<String, Object>) YAML.load(in));
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }

        asString(root.get(), "inherit", emptyMap(), inherit -> {
            Map<String, Object> inherited = deserialize(path.getParent().resolve(inherit));
            merge(inherited, root.get());
            root.set(inherited);
        });

        return root.get();
    }

    static Object getEmptyNamedObject(List<Object> array, String name) {
        Map<String, Object> value = new HashMap<>();
        value.put("name", name);
        array.add(value);
        return value;
    }

    static String getName(Map<String, Object> raw) {
        return Optional.ofNullable(raw.get("name")).map(String.class::cast).orElseThrow(() -> new IllegalStateException("Application does not contain required 'name' value"));
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> getNamedObject(List<Object> array, String name) {
        return (Map<String, Object>) array.stream()
            .filter(value -> value instanceof Map && name.equals(((Map<String, String>) value).get("name")))
            .findFirst()
            .orElseGet(() -> getEmptyNamedObject(array, name));
    }

    @SuppressWarnings("unchecked")
    static Route getRoute(Map<String, Object> raw, Map<String, String> variables) {
        Route.Builder builder = Route.builder();
        asString(raw, "route", variables, builder::route);
        asString(raw, "protocol", variables, p -> builder.protocol(ManifestV3RouteProtocol.from(p)));
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    static void merge(Map<String, Object> first, Map<String, Object> second) {
        second.forEach((key, value) -> first.merge(key, value, (firstValue, secondValue) -> {
            if (secondValue instanceof Map) {
                merge((Map<String, Object>) firstValue, (Map<String, Object>) secondValue);
                return firstValue;
            } else if (secondValue instanceof List) {
                merge((List<Object>) firstValue, (List<Object>) secondValue);
                return firstValue;
            } else {
                return secondValue;
            }
        }));
    }

    @SuppressWarnings("unchecked")
    static void merge(List<Object> first, List<Object> second) {
        second
            .forEach(element -> {
                if (element instanceof Map) {
                    Object name = ((Map<String, Object>) element).get("name");

                    if (name != null) {
                        Map<String, Object> named = getNamedObject(first, (String) name);
                        merge(named, (Map<String, Object>) element);
                    } else {
                        first.add(element);
                    }
                } else {
                    first.add(element);
                }
            });
    }

    static Function<Path, Object> pathToString() {
        return path -> Optional.ofNullable(path).map(Path::toString).orElse(null);
    }

    static Function<ApplicationHealthCheck, Object> protectApplicationHealthCheck() {
        return applicationHealthCheck -> Optional.ofNullable(applicationHealthCheck).map(ApplicationHealthCheck::getValue).orElse(null);
    }

    static void putIfPresent(Map<String, Object> yaml, String key, Object value) {
        putIfPresent(yaml, key, value, Function.identity());
    }

    static <T> void putIfPresent(Map<String, Object> yaml, String key, T value, Function<T, Object> valueMapper) {
        Optional.ofNullable(value).map(valueMapper).ifPresent(v -> yaml.put(key, v));
    }

    static List<Map<String, Object>> toRoutesYaml(List<Route> routes) {
        return routes.stream()
            .map(route -> {
                Map<String, Object> yaml = new TreeMap<>();
                yaml.put("route", route.getRoute());
                putIfPresent(yaml, "protocol", route.getProtocol(), ManifestV3RouteProtocol::getValue);
                return yaml;
            })
            .collect(Collectors.toList());
    }

    static Map<String, Object> toApplicationYaml(_ApplicationManifestCommon applicationManifest) {
        Map<String, Object> yaml = new TreeMap<>();

        putIfPresent(yaml, "buildpacks", applicationManifest.getBuildpacks());
        putIfPresent(yaml, "command", applicationManifest.getCommand());
        Integer disk = applicationManifest.getDisk();
        if (null != disk) {
            putIfPresent(yaml, "disk_quota", applicationManifest.getDisk().toString() + "M");
        }
        putIfPresent(yaml, "docker", applicationManifest.getDocker());
        putIfPresent(yaml, "domains", applicationManifest.getDomains());
        putIfPresent(yaml, "env", applicationManifest.getEnvironmentVariables());
        putIfPresent(yaml, "health-check-http-endpoint", applicationManifest.getHealthCheckHttpEndpoint());
        putIfPresent(yaml, "health-check-type", applicationManifest.getHealthCheckType(), protectApplicationHealthCheck());
        putIfPresent(yaml, "hosts", applicationManifest.getHosts());
        putIfPresent(yaml, "instances", applicationManifest.getInstances());
        Integer memory = applicationManifest.getMemory();
        if (null != memory) {
            putIfPresent(yaml, "memory", memory + "M");
        }
        putIfPresent(yaml, "name", applicationManifest.getName());
        putIfPresent(yaml, "no-hostname", applicationManifest.getNoHostname());
        putIfPresent(yaml, "no-route", applicationManifest.getNoRoute());
        putIfPresent(yaml, "path", applicationManifest.getPath(), pathToString());
        putIfPresent(yaml, "random-route", applicationManifest.getRandomRoute());
        putIfPresent(yaml, "route-path", applicationManifest.getRoutePath());
        putIfPresent(yaml, "routes", applicationManifest.getRoutes(), ApplicationManifestUtilsCommon::toRoutesYaml);
        putIfPresent(yaml, "stack", applicationManifest.getStack());
        putIfPresent(yaml, "timeout", applicationManifest.getTimeout());

        return yaml;
    }

    static Map<String, Object> toDockerYaml(Docker docker) {
        if (docker == null) return null;
        Map<String, Object> yaml = new TreeMap<>();
        putIfPresent(yaml, "image", docker.getImage());
        putIfPresent(yaml, "username", docker.getUsername());
        putIfPresent(yaml, "password", docker.getPassword());
        return yaml;
    }

    static <T, S> List<S> convertCollection(Collection<T> collection, Function<T, S> converter) {
        if (collection == null) return null;
        return collection.stream()
            .map(converter)
            .collect(Collectors.toList());
    }
}
