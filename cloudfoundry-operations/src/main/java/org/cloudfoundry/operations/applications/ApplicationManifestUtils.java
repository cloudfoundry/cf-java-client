/*
 * Copyright 2013-2017 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.cloudfoundry.util.tuple.Consumer2;
import reactor.core.Exceptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utilities for dealing with {@link ApplicationManifest}s.  Includes the functionality to transform to and from standard CLI YAML files.
 */
public final class ApplicationManifestUtils {

    private static final int GIBI = 1_024;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new YAMLFactory()
        .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES))
        .setSerializationInclusion(JsonInclude.Include.NON_NULL);

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
        return doRead(path.toAbsolutePath());
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
        try {
            OBJECT_MAPPER.writeValue(out, Collections.singletonMap("applications", applicationManifests));
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    private static <T> void as(JsonNode payload, String key, Function<JsonNode, T> mapper, Consumer<T> consumer) {
        Optional.ofNullable(payload.get(key))
            .map(mapper)
            .ifPresent(consumer);
    }

    private static void asBoolean(JsonNode payload, String key, Consumer<Boolean> consumer) {
        as(payload, key, JsonNode::asBoolean, consumer);
    }

    private static void asInteger(JsonNode payload, String key, Consumer<Integer> consumer) {
        as(payload, key, JsonNode::asInt, consumer);
    }

    private static <T> void asList(JsonNode payload, String key, Function<JsonNode, T> mapper, Consumer<T> consumer) {
        as(payload, key, ApplicationManifestUtils::streamOf,
            domains -> domains
                .map(mapper)
                .forEach(consumer));
    }

    private static void asListOfString(JsonNode payload, String key, Consumer<String> consumer) {
        asList(payload, key, JsonNode::asText, consumer);
    }

    private static <T> void asMap(JsonNode payload, String key, Function<JsonNode, T> valueMapper, Consumer2<String, T> consumer) {
        as(payload, key, environmentVariables -> streamOf(environmentVariables.fields()),
            environmentVariables -> environmentVariables
                .forEach(entry -> consumer.accept(entry.getKey(), valueMapper.apply(entry.getValue()))));
    }

    private static void asMapOfStringString(JsonNode payload, String key, Consumer2<String, String> consumer) {
        asMap(payload, key, JsonNode::asText, consumer);
    }

    private static void asMemoryInteger(JsonNode payload, String key, Consumer<Integer> consumer) {
        as(payload, key, raw -> {
            if (raw.isNumber()) {
                return raw.asInt();
            } else if (raw.isTextual()) {
                String text = raw.asText().toUpperCase();

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

    private static void asString(JsonNode payload, String key, Consumer<String> consumer) {
        as(payload, key, JsonNode::asText, consumer);
    }

    private static ObjectNode deserialize(Path path) {
        AtomicReference<ObjectNode> root = new AtomicReference<>();

        try (InputStream in = Files.newInputStream(path, StandardOpenOption.READ)) {
            root.set(((ObjectNode) OBJECT_MAPPER.readTree(in)));
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }

        asString(root.get(), "inherit", inherit -> root.set(merge(deserialize(path.getParent().resolve(inherit)), root.get())));

        return root.get();
    }

    private static List<ApplicationManifest> doRead(Path path) {
        JsonNode root = deserialize(path);

        ApplicationManifest template = getTemplate(path, root);

        return Optional.ofNullable(root.get("applications"))
            .map(ApplicationManifestUtils::streamOf)
            .orElseGet(Stream::empty)
            .map(application -> {
                String name = getName(application);
                return toApplicationManifest(application, ApplicationManifest.builder().from(template), path)
                    .name(name)
                    .build();
            })
            .collect(Collectors.toList());
    }

    private static String getName(JsonNode raw) {
        return Optional.ofNullable(raw.get("name")).map(JsonNode::asText).orElseThrow(() -> new IllegalStateException("Application does not contain required 'name' value"));
    }

    private static ObjectNode getNamedObject(ArrayNode array, String name) {
        return (ObjectNode) streamOf(array)
            .filter(object -> object.has("name") && name.equals(object.get("name").asText()))
            .findFirst()
            .orElseGet(array::addObject);
    }

    private static Route getRoute(JsonNode raw) {
        String route = Optional.ofNullable(raw.get("route")).map(JsonNode::asText).orElseThrow(() -> new IllegalStateException("Route does not contain required 'route' value"));
        return Route.builder().route(route).build();
    }

    private static ApplicationManifest getTemplate(Path path, JsonNode root) {
        return toApplicationManifest(root, ApplicationManifest.builder(), path)
            .name("template")
            .build();
    }

    private static ObjectNode merge(ObjectNode first, ObjectNode second) {
        streamOf(second.fields())
            .forEach(field -> {
                String key = field.getKey();
                JsonNode value = field.getValue();

                if (value instanceof ValueNode) {
                    first.replace(key, value);
                } else if (value instanceof ArrayNode) {
                    streamOf(value)
                        .forEach(element -> {
                            JsonNode name = element.get("name");

                            if (name != null) {
                                ObjectNode named = getNamedObject(first.withArray(key), name.asText());
                                merge(named, (ObjectNode) element);
                            } else {
                                first.withArray(key).add(element);
                            }
                        });
                } else if (value instanceof ObjectNode) {
                    first.with(key).setAll((ObjectNode) value);
                }
            });

        return first;
    }

    private static <T> Stream<T> streamOf(Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }

    private static <T> Stream<T> streamOf(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private static ApplicationManifest.Builder toApplicationManifest(JsonNode application, ApplicationManifest.Builder builder, Path root) {
        asString(application, "buildpack", builder::buildpack);
        asString(application, "command", builder::command);
        asMemoryInteger(application, "disk_quota", builder::disk);
        asString(application, "domain", builder::domain);
        asListOfString(application, "domains", builder::domain);
        asMapOfStringString(application, "env", builder::environmentVariable);
        asString(application, "health-check-http-endpoint", builder::healthCheckHttpEndpoint);
        asString(application, "health-check-type", healthCheckType -> builder.healthCheckType(ApplicationHealthCheck.from(healthCheckType)));
        asString(application, "host", builder::host);
        asListOfString(application, "hosts", builder::host);
        asInteger(application, "instances", builder::instances);
        asMemoryInteger(application, "memory", builder::memory);
        asString(application, "name", builder::name);
        asBoolean(application, "no-hostname", builder::noHostname);
        asBoolean(application, "no-route", builder::noRoute);
        asString(application, "path", path -> builder.path(root.getParent().resolve(path)));
        asBoolean(application, "random-route", builder::randomRoute);
        asList(application, "routes", ApplicationManifestUtils::getRoute, builder::route);
        asListOfString(application, "services", builder::service);
        asString(application, "stack", builder::stack);
        asInteger(application, "timeout", builder::timeout);

        return builder;
    }

}
