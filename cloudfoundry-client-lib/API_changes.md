Cloud Foundry Client Library
============================

API Changes for version 0.8.6:
------------------------------

### Removed V1 concepts

* Frameworks
* Runtimes
* ServiceConfiguration (replaced by CloudServiceOffering)
* Application plans
* CloudFoundryClient constructors and parameters modified accordingly

### Added V2 concepts

* Buildpack and command fields were added to the Staging object used for createApplication
* Space name and Org name added as a CloudFoundryClient constructor parameters

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

All entity classes in the _org.cloudfoundry.client.lib.domain_ package have a CloudEntity.Meta property that hold the following attributes:

  * UUID guid;
  * Date created;
  * Date updated;
  * int version;

This allows you to determine the version of any entity using getMeta().getVersion() which should return 1 or 2 depending on the cloud controller version currently connected to.

The following method has been removed from _CloudFoundryClient_:

    public <T> T getFile(String appName, int instanceIndex, String filePath, RequestCallback requestCallback, ResponseExtractor<T> responseHandler)

The following methods have been removed from _CloudApplication_:

    public Map<String, Object> getMeta()
    public void setMeta(Map<String, Object> meta)

They are replaced with the following methods that are now available for all cloud entity classes:

    public CloudEntity.Meta getMeta()
    public void setMeta(CloudEntity.Meta meta)


### Deprecations:

The _org.cloudfoundry.client.lib.domain.CloudApplication_ class has been deprecated since it's not used anywhere in the library. You will have to replace this with your own implementation.
