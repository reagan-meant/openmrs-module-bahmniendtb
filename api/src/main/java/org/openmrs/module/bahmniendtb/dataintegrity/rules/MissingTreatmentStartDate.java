package org.openmrs.module.bahmniendtb.dataintegrity.rules;

import org.openmrs.module.dataintegrity.DataIntegrityRule;
import org.openmrs.module.dataintegrity.rule.RuleDefinition;
import org.openmrs.module.dataintegrity.rule.RuleResult;
import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.dataintegrity.rules.helper.TIFormInconsistencyHelper;

import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.bahmniendtb.EndTBConstants.TI_START_DATE;

public class MissingTreatmentStartDate implements RuleDefinition<PatientProgram> {

    private ConceptService conceptService;
    private TIFormInconsistencyHelper tiFormInconsistencyHelper;

    public MissingTreatmentStartDate() {
        conceptService = Context.getConceptService();
        tiFormInconsistencyHelper = Context.getRegisteredComponent("TIFormInconsistencyHelper", TIFormInconsistencyHelper.class);
    }

    public MissingTreatmentStartDate(TIFormInconsistencyHelper tiFormInconsistencyHelper, ConceptService conceptService) {
        this.tiFormInconsistencyHelper = tiFormInconsistencyHelper;
        this.conceptService = conceptService;
    }

    @Override
    public List<RuleResult<PatientProgram>> evaluate() {
        Concept treatmentStartDateConcept = conceptService.getConceptByName(TI_START_DATE);
        return tiFormInconsistencyHelper.getDataWithMissingStartTreatmentDate(Arrays.asList(treatmentStartDateConcept));
    }

    @Override
    public DataIntegrityRule getRule() {
        return null;
    }
}
