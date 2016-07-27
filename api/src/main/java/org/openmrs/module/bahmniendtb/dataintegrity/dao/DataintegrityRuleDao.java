package org.openmrs.module.bahmniendtb.dataintegrity.dao;

import org.bahmni.module.dataintegrity.rule.RuleResult;
import org.openmrs.PatientProgram;

import java.util.List;
import java.util.Map;

public interface DataintegrityRuleDao {

    List<RuleResult<PatientProgram>> getAllByObsAndDrugs(List<String> drugsList, Map<String, List<String>> codedObs);
}
