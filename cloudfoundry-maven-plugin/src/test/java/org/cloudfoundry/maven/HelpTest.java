package org.cloudfoundry.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.DefaultArtifactResolver;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.cloudfoundry.maven.common.SystemProperties;

import java.io.File;
import java.util.List;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class HelpTest extends AbstractMojoTestCase {

    private Help help;
    private ArtifactResolver artifactResolver;

    protected void setUp() throws Exception {
        super.setUp();

        artifactResolver = mock(DefaultArtifactResolver.class);
        doThrow(ArtifactNotFoundException.class).when(artifactResolver).resolve(isA(Artifact.class), (List)isNull(), (ArtifactRepository)isNull());

        File testPom = new File(getBasedir(), "src/test/resources/test-pom.xml");

        Help unspiedMojo = (Help) lookupMojo("help", testPom);

        help = spy(unspiedMojo);

        Log log = mock(Log.class);
        doReturn(log).when(help).getLog();
        /**
         * Injecting some test values as expressions are not evaluated.
         */
        setVariableValueToObject(help, "artifactId", "cf-maven-tests");
    }

    public void testNoConfigurationRequired() {
        doReturn(null).when(help).getCommandlineProperty(isA(SystemProperties.class));

        try {
            help.execute();
        } catch (Exception e) {
            fail("Not expecting an exception being thrown, but got:" + e.getMessage());
        }
    }

    public void testArtifactNotRequired() throws Exception {
        doReturn("42").when(help).getCommandlineProperty(isA(SystemProperties.class));
        doReturn(null).when(help).getCommandlineProperty(SystemProperties.TARGET);
        doReturn(null).when(help).getCommandlineProperty(SystemProperties.PATH);

        setVariableValueToObject(help, "artifact", "com.cloudfoundry:java-cf-client:war:2.1.1");
        setVariableValueToObject(help, "artifactResolver", artifactResolver);

        try {
            help.execute();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Not expecting an exception being thrown, but got:" + e.getMessage());
        }
    }

}
