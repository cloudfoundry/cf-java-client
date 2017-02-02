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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Utilities for {@link FileSystem}s
 */
public final class FileUtils {

    private static final Integer DEFAULT_PERMISSIONS = 0744;

    private static final int MIBIBYTE = 1_024 * 1_024;

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
     * Compresses a candidate {@link Path} if it is a directory.  Otherwise returns the original {@link Path}.
     *
     * @param candidate the candidate {@link Path} to compress
     * @return the {@link Path} for a compressed artifact
     */
    public static Mono<Path> compress(Path candidate) {
        if (!Files.isDirectory(candidate)) {
            return Mono.just(candidate);
        }

        return compress(candidate, path -> true);
    }

    /**
     * Compresses a candidate {@link Path} filtering out entries
     *
     * @param candidate the candidate {@link Path} to compress
     * @param filter    a filter applied to each path
     * @return the {@link Path} for a compressed artifact
     */
    public static Mono<Path> compress(Path candidate, Predicate<String> filter) {
        return Mono
            .defer(() -> {
                try {
                    Path staging = Files.createTempFile(null, null);

                    try (Stream<Path> contents = Files.walk(candidate); ZipArchiveOutputStream out = new ZipArchiveOutputStream(staging.toFile())) {
                        contents
                            .filter(path -> filter.test(getRelativePathName(candidate, path)))
                            .forEach(p -> write(candidate, p, out));
                    }

                    return Mono.just(staging);
                } catch (IOException e) {
                    throw Exceptions.propagate(e);
                }
            })
            .subscribeOn(Schedulers.elastic());
    }

    /**
     * Get the relative path of an application
     *
     * @param root the root to relativize against
     * @param path the path to relativize
     * @return the relative path
     */
    public static String getRelativePathName(Path root, Path path) {
        Path relative = root.relativize(path);
        return Files.isDirectory(path) && !relative.toString().endsWith("/") ? String.format("%s/", relative.toString()) : relative.toString();
    }

    /**
     * Calculates the SHA-1 hash for a {@link Path}
     *
     * @param path the {@link Path} to calculate the hash for
     * @return a {@link String} representation of the hash
     */
    public static String hash(Path path) {
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
            MessageDigest digest = MessageDigest.getInstance("sha1");

            ByteBuffer buffer = ByteBuffer.allocateDirect(MIBIBYTE);
            while (fileChannel.read(buffer) != -1) {
                buffer.flip();
                digest.update(buffer);
                buffer.clear();
            }

            return String.format("%040x", new BigInteger(1, digest.digest()));
        } catch (IOException | NoSuchAlgorithmException e) {
            throw Exceptions.propagate(e);
        }
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
     * Calculates permissions for a {@link Path}
     *
     * @param path the {@link Path} to calculate the permissions for
     * @return a {@link String} representation of the permissions
     */
    public static String permissions(Path path) {
        try {
            return Integer.toOctalString(getUnixMode(path));
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    /**
     * Calculates the size of a {@link Path}
     *
     * @param path the {@link Path} to calculate the size for
     * @return the size
     */
    public static int size(Path path) {
        try {
            return (int) Files.size(path);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
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
