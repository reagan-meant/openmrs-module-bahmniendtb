package org.openmrs.module.bahmniendtb.dataintegrity.rules;

import org.bahmni.module.dataintegrity.rule.RuleDefn;
import org.bahmni.module.dataintegrity.rule.RuleResult;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.dataintegrity.service.DataintegrityRuleService;
import org.openmrs.module.bahmniendtb.dataintegrity.service.ObsQueryHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EndTBDrugConsent implements RuleDefn{
    DataintegrityRuleService ruleService;

    public EndTBDrugConsent() {
        ruleService = Context.getRegisteredComponent("dataintegrityRuleService", DataintegrityRuleService.class);
    }

    @Override
    public List<RuleResult> evaluate() {
        HashMap<List<String>, List<String>> conceptValuesMap = new HashMap<>();
        ObsQueryHelper consentObsQuery = new ObsQueryHelper();
        consentObsQuery.setConceptPath("TI, Has the endTB Observational Study Consent Form been explained and signed");
//        consentObsQuery.setObsValuesAllowed(As)
//        consentObsQueryonsentObsQuery.setObsValuesAllowed(As)

        conceptValuesMap.put(Arrays.asList("TI, Has the endTB Observational Study Consent Form been explained and signed"), Arrays.asList(""));

//        List<RuleResult> results
//                = ruleService.getAllByObsAndDrugs(Arrays.asList("Bedaquiline", "Delamanid"), conceptValuesMap);

        return null;
    }
}
