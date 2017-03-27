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
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utilities for dealing with {@link ApplicationManifest}s.  Includes the functionality to transform to and from standard CLI YAML files.
 */
public final class ApplicationManifestUtils {

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
        return doRead(path)
            .values().stream()
            .map(ApplicationManifest.Builder::build)
            .collect(Collectors.toList());
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

    private static void asString(JsonNode payload, String key, Consumer<String> consumer) {
        as(payload, key, JsonNode::asText, consumer);
    }

    private static JsonNode deserialize(Path path) {
        try (InputStream in = Files.newInputStream(path, StandardOpenOption.READ)) {
            return OBJECT_MAPPER.readTree(in);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    private static Map<String, ApplicationManifest.Builder> doRead(Path path) {
        Map<String, ApplicationManifest.Builder> applicationManifests = new TreeMap<>();

        JsonNode root = deserialize(path);

        asString(root, "inherit", inherit -> applicationManifests.putAll(doRead(path.getParent().resolve(inherit))));

        applicationManifests
            .forEach((name, builder) -> applicationManifests.put(name, toApplicationManifest(root, builder, path)));

        ApplicationManifest template = getTemplate(path, root);

        Optional.ofNullable(root.get("applications"))
            .map(ApplicationManifestUtils::streamOf)
            .ifPresent(applications -> applications
                .forEach(application -> {
                    String name = application.get("name").asText();
                    ApplicationManifest.Builder builder = getBuilder(applicationManifests, template, name);

                    applicationManifests.put(name, toApplicationManifest(application, builder, path));
                }));

        return applicationManifests;
    }

    private static ApplicationManifest.Builder getBuilder(Map<String, ApplicationManifest.Builder> applicationManifests, ApplicationManifest template, String name) {
        ApplicationManifest.Builder builder = applicationManifests.get(name);
        if (builder == null) {
            builder = ApplicationManifest.builder().from(template);
        }
        return builder;
    }

    private static ApplicationManifest getTemplate(Path path, JsonNode root) {
        return toApplicationManifest(root, ApplicationManifest.builder(), path)
            .name("template")
            .build();
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
        asInteger(application, "disk_quota", builder::disk);
        asString(application, "domain", builder::domain);
        asListOfString(application, "domains", builder::domain);
        asMapOfStringString(application, "env", builder::environmentVariable);
        asString(application, "health-check-http-endpoint", builder::healthCheckHttpEndpoint);
        asString(application, "health-check-type", healthCheckType -> builder.healthCheckType(ApplicationHealthCheck.from(healthCheckType)));
        asString(application, "host", builder::host);
        asListOfString(application, "hosts", builder::host);
        asInteger(application, "instances", builder::instances);
        asInteger(application, "memory", builder::memory);
        asString(application, "name", builder::name);
        asBoolean(application, "no-hostname", builder::noHostname);
        asBoolean(application, "no-route", builder::noRoute);
        asString(application, "path", path -> builder.path(root.resolve(path)));
        asBoolean(application, "random-route", builder::randomRoute);
        asList(application, "routes", route -> Route.builder().route(route.get("route").asText()).build(), builder::route);
        asListOfString(application, "services", builder::service);
        asString(application, "stack", builder::stack);
        asInteger(application, "timeout", builder::timeout);

        return builder;
    }

}
