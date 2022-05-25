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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static java.util.regex.Pattern.quote;
import static java.util.stream.Collectors.toMap;

/**
 * Utilities for dealing with {@link ApplicationManifest}s.  Includes the functionality to transform to and from standard CLI YAML files.
 */
public final class ApplicationManifestUtils {

    private static final int GIBI = 1_024;

    private static final Yaml YAML;

    static {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setExplicitStart(true);

        YAML = new Yaml(dumperOptions);
    }

    private ApplicationManifestUtils() {
    }

    /**
     * Reads a YAML manifest file (defined by the <a href="https://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html">CLI</a>) from a {@link Path} and converts it into a collection of {@link
     * ApplicationManifest}s.  Note that all resolution (both inheritance and common) is performed during read.
     *
     * @param path the path to read from
     * @return the resolved manifests
     */
    public static List<ApplicationManifest> read(Path path) {
        return doRead(path.toAbsolutePath(), emptyMap());
    }

    /**
     * Reads a YAML manifest file (defined by the <a href="https://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html">CLI</a>) from a {@link Path} and converts it into a collection of {@link
     * ApplicationManifest}s.  Note that all resolution (both inheritance and common) is performed during read.
     *
     * @param path the path to read from
     * @param variablesPath use variable substitution (described in <a href="https://docs.cloudfoundry.org/devguide/deploy-apps/manifest-attributes.html#variable-substitution">Add Variables to a Manifest</a>)
     * @return the resolved manifests
     */
    public static List<ApplicationManifest> read(Path path, Path variablesPath) {
        Map<String, String> variables = deserialize(variablesPath.toAbsolutePath())
            .entrySet()
            .stream()
            .collect(toMap(e -> String.format("\\(\\(%s\\)\\)", quote(e.getKey())), e -> String.valueOf(e.getValue())));

        return doRead(path.toAbsolutePath(), variables);
    }

    /**
     * Write {@link ApplicationManifest}s to a {@link Path}
     *
     * @param path                 the path to write to
     * @param applicationManifests the manifests to write
     */
    public static void write(Path path, ApplicationManifest... applicationManifests) {
        write(path, Arrays.asList(applicationManifests));
    }

    /**
     * Write {@link ApplicationManifest}s to a {@link Path}
     *
     * @param path                 the path to write to
     * @param applicationManifests the manifests to write
     */
    public static void write(Path path, List<ApplicationManifest> applicationManifests) {
        try (OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            write(out, applicationManifests);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    /**
     * Write {@link ApplicationManifest}s to an {@link OutputStream}
     *
     * @param out                  the {@link OutputStream} to write to
     * @param applicationManifests the manifests to write
     */
    public static void write(OutputStream out, ApplicationManifest... applicationManifests) {
        write(out, Arrays.asList(applicationManifests));
    }

    /**
     * Write {@link ApplicationManifest}s to an {@link OutputStream}
     *
     * @param out                  the {@link OutputStream} to write to
     * @param applicationManifests the manifests to write
     */
    public static void write(OutputStream out, List<ApplicationManifest> applicationManifests) {
        try (Writer writer = new OutputStreamWriter(out)) {
            YAML.dump(Collections.singletonMap("applications",
                applicationManifests.stream()
                    .map(ApplicationManifestUtils::toYaml)
                    .collect(Collectors.toList())),
                writer);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    private static <T> void as(Map<String, Object> payload, String key, Map<String, String> variables, Function<Object, T> mapper, Consumer<T> consumer) {
        Optional.ofNullable(payload.get(key))
            .map(o -> {
                if(o instanceof String) {
                    String result = ((String) o);
                    for(Map.Entry<String, String> e : variables.entrySet()) {
                        result = result.replaceAll(e.getKey(), e.getValue());
                    }
                    return result;
                }
                return o;
            })
            .map(mapper)
            .ifPresent(consumer);
    }

    private static void asBoolean(Map<String, Object> payload, String key, Map<String, String> variables, Consumer<Boolean> consumer) {
        as(payload, key, variables, Boolean.class::cast, consumer);
    }

    @SuppressWarnings("unchecked")
    private static void asDocker(Map<String, Object> payload, String key, Map<String, String> variables, Consumer<Docker> consumer) {
        as(payload, key, variables, value -> {
            Map<String, String> docker = ((Map<String, String>) value);
            return Docker.builder()
                .image(docker.get("image"))
                .password(docker.get("password"))
                .username(docker.get("username"))
                .build();
        }, consumer);
    }

    private static void asInteger(Map<String, Object> payload, String key, Map<String, String> variables, Consumer<Integer> consumer) {
        as(payload, key, variables, (e) -> {
            if(e instanceof String) {
                return Integer.parseInt((String)e);
            }
            return (Integer) e;
        }, consumer);
    }

    @SuppressWarnings("unchecked")
    private static <T> void asList(Map<String, Object> payload, String key, Map<String, String> variables, Function<Object, T> mapper, Consumer<T> consumer) {
        as(payload, key, variables, value -> ((List<Object>) value).stream(),
            values -> values
                .map(mapper)
                .forEach(consumer));
    }

    private static void asListOfString(Map<String, Object> payload, String key, Map<String, String> variables, Consumer<String> consumer) {
        asList(payload, key, variables, String.class::cast, consumer);
    }

    @SuppressWarnings("unchecked")
    private static <T> void asMap(Map<String, Object> payload, String key, Map<String, String> variables, Function<Object, T> valueMapper, Consumer2<String, T> consumer) {
        as(payload, key, variables, value -> ((Map<String, Object>) value),
            values -> values.forEach((k, v) -> consumer.accept(k, valueMapper.apply(v))));
    }

    private static void asMapOfStringString(Map<String, Object> payload, String key, Map<String, String> variables, Consumer2<String, String> consumer) {
        asMap(payload, key, variables, String::valueOf, consumer);
    }

    @SuppressWarnings("unchecked")
    private static void asMemoryInteger(Map<String, Object> payload, String key, Map<String, String> variables, Consumer<Integer> consumer) {
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

    private static void asString(Map<String, Object> payload, String key, Map<String, String> variables, Consumer<String> consumer) {
        as(payload, key, variables, String.class::cast, consumer);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> deserialize(Path path) {
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

    @SuppressWarnings("unchecked")
    private static List<ApplicationManifest> doRead(Path path, Map<String, String> variables) {
        Map<String, Object> root = deserialize(path);

        ApplicationManifest template = getTemplate(path, root, variables);

        return Optional.ofNullable(root.get("applications"))
            .map(value -> ((List<Map<String, Object>>) value).stream())
            .orElseGet(Stream::empty)
            .map(application -> {
                String name = getName(application);
                return toApplicationManifest(application, variables, ApplicationManifest.builder().from(template), path)
                    .name(name)
                    .build();
            })
            .collect(Collectors.toList());
    }

    private static Object getEmptyNamedObject(List<Object> array, String name) {
        Map<String, Object> value = new HashMap<>();
        value.put("name", name);
        array.add(value);
        return value;
    }

    private static String getName(Map<String, Object> raw) {
        return Optional.ofNullable(raw.get("name")).map(String.class::cast).orElseThrow(() -> new IllegalStateException("Application does not contain required 'name' value"));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getNamedObject(List<Object> array, String name) {
        return (Map<String, Object>) array.stream()
            .filter(value -> value instanceof Map && name.equals(((Map<String, String>) value).get("name")))
            .findFirst()
            .orElseGet(() -> getEmptyNamedObject(array, name));
    }

    @SuppressWarnings("unchecked")
    private static Route getRoute(Map<String, Object> raw) {
        String route = Optional.ofNullable(raw.get("route")).map(String.class::cast).orElseThrow(() -> new IllegalStateException("Route does not contain required 'route' value"));
        return Route.builder().route(route).build();
    }

    private static ApplicationManifest getTemplate(Path path, Map<String, Object> root, Map<String, String> variables) {
        return toApplicationManifest(root, variables, ApplicationManifest.builder(), path)
            .name("template")
            .build();
    }

    @SuppressWarnings("unchecked")
    private static void merge(Map<String, Object> first, Map<String, Object> second) {
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
    private static void merge(List<Object> first, List<Object> second) {
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

    private static Function<Path, Object> pathToString() {
        return path -> Optional.ofNullable(path).map(Path::toString).orElse(null);
    }

    private static Function<ApplicationHealthCheck, Object> protectApplicationHealthCheck() {
        return applicationHealthCheck -> Optional.ofNullable(applicationHealthCheck).map(ApplicationHealthCheck::getValue).orElse(null);
    }

    private static void putIfPresent(Map<String, Object> yaml, String key, Object value) {
        putIfPresent(yaml, key, value, Function.identity());
    }

    private static <T> void putIfPresent(Map<String, Object> yaml, String key, T value, Function<T, Object> valueMapper) {
        Optional.ofNullable(value).map(valueMapper).ifPresent(v -> yaml.put(key, v));
    }

    @SuppressWarnings("unchecked")
    private static ApplicationManifest.Builder toApplicationManifest(Map<String, Object> application, Map<String, String> variables, ApplicationManifest.Builder builder, Path root) {
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
        asList(application, "routes", variables, raw -> getRoute((Map<String, Object>) raw), builder::route);
        asListOfString(application, "services", variables, builder::service);
        asString(application, "stack", variables, builder::stack);
        asInteger(application, "timeout", variables, builder::timeout);

        return builder;
    }

    private static List<Map<String, String>> toRoutesYaml(List<Route> routes) {
        return routes.stream()
            .map(route -> Collections.singletonMap("route", route.getRoute()))
            .collect(Collectors.toList());
    }

    private static Map<String, Object> toYaml(ApplicationManifest applicationManifest) {
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
        putIfPresent(yaml, "routes", applicationManifest.getRoutes(), ApplicationManifestUtils::toRoutesYaml);
        putIfPresent(yaml, "services", applicationManifest.getServices());
        putIfPresent(yaml, "stack", applicationManifest.getStack());
        putIfPresent(yaml, "timeout", applicationManifest.getTimeout());

        return yaml;
    }

}
