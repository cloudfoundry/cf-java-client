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

import org.cloudfoundry.client.v3.processes.HealthCheckType;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import reactor.core.Exceptions;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

/**
 * Utilities for dealing with {@link ManifestV3}s.  Includes the functionality to transform to and from standard CLI YAML files.
 */
public final class ApplicationManifestUtilsV3 extends ApplicationManifestUtilsCommon {
    private static final Yaml YAML;

    static {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setExplicitStart(true);

        YAML = new Yaml(dumperOptions);
    }

    private static final Pattern FIND_VARIABLE_REGEX = Pattern.compile("\\(\\(([a-zA-Z]\\w+)\\)\\)");

    private ApplicationManifestUtilsV3() {
    }

    /**
     * Reads a YAML manifest file (defined by the <a href="https://v3-apidocs.cloudfoundry.org/version/3.124.0/index.html#manifests">CC API</a>) from a {@link Path} and converts it into a {@link
     * ManifestV3} object. Note that all resolution (both inheritance and common) is performed during read.
     *
     * @param path the path to read from
     * @return the resolved manifest
     */
    public static ManifestV3 read(Path path) {
        return doRead(path.toAbsolutePath(), emptyMap());
    }

    /**
     * Reads a YAML manifest file (defined by the <a href="https://v3-apidocs.cloudfoundry.org/version/3.124.0/index.html#manifests">CC API</a>) from a {@link Path} and converts it into a {@link
     * ManifestV3} object. Note that all resolution (both inheritance and common) is performed during read.
     *
     * @param path the path to read from
     * @param variablesPath use variable substitution (described in <a href="https://docs.cloudfoundry.org/devguide/deploy-apps/manifest-attributes.html#variable-substitution">Add Variables to a Manifest</a>)
     * @return the resolved manifest
     */
    public static ManifestV3 read(Path path, Path variablesPath) {
        Map<String, String> variables = deserialize(variablesPath.toAbsolutePath())
            .entrySet()
            .stream()
            .collect(toMap(Map.Entry::getKey,e -> String.valueOf(e.getValue())));

        return doRead(path.toAbsolutePath(), variables);
    }

    /**
     * Write a {@link ManifestV3} to a {@link Path}
     *
     * @param path     the path to write to
     * @param manifest the manifest to write
     */
    public static void write(Path path, ManifestV3 manifest) {
        try (OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            write(out, manifest);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    /**
     * Write a {@link ManifestV3} to an {@link OutputStream}
     *
     * @param out      the {@link OutputStream} to write to
     * @param manifest the manifest to write
     */
    public static void write(OutputStream out, ManifestV3 manifest) {
        try (Writer writer = new OutputStreamWriter(out)) {
            YAML.dump(toYaml(manifest), writer);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static ManifestV3 doRead(Path path, Map<String, String> variables) {
        Map<String, Object> root = deserialize(path);

        ManifestV3.Builder builder = ManifestV3.builder();
        asInteger(root, "version", variables, builder::version);

        ManifestV3Application template = getTemplate(path, root, variables);
        Optional.ofNullable(root.get("applications"))
            .map(value -> ((List<Map<String, Object>>) value).stream())
            .orElseGet(Stream::empty)
            .map(application -> {
                String name = getName(application);
                return toApplicationManifest(application, variables, ManifestV3Application.builder().from(template), path)
                    .name(name)
                    .build();
            })
            .forEach(builder::application);
        return builder.build();
    }

    private static ManifestV3Application getTemplate(Path path, Map<String, Object> root, Map<String, String> variables) {
        return toApplicationManifest(root, variables, ManifestV3Application.builder(), path)
            .name("template")
            .build();
    }

    @SuppressWarnings("unchecked")
    private static ManifestV3Application.Builder toApplicationManifest(Map<String, Object> application, Map<String, String> variables, ManifestV3Application.Builder builder, Path root) {
        toApplicationManifestCommon(application, variables, builder, root);

        asList(application, "processes", variables, raw -> getProcess((Map<String, Object>) raw, variables), builder::processe);
        asList(application, "services", variables, raw -> getService(raw, variables), builder::service);
        asList(application, "sidecars", variables, raw -> getSidecar((Map<String, Object>) raw, variables), builder::sidecar);
        as(application, "labels", variables, Map.class::cast, builder::labels);
        as(application, "annotations", variables, Map.class::cast, builder::annotations);

        return builder;
    }

    private static ManifestV3Sidecar getSidecar(Map<String, Object> raw, Map<String, String> variables) {
        ManifestV3Sidecar.Builder builder = ManifestV3Sidecar.builder();

        asString(raw, "name", variables, builder::name);
        asString(raw, "command", variables, builder::command);
        asListOfString(raw, "process_types", variables, builder::processType);
        asInteger(raw, "memory", variables, builder::memory);

        return builder.build();
    }

    private static ManifestV3Process getProcess(Map<String, Object> raw, Map<String, String> variables) {
        ManifestV3Process.Builder builder = ManifestV3Process.builder();

        asString(raw, "type", variables, builder::type);
        asString(raw, "command", variables, builder::command);
        asString(raw, "disk", variables, builder::disk);
        asString(raw, "health-check-http-endpoint", variables, builder::healthCheckHttpEndpoint);
        asInteger(raw, "health-check-invocation-timeout", variables, builder::healthCheckInvocationTimeout);
        as(raw, "health-check-type", variables, s -> HealthCheckType.from((String) s), builder::healthCheckType);
        asInteger(raw, "instances", variables, builder::instances);
        asString(raw, "memory", variables, builder::memory);
        asInteger(raw, "timeout", variables, builder::timeout);

        return builder.build();
    }

    private static ManifestV3Service getService(Object raw, Map<String, String> variables) {
        ManifestV3Service.Builder builder = ManifestV3Service.builder();
        if (raw instanceof String) {
            builder.name((String) raw);
        } else if (raw instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) raw;

            asString(map, "name", variables, builder::name);
            asString(map, "bindingName", variables, builder::bindingName);
            builder.parameters((map).get("parameters"));
        }
        return builder.build();
    }

    private static Map<String, Object> toYaml(ManifestV3 manifest) {
        Map<String, Object> yaml = new TreeMap<>();
        yaml.put("version", manifest.getVersion());
        yaml.put("applications", convertCollection(manifest.getApplications(), ApplicationManifestUtilsV3::toApplicationYaml));
        return yaml;
    }

    private static Map<String, Object> toApplicationYaml(ManifestV3Application application) {
        Map<String, Object> yaml = ApplicationManifestUtilsCommon.toApplicationYaml(application);

        putIfPresent(yaml, "processes", convertCollection(application.getProcesses(), ApplicationManifestUtilsV3::toProcessYaml));
        putIfPresent(yaml, "default-route", application.getDefaultRoute());
        putIfPresent(yaml, "services", convertCollection(application.getServices(), ApplicationManifestUtilsV3::toServiceYaml));
        putIfPresent(yaml, "sidecars", convertCollection(application.getSidecars(), ApplicationManifestUtilsV3::toSidecarsYaml));
        putIfPresent(yaml, "labels", application.getLabels());
        putIfPresent(yaml, "annotations", application.getAnnotations());
        return yaml;
    }

    private static Map<String, Object> toSidecarsYaml(ManifestV3Sidecar sidecar) {
        if (sidecar == null) return null;
        Map<String, Object> yaml = new TreeMap<>();
        putIfPresent(yaml, "name", sidecar.getName());
        putIfPresent(yaml, "command", sidecar.getCommand());
        putIfPresent(yaml, "process_types", sidecar.getProcessTypes());
        putIfPresent(yaml, "memory", sidecar.getMemory());
        return yaml;
    }

    private static Map<String, Object> toServiceYaml(ManifestV3Service service) {
        if (service == null) return null;
        Map<String, Object> yaml = new TreeMap<>();
        putIfPresent(yaml, "name", service.getName());
        putIfPresent(yaml, "binding_name", service.getBindingName());
        putIfPresent(yaml, "parameters", service.getParameters());
        return yaml;
    }

    private static Map<String, Object> toProcessYaml(ManifestV3Process process) {
        if (process == null) return null;
        Map<String, Object> yaml = new TreeMap<>();
        putIfPresent(yaml, "type", process.getType());
        putIfPresent(yaml, "command", process.getCommand());
        putIfPresent(yaml, "disk_quota", process.getDisk());
        putIfPresent(yaml, "health-check-http-endpoint", process.getHealthCheckHttpEndpoint());
        putIfPresent(yaml, "health-check-invocation-timeout", process.getHealthCheckInvocationTimeout());
        putIfPresent(yaml, "health-check-type", process.getHealthCheckType());
        putIfPresent(yaml, "instances", process.getInstances());
        putIfPresent(yaml, "memory", process.getMemory());
        putIfPresent(yaml, "timeout", process.getTimeout());
        return yaml;
    }
}
