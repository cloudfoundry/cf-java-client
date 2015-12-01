package org.cloudfoundry.client.lib.domain;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

public class CloudApplicationTest {

    @Test
    public void getEnvAsMap() {
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

    @Test
    public void setEnvWithNonString() {
        // creating as an un-typed map to mimic Jackson behavior
        Map envMap = new HashMap();
        envMap.put("key1", "value1");
        envMap.put("key2", 3);
        envMap.put("key3", true);

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("instances", 1);
        attributes.put("name", "Test1");
        attributes.put("state", CloudApplication.AppState.STOPPED.name());

        CloudApplication cloudApplication = new CloudApplication(attributes);

        cloudApplication.setEnv(envMap);

        Map<String, String> actual = cloudApplication.getEnvAsMap();
        assertThat(actual.get("key1"), is("value1"));
        assertThat(actual.get("key2"), is("3"));
        assertThat(actual.get("key3"), is("true"));
    }
}
