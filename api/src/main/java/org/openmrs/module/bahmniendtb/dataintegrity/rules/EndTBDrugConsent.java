package org.openmrs.module.bahmniendtb.dataintegrity.rules;

import org.bahmni.module.dataintegrity.rule.RuleDefn;
import org.bahmni.module.dataintegrity.rule.RuleResult;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.dataintegrity.service.DataintegrityRuleService;

import java.util.ArrayList;
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
        HashMap<String, List<String>> conceptValuesMap = new HashMap<>();

        conceptValuesMap.put("TI, Has the endTB Observational Study Consent Form been explained and signed", Arrays.asList("True"));

        List<RuleResult> results
                = ruleService.getAllByObsAndDrugs(Arrays.asList("Bedaquiline", "Delamanid"), conceptValuesMap);

        return results;
    }
}
