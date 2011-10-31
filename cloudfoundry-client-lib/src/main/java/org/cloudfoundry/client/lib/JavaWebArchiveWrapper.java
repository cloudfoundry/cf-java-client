package org.cloudfoundry.client.lib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * A wrapper to access and process entries a Java web app archive (war) in preparation for its
 * upload to a Cloud Foundry target.
 *
 * @author A.B.Srinivasan
 *
 */
public class JavaWebArchiveWrapper implements ApplicationWrapper {

    private final ZipFile archive;

    JavaWebArchiveWrapper(String archive) throws IOException {
        this.archive = new ZipFile(archive);
    }

    public List<Map<String, Object>> generateResourcePayload()
    throws IOException {
        List<Map<String, Object>> payload = new ArrayList<Map<String, Object>>();
        Enumeration<? extends ZipEntry> entries = archive.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory()) {
                String sha1sum = CloudUtil.computeSha1Digest(archive.getInputStream(entry));
                Map<String, Object> entryPayload = new HashMap<String, Object>();
                entryPayload.put("size", entry.getSize());
                entryPayload.put("sha1", sha1sum);
                entryPayload.put("fn", entry.getName());
                payload.add(entryPayload);
            }
        }
        return payload;
    }

    public byte[] processMatchedResources(Set<String> matchedFileNames)
    throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ZipOutputStream zout = new ZipOutputStream(out);

        Enumeration<? extends ZipEntry> entries = archive.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!matchedFileNames.contains(entry.getName())) {
                zout.putNextEntry(new ZipEntry(entry.getName()));
                if (!entry.isDirectory()) {
                    InputStream in = archive.getInputStream(entry);
                    byte[] buffer = new byte[CloudUtil.BUFFER_SIZE];
                    while(true) {
                        int read = in.read(buffer);
                        if (read == -1) {
                            break;
                        }
                        zout.write(buffer, 0, read);
                    }
                    in.close();
                }
                zout.closeEntry();
            }
        }
        zout.close();

        return out.toByteArray();
    }

}
