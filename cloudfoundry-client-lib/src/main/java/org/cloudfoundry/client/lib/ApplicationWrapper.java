package org.cloudfoundry.client.lib;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Common abstraction through which application artifacts are accessed for processing before an
 * upload to Cloud Foundry.
 * The two implementations of this interface are the ZippedAppWrapper and the DirectoryBasedAppWrapper.
 *
 * @author A.B.Srinivasan
 *
 */
public interface ApplicationWrapper {

    /**
     * Generates a fingerprint of the application. A fingerprint is made up of a list of
     * Map entries, one for each file in the app. Each Map entry captures meta-data associated
     * with a file and consists of keys and values associated with the file's name,
     * its size and its SHA1 digest.
     * The generated fingerprint is used to limit the data contents uploaded for an app to
     * its delta file set i.e. the contents of files that are not present on the Cloud
     * Foundry target.
     *
     * @return - a list of Map entries, one for each file in the app, where each Map entry contains
     *           the keys ('fn', 'size', 'sha1') and associated values for the files name, its size and
     *           its SHA1 digest.
     * @throws IOException
     */
    public List<Map<String, Object>> generateFingerprint() throws IOException;

    /**
     * Returns the contents of application as a byte array. Contents of the named files in
     * the provided input are omitted from the returned payload .
     *
     * @param filesToOmit - a set of file names whose contents should not be included in the
     *        output.
     * @return - a byte array payload of the application contents.
     * @throws IOException
     */
    public byte[] getContents(Set<String> filesToOmit) throws IOException;

}
