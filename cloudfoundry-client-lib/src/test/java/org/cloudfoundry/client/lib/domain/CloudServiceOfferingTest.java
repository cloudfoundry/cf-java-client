package org.cloudfoundry.client.lib.domain;

import org.junit.Assert;
import org.junit.Test;

public class CloudServiceOfferingTest {

    @Test
    public void testAddCloudServicePlan() {
        CloudServiceOffering offering = new CloudServiceOffering(
                CloudEntity.Meta.defaultMeta(), "<name>");

        Assert.assertTrue(offering.getCloudServicePlans().isEmpty());

        CloudServicePlan plan0 = new CloudServicePlan();
        CloudServicePlan plan1 = new CloudServicePlan();
        CloudServicePlan plan2 = new CloudServicePlan();
        offering.addCloudServicePlan(plan0);
        offering.addCloudServicePlan(plan1);
        offering.addCloudServicePlan(plan2);

        Assert.assertEquals(3, offering.getCloudServicePlans().size());
        Assert.assertEquals(plan0, offering.getCloudServicePlans().get(0));
        Assert.assertEquals(plan1, offering.getCloudServicePlans().get(1));
        Assert.assertEquals(plan2, offering.getCloudServicePlans().get(2));
    }

    @Test
    public void testConstructorFull() {
        CloudServiceOffering offering = new CloudServiceOffering(
                CloudEntity.Meta.defaultMeta(),
                "<name>",
                "<provider>",
                "<version>",
                "<description>",
                true,
                true,
                "<url>",
                "<infoUrl>",
                "<uniqueId>",
                "<extra>",
                "<docUrl>"
        );

        Assert.assertEquals("<name>", offering.getName());
        Assert.assertEquals("<name>", offering.getLabel());
        Assert.assertEquals("<description>", offering.getDescription());
        Assert.assertEquals("<provider>", offering.getProvider());
        Assert.assertEquals("<version>", offering.getVersion());
        Assert.assertTrue(offering.isActive());
        Assert.assertTrue(offering.isBindable());
        Assert.assertEquals("<url>", offering.getUrl());
        Assert.assertEquals("<infoUrl>", offering.getInfoUrl());
        Assert.assertEquals("<uniqueId>", offering.getUniqueId());
        Assert.assertEquals("<extra>", offering.getExtra());
        Assert.assertEquals("<docUrl>", offering.getDocumentationUrl());
        Assert.assertTrue(offering.getCloudServicePlans().isEmpty());
    }

    @Test
    public void testConstructorMinimal() {
        CloudServiceOffering offering = new CloudServiceOffering(
                CloudEntity.Meta.defaultMeta(), "<name>", "<provider>", "<version>");

        Assert.assertEquals("<name>", offering.getName());
        Assert.assertEquals("<name>", offering.getLabel());
        Assert.assertNull(offering.getDescription());
        Assert.assertEquals("<provider>", offering.getProvider());
        Assert.assertEquals("<version>", offering.getVersion());
        Assert.assertFalse(offering.isActive());
        Assert.assertFalse(offering.isBindable());
        Assert.assertNull(offering.getUrl());
        Assert.assertNull(offering.getInfoUrl());
        Assert.assertNull(offering.getUniqueId());
        Assert.assertNull(offering.getExtra());
        Assert.assertNull(offering.getDocumentationUrl());
        Assert.assertTrue(offering.getCloudServicePlans().isEmpty());
    }

    @Test
    public void testConstructorNameOnly() {
        CloudServiceOffering offering = new CloudServiceOffering(
                CloudEntity.Meta.defaultMeta(), "<name>");

        Assert.assertEquals("<name>", offering.getName());
        Assert.assertEquals("<name>", offering.getLabel());
        Assert.assertNull(offering.getDescription());
        Assert.assertNull(offering.getProvider());
        Assert.assertNull(offering.getVersion());
        Assert.assertFalse(offering.isActive());
        Assert.assertFalse(offering.isBindable());
        Assert.assertNull(offering.getUrl());
        Assert.assertNull(offering.getInfoUrl());
        Assert.assertNull(offering.getUniqueId());
        Assert.assertNull(offering.getExtra());
        Assert.assertNull(offering.getDocumentationUrl());
        Assert.assertTrue(offering.getCloudServicePlans().isEmpty());
    }
}
