VMware Cloud Application Platform - Java Client Library
=======================================================

API Changes for version 0.8.0
-----------------------------

### Package changes:

The following classes have moved to the _org.cloudfoundry.client.lib.domain_ package

  * ApplicationStats.java
  * CloudApplication.java
  * CloudEntity.java
  * CloudInfo.java
  * CloudResource.java
  * CloudResources.java
  * CloudService.java
  * CrashInfo.java
  * CrashesInfo.java
  * DeploymentInfo.java
  * InstanceInfo.java
  * InstanceStats.java
  * InstancesInfo.java
  * ServiceConfiguration.java
  * Staging.java
  * UploadApplicationPayload.java

The following classes have moved to the _org.cloudfoundry.client.lib.util_ package

  * CloudUtil.java
  * StringHttpMessageConverterWithoutMediaType.java
  * UploadApplicationPayloadHttpMessageConverter.java

You will need to adjust the import statements for these classes


### Implementation changes:

The _org.cloudfoundry.client.lib.CloudFoundryOperations_ interface defines the public API. You can use this interface or continue using the _org.cloudfoundry.client.lib.CloudFoundryClient_ class.

The following method has been removed from _CloudFoundryClient_:

    public <T> T getFile(String appName, int instanceIndex, String filePath, RequestCallback requestCallback, ResponseExtractor<T> responseHandler)


### Deprecations:

The _org.cloudfoundry.client.lib.domain.CloudApplication_ class has been deprecated since it's not used anywhere in the library. You will have to replace this with your own implementation.
