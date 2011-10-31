package org.cloudfoundry.client.lib;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Common abstraction through which application artifacts are accessed and processed before an
 * upload to Cloud Foundry.
 * The two implementations of this interface are the ZippedAppWrapper and the DirectoryBasedAppWrapper.
 *
 * @author A.B.Srinivasan
 *
 */
public interface ApplicationWrapper {

    /**
     * Preps the app bits for introspection and subsequent processing.
     *
     * @throws IOException
     */
    public void prepApplication() throws IOException;

    /**
     * Generates a fingerprint of the application comprising of a list of <size, sha1 digest, name> tuples
     * for each file in the app. This fingerprint is the basis for determining the delta file set
     * of the app (comprising of entries not present on the Cloud Foundry target) that is to be sent
     * on an app upload.
     *
     * @return - a list of maps (one for each file in the app) where each map contains information on
     *           the size of the file, its SHA1 digest and its name.
     * @throws IOException
     */
    public List<Map<String, Object>> generateResourcePayload() throws IOException;

    /**
     * Deletes entries associated with the matchedFileNames and returns the remaining
     * entries as a byte array.
     *
     * @param matchedFileNames - a set of file names that can be removed from the app in preparation
     *        for its upload since those files are already present on the Cloud Foundry target.
     * @return - a byte array payload of the application comprising only of files not already
     *        present on the Cloud Foundry target.
     * @throws IOException
     */
    public byte[] processMatchedResources(Set<String> matchedFileNames) throws IOException;

    public InputStream getInputStream() throws IOException;

    public long getSize() throws IOException;

}
