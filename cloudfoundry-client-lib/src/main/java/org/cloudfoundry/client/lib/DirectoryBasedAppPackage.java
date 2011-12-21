package org.cloudfoundry.client.lib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * A wrapper to access and process entries a directory based application (could be an exploded
 * Java web archive for instance) in preparation for its upload to a Cloud Foundry target.
 *
 * @author A.B.Srinivasan
 *
 */
public class DirectoryBasedAppPackage implements ApplicationPackage {

    private final String directoryPath;

    public DirectoryBasedAppPackage(String explodedDirPath) {
        this.directoryPath = explodedDirPath;
    }

    public List<Map<String, Object>> generateFingerprint() throws IOException {
        List<Map<String, Object>> payload = new ArrayList<Map<String, Object>>();
        return generateResourcePayload(directoryPath, payload);
    }

    public byte[] getContents(Set<String> matchedFileNames)
    throws IOException {
        return createZip(matchedFileNames);
    }

    private String getRelativePath(File file) throws IOException {
        String rootCanonicalPath = new File(directoryPath).getAbsolutePath() + File.separator;
        String fileCanonicalPath = file.getAbsolutePath();
        return fileCanonicalPath.substring(rootCanonicalPath.length());
    }

    private List<Map<String, Object>> generateResourcePayload(String dir, List<Map<String, Object>> payload) throws IOException {
        File explodeDir = new File(dir);
        File[] entries = explodeDir.listFiles();
        for (File entry: entries) {
            if (!entry.isDirectory()) {
                String sha1sum = CloudUtil.computeSha1Digest(new FileInputStream(entry));
                Map<String, Object> entryPayload = new HashMap<String, Object>();
                entryPayload.put("size", entry.length());
                entryPayload.put("sha1", sha1sum);
                entryPayload.put("fn", getRelativePath(entry));
                payload.add(entryPayload);
            } else {
                payload = generateResourcePayload(entry.getAbsolutePath(), payload);
            }
        }
        return payload;
    }

    private byte[] createZip(Set<String> matchedFileNames) throws IOException {
        File dirToZip = new File(directoryPath);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(out);
        try {
            zipDir(dirToZip, zos, null, matchedFileNames);
        } finally {
            zos.flush();
            if (out.size() > 0) {
                zos.close();
            }
        }
        return out.toByteArray();
    }

    private void zipDir(File dirOrFileToZip, ZipOutputStream zos, String path, Set<String> matchedFileNames) throws IOException {
        if (dirOrFileToZip.isDirectory()) {
          String subPath = createDirZipEntry(dirOrFileToZip, zos, path);
          String[] dirList = dirOrFileToZip.list();
          for (int i = 0; i < dirList.length; i++) {
            File f = new File(dirOrFileToZip, dirList[i]);
            zipDir(f, zos, subPath, matchedFileNames);
          }
        } else {
            if (!matchedFileNames.contains(getRelativePath(dirOrFileToZip))) {
                createZipFileEntry(dirOrFileToZip, zos, path);
            }
        }
      }

      private void createZipFileEntry(File dirOrFileToZip, ZipOutputStream zos,
              String path) throws FileNotFoundException,
              IOException {
          int count;
          byte[] buffer = new byte[CloudUtil.BUFFER_SIZE];

          FileInputStream fis = new FileInputStream(dirOrFileToZip);
            try {
                ZipEntry entry = new ZipEntry(path + dirOrFileToZip.getName());
                entry.setTime(dirOrFileToZip.lastModified());
                zos.putNextEntry(entry);
                while ((count = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, count);
                }
                zos.flush();
                zos.closeEntry();
            } finally {
                fis.close();
            }
      }

      private String createDirZipEntry(File dirOrFileToZip, ZipOutputStream zos,
              String path) throws IOException {
          String subPath =
              (path == null) ? "" : (path + dirOrFileToZip.getName() + '/');
          if (path != null) {
            ZipEntry ze = new ZipEntry(subPath);
            ze.setTime(dirOrFileToZip.lastModified());
            zos.putNextEntry(ze);
            zos.flush();
            zos.closeEntry();
          }
          return subPath;
      }

}
