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

package org.cloudfoundry.util;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import reactor.core.Exceptions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
     * Returns a relative and normalized name for a {@link Path}.  This method ensures that directories have a single trailing slash in their name
     *
     * @param root the root {@link Path} to relativize against
     * @param path the {@link Path} to get the name for
     * @return The relative and normalized name
     */
    public static String getRelativePathName(Path root, Path path) {
        Path relative = root.relativize(path);
        return Files.isDirectory(path) && !relative.toString().endsWith("/") ? String.format("%s/", relative.toString()) : relative.toString();
    }

    /**
     * Returns a normalized {@link Path}.  In the case of directories, it returns the {@link Path} as it was passed in.  In the case of files, it returns a {@link Path} representing the root of a
     * filesystem mounted using {@link FileSystems#newFileSystem}.
     *
     * @param path the {@link Path} to normalized
     * @return the normalized path
     */
    public static Path normalize(Path path) {
        try {
            return Files.isDirectory(path) ? path : FileSystems.newFileSystem(path, null).getPath("/");
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    /**
     * Converts a the contents of a {@link Path} to a {@link InputStream}.  If the {@link Path} is a directory, compresses the full contents of the directory into the stream.  If the {@link Path}
     * is a file, the contents of the file are examined using {@link FileSystems#newFileSystem} starting at the root.  This allows both exploded and compressed artifacts to be used interchangeably.
     *
     * @param path a {@link Path} representing either a compressed <i>or</i> exploded artifact
     * @return a {@link InputStream} containing the compressed contents of the {@code path}
     */
    public static InputStream toInputStream(Path path) {
        return toInputStream(path, p -> true);
    }

    /**
     * Converts a the contents of a {@link Path} to a {@link InputStream}.  If the {@link Path} is a directory, compresses the full contents of the directory into the stream.  If the {@link Path}
     * is a file, the contents of the file are examined using {@link FileSystems#newFileSystem} starting at the root.  This allows both exploded and compressed artifacts to be used interchangeably.
     *
     * @param path   a {@link Path} representing either a compressed <i>or</i> exploded artifact
     * @param filter a {@link Predicate} to filter the {@link Path}s that will be added to the {@link InputStream}
     * @return a {@link InputStream} containing the compressed contents of the {@code path}
     */
    public static InputStream toInputStream(Path path, Predicate<Path> filter) {
        Path root = normalize(path);

        try {
            Path staging = Files.createTempFile(null, null);

            try (Stream<Path> contents = Files.walk(root); ZipArchiveOutputStream out = new ZipArchiveOutputStream(staging.toFile())) {
                contents
                    .filter(filter)
                    .forEach(p -> write(root, p, out));
            }

            return Files.newInputStream(staging);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    private static int getUnixMode(Path path) throws IOException {
        return Optional.ofNullable(Files.readAttributes(path, PosixFileAttributes.class))
            .map(attributes -> attributes.permissions().stream()
                .map(PERMISSION_MODES::get)
                .collect(Collectors.summingInt(i -> i)))
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
