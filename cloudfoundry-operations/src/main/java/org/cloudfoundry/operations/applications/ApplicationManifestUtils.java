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

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import reactor.core.Exceptions;

/**
 * Utilities for dealing with {@link ApplicationManifest}s.  Includes the functionality to transform to and from standard CLI YAML files.
 */
public final class ApplicationManifestUtils extends ApplicationManifestUtilsCommon {
    private ApplicationManifestUtils() {}

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
        Map<String, String> variables =
                deserialize(variablesPath.toAbsolutePath()).entrySet().stream()
                        .collect(toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));

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
        try (OutputStream out =
                Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
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
            YAML.dump(
                    Collections.singletonMap(
                            "applications",
                            applicationManifests.stream()
                                    .map(ApplicationManifestUtils::toYaml)
                                    .collect(Collectors.toList())),
                    writer);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    private static ApplicationManifest getTemplate(
            Path path, Map<String, Object> root, Map<String, String> variables) {
        return toApplicationManifest(root, variables, ApplicationManifest.builder(), path)
                .name("template")
                .build();
    }

    @SuppressWarnings("unchecked")
    private static ApplicationManifest.Builder toApplicationManifest(
            Map<String, Object> application,
            Map<String, String> variables,
            ApplicationManifest.Builder builder,
            Path root) {
        toApplicationManifestCommon(application, variables, builder, root);
        asListOfString(application, "services", variables, builder::service);

        return builder;
    }

    private static Map<String, Object> toYaml(ApplicationManifest applicationManifest) {
        Map<String, Object> yaml =
                ApplicationManifestUtilsCommon.toApplicationYaml(applicationManifest);
        putIfPresent(yaml, "services", applicationManifest.getServices());
        return yaml;
    }

    @SuppressWarnings("unchecked")
    private static List<ApplicationManifest> doRead(Path path, Map<String, String> variables) {
        Map<String, Object> root = deserialize(path);

        ApplicationManifest template = getTemplate(path, root, variables);

        return Optional.ofNullable(root.get("applications"))
                .map(value -> ((List<Map<String, Object>>) value).stream())
                .orElseGet(Stream::empty)
                .map(
                        application -> {
                            String name = getName(application);
                            return toApplicationManifest(
                                            application,
                                            variables,
                                            ApplicationManifest.builder().from(template),
                                            path)
                                    .name(name)
                                    .build();
                        })
                .collect(Collectors.toList());
    }
}
