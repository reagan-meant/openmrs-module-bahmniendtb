package org.openmrs.module.bahmniendtb.dataintegrity.rules;

import org.bahmni.module.dataintegrity.rule.RuleResult;
import org.bahmni.module.dataintegrity.rule.impl.PatientProgramRuleDefn;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.dataintegrity.dao.DataintegrityRuleDao;
import org.openmrs.module.bahmniendtb.dataintegrity.dao.impl.DataintegrityRuleDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DrugConsent extends PatientProgramRuleDefn {
    DataintegrityRuleDao dataintegrityRuleDao;

    public DrugConsent() {
        dataintegrityRuleDao = Context.getRegisteredComponent("DataintegrityRuleDao", DataintegrityRuleDaoImpl.class);
    }

    @Override
    public List<RuleResult<PatientProgram>> evaluate() {
        List<RuleResult<PatientProgram>> ruleResults = new ArrayList<>();
        HashMap<String, List<String>> conceptValuesMap = new HashMap<>();

        conceptValuesMap.put("TI, Has the Treatment with New Drugs Consent Form been explained and signed", Arrays.asList("True"));

        List<RuleResult<PatientProgram>> results
                = dataintegrityRuleDao.getAllByObsAndDrugs(Arrays.asList("Bedaquiline", "Delamanid"), conceptValuesMap);

        for(RuleResult<PatientProgram> result : results) ruleResults.add(result);

        return ruleResults;
    }
}


