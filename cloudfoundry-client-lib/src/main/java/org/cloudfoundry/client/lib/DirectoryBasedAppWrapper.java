package org.cloudfoundry.client.lib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

/**
 * A wrapper to access and process entries a directory based application (could be an exploded
 * Java web archive for instance) in preparation for its upload to a Cloud Foundry target.
 *
 * @author A.B.Srinivasan
 *
 */
public class DirectoryBasedAppWrapper implements ApplicationWrapper {

    private final String file;
    private final String explodedDirPath;

    DirectoryBasedAppWrapper(String appName, String file) throws IOException {
        this.file = file;
        this.explodedDirPath = FileUtils.getTempDirectory().getCanonicalPath() +
            "/.vmc_java_" + appName + "_files";
    }

    public void prepApplication() throws IOException {
        File explodedDir = new File(explodedDirPath);
        if (explodedDir.exists()) {
            FileUtils.forceDelete(explodedDir);
        }
        File path = new File(file);
        if (path.getCanonicalFile().isDirectory()) {
            copyDirToExplodedDir(path, explodedDirPath);
        } else {
            FileUtils.copyFileToDirectory(path, explodedDir);
        }
    }

    public List<Map<String, Object>> generateResourcePayload() throws IOException {
        List<Map<String, Object>> payload = new ArrayList<Map<String, Object>>();
        return generateResourcePayload(explodedDirPath, explodedDirPath, payload);
    }

    public byte[] processMatchedResources(Set<String> matchedFileNames)
    throws IOException {
        deleteMatchedFiles(explodedDirPath, explodedDirPath, matchedFileNames);
        return createZip();
    }

    private byte[] appBytes = null;
    public InputStream getInputStream() throws IOException {
        if (appBytes != null) {
            appBytes = createZip();
        }
        return new ByteArrayInputStream(appBytes);
    }

    public long getSize() throws IOException {
        if (appBytes != null) {
            appBytes = createZip();
        }
        return appBytes.length;
    }

    private void copyDirToExplodedDir(File path, String explodedDirPath) throws IOException {
        File explodeDir = new File(explodedDirPath);
        File[] fileList = path.getCanonicalFile().listFiles();
        for (File fileToBeCopied: fileList) {
            if (fileToBeCopied.getCanonicalFile().isDirectory() &&
                !fileToBeCopied.getName().equals(".git")) {
                    FileUtils.copyDirectoryToDirectory(fileToBeCopied, explodeDir);
            } else {
                FileUtils.copyFileToDirectory(fileToBeCopied, explodeDir);
            }
        }
    }

    private List<Map<String, Object>> generateResourcePayload(String root, String dir, List<Map<String, Object>> payload) throws IOException {
        File explodeDir = new File(dir);
        File[] entries = explodeDir.listFiles();
        for (File entry: entries) {
            if (!entry.isDirectory()) {
                String sha1sum = CloudUtil.computeSha1Digest(new FileInputStream(entry));
                Map<String, Object> entryPayload = new HashMap<String, Object>();
                entryPayload.put("size", entry.length());
                entryPayload.put("sha1", sha1sum);
                entryPayload.put("fn", entry.getAbsolutePath().replaceFirst(root + File.separator, ""));
                payload.add(entryPayload);
            } else {
                payload = generateResourcePayload(root, entry.getAbsolutePath(), payload);
            }
        }
        return payload;
    }

    private void deleteMatchedFiles(String root, String explodeDirPath,
            Set<String> matchedFileNames) throws IOException {
        File explodeDir = new File(explodeDirPath);
        File[] entries = explodeDir.listFiles();
        Set<File> deletedFiles = new HashSet<File>();
        for (File entry: entries) {
            if (entry.isDirectory()) {
                deleteMatchedFiles(root, entry.getCanonicalPath(), matchedFileNames);
            } else {
                if (entry.isFile() && matchedFileNames.contains(entry.getAbsolutePath().replaceFirst(root + File.separator, ""))) {
                    deletedFiles.add(entry);
                }
            }
        }
        if (deletedFiles.size() > 0) {
            for (File deletedFile:deletedFiles) {
                FileUtils.forceDelete(deletedFile);
            }
        }
    }

    private byte[] createZip() throws IOException {
        File dirToZip = new File(explodedDirPath);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(out);
        try {
            zipDir(dirToZip, zos, null);
        } finally {
            if (zos != null) {
                zos.flush();
                zos.close();
            }
        }
        appBytes = out.toByteArray();
        return appBytes;
    }

    private void zipDir(File dirOrFileToZip, ZipOutputStream zos, String path) throws IOException {
        if (dirOrFileToZip.isDirectory()) {
          String subPath = createDirZipEntry(dirOrFileToZip, zos, path);
          String[] dirList = dirOrFileToZip.list();
          for (int i = 0; i < dirList.length; i++) {
            File f = new File(dirOrFileToZip, dirList[i]);
            zipDir(f, zos, subPath);
          }
        } else {
            createZipFileEntry(dirOrFileToZip, zos, path);
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
