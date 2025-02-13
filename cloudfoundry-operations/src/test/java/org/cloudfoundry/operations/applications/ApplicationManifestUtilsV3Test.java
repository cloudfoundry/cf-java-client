package org.cloudfoundry.operations.applications;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ApplicationManifestUtilsV3Test {
    @Test
    void testGenericApplication() throws IOException {
        ManifestV3 manifest =
                ManifestV3.builder()
                        .application(
                                ManifestV3Application.builder()
                                        .name("test-app")
                                        .buildpack("test-buildpack")
                                        .command("test-command")
                                        .disk(512)
                                        .healthCheckHttpEndpoint("test-health-check-http-endpoint")
                                        .instances(2)
                                        .memory(512)
                                        .randomRoute(true)
                                        .stack("test-stack")
                                        .timeout(120)
                                        .environmentVariable("TEST_KEY_1", "test-value-1")
                                        .service(
                                                ManifestV3Service.builder()
                                                        .name("test-service-1")
                                                        .build())
                                        .build())
                        .build();

        assertSerializeDeserialize(manifest);
    }

    @Test
    void testWithDockerApp() throws IOException {
        ManifestV3 manifest =
                ManifestV3.builder()
                        .application(
                                ManifestV3Application.builder()
                                        .name("test-app")
                                        .docker(Docker.builder().image("test-image").build())
                                        .build())
                        .build();

        assertSerializeDeserialize(manifest);
    }

    private void assertSerializeDeserialize(ManifestV3 manifest) throws IOException {
        Path file = Files.createTempFile("test-manifest-", ".yml");
        ApplicationManifestUtilsV3.write(file, manifest);
        ManifestV3 read = ApplicationManifestUtilsV3.read(file);

        assertEquals(manifest, read);
    }
}
