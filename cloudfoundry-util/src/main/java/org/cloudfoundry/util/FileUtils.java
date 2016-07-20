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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Exceptions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utilities for {@link FileSystem}s
 */
public final class FileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {
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
        Path root = normalize(path);

        try {
            Path staging = Files.createTempFile(null, null);

            try (Stream<Path> contents = Files.walk(root); ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(staging))) {
                contents.forEach(p -> write(root, p, out));
            }

            return Files.newInputStream(staging);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    private static String getName(Path path) {
        return Files.isDirectory(path) && !path.toString().endsWith("/") ? String.format("%s/", path.toString()) : path.toString();
    }

    private static void write(Path root, Path path, ZipOutputStream out) {
        try {
            if (Files.isSameFile(root, path)) {
                return;
            }

            ZipEntry entry = new ZipEntry(getName(root.relativize(path)));
            entry.setLastModifiedTime(Files.getLastModifiedTime(path));
            out.putNextEntry(entry);

            if (Files.isRegularFile(path)) {
                Files.copy(path, out);
            }

            out.closeEntry();
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

}
