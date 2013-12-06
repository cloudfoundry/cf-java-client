package org.cloudfoundry.client.lib.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class CloudApplicationTest {
  @Test
  public void testGetEnvAsMap() {
    Map<String, Object> attributes = new HashMap<String, Object>();
    attributes.put("env", Arrays.asList("ENV1=VAL1", "ENV2=", "ENV3"));
    attributes.put("instances", 1);
    attributes.put("name", "Test1");
    attributes.put("state", CloudApplication.AppState.STOPPED.name());

    CloudApplication cloudApplication = new CloudApplication(attributes);

    Map<String, String> envMap = cloudApplication.getEnvAsMap();
    assertThat(envMap.size(), is(3));
    assertThat(envMap.get("ENV1"), is("VAL1"));
    assertThat(envMap.get("ENV2"), is(nullValue()));
    assertThat(envMap.get("ENV3"), is(nullValue()));
  }
}
