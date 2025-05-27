package org.cloudfoundry.client.v2.buildpacks;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class BuildpackEntityTest {
    @Test
    void jsonSerialization() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        BuildpackEntity originalBuildpack =
                BuildpackEntity.builder()
                        .enabled(false)
                        .locked(true)
                        .name("test-buildpack")
                        .position(42)
                        .lifecycle(LifecycleType.BUILDPACK)
                        .build();

        String serialized = mapper.writeValueAsString(originalBuildpack);
        BuildpackEntity deserialized = mapper.readValue(serialized, BuildpackEntity.class);

        assertThat(deserialized).isEqualTo(originalBuildpack);
    }
}
