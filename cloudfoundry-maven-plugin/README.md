# Cloud Foundry Maven Plugin
Version 1.0.0.M1-SNAPSHOT, August 30, 2011 Draft

Source code: git@github.com:vmware-ac/vmc-java.git

## Introduction

Since Maven is one of the dominant build and deployment tools in the Java world, we made the core functionality of the [Cloud Foundry VMC command line tool](http://support.cloudfoundry.com/entries/20012337-getting-started-guide-command-line-vmc-users) also available to Maven users.

As an immediate step, we have implemented the following functionality as a Maven Plugin:

* Deploy (Push) Maven based projects to Cloud Foundry
* Undeploy Maven based projects from Cloud Foundry
* Redeploy (Update) Maven based Cloud Foundry projects

The Maven plugin also closely mimics the feature set provided by the [Grails Cloud Foundry Plugin](http://grails-plugins.github.com/grails-cloud-foundry/docs/manual/index.html).

## Basic Configuration

In order to get started you must as a minimum add the **cf-maven-plugin** to your project's pom.xml:

    <plugin>
        <groupId>org.cloudfoundry</groupId>
        <artifactId>cf-maven-plugin</artifactId>
        <version>1.0.0.M1-SNAPSHOT</version>
    </plugin>

This minimal configuration will be sufficient to execute many of the plugin's Maven Goals.

> All configuration options that can be specified either through configuration parameters in the pom.xml, or via command-line-provided system properties (e.g. *mvn cf:push \-Dcf.appname=greenhouse*). Please read on for further details.

## Advanced Configuration

As mentioned above, the Cloud Foundry Maven Plugin can be configured either by providing relevant information in the *pom.xml* file and/or via *command line* parameters (system properties). This allows users to chose the configuration path most appropriate to their business needs. In most cases though, we expect users to configure the static and non-security-sensitive parameters in the pom.xml file.

### Security and Storing of Cloud Foundry Credentials

While it is possible to configure Cloud Foundry security credentials within the pom.xml file (discouraged), they can also be configured via system properties. However, even better, the security credentials for your Cloud Foundry instance can also be configured using the standard "server" Xml configuration element ([http://maven.apache.org/settings.html#Servers]). This allows for keeping out security-sensitive information from the *pom.xml* file, yet eliminating the need to provide the security credential every time you interact with Cloud Foundry. In that case, the username and password information is stored in the **settings.xml** file, which is usually placed under  *~/.m2/settings.xml* (home directory). The following example illustrated the necessary configuration:

Plugin configuration in **pom.xml**:

    <plugin>
      <groupId>org.cloudfoundry</groupId>
      <artifactId>cf-maven-plugin</artifactId>
      <version>1.0.0.M1-SNAPSHOT</version>
      <configuration>
          <server>mycloudfoundry-instance</server>
          <target>http://api.cloudfoundry.com</target>
      </configuration>
    </plugin>

> The **server** configuration element is actually optional. If not explicitly set, its value will default to **cloud-foundry-credentials**:

Configuration of *mycloudfoundry-instance* in **settings.xml**:

    <settings>
      ...
      <servers>
        ...
        <server>
          <id>mycloudfoundry-instance</id>
          <username>myusername</username>
          <password>s3cr3t</password>
        </server>
        ...
      </servers>
      ...
    </settings>

As mentioned previously, you can also provide the user credentials through following command line parameters instead:

* cf.username
* cf.password

e.g. by using:

    $ mvn cf:info -Dcf.username=myusername -Dcf.password=s3cr3t -Dtarget=http://api.cloudfoundry.com


> If the credentials are defined via the server element (in settings.xml) AND through the command line, then the command line parameter takes the precedence.

Finally, describing probably a rather rare use-case: If you have multiple Cloud Foundry specific **server** elements defined in your **settings.xml**, you can address those through command line parameters as well using:

* cf.server (e.g. *mvn push -Dcf.server=mycloudfoundry-instance*)

### A complete configuration example

Following, a typical (expected) configuration example is shown, which uses several of the available configuration parameters. However for a complete listing of proposed configuration options, please have a look under section *Command Line Usage*".

    <project>
      ...
    <build>
        <plugins>
            <plugin>
                <groupId>org.cloudfoundry</groupId>
                <artifactId>maven-cf-plugin</artifactId>
                <version>1.0.0.M1-SNAPSHOT</version>
                <configuration>
                    <server>mycloudfoundry-instance</server>
                    <target>http://api.cloudfoundry.com</target>
                    <appname>spring-integration-rocks</appname>
                    <url>spring-integration-rocks.cloudfoundry.com</url>
                    <memory>1024</memory>
                    <services>mongo-instance, my-sql-instance</services>
                </configuration>
            </plugin>
        </plugins>
    </build>
      ...
    </project>

> The **url** element is optional. If not specified, it defaults to *appname*.<main *target* domain> (using the *appname* element)
> e.g. if your **appname** is *spring-integration-rocks* and the **target** is defined as *api.cloudfoundry.com* then the url will default to: **spring-integration-rocks.cloudfoundry.com**

    <settings>
      <servers>
        <server>
          <id>mycloudfoundry-instance</id>
          <username>gunnar@hillert.com</username>
          <password>s3cr3t</password>
        </server>
      </servers>
    </settings>

## Command Line Usage

### Overview

The following Maven *goals* are available for the Cloud Foundry Maven Plugin:

<table>
    <tr><th align="left">cf:add-user</th>   <td>Register a new user.</td></tr>
    <tr><th align="left">cf:apps</th>       <td>List deployed applications.</td></tr>
    <tr><th align="left">cf:delete</th>     <td>Deletes an application.</td></tr>
    <tr><th align="left">cf:delete-user</th><td>Delete a user and all apps and services.</td></tr>
    <tr><th align="left">cf:help</th>       <td>Documentation for all available commands.</td></tr>
    <tr><th align="left">cf:info</th>       <td>Shows usage information.</td></tr>
    <tr><th align="left">cf:instances</th>  <td>Scale the application instances up or down.</td></tr>
    <tr><th align="left">cf:push</th>       <td>Push and optionally start an application.</td></tr>
    <tr><th align="left">cf:restart</th>    <td>Restarts an application.</td></tr>
    <tr><th align="left">cf:start</th>      <td>Starts an application.</td></tr>
    <tr><th align="left">cf:stop</th>       <td>Stops an application.</td></tr>
    <tr><th align="left">cf:update</th>     <td>Updates an application.</td></tr>
</table>

#### Usage Examples

**Add a User**

    $ mvn cf:add-user [-Dcf.username] [-Dcf.password]

**List deployed applications**

    $ mvn cf:apps

**Delete an application**

    $ mvn cf:delete [-Dcf.appname] [-Dcf.force]

**Delete a User**

    $ mvn cf:delete-user [-Dcf.username] [-Dcf.password]

**Documentation for all available commands**

    $ mvn cf:help

    Cloud Foundry Maven Plugin detected Parameters and/or default values:

    +-----------+-----------------------------------------------------------------------------------+
    | Parameter | Value (Configured or Default)                                                     |
    +-----------+-----------------------------------------------------------------------------------+
    | Appname   | hello-java                                                                        |
    | Instances | N/A                                                                               |
    | Memory    | 512                                                                               |
    | No-start  | false                                                                             |
    | Password  | *****                                                                             |
    | Server    | cloud-foundry-credentials                                                         |
    | Services  |                                                                                   |
    | Target    | N/A                                                                               |
    | Url       | hello-java.cloudfoundry.com                                                       |
    | Username  | demouser@cloudfoundry.com                                                         |
    | Warfile   | /Users/demouser/dev/git/cloudfoundry-samples/hello-java/target/hello-java-1.0.war |
    +-----------+-----------------------------------------------------------------------------------+
    Usage: mvn cf:command [command_options]

    Currently available Cloud Foundry Maven Plugin Goals are:

      Getting Started
        info -Dcf.username -Dcf.password                                        System and account information

      Applications
        apps -Dcf.username -Dcf.password                                        List deployed applications

      Application Creation
        push -Dcf.username -Dcf.password [-Dcf.appname]                         Create, push, map, and start a new application
        push -Dcf.username -Dcf.password [-Dcf.appname] [-Dcf.warfile]          Push application from specified path
        push -Dcf.username -Dcf.password [-Dcf.appname] [-Dcf.url]              Set the url for the application
        push -Dcf.username -Dcf.password [-Dcf.appname] [-Dcf.instances]        Set the expected number of instances
        push -Dcf.username -Dcf.password [-Dcf.appname] [-Dcf.memory]           Set the memory reservation for the application
        push -Dcf.username -Dcf.password [-Dcf.appname] [-Dcf.services]         Set the runtime to use for the application
        push -Dcf.username -Dcf.password [-Dcf.appname] [-Dcf.no-start]         Do not auto-start the application

      Application Operations
        start   -Dcf.username -Dcf.password [-Dcf.appname]                      Start the application
        stop    -Dcf.username -Dcf.password [-Dcf.appname]                      Stop the application
        restart -Dcf.username -Dcf.password [-Dcf.appname]                      Restart the application
        delete  -Dcf.username -Dcf.password [-Dcf.appname]                      Delete the application

      Application Updates
        update    -Dcf.username -Dcf.password [-Dcf.warfile]                    Update the application bits
        instances -Dcf.username -Dcf.password [-Dcf.appname] [-Dcf.instances]   Scale the application instances up or down

      Administration
        add-user    -Dcf.username -Dcf.password                                 Register a new user
        register    -Dcf.username -Dcf.password                                 Register a new user (Alias for 'add-user')
        delete-user -Dcf.username -Dcf.password                                 Delete a user and all apps and services

      Application Information
        instances -Dcf.username -Dcf.password [-Dcf.appname]                    List application instances

      Help
        help                                                                    Get general help

**Show usage information**

    $ mvn cf:info

    --------------------------------------------------------
    Target:      http://api.cloudfoundry.com (v0.999 build 2222)
    Description: VMware's Cloud Application Platform
    Name:        vcap
    Support:     support@cloudfoundry.com
    User:        yep-it-rocks@cloudfoundry.com
    Usage:
        Memory:       1536M of 2048M total
        Services:     4 of 16 total
        Apps:         3 of 20 total
        Uris Per App: 0 of 4 total
    --------------------------------------------------------

**Scale the application instances up or down**

    $ mvn cf:instances [-Dcf.appname] [-Dcf.instances]

**Push and optionally start an application**

    $ mvn cf:push [-Dcf.appname] [-Dcf.warfile] [-Dcf.url] [-Dcf.instances] [-Dcf.memory] [-Dcf.services] [-Dcf.no-start]

**Restart the application**

    $ mvn cf:restart [-Dcf.appname]

**Start the application**

    $ mvn cf:start [-Dcf.appname]

**Stop the application**

    $ mvn cf:stop [-Dcf.appname]

**Update the application**

    $ mvn cf:update [-Dcf.appname] [-Dcf.warfile]

## Sensible Defaults

### Precedence

As the same configuration parameters can be provided either through system properties,
configuration elements in the the pom.xml and through the settings.xml file (username and
password information only), the following precedence rules apply (starting with the highest precedence):

1. **System Properties** e.g. mvn cf:start -Dcf.appname
2. **Setting.xml parameters** for username and password
3. **Pom.xml** Configuration parameters e.g. <configuration><appname>myApp</appname></configuration>
4. **Pom.xml** Properties e.g. <properties><cf.appname>myApp</cf.appname><properties>

> **INFO** The implemented behavior deviates slightly from standard Maven behavior.
  Usually Maven configuration parameters in the pom take precedence over system
  properties passed in via the command line. Within the scope of the Cloud Foundry
  Maven Plugin, however, system properties passed in via e.g. -Dcf.appname take precedence
  over pom configuration parameters.

Additional certain configuration parameter will fall back to using default values in case **no configuration value was provided**:

### Defaults

+ **appname**: If no app name is specified, the Maven artifact id is being used
+ **no-start**: Defaults to *false*
+ **memory**: Defaults to *512* (MB)
+ **server**: Special parameter to tell **Maven** which server element in *settings.xml*
  holds the credentials for Cloud Foundry. Defaults to *cloud-foundry-credentials*
+ **url**: If no Url is specified, then the *appname* and the main domain from the *target* parameter are used to dynamically form the url
+ **warfile**: If not provided it defaults to: *${project.build.directory}/${project.build.finalName}.war*

> The parameters **username**, **password** and **target** don't have default values and you are required to provide them



