package ca.uhn.fhir.jpa.starter;


import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.server.interceptor.auth.AuthorizationInterceptor;
import ca.uhn.fhir.rest.server.interceptor.auth.IRuleApplier;
import ca.uhn.fhir.rest.server.interceptor.auth.PolicyEnum;
import ca.uhn.fhir.rest.server.interceptor.consent.ConsentOperationStatusEnum;
import ca.uhn.fhir.rest.server.interceptor.consent.ConsentOutcome;
import ca.uhn.fhir.rest.server.interceptor.consent.RuleFilteringConsentService;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
public class RuleFilteringConsentTest {

	@Mock
	IRuleApplier myRuleApplier;
	RuleFilteringConsentService myRuleFilteringConsentService;
	ServletRequestDetails myRequestDetails = new ServletRequestDetails();

	@BeforeEach
	void setUp() {
		myRequestDetails.setRestOperationType(RestOperationTypeEnum.SEARCH_TYPE);
		myRuleFilteringConsentService = new RuleFilteringConsentService(myRuleApplier);
	}


	@Test
	void allowPasses() {
		when(myRuleApplier.applyRulesAndReturnDecision(any(), any(), any(), any(), any(), any()))
			.thenReturn(new AuthorizationInterceptor.Verdict(PolicyEnum.ALLOW, null));

		ConsentOutcome consentDecision = myRuleFilteringConsentService.canSeeResource(myRequestDetails, null, null);

		assertThat(consentDecision.getStatus(), equalTo(ConsentOperationStatusEnum.PROCEED));

	}

	@Test
	void denyIsRejected() {
		when(myRuleApplier.applyRulesAndReturnDecision(any(), any(), any(), any(), any(), any()))
			.thenReturn(new AuthorizationInterceptor.Verdict(PolicyEnum.DENY, null));

		ConsentOutcome consentDecision = myRuleFilteringConsentService.canSeeResource(myRequestDetails, null, null);

		assertThat(consentDecision.getStatus(), equalTo(ConsentOperationStatusEnum.REJECT));
	}

}
