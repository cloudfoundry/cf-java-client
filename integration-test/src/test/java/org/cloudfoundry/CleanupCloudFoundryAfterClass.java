package org.cloudfoundry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Meta-annotation to show that a test class will add too much to the CF instance, and that a full universe
 * cleanup should occur. This is important because otherwise the integration tests create too many apps and
 * blow up the memory quota. We do not want to recreate the full environment for EVERY test class because
 * the process takes 30~60s, so in total it could add more than an hour to the integration tests.
 * <p>
 * Technically, this is achieved by recreating a Spring ApplicationContext with {@link DirtiesContext}. The
 * {@link CloudFoundryCleaner} bean will be destroyed, which triggers {@link CloudFoundryCleaner#clean()}.
 * After that, every {@link Bean} in {@link IntegrationTestConfiguration} is recreated, including users,
 * clients, organizations, etc: everything required to run a test.
 * <p>
 * We use a meta-annotation instead of a raw {@link DirtiesContext} to make it clear what it does, rather
 * than having to understand complicated lifecycle issues.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@DirtiesContext
public @interface CleanupCloudFoundryAfterClass {}
