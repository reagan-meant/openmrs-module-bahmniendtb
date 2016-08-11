package org.openmrs.module.bahmniendtb.dataintegrity.rules;

import org.bahmni.module.dataintegrity.rule.RuleDefn;
import org.bahmni.module.dataintegrity.rule.RuleResult;
import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.dataintegrity.rules.helper.MissingOutcomeHelper;

import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.bahmniendtb.EndTBConstants.*;


public class MissingOutcomeViolation implements RuleDefn<PatientProgram> {

    private ConceptService conceptService;
    private MissingOutcomeHelper missingOutcomeHelper;

    public MissingOutcomeViolation() {
        conceptService = Context.getConceptService();
        missingOutcomeHelper = Context.getRegisteredComponent("missingOutcomeHelper", MissingOutcomeHelper.class);
    }

    public MissingOutcomeViolation(MissingOutcomeHelper missingOutcomeHelper, ConceptService conceptService) {
        this.missingOutcomeHelper = missingOutcomeHelper;
        this.conceptService = conceptService;
    }

    @Override
    public List<RuleResult<PatientProgram>> evaluate() {
        Concept treatmentStartDateConcept = conceptService.getConceptByName(TI_START_DATE);
        Concept treatmentStopDateConcept = conceptService.getConceptByName(EOT_STOP_DATE);
        return missingOutcomeHelper.fetchMissingOutComeData(Arrays.asList(treatmentStartDateConcept, treatmentStopDateConcept));
    }
}
