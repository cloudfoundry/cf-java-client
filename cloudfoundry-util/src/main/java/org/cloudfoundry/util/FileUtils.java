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

package org.cloudfoundry.util;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import reactor.core.Exceptions;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Utilities for {@link FileSystem}s
 */
public final class FileUtils {

    private static final Integer DEFAULT_PERMISSIONS = 0744;

    private static final Map<PosixFilePermission, Integer> PERMISSION_MODES = FluentMap.<PosixFilePermission, Integer>builder()
        .entry(PosixFilePermission.OWNER_READ, 0400)
        .entry(PosixFilePermission.OWNER_WRITE, 0200)
        .entry(PosixFilePermission.OWNER_EXECUTE, 0100)
        .entry(PosixFilePermission.GROUP_READ, 0040)
        .entry(PosixFilePermission.GROUP_WRITE, 0020)
        .entry(PosixFilePermission.GROUP_EXECUTE, 0010)
        .entry(PosixFilePermission.OTHERS_READ, 0004)
        .entry(PosixFilePermission.OTHERS_WRITE, 0002)
        .entry(PosixFilePermission.OTHERS_EXECUTE, 0001)
        .build();

    private FileUtils() {
    }

    /**
     * Compresses a candidate {@link Path}if it is a directory.  Otherwise returns the original {@link Path}.
     *
     * @param candidate the candidate {@link Path} to compress
     * @return the {@link Path} for a compressed artifact
     */
    public static Path compress(Path candidate) {
        if (!Files.isDirectory(candidate)) {
            return candidate;
        }

        try {
            Path staging = Files.createTempFile(null, null);

            try (Stream<Path> contents = Files.walk(candidate); ZipArchiveOutputStream out = new ZipArchiveOutputStream(staging.toFile())) {
                contents
                    .forEach(p -> write(candidate, p, out));
            }

            return staging;
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    private static String getRelativePathName(Path root, Path path) {
        Path relative = root.relativize(path);
        return Files.isDirectory(path) && !relative.toString().endsWith("/") ? String.format("%s/", relative.toString()) : relative.toString();
    }

    private static int getUnixMode(Path path) throws IOException {
        return Optional.ofNullable(Files.readAttributes(path, PosixFileAttributes.class))
            .map(attributes -> attributes.permissions().stream()
                .map(PERMISSION_MODES::get)
                .mapToInt(i -> i)
                .sum())
            .orElse(DEFAULT_PERMISSIONS);
    }

    private static void write(Path root, Path path, ZipArchiveOutputStream out) {
        try {
            if (Files.isSameFile(root, path)) {
                return;
            }

            ZipArchiveEntry entry = new ZipArchiveEntry(getRelativePathName(root, path));
            entry.setUnixMode(getUnixMode(path));
            entry.setLastModifiedTime(Files.getLastModifiedTime(path));
            out.putArchiveEntry(entry);

            if (Files.isRegularFile(path)) {
                Files.copy(path, out);
            }

            out.closeArchiveEntry();
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

}
