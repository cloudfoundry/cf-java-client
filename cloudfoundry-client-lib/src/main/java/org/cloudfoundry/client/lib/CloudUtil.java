/*
 * Copyright 2009-2011 the original author or authors.
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

package org.cloudfoundry.client.lib;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Some helper utilities used by the Cloud Foundry Java client.
 *
 * @author Ramnivas Laddad
 * @author A.B.Srinivasan
 *
 */
public class CloudUtil {

    public static final int BUFFER_SIZE = 16 * 1024;

        private static Double DEFAULT_DOUBLE = new Double(0.0);

        private static Integer DEFAULT_INTEGER = new Integer(0);

        private static Long DEFAULT_LONG = new Long(0);

        private static final String HEX_CHARS = "0123456789ABCDEF";

        private static final char EXTENSION_SEPARATOR = '.';

        public static String parse(Object object) {
                return parse(String.class, object);
        }

        @SuppressWarnings("unchecked")
        public static <T> T parse(Class<T> clazz, Object object) {
                T defaultValue = null;
                try {
                        if (clazz == Date.class) {
                                String stringValue = parse(String.class, object);
                                return clazz.cast(new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy",
                                                Locale.US).parse(stringValue));
                        }

                        if (clazz == Integer.class) {
                                defaultValue = (T) DEFAULT_INTEGER;
                        } else if (clazz == Long.class) {
                                defaultValue = (T) DEFAULT_LONG;
                        } else if (clazz == Double.class) {
                                defaultValue = (T) DEFAULT_DOUBLE;
                        }

                        if (object == null) {
                                return defaultValue;
                        }

                        // special handling for int and long since smaller numbers become ints
                        // but may be requested as long and vice versa
                        if (clazz == Integer.class) {
                                return clazz.cast(Integer.valueOf(((Number)object).intValue()));
                        }
                        if (clazz == Long.class) {
                                return clazz.cast(Long.valueOf(((Number)object).longValue()));
                        }

                        return clazz.cast(object);
                } catch (ClassCastException e) {
                        // ignore
                } catch (ParseException e) {
                        // ignore
                }
                return defaultValue;
        }

        public static void unpackWar(String warFile, String destDir) throws IOException {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(warFile));
            try {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (entry.isDirectory()) {
                        unpackDir(destDir, entry);
                    } else {
                        unpackFile(destDir, entry, zis);
                    }
                }
            } finally {
                if (zis != null) {
                    zis.close();
                }
            }
        }

        public static boolean isWar(String filePath) {
            return "war".equalsIgnoreCase(extension(filePath));
        }

    public static String computeSha1Digest(InputStream in) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] buffer = new byte[CloudUtil.BUFFER_SIZE];
        while(true) {
            int read = in.read(buffer);
            if (read == -1) {
                break;
            }
            digest.update(buffer, 0, read);
        }
        in.close();
        return bytesToHex(digest.digest());
    }

        private static String bytesToHex(byte[] bytes) {
            if (bytes == null) {
                return null;
            }
            final StringBuilder hex = new StringBuilder(2 * bytes.length);
            for (final byte b : bytes) {
                hex.append(HEX_CHARS.charAt((b & 0xF0) >> 4)).append(HEX_CHARS.charAt((b & 0x0F)));
            }
            return hex.toString();
        }

        private static void unpackDir(String destDir, ZipEntry entry) {
            File dir = new File(destDir, entry.getName());
            dir.mkdir();
            if (entry.getTime() != -1) {
                dir.setLastModified(entry.getTime());
            }
        }

        private static void unpackFile(String destDir, ZipEntry entry, ZipInputStream zis) throws IOException {
            BufferedOutputStream dest = null;
            int count;
            byte data[] = new byte[BUFFER_SIZE];
            File destFile;
            try {
                destFile = new File(destDir, entry.getName());
                FileOutputStream fos = new FileOutputStream(destFile);
                dest = new BufferedOutputStream(fos, BUFFER_SIZE);
                while ((count = zis.read(data, 0, BUFFER_SIZE)) != -1) {
                    dest.write(data, 0, count);
                }
                if (entry.getTime() != -1) {
                    destFile.setLastModified(entry.getTime());
                }
            } finally {
                if (dest != null) {
                    dest.flush();
                    dest.close();
                }
            }
        }

        private static String extension(String filePath) {
            int dot = filePath.lastIndexOf(EXTENSION_SEPARATOR);
            return filePath.substring(dot + 1);
        }

}
