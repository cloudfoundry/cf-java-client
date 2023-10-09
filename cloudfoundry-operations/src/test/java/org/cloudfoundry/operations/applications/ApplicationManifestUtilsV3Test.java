/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.operations.applications;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.cloudfoundry.operations.applications.ApplicationHealthCheck.NONE;
import static org.cloudfoundry.operations.applications.ApplicationHealthCheck.PORT;
import static org.junit.Assume.assumeTrue;

public final class ApplicationManifestUtilsV3Test {

    @Test
    public void anchorsAndReferences() throws IOException {
        ManifestV3 expected =
            ManifestV3.builder()
                .application(ManifestV3Application.builder()
                .name("test-application")
                .service(ManifestV3Service.builder().name("test-service-name").build())
                    .build())
                .build();

        ManifestV3 actual = ApplicationManifestUtilsV3.read(new ClassPathResource("fixtures/manifest-kilo.yml").getFile().toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void readCommon() throws IOException {
        ManifestV3 expected = ManifestV3.builder().applications(
            ManifestV3Application.builder()
                .name("charlie-application-1")
                .buildpack("charlie-buildpack")
                .command("charlie-command")
                .disk(-1)
                .healthCheckHttpEndpoint("charlie-health-check-http-endpoint")
                .healthCheckType(NONE)
                .instances(-1)
                .memory(-1)
                .noRoute(true)
                .path(Paths.get("/charlie-path").toAbsolutePath())
                .randomRoute(true)
                .route(Route.builder()
                    .route("charlie-route-1")
                    .build())
                .route(Route.builder()
                    .route("charlie-route-2")
                    .build())
                .stack("charlie-stack")
                .timeout(-1)
                .environmentVariable("CHARLIE_KEY_1", "charlie-value-1")
                .environmentVariable("CHARLIE_KEY_2", "charlie-value-2")
                .service(ManifestV3Service.builder().name("charlie-instance-1").build())
                .service(ManifestV3Service.builder().name("charlie-instance-2").build())
                .build(),
            ManifestV3Application.builder()
                .name("charlie-application-2")
                .buildpacks("charlie-buildpack", "alternate-buildpack")
                .command("alternate-command")
                .disk(-2)
                .healthCheckHttpEndpoint("alternate-health-check-http-endpoint")
                .healthCheckType(PORT)
                .instances(-2)
                .memory(-2)
                .noRoute(false)
                .path(Paths.get("/alternate-path").toAbsolutePath())
                .randomRoute(false)
                .route(Route.builder()
                    .route("charlie-route-1")
                    .build())
                .route(Route.builder()
                    .route("charlie-route-2")
                    .build())
                .route(Route.builder()
                    .route("alternate-route-1")
                    .build())
                .route(Route.builder()
                    .route("alternate-route-2")
                    .build())
                .stack("alternate-stack")
                .timeout(-2)
                .environmentVariable("CHARLIE_KEY_1", "charlie-value-1")
                .environmentVariable("CHARLIE_KEY_2", "charlie-value-2")
                .environmentVariable("ALTERNATE_KEY_1", "alternate-value-1")
                .environmentVariable("ALTERNATE_KEY_2", "alternate-value-2")
                .service(ManifestV3Service.builder().name("charlie-instance-1").build())
                .service(ManifestV3Service.builder().name("charlie-instance-2").build())
                .service(ManifestV3Service.builder().name("alternate-instance-1").build())
                .service(ManifestV3Service.builder().name("alternate-instance-2").build())
                .build()).build();

        ManifestV3 actual = ApplicationManifestUtilsV3.read(new ClassPathResource("fixtures/manifest-charlie.yml").getFile().toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void readCommonAndInherit() throws IOException {
        ManifestV3 expected = ManifestV3.builder().applications(
            ManifestV3Application.builder()
                .name("charlie-application-1")
                .buildpacks("charlie-buildpack", "delta-buildpack")
                .command("delta-command")
                .disk(-3)
                .healthCheckHttpEndpoint("delta-health-check-http-endpoint")
                .healthCheckType(NONE)
                .instances(-3)
                .memory(-3)
                .noRoute(true)
                .path(Paths.get("/delta-path").toAbsolutePath())
                .randomRoute(true)
                .route(Route.builder()
                    .route("charlie-route-1")
                    .build())
                .route(Route.builder()
                    .route("charlie-route-2")
                    .build())
                .route(Route.builder()
                    .route("delta-route-1")
                    .build())
                .route(Route.builder()
                    .route("delta-route-2")
                    .build())
                .stack("delta-stack")
                .timeout(-3)
                .environmentVariable("CHARLIE_KEY_1", "charlie-value-1")
                .environmentVariable("CHARLIE_KEY_2", "charlie-value-2")
                .environmentVariable("DELTA_KEY_1", "delta-value-1")
                .environmentVariable("DELTA_KEY_2", "delta-value-2")
                .service(ManifestV3Service.builder().name("charlie-instance-1").build())
                .service(ManifestV3Service.builder().name("charlie-instance-2").build())
                .service(ManifestV3Service.builder().name("delta-instance-1").build())
                .service(ManifestV3Service.builder().name("delta-instance-2").build())
                .build(),
            ManifestV3Application.builder()
                .name("charlie-application-2")
                .buildpacks("charlie-buildpack", "delta-buildpack", "alternate-buildpack")
                .command("alternate-command")
                .disk(-2)
                .healthCheckHttpEndpoint("alternate-health-check-http-endpoint")
                .healthCheckType(PORT)
                .instances(-2)
                .memory(-2)
                .noRoute(false)
                .path(Paths.get("/alternate-path").toAbsolutePath())
                .randomRoute(false)
                .route(Route.builder()
                    .route("charlie-route-1")
                    .build())
                .route(Route.builder()
                    .route("charlie-route-2")
                    .build())
                .route(Route.builder()
                    .route("delta-route-1")
                    .build())
                .route(Route.builder()
                    .route("delta-route-2")
                    .build())
                .route(Route.builder()
                    .route("alternate-route-1")
                    .build())
                .route(Route.builder()
                    .route("alternate-route-2")
                    .build())
                .stack("alternate-stack")
                .timeout(-2)
                .environmentVariable("CHARLIE_KEY_1", "charlie-value-1")
                .environmentVariable("CHARLIE_KEY_2", "charlie-value-2")
                .environmentVariable("DELTA_KEY_1", "delta-value-1")
                .environmentVariable("DELTA_KEY_2", "delta-value-2")
                .environmentVariable("ALTERNATE_KEY_1", "alternate-value-1")
                .environmentVariable("ALTERNATE_KEY_2", "alternate-value-2")
                .service(ManifestV3Service.builder().name("charlie-instance-1").build())
                .service(ManifestV3Service.builder().name("charlie-instance-2").build())
                .service(ManifestV3Service.builder().name("delta-instance-1").build())
                .service(ManifestV3Service.builder().name("delta-instance-2").build())
                .service(ManifestV3Service.builder().name("alternate-instance-1").build())
                .service(ManifestV3Service.builder().name("alternate-instance-2").build())
                .build()).build();

        ManifestV3 actual = ApplicationManifestUtilsV3.read(new ClassPathResource("fixtures/manifest-delta.yml").getFile().toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void readDocker() throws IOException {
        ManifestV3 expected = ManifestV3.builder().application(
            ManifestV3Application.builder()
                .name("lima-application-1")
                .docker(Docker.builder()
                    .image("lima-docker-image")
                    .password("lima-docker-password")
                    .username("lima-docker-username")
                    .build())
                .healthCheckHttpEndpoint("lima-health-check-http-endpoint")
                .healthCheckType(NONE)
                .noRoute(false)
                .build()).build();

        ManifestV3 actual = ApplicationManifestUtilsV3.read(new ClassPathResource("fixtures/manifest-lima.yml").getFile().toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void readInherit() throws IOException {
        ManifestV3 expected = ManifestV3.builder().applications(
            ManifestV3Application.builder()
                .name("alpha-application-1")
                .buildpacks("alpha-buildpack", "beta-buildpack")
                .command("beta-command")
                .disk(-2)
                .healthCheckHttpEndpoint("beta-health-check-http-endpoint")
                .healthCheckType(NONE)
                .instances(-2)
                .memory(2)
                .noRoute(false)
                .path(Paths.get("/beta-path").toAbsolutePath())
                .randomRoute(false)
                .route(Route.builder()
                    .route("alpha-route-1")
                    .build())
                .route(Route.builder()
                    .route("alpha-route-2")
                    .build())
                .route(Route.builder()
                    .route("beta-route-1")
                    .build())
                .route(Route.builder()
                    .route("beta-route-2")
                    .build())
                .stack("beta-stack")
                .timeout(-2)
                .environmentVariable("ALPHA_KEY_1", "alpha-value-1")
                .environmentVariable("ALPHA_KEY_2", "alpha-value-2")
                .environmentVariable("BETA_KEY_1", "beta-value-1")
                .environmentVariable("BETA_KEY_2", "beta-value-2")
                .service(ManifestV3Service.builder().name("alpha-instance-1").build())
                .service(ManifestV3Service.builder().name("alpha-instance-2").build())
                .service(ManifestV3Service.builder().name("beta-instance-1").build())
                .service(ManifestV3Service.builder().name("beta-instance-2").build())
                .build(),
            ManifestV3Application.builder()
                .name("beta-application-1")
                .buildpack("beta-buildpack")
                .command("beta-command")
                .disk(-1)
                .healthCheckHttpEndpoint("beta-health-check-http-endpoint")
                .healthCheckType(NONE)
                .instances(-1)
                .memory(2048)
                .noRoute(true)
                .path(Paths.get("/beta-path").toAbsolutePath())
                .randomRoute(true)
                .route(Route.builder()
                    .route("beta-route-1")
                    .build())
                .route(Route.builder()
                    .route("beta-route-2")
                    .build())
                .stack("beta-stack")
                .timeout(-1)
                .environmentVariable("BETA_KEY_1", "beta-value-1")
                .environmentVariable("BETA_KEY_2", "beta-value-2")
                .service(ManifestV3Service.builder().name("beta-instance-1").build())
                .service(ManifestV3Service.builder().name("beta-instance-2").build())
                .build(),
            ManifestV3Application.builder()
                .name("beta-application-2")
                .buildpack("beta-buildpack")
                .command("beta-command")
                .disk(-1)
                .domain("beta-domain")
                .domain("beta-domains-1")
                .domain("beta-domains-2")
                .healthCheckHttpEndpoint("beta-health-check-http-endpoint")
                .healthCheckType(NONE)
                .host("beta-host")
                .host("beta-hosts-1")
                .host("beta-hosts-2")
                .instances(-1)
                .memory(2)
                .noHostname(true)
                .noRoute(true)
                .path(Paths.get("/beta-path").toAbsolutePath())
                .randomRoute(true)
                .stack("beta-stack")
                .timeout(-1)
                .environmentVariable("BETA_KEY_1", "beta-value-1")
                .environmentVariable("BETA_KEY_2", "beta-value-2")
                .service(ManifestV3Service.builder().name("beta-instance-1").build())
                .service(ManifestV3Service.builder().name("beta-instance-2").build())
                .build()).build();

        ManifestV3 actual = ApplicationManifestUtilsV3.read(new ClassPathResource("fixtures/manifest-beta.yml").getFile().toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void readInheritCommon() throws IOException {
        ManifestV3 expected = ManifestV3.builder().application(
            ManifestV3Application.builder()
                .name("juliet-application")
                .buildpack("indigo-buildpack")
                .command("indigo-command")
                .disk(-1)
                .healthCheckHttpEndpoint("indigo-health-check-http-endpoint")
                .healthCheckType(NONE)
                .instances(-1)
                .memory(-1)
                .noRoute(true)
                .path(Paths.get("/indigo-path").toAbsolutePath())
                .randomRoute(true)
                .route(Route.builder()
                    .route("indigo-route-1")
                    .build())
                .route(Route.builder()
                    .route("indigo-route-2")
                    .build())
                .stack("indigo-stack")
                .timeout(-1)
                .environmentVariable("INDIGO_KEY_1", "indigo-value-1")
                .environmentVariable("INDIGO_KEY_2", "indigo-value-2")
                .service(ManifestV3Service.builder().name("indigo-instance-1").build())
                .service(ManifestV3Service.builder().name("indigo-instance-2").build())
                .build()).build();

        ManifestV3 actual = ApplicationManifestUtilsV3.read(new ClassPathResource("fixtures/manifest-juliet.yml").getFile().toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void readNoApplications() throws IOException {
        assertThatThrownBy(()->ApplicationManifestUtilsV3.read(new ClassPathResource("fixtures/manifest-hotel.yml").getFile().toPath()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("At least one application is required");
    }

    @Test(expected = IllegalStateException.class)
    public void readNoName() throws IOException {
        ApplicationManifestUtilsV3.read(new ClassPathResource("fixtures/manifest-foxtrot.yml").getFile().toPath());
    }

    @Test(expected = IllegalStateException.class)
    public void readNoRoute() throws IOException {
        ApplicationManifestUtilsV3.read(new ClassPathResource("fixtures/manifest-golf.yml").getFile().toPath());
    }

    @Test
    public void relativePath() throws IOException {
        Path root = new ClassPathResource("fixtures/manifest-november.yml").getFile().toPath();

        ManifestV3 expected = ManifestV3.builder().application(
            ManifestV3Application.builder()
                .name("alpha-application-1")
                .path(root.getParent().resolve("alpha-path"))
                .build()).build();

        ManifestV3 actual = ApplicationManifestUtilsV3.read(root);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testDiskQuotaAndMemoryParsing() throws Exception {
        ManifestV3 expected = ManifestV3.builder().applications(
            ManifestV3Application.builder()
                .name("quota-test-1")
                .memory(1)
                .disk(2)
                .build(),
            ManifestV3Application.builder()
                .name("quota-test-2")
                .memory(3)
                .disk(4)
                .build(),
            ManifestV3Application.builder()
                .name("quota-test-3")
                .memory(5)
                .disk(6)
                .build(),
            ManifestV3Application.builder()
                .name("quota-test-4")
                .memory(7)
                .disk(8)
                .build(),
            ManifestV3Application.builder()
                .name("quota-test-5")
                .memory(1024)
                .disk(2048)
                .build(),
            ManifestV3Application.builder()
                .name("quota-test-6")
                .memory(3072)
                .disk(4096)
                .build(),
            ManifestV3Application.builder()
                .name("quota-test-7")
                .memory(5120)
                .disk(6144)
                .build(),
            ManifestV3Application.builder()
                .name("quota-test-8")
                .memory(7168)
                .disk(8192)
                .build(),
            ManifestV3Application.builder()
                .name("quota-test-9")
                .memory(1234)
                .disk(5678)
                .build()
        ).build();

        ManifestV3 actual = ApplicationManifestUtilsV3.read(new ClassPathResource("fixtures/manifest-mike.yml").getFile().toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void readSingleBuildpack() throws IOException {
        ManifestV3 expected = ManifestV3.builder().application(
            ManifestV3Application.builder()
                .name("oscar-application")
                .buildpack("oscar-buildpack")
                .build()).build();

        ManifestV3 actual = ApplicationManifestUtilsV3.read(new ClassPathResource("fixtures/manifest-oscar.yml").getFile().toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void readWithVariableSubstitution() throws IOException {
        ManifestV3 expected = ManifestV3.builder().application(
            ManifestV3Application.builder()
                .name("papa-1-application")
                .buildpack("papa-buildpack")
                .instances(2)
                .memory(1024)
                .build()).build();

        ManifestV3 actual = ApplicationManifestUtilsV3.read(
            new ClassPathResource("fixtures/manifest-papa-1.yml").getFile().toPath(),
            new ClassPathResource("fixtures/vars-papa-1.yml").getFile().toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void readWithVariableSubstitution_throwExceptionOnMissing() throws IOException {
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> {
                ApplicationManifestUtilsV3.read(
                    new ClassPathResource("fixtures/manifest-papa-2.yml").getFile().toPath(),
                    new ClassPathResource("fixtures/vars-papa-2.yml").getFile().toPath());
            }).withMessageMatching("Expected to find variable: abcdef");
    }
    @Test
    public void readWithVariableSubstitution_dontEvaluateRegex() throws IOException {
        ManifestV3 expected = ManifestV3.builder().application(
            ManifestV3Application.builder()
                .name("papa-7-application")
                .buildpack("((regex*))")
                .build()).build();

        ManifestV3 actual = ApplicationManifestUtilsV3.read(
            new ClassPathResource("fixtures/manifest-papa-7.yml").getFile().toPath(),
            new ClassPathResource("fixtures/vars-papa-7.yml").getFile().toPath());

        assertThat(actual).isEqualTo(expected);
    }
    @Test
    public void readWithVariableSubstitution_avoidEndlessSubstitution() throws IOException {
        ManifestV3 expected = ManifestV3.builder().application(
            ManifestV3Application.builder()
                .name("papa-3-application")
                .buildpack("((endless_2))")
                .build()).build();

        ManifestV3 actual = ApplicationManifestUtilsV3.read(
            new ClassPathResource("fixtures/manifest-papa-3.yml").getFile().toPath(),
            new ClassPathResource("fixtures/vars-papa-3.yml").getFile().toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void readWithVariableSubstitution_dontAllowInjectionTest() throws IOException {
        ManifestV3 expected = ManifestV3.builder().application(
            ManifestV3Application.builder()
                .name("papa-4-application")
                .buildpack("((test))")
                .build()).build();

        ManifestV3 actual = ApplicationManifestUtilsV3.read(
            new ClassPathResource("fixtures/manifest-papa-4.yml").getFile().toPath(),
            new ClassPathResource("fixtures/vars-papa-4.yml").getFile().toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void readWithVariableSubstitution_addMultipleVariablesInOneField() throws IOException {
        ManifestV3 expected = ManifestV3.builder().application(
            ManifestV3Application.builder()
                .name("papa-5-application")
                .buildpack("one and two is a very nice buildpack name for three")
                .build()).build();

        ManifestV3 actual = ApplicationManifestUtilsV3.read(
            new ClassPathResource("fixtures/manifest-papa-5.yml").getFile().toPath(),
            new ClassPathResource("fixtures/vars-papa-5.yml").getFile().toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void readWithVariableSubstitution_noSubstitutionAtAll() throws IOException {
        ManifestV3 expected = ManifestV3.builder().application(
            ManifestV3Application.builder()
                .name("papa-6-application")
                .buildpack("buildpack_papa_6")
                .build()).build();

        ManifestV3 actual = ApplicationManifestUtilsV3.read(
            new ClassPathResource("fixtures/manifest-papa-6.yml").getFile().toPath(),
            new ClassPathResource("fixtures/vars-papa-6.yml").getFile().toPath());

        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void unixRead() throws IOException {
        assumeTrue(SystemUtils.IS_OS_UNIX);
        read("/alpha-path", "fixtures/manifestv3-alpha-unix.yml");
    }

    @Test
    public void unixWrite() throws IOException {
        assumeTrue(SystemUtils.IS_OS_UNIX);
        write("/alpha-path", "fixtures/manifestv3-echo-unix.yml");
    }

    @Test
    public void windowsRead() throws IOException {
        assumeTrue(SystemUtils.IS_OS_WINDOWS);
        read("c:\\alpha-path", "fixtures/manifestv3-alpha-windows.yml");
    }

    @Test
    public void windowsWrite() throws IOException {
        assumeTrue(SystemUtils.IS_OS_WINDOWS);
        write("c:\\alpha-path", "fixtures/manifestv3-echo-windows.yml");
    }

    private void read(String path, String expectedManifest) throws IOException {
        ManifestV3 expected = ManifestV3.builder().application(
            ManifestV3Application.builder()
                .name("alpha-application-1")
                .buildpack("alpha-buildpack")
                .command("alpha-command")
                .disk(-1)
                .healthCheckHttpEndpoint("alpha-health-check-http-endpoint")
                .healthCheckType(NONE)
                .instances(-1)
                .memory(1)
                .noRoute(true)
                .path(Paths.get(path))
                .randomRoute(true)
                .route(Route.builder()
                    .route("alpha-route-1")
                    .build())
                .route(Route.builder()
                    .route("alpha-route-2")
                    .build())
                .stack("alpha-stack")
                .metadata(ManifestV3Metadata.builder()
                    .label("ALPHA_LABEL_1", "alpha_label_value_1")
                    .label("ALPHA_LABEL_2", "alpha_label_value_2")
                    .annotation("ALPHA_ANNOTATION_1","alpha_annotation_value_1")
                    .annotation("ALPHA_ANNOTATION_2","alpha_annotation_value_2")
                    .build())
                .timeout(-1)
                .environmentVariable("ALPHA_KEY_1", "alpha-value-1")
                .environmentVariable("ALPHA_KEY_2", "alpha-value-2")
                .service(ManifestV3Service.builder().name("alpha-instance-1").build())
                .service(ManifestV3Service.builder().name("alpha-instance-2").build())
                .build()).build();

        ManifestV3 actual = ApplicationManifestUtilsV3.read(new ClassPathResource(expectedManifest).getFile().toPath());

        assertThat(actual).isEqualTo(expected);
    }

    private void write(String path, String expectedManifest) throws IOException {
        Path out = Files.createTempFile("test-manifest-", ".yml");

        ApplicationManifestUtilsV3.write(out, ManifestV3.builder().applications(
            ManifestV3Application.builder()
                .name("alpha-application-1")
                .buildpack("alpha-buildpack")
                .command("alpha-command")
                .disk(512)
                .healthCheckHttpEndpoint("alpha-health-check-http-endpoint")
                .instances(-1)
                .memory(512)
                .noRoute(true)
                .path(Paths.get(path))
                .randomRoute(true)
                .route(Route.builder()
                    .route("alpha-route-1")
                    .build())
                .route(Route.builder()
                    .route("alpha-route-2")
                    .build())
                .stack("alpha-stack")
                .timeout(-1)
                .environmentVariable("ALPHA_KEY_1", "alpha-value-1")
                .environmentVariable("ALPHA_KEY_2", "alpha-value-2")
                .service(ManifestV3Service.builder().name("alpha-instance-1").build())
                .service(ManifestV3Service.builder().name("alpha-instance-2").build())
                .metadata(ManifestV3Metadata.builder()
                    .label("ALPHA_LABEL_1", "alpha_label_value_1")
                    .label("ALPHA_LABEL_2", "alpha_label_value_2")
                    .annotation("ALPHA_ANNOTATION_1","alpha_annotation_value_1")
                    .annotation("ALPHA_ANNOTATION_2","alpha_annotation_value_2")
                    .build())
                .build(),
            ManifestV3Application.builder()
                .name("alpha-application-2")
                .buildpack("alpha-buildpack")
                .command("alpha-command")
                .domain("alpha-domain")
                .domain("alpha-domains-1")
                .domain("alpha-domains-2")
                .healthCheckHttpEndpoint("alpha-health-check-http-endpoint")
                .healthCheckType(PORT)
                .host("alpha-host")
                .host("alpha-hosts-1")
                .host("alpha-hosts-2")
                .instances(-1)
                .noHostname(true)
                .noRoute(true)
                .path(Paths.get(path))
                .randomRoute(true)
                .stack("alpha-stack")
                .timeout(-1)
                .environmentVariable("ALPHA_KEY_1", "alpha-value-1")
                .environmentVariable("ALPHA_KEY_2", "alpha-value-2")
                .service(ManifestV3Service.builder().name("alpha-instance-1").build())
                .service(ManifestV3Service.builder().name("alpha-instance-2").build())
                .build()).build());

        List<String> expected = Files.readAllLines(new ClassPathResource(expectedManifest).getFile().toPath());
        List<String> actual = Files.readAllLines(out);

        assertThat(actual).isEqualTo(expected);
    }

}
