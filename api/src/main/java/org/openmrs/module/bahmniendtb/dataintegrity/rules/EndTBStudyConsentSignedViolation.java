package org.openmrs.module.bahmniendtb.dataintegrity.rules;

import org.openmrs.module.dataintegrity.rule.RuleDefn;
import org.openmrs.module.dataintegrity.rule.RuleResult;
import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.dataintegrity.rules.helper.TIFormInconsistencyHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.bahmniendtb.EndTBConstants.*;


public class EndTBStudyConsentSignedViolation implements RuleDefn<PatientProgram> {
    private ConceptService conceptService;

    private TIFormInconsistencyHelper tiFormInconsistencyHelper;

    public EndTBStudyConsentSignedViolation() {
        conceptService = Context.getConceptService();
        tiFormInconsistencyHelper = Context.getRegisteredComponent("TIFormInconsistencyHelper", TIFormInconsistencyHelper.class);
    }

    public EndTBStudyConsentSignedViolation(TIFormInconsistencyHelper tiFormInconsistencyHelper, ConceptService conceptService) {
        this.tiFormInconsistencyHelper = tiFormInconsistencyHelper;
        this.conceptService = conceptService;
    }

    @Override
    public List<RuleResult<PatientProgram>> evaluate() {
        List<Concept> unacceptableConsentResponses = new ArrayList<>();
        addConceptByNameToList(Arrays.asList(UNKNOWN), unacceptableConsentResponses);

        return tiFormInconsistencyHelper.getInconsistenciesForQuestion(FSN_TREATMENT_INITIATION_FORM, FSN_TI_ENDTB_STUDY_CONSENT_QUESTION, unacceptableConsentResponses);
    }

    private void addConceptByNameToList(List<String> conceptNames, List<Concept> listToAdd) {
        for (String concept : conceptNames) {
            listToAdd.add(conceptService.getConceptByName(concept));
        }
    }
}
