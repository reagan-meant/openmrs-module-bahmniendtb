package org.openmrs.module.bahmniendtb.dataintegrity.rules;

import org.bahmni.module.dataintegrity.rule.RuleDefn;
import org.bahmni.module.dataintegrity.rule.RuleResult;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.dataintegrity.service.DataintegrityRuleService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DrugConsent implements RuleDefn {
    DataintegrityRuleService ruleService;

    public DrugConsent() {
        ruleService = Context.getRegisteredComponent("dataintegrityRuleService", DataintegrityRuleService.class);
    }

    @Override
    public List<RuleResult> evaluate() {
        HashMap<String, List<String>> conceptValuesMap = new HashMap<>();

        return ruleService.getDrugConsentResults();
    }
}


