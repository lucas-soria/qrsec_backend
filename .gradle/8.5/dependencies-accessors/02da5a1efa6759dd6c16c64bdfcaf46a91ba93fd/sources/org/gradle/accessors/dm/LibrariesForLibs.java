package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the `libs` extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final ComLibraryAccessors laccForComLibraryAccessors = new ComLibraryAccessors(owner);
    private final OrgLibraryAccessors laccForOrgLibraryAccessors = new OrgLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Returns the group of libraries at com
     */
    public ComLibraryAccessors getCom() {
        return laccForComLibraryAccessors;
    }

    /**
     * Returns the group of libraries at org
     */
    public OrgLibraryAccessors getOrg() {
        return laccForOrgLibraryAccessors;
    }

    /**
     * Returns the group of versions at versions
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Returns the group of bundles at bundles
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Returns the group of plugins at plugins
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class ComLibraryAccessors extends SubDependencyFactory {
        private final ComAuth0LibraryAccessors laccForComAuth0LibraryAccessors = new ComAuth0LibraryAccessors(owner);

        public ComLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at com.auth0
         */
        public ComAuth0LibraryAccessors getAuth0() {
            return laccForComAuth0LibraryAccessors;
        }

    }

    public static class ComAuth0LibraryAccessors extends SubDependencyFactory {
        private final ComAuth0JavaLibraryAccessors laccForComAuth0JavaLibraryAccessors = new ComAuth0JavaLibraryAccessors(owner);

        public ComAuth0LibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at com.auth0.java
         */
        public ComAuth0JavaLibraryAccessors getJava() {
            return laccForComAuth0JavaLibraryAccessors;
        }

    }

    public static class ComAuth0JavaLibraryAccessors extends SubDependencyFactory {

        public ComAuth0JavaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for jwt (com.auth0:java-jwt)
         * with versionRef 'com.auth0.java.jwt'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getJwt() {
                return create("com.auth0.java.jwt");
        }

    }

    public static class OrgLibraryAccessors extends SubDependencyFactory {
        private final OrgJetbrainsLibraryAccessors laccForOrgJetbrainsLibraryAccessors = new OrgJetbrainsLibraryAccessors(owner);
        private final OrgMapstructLibraryAccessors laccForOrgMapstructLibraryAccessors = new OrgMapstructLibraryAccessors(owner);
        private final OrgProjectlombokLibraryAccessors laccForOrgProjectlombokLibraryAccessors = new OrgProjectlombokLibraryAccessors(owner);
        private final OrgSpringframeworkLibraryAccessors laccForOrgSpringframeworkLibraryAccessors = new OrgSpringframeworkLibraryAccessors(owner);

        public OrgLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at org.jetbrains
         */
        public OrgJetbrainsLibraryAccessors getJetbrains() {
            return laccForOrgJetbrainsLibraryAccessors;
        }

        /**
         * Returns the group of libraries at org.mapstruct
         */
        public OrgMapstructLibraryAccessors getMapstruct() {
            return laccForOrgMapstructLibraryAccessors;
        }

        /**
         * Returns the group of libraries at org.projectlombok
         */
        public OrgProjectlombokLibraryAccessors getProjectlombok() {
            return laccForOrgProjectlombokLibraryAccessors;
        }

        /**
         * Returns the group of libraries at org.springframework
         */
        public OrgSpringframeworkLibraryAccessors getSpringframework() {
            return laccForOrgSpringframeworkLibraryAccessors;
        }

    }

    public static class OrgJetbrainsLibraryAccessors extends SubDependencyFactory {

        public OrgJetbrainsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for annotations (org.jetbrains:annotations)
         * with versionRef 'org.jetbrains.annotations'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getAnnotations() {
                return create("org.jetbrains.annotations");
        }

    }

    public static class OrgMapstructLibraryAccessors extends SubDependencyFactory {
        private final OrgMapstructMapstructLibraryAccessors laccForOrgMapstructMapstructLibraryAccessors = new OrgMapstructMapstructLibraryAccessors(owner);

        public OrgMapstructLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at org.mapstruct.mapstruct
         */
        public OrgMapstructMapstructLibraryAccessors getMapstruct() {
            return laccForOrgMapstructMapstructLibraryAccessors;
        }

    }

    public static class OrgMapstructMapstructLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public OrgMapstructMapstructLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for mapstruct (org.mapstruct:mapstruct)
         * with versionRef 'org.mapstruct.mapstruct'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> asProvider() {
                return create("org.mapstruct.mapstruct");
        }

            /**
             * Creates a dependency provider for processor (org.mapstruct:mapstruct-processor)
         * with versionRef 'org.mapstruct.mapstruct.processor'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getProcessor() {
                return create("org.mapstruct.mapstruct.processor");
        }

    }

    public static class OrgProjectlombokLibraryAccessors extends SubDependencyFactory {

        public OrgProjectlombokLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for lombok (org.projectlombok:lombok)
         * with versionRef 'org.projectlombok.lombok'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getLombok() {
                return create("org.projectlombok.lombok");
        }

    }

    public static class OrgSpringframeworkLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootLibraryAccessors laccForOrgSpringframeworkBootLibraryAccessors = new OrgSpringframeworkBootLibraryAccessors(owner);
        private final OrgSpringframeworkSecurityLibraryAccessors laccForOrgSpringframeworkSecurityLibraryAccessors = new OrgSpringframeworkSecurityLibraryAccessors(owner);
        private final OrgSpringframeworkSessionLibraryAccessors laccForOrgSpringframeworkSessionLibraryAccessors = new OrgSpringframeworkSessionLibraryAccessors(owner);

        public OrgSpringframeworkLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at org.springframework.boot
         */
        public OrgSpringframeworkBootLibraryAccessors getBoot() {
            return laccForOrgSpringframeworkBootLibraryAccessors;
        }

        /**
         * Returns the group of libraries at org.springframework.security
         */
        public OrgSpringframeworkSecurityLibraryAccessors getSecurity() {
            return laccForOrgSpringframeworkSecurityLibraryAccessors;
        }

        /**
         * Returns the group of libraries at org.springframework.session
         */
        public OrgSpringframeworkSessionLibraryAccessors getSession() {
            return laccForOrgSpringframeworkSessionLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringLibraryAccessors laccForOrgSpringframeworkBootSpringLibraryAccessors = new OrgSpringframeworkBootSpringLibraryAccessors(owner);

        public OrgSpringframeworkBootLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at org.springframework.boot.spring
         */
        public OrgSpringframeworkBootSpringLibraryAccessors getSpring() {
            return laccForOrgSpringframeworkBootSpringLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringBootLibraryAccessors laccForOrgSpringframeworkBootSpringBootLibraryAccessors = new OrgSpringframeworkBootSpringBootLibraryAccessors(owner);

        public OrgSpringframeworkBootSpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at org.springframework.boot.spring.boot
         */
        public OrgSpringframeworkBootSpringBootLibraryAccessors getBoot() {
            return laccForOrgSpringframeworkBootSpringBootLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringBootStarterLibraryAccessors laccForOrgSpringframeworkBootSpringBootStarterLibraryAccessors = new OrgSpringframeworkBootSpringBootStarterLibraryAccessors(owner);

        public OrgSpringframeworkBootSpringBootLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for devtools (org.springframework.boot:spring-boot-devtools)
         * with versionRef 'org.springframework.boot.spring.boot.devtools'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getDevtools() {
                return create("org.springframework.boot.spring.boot.devtools");
        }

        /**
         * Returns the group of libraries at org.springframework.boot.spring.boot.starter
         */
        public OrgSpringframeworkBootSpringBootStarterLibraryAccessors getStarter() {
            return laccForOrgSpringframeworkBootSpringBootStarterLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootStarterLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors laccForOrgSpringframeworkBootSpringBootStarterDataLibraryAccessors = new OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors(owner);

        public OrgSpringframeworkBootSpringBootStarterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for security (org.springframework.boot:spring-boot-starter-security)
         * with versionRef 'org.springframework.boot.spring.boot.starter.security'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getSecurity() {
                return create("org.springframework.boot.spring.boot.starter.security");
        }

            /**
             * Creates a dependency provider for test (org.springframework.boot:spring-boot-starter-test)
         * with versionRef 'org.springframework.boot.spring.boot.starter.test'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getTest() {
                return create("org.springframework.boot.spring.boot.starter.test");
        }

            /**
             * Creates a dependency provider for web (org.springframework.boot:spring-boot-starter-web)
         * with versionRef 'org.springframework.boot.spring.boot.starter.web'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getWeb() {
                return create("org.springframework.boot.spring.boot.starter.web");
        }

        /**
         * Returns the group of libraries at org.springframework.boot.spring.boot.starter.data
         */
        public OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors getData() {
            return laccForOrgSpringframeworkBootSpringBootStarterDataLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors extends SubDependencyFactory {

        public OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for mongodb (org.springframework.boot:spring-boot-starter-data-mongodb)
         * with versionRef 'org.springframework.boot.spring.boot.starter.data.mongodb'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getMongodb() {
                return create("org.springframework.boot.spring.boot.starter.data.mongodb");
        }

            /**
             * Creates a dependency provider for rest (org.springframework.boot:spring-boot-starter-data-rest)
         * with versionRef 'org.springframework.boot.spring.boot.starter.data.rest'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getRest() {
                return create("org.springframework.boot.spring.boot.starter.data.rest");
        }

    }

    public static class OrgSpringframeworkSecurityLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkSecuritySpringLibraryAccessors laccForOrgSpringframeworkSecuritySpringLibraryAccessors = new OrgSpringframeworkSecuritySpringLibraryAccessors(owner);

        public OrgSpringframeworkSecurityLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at org.springframework.security.spring
         */
        public OrgSpringframeworkSecuritySpringLibraryAccessors getSpring() {
            return laccForOrgSpringframeworkSecuritySpringLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkSecuritySpringLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkSecuritySpringSecurityLibraryAccessors laccForOrgSpringframeworkSecuritySpringSecurityLibraryAccessors = new OrgSpringframeworkSecuritySpringSecurityLibraryAccessors(owner);

        public OrgSpringframeworkSecuritySpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at org.springframework.security.spring.security
         */
        public OrgSpringframeworkSecuritySpringSecurityLibraryAccessors getSecurity() {
            return laccForOrgSpringframeworkSecuritySpringSecurityLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkSecuritySpringSecurityLibraryAccessors extends SubDependencyFactory {

        public OrgSpringframeworkSecuritySpringSecurityLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for test (org.springframework.security:spring-security-test)
         * with versionRef 'org.springframework.security.spring.security.test'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getTest() {
                return create("org.springframework.security.spring.security.test");
        }

    }

    public static class OrgSpringframeworkSessionLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkSessionSpringLibraryAccessors laccForOrgSpringframeworkSessionSpringLibraryAccessors = new OrgSpringframeworkSessionSpringLibraryAccessors(owner);

        public OrgSpringframeworkSessionLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at org.springframework.session.spring
         */
        public OrgSpringframeworkSessionSpringLibraryAccessors getSpring() {
            return laccForOrgSpringframeworkSessionSpringLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkSessionSpringLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkSessionSpringSessionLibraryAccessors laccForOrgSpringframeworkSessionSpringSessionLibraryAccessors = new OrgSpringframeworkSessionSpringSessionLibraryAccessors(owner);

        public OrgSpringframeworkSessionSpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at org.springframework.session.spring.session
         */
        public OrgSpringframeworkSessionSpringSessionLibraryAccessors getSession() {
            return laccForOrgSpringframeworkSessionSpringSessionLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkSessionSpringSessionLibraryAccessors extends SubDependencyFactory {

        public OrgSpringframeworkSessionSpringSessionLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for core (org.springframework.session:spring-session-core)
         * with versionRef 'org.springframework.session.spring.session.core'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() {
                return create("org.springframework.session.spring.session.core");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        private final ComVersionAccessors vaccForComVersionAccessors = new ComVersionAccessors(providers, config);
        private final OrgVersionAccessors vaccForOrgVersionAccessors = new OrgVersionAccessors(providers, config);
        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the group of versions at versions.com
         */
        public ComVersionAccessors getCom() {
            return vaccForComVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.org
         */
        public OrgVersionAccessors getOrg() {
            return vaccForOrgVersionAccessors;
        }

    }

    public static class ComVersionAccessors extends VersionFactory  {

        private final ComAuth0VersionAccessors vaccForComAuth0VersionAccessors = new ComAuth0VersionAccessors(providers, config);
        public ComVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the group of versions at versions.com.auth0
         */
        public ComAuth0VersionAccessors getAuth0() {
            return vaccForComAuth0VersionAccessors;
        }

    }

    public static class ComAuth0VersionAccessors extends VersionFactory  {

        private final ComAuth0JavaVersionAccessors vaccForComAuth0JavaVersionAccessors = new ComAuth0JavaVersionAccessors(providers, config);
        public ComAuth0VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the group of versions at versions.com.auth0.java
         */
        public ComAuth0JavaVersionAccessors getJava() {
            return vaccForComAuth0JavaVersionAccessors;
        }

    }

    public static class ComAuth0JavaVersionAccessors extends VersionFactory  {

        public ComAuth0JavaVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: com.auth0.java.jwt (3.19.2)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getJwt() { return getVersion("com.auth0.java.jwt"); }

    }

    public static class OrgVersionAccessors extends VersionFactory  {

        private final OrgJetbrainsVersionAccessors vaccForOrgJetbrainsVersionAccessors = new OrgJetbrainsVersionAccessors(providers, config);
        private final OrgMapstructVersionAccessors vaccForOrgMapstructVersionAccessors = new OrgMapstructVersionAccessors(providers, config);
        private final OrgProjectlombokVersionAccessors vaccForOrgProjectlombokVersionAccessors = new OrgProjectlombokVersionAccessors(providers, config);
        private final OrgSpringframeworkVersionAccessors vaccForOrgSpringframeworkVersionAccessors = new OrgSpringframeworkVersionAccessors(providers, config);
        public OrgVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the group of versions at versions.org.jetbrains
         */
        public OrgJetbrainsVersionAccessors getJetbrains() {
            return vaccForOrgJetbrainsVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.org.mapstruct
         */
        public OrgMapstructVersionAccessors getMapstruct() {
            return vaccForOrgMapstructVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.org.projectlombok
         */
        public OrgProjectlombokVersionAccessors getProjectlombok() {
            return vaccForOrgProjectlombokVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.org.springframework
         */
        public OrgSpringframeworkVersionAccessors getSpringframework() {
            return vaccForOrgSpringframeworkVersionAccessors;
        }

    }

    public static class OrgJetbrainsVersionAccessors extends VersionFactory  {

        public OrgJetbrainsVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: org.jetbrains.annotations (23.0.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAnnotations() { return getVersion("org.jetbrains.annotations"); }

    }

    public static class OrgMapstructVersionAccessors extends VersionFactory  {

        private final OrgMapstructMapstructVersionAccessors vaccForOrgMapstructMapstructVersionAccessors = new OrgMapstructMapstructVersionAccessors(providers, config);
        public OrgMapstructVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the group of versions at versions.org.mapstruct.mapstruct
         */
        public OrgMapstructMapstructVersionAccessors getMapstruct() {
            return vaccForOrgMapstructMapstructVersionAccessors;
        }

    }

    public static class OrgMapstructMapstructVersionAccessors extends VersionFactory  implements VersionNotationSupplier {

        public OrgMapstructMapstructVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the version associated to this alias: org.mapstruct.mapstruct (1.4.2.Final)
         * If the version is a rich version and that its not expressible as a
         * single version string, then an empty string is returned.
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> asProvider() { return getVersion("org.mapstruct.mapstruct"); }

            /**
             * Returns the version associated to this alias: org.mapstruct.mapstruct.processor (1.4.2.Final)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getProcessor() { return getVersion("org.mapstruct.mapstruct.processor"); }

    }

    public static class OrgProjectlombokVersionAccessors extends VersionFactory  {

        public OrgProjectlombokVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: org.projectlombok.lombok (1.18.24)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getLombok() { return getVersion("org.projectlombok.lombok"); }

    }

    public static class OrgSpringframeworkVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootVersionAccessors vaccForOrgSpringframeworkBootVersionAccessors = new OrgSpringframeworkBootVersionAccessors(providers, config);
        private final OrgSpringframeworkSecurityVersionAccessors vaccForOrgSpringframeworkSecurityVersionAccessors = new OrgSpringframeworkSecurityVersionAccessors(providers, config);
        private final OrgSpringframeworkSessionVersionAccessors vaccForOrgSpringframeworkSessionVersionAccessors = new OrgSpringframeworkSessionVersionAccessors(providers, config);
        public OrgSpringframeworkVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the group of versions at versions.org.springframework.boot
         */
        public OrgSpringframeworkBootVersionAccessors getBoot() {
            return vaccForOrgSpringframeworkBootVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.org.springframework.security
         */
        public OrgSpringframeworkSecurityVersionAccessors getSecurity() {
            return vaccForOrgSpringframeworkSecurityVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.org.springframework.session
         */
        public OrgSpringframeworkSessionVersionAccessors getSession() {
            return vaccForOrgSpringframeworkSessionVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringVersionAccessors vaccForOrgSpringframeworkBootSpringVersionAccessors = new OrgSpringframeworkBootSpringVersionAccessors(providers, config);
        public OrgSpringframeworkBootVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the group of versions at versions.org.springframework.boot.spring
         */
        public OrgSpringframeworkBootSpringVersionAccessors getSpring() {
            return vaccForOrgSpringframeworkBootSpringVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringBootVersionAccessors vaccForOrgSpringframeworkBootSpringBootVersionAccessors = new OrgSpringframeworkBootSpringBootVersionAccessors(providers, config);
        public OrgSpringframeworkBootSpringVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the group of versions at versions.org.springframework.boot.spring.boot
         */
        public OrgSpringframeworkBootSpringBootVersionAccessors getBoot() {
            return vaccForOrgSpringframeworkBootSpringBootVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringBootStarterVersionAccessors vaccForOrgSpringframeworkBootSpringBootStarterVersionAccessors = new OrgSpringframeworkBootSpringBootStarterVersionAccessors(providers, config);
        public OrgSpringframeworkBootSpringBootVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: org.springframework.boot.spring.boot.devtools (2.6.6)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getDevtools() { return getVersion("org.springframework.boot.spring.boot.devtools"); }

        /**
         * Returns the group of versions at versions.org.springframework.boot.spring.boot.starter
         */
        public OrgSpringframeworkBootSpringBootStarterVersionAccessors getStarter() {
            return vaccForOrgSpringframeworkBootSpringBootStarterVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootStarterVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringBootStarterDataVersionAccessors vaccForOrgSpringframeworkBootSpringBootStarterDataVersionAccessors = new OrgSpringframeworkBootSpringBootStarterDataVersionAccessors(providers, config);
        public OrgSpringframeworkBootSpringBootStarterVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: org.springframework.boot.spring.boot.starter.security (2.6.6)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSecurity() { return getVersion("org.springframework.boot.spring.boot.starter.security"); }

            /**
             * Returns the version associated to this alias: org.springframework.boot.spring.boot.starter.test (2.6.6)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getTest() { return getVersion("org.springframework.boot.spring.boot.starter.test"); }

            /**
             * Returns the version associated to this alias: org.springframework.boot.spring.boot.starter.web (2.6.6)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getWeb() { return getVersion("org.springframework.boot.spring.boot.starter.web"); }

        /**
         * Returns the group of versions at versions.org.springframework.boot.spring.boot.starter.data
         */
        public OrgSpringframeworkBootSpringBootStarterDataVersionAccessors getData() {
            return vaccForOrgSpringframeworkBootSpringBootStarterDataVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootStarterDataVersionAccessors extends VersionFactory  {

        public OrgSpringframeworkBootSpringBootStarterDataVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: org.springframework.boot.spring.boot.starter.data.mongodb (2.6.6)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getMongodb() { return getVersion("org.springframework.boot.spring.boot.starter.data.mongodb"); }

            /**
             * Returns the version associated to this alias: org.springframework.boot.spring.boot.starter.data.rest (2.6.6)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getRest() { return getVersion("org.springframework.boot.spring.boot.starter.data.rest"); }

    }

    public static class OrgSpringframeworkSecurityVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkSecuritySpringVersionAccessors vaccForOrgSpringframeworkSecuritySpringVersionAccessors = new OrgSpringframeworkSecuritySpringVersionAccessors(providers, config);
        public OrgSpringframeworkSecurityVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the group of versions at versions.org.springframework.security.spring
         */
        public OrgSpringframeworkSecuritySpringVersionAccessors getSpring() {
            return vaccForOrgSpringframeworkSecuritySpringVersionAccessors;
        }

    }

    public static class OrgSpringframeworkSecuritySpringVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkSecuritySpringSecurityVersionAccessors vaccForOrgSpringframeworkSecuritySpringSecurityVersionAccessors = new OrgSpringframeworkSecuritySpringSecurityVersionAccessors(providers, config);
        public OrgSpringframeworkSecuritySpringVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the group of versions at versions.org.springframework.security.spring.security
         */
        public OrgSpringframeworkSecuritySpringSecurityVersionAccessors getSecurity() {
            return vaccForOrgSpringframeworkSecuritySpringSecurityVersionAccessors;
        }

    }

    public static class OrgSpringframeworkSecuritySpringSecurityVersionAccessors extends VersionFactory  {

        public OrgSpringframeworkSecuritySpringSecurityVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: org.springframework.security.spring.security.test (5.6.2)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getTest() { return getVersion("org.springframework.security.spring.security.test"); }

    }

    public static class OrgSpringframeworkSessionVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkSessionSpringVersionAccessors vaccForOrgSpringframeworkSessionSpringVersionAccessors = new OrgSpringframeworkSessionSpringVersionAccessors(providers, config);
        public OrgSpringframeworkSessionVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the group of versions at versions.org.springframework.session.spring
         */
        public OrgSpringframeworkSessionSpringVersionAccessors getSpring() {
            return vaccForOrgSpringframeworkSessionSpringVersionAccessors;
        }

    }

    public static class OrgSpringframeworkSessionSpringVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkSessionSpringSessionVersionAccessors vaccForOrgSpringframeworkSessionSpringSessionVersionAccessors = new OrgSpringframeworkSessionSpringSessionVersionAccessors(providers, config);
        public OrgSpringframeworkSessionSpringVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the group of versions at versions.org.springframework.session.spring.session
         */
        public OrgSpringframeworkSessionSpringSessionVersionAccessors getSession() {
            return vaccForOrgSpringframeworkSessionSpringSessionVersionAccessors;
        }

    }

    public static class OrgSpringframeworkSessionSpringSessionVersionAccessors extends VersionFactory  {

        public OrgSpringframeworkSessionSpringSessionVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: org.springframework.session.spring.session.core (2.6.2)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getCore() { return getVersion("org.springframework.session.spring.session.core"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
