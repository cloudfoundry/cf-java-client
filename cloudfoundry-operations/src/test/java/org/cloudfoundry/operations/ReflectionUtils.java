package org.cloudfoundry.operations;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ReflectionUtils {

    private ReflectionUtils() { // do not instantiate this class
    }

    /**
     * Find implementations for a given interface type. Uses reflection.
     */
    public static <T> List<Class<? extends T>> findImplementations(Class<T> interfaceType) {
        try {
            ClassLoader classLoader = interfaceType.getClassLoader();

            String path = interfaceType.getPackage().getName().replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            ArrayList<URL> lr = Collections.list(resources);

            return lr.stream()
                    .flatMap(
                            url -> {
                                if (url.getProtocol().equals("jar")) {
                                    // Handle JAR URLs
                                    return scanJar(
                                            url,
                                            interfaceType.getPackage().getName(),
                                            interfaceType);
                                } else {
                                    return scanDirectory(
                                            new File(url.getFile()),
                                            interfaceType.getPackage().getName(),
                                            interfaceType);
                                }
                            })
                    .collect(Collectors.toList());
        } catch (Exception ignored) {

        }
        return Collections.emptyList();
    }

    /**
     * Find implementations for the given interface type in a source directory.
     */
    private static <T> Stream<Class<? extends T>> scanDirectory(
            File directory, String packageName, Class<T> interfaceType) {
        File[] files = directory.listFiles();
        if (files == null) {
            return Stream.empty();
        }

        Stream<Class<? extends T>> classes =
                Arrays.stream(files)
                        .filter(fileName -> fileName.getName().endsWith(".class"))
                        .map(
                                fileName ->
                                        packageName
                                                + '.'
                                                + fileName.getName().replaceAll("\\.class$", ""))
                        .<Class<? extends T>>map(
                                className ->
                                        getClassIfImplementsInterface(className, interfaceType))
                        .filter(Objects::nonNull);
        Stream<Class<? extends T>> directories =
                Arrays.stream(files)
                        .filter(File::isDirectory)
                        .flatMap(
                                fileName ->
                                        scanDirectory(
                                                fileName,
                                                packageName + "." + fileName.getName(),
                                                interfaceType));
        return Stream.concat(classes, directories);
    }

    /**
     * Find implementations for the given interface type in a packaged jar.
     * When running {@code mvn package}, class files are packaged in jar files,
     * and is not available directly on the filesystem.
     */
    private static <T> Stream<Class<? extends T>> scanJar(
            URL jarUrl, String packageName, Class<T> interfaceType) {
        try {
            JarURLConnection jarConnection = (JarURLConnection) jarUrl.openConnection();
            JarFile jarFile = jarConnection.getJarFile();
            String packagePath = packageName.replace('.', '/');

            return jarFile.stream()
                    .filter(
                            entry -> {
                                String name = entry.getName();
                                return name.startsWith(packagePath)
                                        && name.endsWith(".class")
                                        && !name.equals(packagePath + ".class");
                            })
                    .map(entry -> entry.getName().replace('/', '.').replaceAll("\\.class$", ""))
                    .<Class<? extends T>>map(
                            className -> getClassIfImplementsInterface(className, interfaceType))
                    .filter(Objects::nonNull);
        } catch (Exception e) {
            return Stream.empty();
        }
    }

    /**
     * Return the {@link Class} instance for {@code className}, if it implements {@code interfaceType}. Otherwise, return null.
     */
    private static <T> Class<? extends T> getClassIfImplementsInterface(
            String className, Class<T> interfaceType) {
        try {
            Class<?> clazz = Class.forName(className);
            if (interfaceType.isAssignableFrom(clazz)
                    && !clazz.isInterface()
                    && !Modifier.isAbstract(clazz.getModifiers())) {
                Class<? extends T> subclass = clazz.asSubclass(interfaceType);
                return subclass;
            }
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }
}
