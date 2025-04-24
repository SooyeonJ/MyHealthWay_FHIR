package ca.uhn.fhir.jpa.starter.common.validation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.interceptor.validation.IRepositoryValidatingRule;
import ca.uhn.fhir.jpa.interceptor.validation.RepositoryValidatingInterceptor;
import ca.uhn.fhir.jpa.interceptor.validation.RepositoryValidatingRuleBuilder;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.jpa.starter.annotations.OnR4Condition;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ca.uhn.fhir.jpa.starter.common.validation.IRepositoryValidationInterceptorFactory.ENABLE_REPOSITORY_VALIDATING_INTERCEPTOR;

/**
 * This class can be customized to enable the {@link ca.uhn.fhir.jpa.interceptor.validation.RepositoryValidatingInterceptor}
 * on this server.
 * <p>
 * The <code>enable_repository_validating_interceptor</code> property must be enabled in <code>application.yaml</code>
 * in order to use this class.
 */
@ConditionalOnProperty(prefix = "hapi.fhir", name = ENABLE_REPOSITORY_VALIDATING_INTERCEPTOR, havingValue = "true")
@Configuration
@Conditional(OnR4Condition.class)
public class RepositoryValidationInterceptorFactoryR4 implements IRepositoryValidationInterceptorFactory {

	private final FhirContext fhirContext;
	private final RepositoryValidatingRuleBuilder repositoryValidatingRuleBuilder;
	private final IFhirResourceDao structureDefinitionResourceProvider;

	public RepositoryValidationInterceptorFactoryR4(RepositoryValidatingRuleBuilder repositoryValidatingRuleBuilder, DaoRegistry daoRegistry) {
		this.repositoryValidatingRuleBuilder = repositoryValidatingRuleBuilder;
		this.fhirContext = daoRegistry.getSystemDao().getContext();
		structureDefinitionResourceProvider = daoRegistry.getResourceDao("StructureDefinition");

	}

	@Override
	public RepositoryValidatingInterceptor buildUsingStoredStructureDefinitions() {

		IBundleProvider results = structureDefinitionResourceProvider.search(new SearchParameterMap().add(StructureDefinition.SP_KIND, new TokenParam("resource")));
		Map<String, List<StructureDefinition>> structureDefintions = results.getResources(0, results.size())
			.stream()
			.map(StructureDefinition.class::cast)
			.collect(Collectors.groupingBy(StructureDefinition::getType));

		structureDefintions.forEach((key, value) -> {
			String[] urls = value.stream().map(StructureDefinition::getUrl).toArray(String[]::new);
			repositoryValidatingRuleBuilder.forResourcesOfType(key).requireAtLeastOneProfileOf(urls).and().requireValidationToDeclaredProfiles().neverReject();
		});

		List<IRepositoryValidatingRule> rules = repositoryValidatingRuleBuilder.build();
		return new RepositoryValidatingInterceptor(fhirContext, rules);
	}

	@Override
	public RepositoryValidatingInterceptor build() {

		// Customize the ruleBuilder here to have the rules you want! We will give a simple example
		// of enabling validation for all Patient resources
		repositoryValidatingRuleBuilder
			.forResourcesOfType("AllergyIntolerance").requireAtLeastOneProfileOf("https://simplifier.net/fhir/MyHealthWay/StructureDefinition/AllergyIntolerance", "https://hins.or.kr/fhir/MyHealthWay/StructureDefinition/myhealthway-allergyintolerance")
			.and().requireValidationToDeclaredProfiles().neverReject().tagOnSeverity(ResultSeverityEnum.ERROR, "http://MyHealthWay/tag", "validation-failure")
			.forResourcesOfType("Condition").requireAtLeastOneProfileOf("https://simplifier.net/fhir/MyHealthWay/StructureDefinition/Condition", "https://hins.or.kr/fhir/MyHealthWay/StructureDefinition/myhealthway-condition")
			.and().requireValidationToDeclaredProfiles().neverReject().tagOnSeverity(ResultSeverityEnum.ERROR, "http://MyHealthWay/tag", "validation-failure")
			.forResourcesOfType("DiagnosticReport").requireAtLeastOneProfileOf("https://simplifier.net/fhir/MyHealthWay/StructureDefinition/DiagnosticReportImaging", "https://simplifier.net/fhir/MyHealthWay/StructureDefinition/DiagnosticReportPathology", "https://hins.or.kr/fhir/MyHealthWay/StructureDefinition/myhealthway-diagnosticreport-imaging", "https://hins.or.kr/fhir/MyHealthWay/StructureDefinition/myhealthway-diagnosticreport-pathology")
			.and().requireValidationToDeclaredProfiles().neverReject().tagOnSeverity(ResultSeverityEnum.ERROR, "http://MyHealthWay/tag", "validation-failure")
			.forResourcesOfType("DocumentReference").requireAtLeastOneProfileOf("https://simplifier.net/fhir/MyHealthWay/StructureDefinition/DocumentReference", "https://hins.or.kr/fhir/MyHealthWay/StructureDefinition/myhealthway-documentreference")
			.and().requireValidationToDeclaredProfiles().neverReject().tagOnSeverity(ResultSeverityEnum.ERROR, "http://MyHealthWay/tag", "validation-failure")
			.forResourcesOfType("Encounter").requireAtLeastOneProfileOf("https://simplifier.net/fhir/MyHealthWay/StructureDefinition/Encounter", "https://hins.or.kr/fhir/MyHealthWay/StructureDefinition/myhealthway-encounter")
			.and().requireValidationToDeclaredProfiles().neverReject().tagOnSeverity(ResultSeverityEnum.ERROR, "http://MyHealthWay/tag", "validation-failure")
			.forResourcesOfType("Endpoint").requireAtLeastOneProfileOf("https://simplifier.net/fhir/MyHealthWay/StructureDefinition/Endpoint", "https://hins.or.kr/fhir/MyHealthWay/StructureDefinition/myhealthway-endpoint")
			.and().requireValidationToDeclaredProfiles().neverReject().tagOnSeverity(ResultSeverityEnum.ERROR, "http://MyHealthWay/tag", "validation-failure")
			.forResourcesOfType("ImagingStudy").requireAtLeastOneProfileOf("https://simplifier.net/fhir/MyHealthWay/StructureDefinition/ImagingStudy", "https://hins.or.kr/fhir/MyHealthWay/StructureDefinition/myhealthway-imagingstudy")
			.and().requireValidationToDeclaredProfiles().neverReject().tagOnSeverity(ResultSeverityEnum.ERROR, "http://MyHealthWay/tag", "validation-failure")
			.forResourcesOfType("Media").requireAtLeastOneProfileOf("https://simplifier.net/fhir/MyHealthWay/StructureDefinition/Media", "https://hins.or.kr/fhir/MyHealthWay/StructureDefinition/myhealthway-media")
			.and().requireValidationToDeclaredProfiles().neverReject().tagOnSeverity(ResultSeverityEnum.ERROR, "http://MyHealthWay/tag", "validation-failure")
			.forResourcesOfType("MedicationRequest").requireAtLeastOneProfileOf("https://simplifier.net/fhir/MyHealthWay/StructureDefinition/MedicationRequest", "https://hins.or.kr/fhir/MyHealthWay/StructureDefinition/myhealthway-medicationrequest")
			.and().requireValidationToDeclaredProfiles().neverReject().tagOnSeverity(ResultSeverityEnum.ERROR, "http://MyHealthWay/tag", "validation-failure")
			.forResourcesOfType("Observation").requireAtLeastOneProfileOf("https://simplifier.net/fhir/MyHealthWay/StructureDefinition/ObservationExam", "https://simplifier.net/fhir/MyHealthWay/StructureDefinition/ObservationLaboratory", "https://hins.or.kr/fhir/MyHealthWay/StructureDefinition/myhealthway-observation-exam", "https://hins.or.kr/fhir/MyHealthWay/StructureDefinition/myhealthway-observation-laboratory")
			.and().requireValidationToDeclaredProfiles().neverReject().tagOnSeverity(ResultSeverityEnum.ERROR, "http://MyHealthWay/tag", "validation-failure")
			.forResourcesOfType("Organization").requireAtLeastOneProfileOf("https://simplifier.net/fhir/MyHealthWay/StructureDefinition/Organization", "https://hins.or.kr/fhir/MyHealthWay/StructureDefinition/myhealthway-organization")
			.and().requireValidationToDeclaredProfiles().neverReject().tagOnSeverity(ResultSeverityEnum.ERROR, "http://MyHealthWay/tag", "validation-failure")
			.forResourcesOfType("Patient").requireAtLeastOneProfileOf("https://simplifier.net/fhir/MyHealthWay/StructureDefinition/Patient", "https://hins.or.kr/fhir/MyHealthWay/StructureDefinition/myhealthway-patient")
			.and().requireValidationToDeclaredProfiles().neverReject().tagOnSeverity(ResultSeverityEnum.ERROR, "http://MyHealthWay/tag", "validation-failure")
			.forResourcesOfType("Practitioner").requireAtLeastOneProfileOf("https://simplifier.net/fhir/MyHealthWay/StructureDefinition/Practitioner", "https://hins.or.kr/fhir/MyHealthWay/StructureDefinition/myhealthway-practitioner")
			.and().requireValidationToDeclaredProfiles().neverReject().tagOnSeverity(ResultSeverityEnum.ERROR, "http://MyHealthWay/tag", "validation-failure")
			.forResourcesOfType("PractitionerRole").requireAtLeastOneProfileOf("https://simplifier.net/fhir/MyHealthWay/StructureDefinition/PractitionerRole", "https://hins.or.kr/fhir/MyHealthWay/StructureDefinition/myhealthway-practitionerrole")
			.and().requireValidationToDeclaredProfiles().neverReject().tagOnSeverity(ResultSeverityEnum.ERROR, "http://MyHealthWay/tag", "validation-failure")
			.forResourcesOfType("Procedure").requireAtLeastOneProfileOf("https://simplifier.net/fhir/MyHealthWay/StructureDefinition/Procedure", "https://hins.or.kr/fhir/MyHealthWay/StructureDefinition/myhealthway-procedure")
			.and().requireValidationToDeclaredProfiles().neverReject().tagOnSeverity(ResultSeverityEnum.ERROR, "http://MyHealthWay/tag", "validation-failure");

		// Do not customize below this line
		List<IRepositoryValidatingRule> rules = repositoryValidatingRuleBuilder.build();
		return new RepositoryValidatingInterceptor(fhirContext, rules);
	}
}
