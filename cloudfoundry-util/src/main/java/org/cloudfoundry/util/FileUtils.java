/*
 * Copyright 2013-2020 the original author or authors.
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
import org.apache.commons.compress.archivers.zip.ZipFile;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Utilities for files
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
     * Compresses a candidate {@link Path} if it is a directory.  Otherwise returns the original {@link Path}.
     *
     * @param candidate the candidate {@link Path} to compress
     * @return the {@link Path} for a compressed artifact
     */
    public static Mono<Path> compress(Path candidate) {
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
                    Path staging = Files.createTempFile(String.format("compressed-%s-", candidate.getFileName()), ".zip");

                    try (ZipArchiveOutputStream out = new ZipArchiveOutputStream(staging.toFile())) {
                        if (Files.isDirectory(candidate)) {
                            compressFromDirectory(candidate, filter, out);
                        } else {
                            compressFromZip(candidate, filter, out);
                        }
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
        try (InputStream in = Files.newInputStream(path)) {
            return hash(in);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    /**
     * Calculates the SHA-1 hash for an {@link InputStream}
     *
     * @param in the {@link InputStream} to calculate the hash for
     * @return {@link String} representation of the hash
     */
    public static String hash(InputStream in) {
        try {
            MessageDigest digest = MessageDigest.getInstance("sha1");

            ByteArrayPool.withByteArray(buffer -> {
                try {
                    int length;
                    while ((length = in.read(buffer)) != -1) {
                        digest.update(buffer, 0, length);
                    }
                } catch (IOException e) {
                    throw Exceptions.propagate(e);
                }
            });

            return String.format("%040x", new BigInteger(1, digest.digest()));
        } catch (NoSuchAlgorithmException e) {
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
            return permissions(getUnixMode(path));
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    /**
     * Calculates permissions for a UNIX mode
     *
     * @param mode the UNIX mode to calculate the permissions for
     * @return a {@link String} representation of the permissions
     */
    public static String permissions(int mode) {
        return Integer.toOctalString(mode == 0 ? DEFAULT_PERMISSIONS : mode);
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

    private static void compressFromDirectory(Path candidate, Predicate<String> filter, ZipArchiveOutputStream out) {
        try (Stream<Path> contents = Files.walk(candidate)) {
            contents
                .filter(path -> {
                    try {
                        return !Files.isSameFile(candidate, path);
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .filter(path -> filter.test(getRelativePathName(candidate, path)))
                .forEach(path -> {
                    try (InputStream in = Files.isDirectory(path) ? null : Files.newInputStream(path)) {
                        write(in, Files.getLastModifiedTime(path), getUnixMode(path), out, getRelativePathName(candidate, path));
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                });
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    private static void compressFromZip(Path candidate, Predicate<String> filter, ZipArchiveOutputStream out) {
        try (ZipFile zipFile = new ZipFile(candidate.toFile())) {
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();

            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();

                if (filter.test(entry.getName())) {
                    try (InputStream in = zipFile.getInputStream(entry)) {
                        int mode = entry.getUnixMode();
                        write(in, entry.getLastModifiedTime(), mode == 0 ? DEFAULT_PERMISSIONS : mode, out, entry.getName());
                    }
                }
            }
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    private static int getUnixMode(Path path) throws IOException {
        if (!isPosixFile(path)) {
            return DEFAULT_PERMISSIONS;
        }

        return Files.getPosixFilePermissions(path).stream()
            .mapToInt(PERMISSION_MODES::get)
            .sum();
    }

    private static boolean isPosixFile(Path path) {
        return path.getFileSystem().supportedFileAttributeViews().contains("posix");
    }

    private static void write(InputStream in, FileTime lastModifiedTime, int mode, ZipArchiveOutputStream out, String path) {
        try {
            ZipArchiveEntry entry = new ZipArchiveEntry(path);
            entry.setUnixMode(mode);
            entry.setLastModifiedTime(lastModifiedTime);
            out.putArchiveEntry(entry);

            if (in != null) {
                ByteArrayPool.withByteArray(buffer -> {
                    try {
                        int length;
                        while ((length = in.read(buffer)) != -1) {
                            out.write(buffer, 0, length);
                        }
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                });
            }

            out.closeArchiveEntry();
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

}
