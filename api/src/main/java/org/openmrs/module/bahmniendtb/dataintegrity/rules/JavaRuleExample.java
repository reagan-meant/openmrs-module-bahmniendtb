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

public class JavaRuleExample extends PatientProgramRuleDefn {

    DataintegrityRuleDao dataintegrityRuleDao;

    public JavaRuleExample() {
        dataintegrityRuleDao = Context.getRegisteredComponent("DataintegrityRuleDao", DataintegrityRuleDaoImpl.class);
    }

    @Override
    public List<RuleResult<PatientProgram>> evaluate() {
        List<RuleResult<PatientProgram>> ruleResults = new ArrayList<>();
        HashMap<String, List<String>> conceptValuesMap = new HashMap<>();

        conceptValuesMap.put("BMI Abnormal", Arrays.asList("True", "False"));

        List<RuleResult<PatientProgram>> results
                = dataintegrityRuleDao.getAllByObsAndDrugs(Arrays.asList("Gatifloxacin", "Delamanid"), conceptValuesMap);

        for(RuleResult<PatientProgram> result : results) ruleResults.add(result);

        return ruleResults;
    }
}
