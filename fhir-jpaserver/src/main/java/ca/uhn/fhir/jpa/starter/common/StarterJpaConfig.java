package ca.uhn.fhir.jpa.starter.common;

import ca.uhn.fhir.batch2.coordinator.JobDefinitionRegistry;
import ca.uhn.fhir.batch2.jobs.imprt.BulkDataImportProvider;
import ca.uhn.fhir.batch2.jobs.reindex.ReindexJobParameters;
import ca.uhn.fhir.batch2.jobs.reindex.ReindexProvider;
import ca.uhn.fhir.batch2.model.JobDefinition;
import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.context.support.IValidationSupport;
import ca.uhn.fhir.interceptor.api.IInterceptorBroadcaster;
import ca.uhn.fhir.jpa.api.IDaoRegistry;
import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.batch.config.NonPersistedBatchConfigurer;
import ca.uhn.fhir.jpa.binary.interceptor.BinaryStorageInterceptor;
import ca.uhn.fhir.jpa.binary.provider.BinaryAccessProvider;
import ca.uhn.fhir.jpa.bulk.export.provider.BulkDataExportProvider;
import ca.uhn.fhir.jpa.config.util.HapiEntityManagerFactoryUtil;
import ca.uhn.fhir.jpa.config.util.ResourceCountCacheUtil;
import ca.uhn.fhir.jpa.config.util.ValidationSupportConfigUtil;
import ca.uhn.fhir.jpa.dao.FulltextSearchSvcImpl;
import ca.uhn.fhir.jpa.dao.IFulltextSearchSvc;
import ca.uhn.fhir.jpa.dao.mdm.MdmLinkDaoJpaImpl;
import ca.uhn.fhir.jpa.dao.search.HSearchSortHelperImpl;
import ca.uhn.fhir.jpa.dao.search.IHSearchSortHelper;
import ca.uhn.fhir.jpa.graphql.GraphQLProvider;
import ca.uhn.fhir.jpa.interceptor.CascadingDeleteInterceptor;
import ca.uhn.fhir.jpa.interceptor.validation.RepositoryValidatingInterceptor;
import ca.uhn.fhir.jpa.model.config.PartitionSettings;
import ca.uhn.fhir.jpa.packages.IPackageInstallerSvc;
import ca.uhn.fhir.jpa.packages.PackageInstallationSpec;
import ca.uhn.fhir.jpa.partition.IPartitionLookupSvc;
import ca.uhn.fhir.jpa.partition.PartitionManagementProvider;
import ca.uhn.fhir.jpa.provider.*;
import ca.uhn.fhir.jpa.provider.dstu3.JpaConformanceProviderDstu3;
import ca.uhn.fhir.jpa.search.DatabaseBackedPagingProvider;
import ca.uhn.fhir.jpa.search.IStaleSearchDeletingSvc;
import ca.uhn.fhir.jpa.search.StaleSearchDeletingSvcImpl;
import ca.uhn.fhir.jpa.starter.AppProperties;
import ca.uhn.fhir.jpa.starter.annotations.OnCorsPresent;
import ca.uhn.fhir.jpa.starter.annotations.OnImplementationGuidesPresent;
import ca.uhn.fhir.jpa.starter.common.validation.IRepositoryValidationInterceptorFactory;
import ca.uhn.fhir.jpa.starter.util.TiberoDialect;
import ca.uhn.fhir.jpa.subscription.util.SubscriptionDebugLogInterceptor;
import ca.uhn.fhir.jpa.util.ResourceCountCache;
import ca.uhn.fhir.jpa.validation.JpaValidationSupportChain;
import ca.uhn.fhir.mdm.dao.IMdmLinkDao;
import ca.uhn.fhir.mdm.provider.MdmProviderLoader;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.narrative2.NullNarrativeGenerator;
import ca.uhn.fhir.rest.api.IResourceSupportedSvc;
import ca.uhn.fhir.rest.openapi.OpenApiInterceptor;
import ca.uhn.fhir.rest.server.*;
import ca.uhn.fhir.rest.server.interceptor.*;
import ca.uhn.fhir.rest.server.interceptor.partition.RequestTenantPartitionInterceptor;
import ca.uhn.fhir.rest.server.provider.ResourceProviderFactory;
import ca.uhn.fhir.rest.server.tenant.UrlBaseTenantIdentificationStrategy;
import ca.uhn.fhir.rest.server.util.ISearchParamRegistry;
import ca.uhn.fhir.validation.IValidatorModule;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import kr.co.iteyes.fhir.jpa.security.interceptor.CustomAuthorizationInterceptor;
import kr.co.iteyes.fhir.jpa.security.interceptor.CustomRequestInterceptor;
import kr.co.iteyes.fhir.jpa.security.interceptor.CustomResponseInterceptor;
import kr.co.iteyes.fhir.jpa.security.service.OAuthAuthorizeService;
import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.web.cors.CorsConfiguration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.*;

import static ca.uhn.fhir.jpa.starter.common.validation.IRepositoryValidationInterceptorFactory.ENABLE_REPOSITORY_VALIDATING_INTERCEPTOR;

@Configuration
public class StarterJpaConfig {

	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(StarterJpaConfig.class);

	@Bean
	public IFulltextSearchSvc fullTextSearchSvc() {
		return new FulltextSearchSvcImpl();
	}

	@Bean
	public IStaleSearchDeletingSvc staleSearchDeletingSvc() {
		return new StaleSearchDeletingSvcImpl();
	}

	@Primary
	@Bean
	public CachingValidationSupport validationSupportChain(JpaValidationSupportChain theJpaValidationSupportChain) {
		return ValidationSupportConfigUtil.newCachingValidationSupport(theJpaValidationSupportChain);
	}

	@Bean
	public BatchConfigurer batchConfigurer() {
		return new NonPersistedBatchConfigurer();
	}

	@Autowired
	private ConfigurableEnvironment configurableEnvironment;

	/**
	 * Customize the default/max page sizes for search results. You can set these however
	 * you want, although very large page sizes will require a lot of RAM.
	 */
	@Bean
	public DatabaseBackedPagingProvider databaseBackedPagingProvider(AppProperties appProperties) {
		DatabaseBackedPagingProvider pagingProvider = new DatabaseBackedPagingProvider();
		pagingProvider.setDefaultPageSize(appProperties.getDefault_page_size());
		pagingProvider.setMaximumPageSize(appProperties.getMax_page_size());
		return pagingProvider;
	}

	@Bean
	public IResourceSupportedSvc resourceSupportedSvc(IDaoRegistry theDaoRegistry) {
		return new DaoRegistryResourceSupportedSvc(theDaoRegistry);
	}

	@Bean(name = "myResourceCountsCache")
	public ResourceCountCache resourceCountsCache(IFhirSystemDao<?, ?> theSystemDao) {
		return ResourceCountCacheUtil.newResourceCountCache(theSystemDao);
	}

	@Primary
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource myDataSource, ConfigurableListableBeanFactory myConfigurableListableBeanFactory, FhirContext theFhirContext) {
		LocalContainerEntityManagerFactoryBean retVal = HapiEntityManagerFactoryUtil.newEntityManagerFactory(myConfigurableListableBeanFactory, theFhirContext);
		retVal.setPersistenceUnitName("HAPI_PU");

		try {
			retVal.setDataSource(myDataSource);
		} catch (Exception e) {
			throw new ConfigurationException("Could not set the data source due to a configuration issue", e);
		}
		// 원본
//		retVal.setJpaProperties(EnvironmentHelper.getHibernateProperties`(configurableEnvironment, myConfigurableListableBeanFactory));

		// tibero 사용을 위해 추가
		retVal.getJpaPropertyMap().put("hibernate.dialect", TiberoDialect.class.getName());
		retVal.getJpaPropertyMap().put("hibernate.search.enabled", false);
		return retVal;
	}

	@Bean
	@Primary
	public JpaTransactionManager hapiTransactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager retVal = new JpaTransactionManager();
		retVal.setEntityManagerFactory(entityManagerFactory);
		return retVal;
	}

	@Bean
	public IHSearchSortHelper hSearchSortHelper(ISearchParamRegistry mySearchParamRegistry) {
		return new HSearchSortHelperImpl(mySearchParamRegistry);
	}

	@Bean
	@ConditionalOnProperty(prefix = "hapi.fhir", name = ENABLE_REPOSITORY_VALIDATING_INTERCEPTOR, havingValue = "true")
	public RepositoryValidatingInterceptor repositoryValidatingInterceptor(IRepositoryValidationInterceptorFactory factory) {
		// Customize Rule Builder build();
//		factory.build();
//		return factory.buildUsingStoredStructureDefinitions();
		return factory.build();
	}

	@Bean
	public CustomAuthorizationInterceptor authorizationInterceptor(AppProperties appProperties) {
		CustomAuthorizationInterceptor authorizationInterceptor = new CustomAuthorizationInterceptor();

		return authorizationInterceptor;
	}

	@Bean
	public CustomResponseInterceptor customResponseInterceptor(AppProperties appProperties) {
		CustomResponseInterceptor customResponseInterceptor = new CustomResponseInterceptor();
		// crypto config
		customResponseInterceptor.setRsa_key_request_url(appProperties.getRsa_key_request_url());

		// oauth config
		customResponseInterceptor.setOauth_server_url(appProperties.getOauth_server_url());
		customResponseInterceptor.setOauth_server_clientid(appProperties.getOauth_server_clientid());
		customResponseInterceptor.setOauth_server_secretkey(appProperties.getOauth_server_secretkey());
		return customResponseInterceptor;
	}

	@Bean
	public CustomRequestInterceptor customRequestInterceptor(AppProperties appProperties) {
		CustomRequestInterceptor customRequestInterceptor = new CustomRequestInterceptor();
		// crypto config
		customRequestInterceptor.setRsa_key_request_url(appProperties.getRsa_key_request_url());
		// oauth config
		customRequestInterceptor.setOauth_server_url(appProperties.getOauth_server_url());
		customRequestInterceptor.setOauth_server_clientid(appProperties.getOauth_server_clientid());
		customRequestInterceptor.setOauth_server_secretkey(appProperties.getOauth_server_secretkey());
		return customRequestInterceptor;
	}


	@Bean
	public OAuthAuthorizeService oAuthAuthorizeService(AppProperties appProperties){

		OAuthAuthorizeService oAuthAuthorizeService = new OAuthAuthorizeService();
		// crypto config
		// oauth config
		oAuthAuthorizeService.setOauth_server_url(appProperties.getOauth_server_url());
		oAuthAuthorizeService.setOauth_server_clientid(appProperties.getOauth_server_clientid());
		oAuthAuthorizeService.setOauth_server_secretkey(appProperties.getOauth_server_secretkey());
		return oAuthAuthorizeService;
	}

	@Bean
	public LoggingInterceptor loggingInterceptor(AppProperties appProperties) {

		/*
		 * Add some logging for each request
		 */

		LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
		return loggingInterceptor;
	}

	@Bean("packageInstaller")
	@Primary
	@Conditional(OnImplementationGuidesPresent.class)
	public IPackageInstallerSvc packageInstaller(AppProperties appProperties, JobDefinition<ReindexJobParameters> reindexJobParametersJobDefinition, JobDefinitionRegistry jobDefinitionRegistry, IPackageInstallerSvc packageInstallerSvc) {
		jobDefinitionRegistry.addJobDefinitionIfNotRegistered(reindexJobParametersJobDefinition);

		if (appProperties.getImplementationGuides() != null) {
			Map<String, AppProperties.ImplementationGuide> guides = appProperties.getImplementationGuides();
			for (Map.Entry<String, AppProperties.ImplementationGuide> guide : guides.entrySet()) {
				PackageInstallationSpec packageInstallationSpec = new PackageInstallationSpec().setPackageUrl(guide.getValue().getUrl()).setName(guide.getValue().getName()).setVersion(guide.getValue().getVersion()).setInstallMode(PackageInstallationSpec.InstallModeEnum.STORE_AND_INSTALL);
				if (appProperties.getInstall_transitive_ig_dependencies()) {
					packageInstallationSpec.setFetchDependencies(true);
					packageInstallationSpec.setDependencyExcludes(ImmutableList.of("hl7.fhir.r2.core", "hl7.fhir.r3.core", "hl7.fhir.r4.core", "hl7.fhir.r5.core"));
				}
				packageInstallerSvc.install(packageInstallationSpec);
			}
		}
		return packageInstallerSvc;
	}

	@Bean
	@Primary
	/*
		This bean is currently necessary to override from MDM settings
	 */
	IMdmLinkDao mdmLinkDao() {
		return new MdmLinkDaoJpaImpl();
	}


	@Bean
	@Conditional(OnCorsPresent.class)
	public CorsInterceptor corsInterceptor(AppProperties appProperties) {
		// Define your CORS configuration. This is an example
		// showing a typical setup. You should customize this
		// to your specific needs
		ourLog.info("CORS is enabled on this server");
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedHeader(HttpHeaders.ORIGIN);
		config.addAllowedHeader(HttpHeaders.ACCEPT);
		config.addAllowedHeader(HttpHeaders.CONTENT_TYPE);
		config.addAllowedHeader(HttpHeaders.AUTHORIZATION);
		config.addAllowedHeader(HttpHeaders.CACHE_CONTROL);
		config.addAllowedHeader("x-fhir-starter");
		config.addAllowedHeader("X-Requested-With");
		config.addAllowedHeader("Prefer");

		List<String> allAllowedCORSOrigins = appProperties.getCors().getAllowed_origin();
		allAllowedCORSOrigins.forEach(config::addAllowedOriginPattern);
		ourLog.info("CORS allows the following origins: " + String.join(", ", allAllowedCORSOrigins));

		config.addExposedHeader("Location");
		config.addExposedHeader("Content-Location");
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
		config.setAllowCredentials(appProperties.getCors().getAllow_Credentials());

		// Create the interceptor and register it
		return new CorsInterceptor(config);
	}



	@Autowired
	private PartitionSettings myPartitionSettings;

	@Autowired
	private IPartitionLookupSvc myPartitionLookupSvc;

	@Autowired
	private PartitionManagementProvider partitionManagementProvider;

	@Bean
	public RestfulServer restfulServer(IFhirSystemDao<?, ?> fhirSystemDao, AppProperties appProperties, DaoRegistry daoRegistry, Optional<MdmProviderLoader> mdmProviderProvider, IJpaSystemProvider jpaSystemProvider, ResourceProviderFactory resourceProviderFactory, DaoConfig daoConfig, ISearchParamRegistry searchParamRegistry, IValidationSupport theValidationSupport, DatabaseBackedPagingProvider databaseBackedPagingProvider, LoggingInterceptor loggingInterceptor, Optional<TerminologyUploaderProvider> terminologyUploaderProvider, Optional<SubscriptionTriggeringProvider> subscriptionTriggeringProvider, Optional<CorsInterceptor> corsInterceptor, IInterceptorBroadcaster interceptorBroadcaster, Optional<BinaryAccessProvider> binaryAccessProvider, BinaryStorageInterceptor binaryStorageInterceptor, IValidatorModule validatorModule, Optional<GraphQLProvider> graphQLProvider, BulkDataExportProvider bulkDataExportProvider, BulkDataImportProvider bulkDataImportProvider, ValueSetOperationProvider theValueSetOperationProvider, ReindexProvider reindexProvider, PartitionManagementProvider partitionManagementProvider, Optional<RepositoryValidatingInterceptor> repositoryValidatingInterceptor, IPackageInstallerSvc packageInstallerSvc, CustomAuthorizationInterceptor authorizationInterceptor, CustomResponseInterceptor customResponseInterceptor, CustomRequestInterceptor customRequestInterceptor) {
		RestfulServer fhirServer = new RestfulServer(fhirSystemDao.getContext());

		List<String> supportedResourceTypes = appProperties.getSupported_resource_types();

		if (!supportedResourceTypes.isEmpty()) {
			if (!supportedResourceTypes.contains("SearchParameter")) {
				supportedResourceTypes.add("SearchParameter");
			}
			daoRegistry.setSupportedResourceTypes(supportedResourceTypes);
		}

		if (appProperties.getNarrative_enabled()) {
			fhirSystemDao.getContext().setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());
		} else {
			fhirSystemDao.getContext().setNarrativeGenerator(new NullNarrativeGenerator());
		}

		if (appProperties.getMdm_enabled()) mdmProviderProvider.get().loadProvider();

		fhirServer.registerProviders(resourceProviderFactory.createProviders());
		fhirServer.registerProvider(jpaSystemProvider);
		fhirServer.setServerConformanceProvider(calculateConformanceProvider(fhirSystemDao, fhirServer, daoConfig, searchParamRegistry, theValidationSupport));

		/*
		 * ETag Support
		 */

		if (!appProperties.getEtag_support_enabled()) fhirServer.setETagSupport(ETagSupportEnum.DISABLED);

		/*
		 * Default to JSON and pretty printing
		 */
		fhirServer.setDefaultPrettyPrint(appProperties.getDefault_pretty_print());

		/*
		 * Default encoding
		 */
		fhirServer.setDefaultResponseEncoding(appProperties.getDefault_encoding());

		/*
		 * This configures the server to page search results to and from
		 * the database, instead of only paging them to memory. This may mean
		 * a performance hit when performing searches that return lots of results,
		 * but makes the server much more scalable.
		 */

		fhirServer.setPagingProvider(databaseBackedPagingProvider);

		/*
		 * This interceptor formats the output using nice colourful
		 * HTML output when the request is detected to come from a
		 * browser.
		 */
		fhirServer.registerInterceptor(new ResponseHighlighterInterceptor());

		if (appProperties.getFhirpath_interceptor_enabled()) {
			fhirServer.registerInterceptor(new FhirPathFilterInterceptor());
		}

		fhirServer.registerInterceptor(loggingInterceptor);
		//Authrorization Interceptor with Oauth Token Wrote by syhan
		fhirServer.registerInterceptor(authorizationInterceptor);  // 주석처리 = GUI ver

		//Multitenancy Interceptor and Provider Wrote by syhan
//		fhirServer.registerProvider(partitionManagementProvider);
//		myPartitionSettings.setPartitioningEnabled(true);
//		fhirServer.setTenantIdentificationStrategy(new UrlBaseTenantIdentificationStrategy());
//		fhirServer.registerInterceptor(new RequestTenantPartitionInterceptor());

		fhirServer.registerInterceptor(customRequestInterceptor); // 주석처리 = GUI ver

		fhirServer.registerInterceptor(customResponseInterceptor); // 주석처리 = GUI ver

		/*
		 * If you are hosting this server at a specific DNS name, the server will try to
		 * figure out the FHIR base URL based on what the web container tells it, but
		 * this doesn't always work. If you are setting links in your search bundles that
		 * just refer to "localhost", you might want to use a server address strategy:
		 */
		String serverAddress = appProperties.getServer_address();
		if (!Strings.isNullOrEmpty(serverAddress)) {
			fhirServer.setServerAddressStrategy(new HardcodedServerAddressStrategy(serverAddress));
		} else if (appProperties.getUse_apache_address_strategy()) {
			boolean useHttps = appProperties.getUse_apache_address_strategy_https();
			fhirServer.setServerAddressStrategy(useHttps ? ApacheProxyAddressStrategy.forHttps() : ApacheProxyAddressStrategy.forHttp());
		} else {
			fhirServer.setServerAddressStrategy(new IncomingRequestAddressStrategy());
		}

		/*
		 * If you are using DSTU3+, you may want to add a terminology uploader, which allows
		 * uploading of external terminologies such as Snomed CT. Note that this uploader
		 * does not have any security attached (any anonymous user may use it by default)
		 * so it is a potential security vulnerability. Consider using an AuthorizationInterceptor
		 * with this feature.
		 */
		if (fhirSystemDao.getContext().getVersion().getVersion().isEqualOrNewerThan(FhirVersionEnum.DSTU3)) { // <-- ENABLED RIGHT NOW
			fhirServer.registerProvider(terminologyUploaderProvider.get());
		}

		// If you want to enable the $trigger-subscription operation to allow
		// manual triggering of a subscription delivery, enable this provider
		if (true) { // <-- ENABLED RIGHT NOW
			fhirServer.registerProvider(subscriptionTriggeringProvider.get());
		}

		corsInterceptor.ifPresent(fhirServer::registerInterceptor);
		if (daoConfig.getSupportedSubscriptionTypes().size() > 0) {
			// Subscription debug logging
			fhirServer.registerInterceptor(new SubscriptionDebugLogInterceptor());
		}

		if (appProperties.getAllow_cascading_deletes()) {
			CascadingDeleteInterceptor cascadingDeleteInterceptor = new CascadingDeleteInterceptor(fhirSystemDao.getContext(), daoRegistry, interceptorBroadcaster);
			fhirServer.registerInterceptor(cascadingDeleteInterceptor);
		}

		// Binary Storage
		if (appProperties.getBinary_storage_enabled() && binaryAccessProvider.isPresent()) {
			fhirServer.registerProvider(binaryAccessProvider.get());
			fhirServer.registerInterceptor(binaryStorageInterceptor);
		}

		// Validation
		if (validatorModule != null) {
			if (appProperties.getValidation().getRequests_enabled()) {
				RequestValidatingInterceptor interceptor = new RequestValidatingInterceptor();
				interceptor.setFailOnSeverity(ResultSeverityEnum.ERROR);
				interceptor.setValidatorModules(Collections.singletonList(validatorModule));
				fhirServer.registerInterceptor(interceptor);
			}
			if (appProperties.getValidation().getResponses_enabled()) {
				ResponseValidatingInterceptor interceptor = new ResponseValidatingInterceptor();
				interceptor.setFailOnSeverity(ResultSeverityEnum.ERROR);
				interceptor.setValidatorModules(Collections.singletonList(validatorModule));
				fhirServer.registerInterceptor(interceptor);
			}
		}

		// GraphQL
		if (appProperties.getGraphql_enabled()) {
			if (fhirSystemDao.getContext().getVersion().getVersion().isEqualOrNewerThan(FhirVersionEnum.DSTU3)) {
				fhirServer.registerProvider(graphQLProvider.get());
			}
		}

		if (appProperties.getOpenapi_enabled()) {
			fhirServer.registerInterceptor(new OpenApiInterceptor());
		}

		// Bulk Export
		if (appProperties.getBulk_export_enabled()) {
			fhirServer.registerProvider(bulkDataExportProvider);
		}

		//Bulk Import
		if (appProperties.getBulk_import_enabled()) {
			fhirServer.registerProvider(bulkDataImportProvider);
		}

		// valueSet Operations i.e $expand
		fhirServer.registerProvider(theValueSetOperationProvider);

		//reindex Provider $reindex
		fhirServer.registerProvider(reindexProvider);

		// Partitioning
		if (appProperties.getPartitioning() != null) {
			fhirServer.registerInterceptor(new RequestTenantPartitionInterceptor());
			fhirServer.setTenantIdentificationStrategy(new UrlBaseTenantIdentificationStrategy());
			fhirServer.registerProviders(partitionManagementProvider);
		}

		repositoryValidatingInterceptor.ifPresent(fhirServer::registerInterceptor);

		return fhirServer;
	}

	public static IServerConformanceProvider<?> calculateConformanceProvider(IFhirSystemDao fhirSystemDao, RestfulServer fhirServer, DaoConfig daoConfig, ISearchParamRegistry searchParamRegistry, IValidationSupport theValidationSupport) {
		FhirVersionEnum fhirVersion = fhirSystemDao.getContext().getVersion().getVersion();
		if (fhirVersion == FhirVersionEnum.DSTU2) {

			JpaConformanceProviderDstu2 confProvider = new JpaConformanceProviderDstu2(fhirServer, fhirSystemDao, daoConfig);
			confProvider.setImplementationDescription("HAPI FHIR DSTU2 Server");
			return confProvider;
		} else if (fhirVersion == FhirVersionEnum.DSTU3) {

			JpaConformanceProviderDstu3 confProvider = new JpaConformanceProviderDstu3(fhirServer, fhirSystemDao, daoConfig, searchParamRegistry);
			confProvider.setImplementationDescription("HAPI FHIR DSTU3 Server");
			return confProvider;
		} else if (fhirVersion == FhirVersionEnum.R4) {

			JpaCapabilityStatementProvider confProvider = new JpaCapabilityStatementProvider(fhirServer, fhirSystemDao, daoConfig, searchParamRegistry, theValidationSupport);
			confProvider.setImplementationDescription("HAPI FHIR R4 Server");
			return confProvider;
		} else if (fhirVersion == FhirVersionEnum.R5) {

			JpaCapabilityStatementProvider confProvider = new JpaCapabilityStatementProvider(fhirServer, fhirSystemDao, daoConfig, searchParamRegistry, theValidationSupport);
			confProvider.setImplementationDescription("HAPI FHIR R5 Server");
			return confProvider;
		} else {
			throw new IllegalStateException();
		}
	}
}