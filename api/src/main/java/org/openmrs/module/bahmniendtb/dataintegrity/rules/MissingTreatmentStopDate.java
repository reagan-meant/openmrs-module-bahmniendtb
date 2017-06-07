package org.openmrs.module.bahmniendtb.dataintegrity.rules;

import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.dataintegrity.rules.helper.OutcomeFormHelper;
import org.openmrs.module.dataintegrity.rule.RuleResult;

import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.bahmniendtb.EndTBConstants.EOT_OUTCOME;
import static org.openmrs.module.bahmniendtb.EndTBConstants.EOT_STOP_DATE;

public class MissingTreatmentStopDate extends EndTbRuleDefinition<PatientProgram> {

    private ConceptService conceptService;
    private OutcomeFormHelper outcomeFormHelper;

    public MissingTreatmentStopDate() {
        conceptService = Context.getConceptService();
        outcomeFormHelper = Context.getRegisteredComponent("outcomeFormHelper", OutcomeFormHelper.class);
    }

    public MissingTreatmentStopDate(OutcomeFormHelper outcomeFormHelper, ConceptService conceptService) {
        this.outcomeFormHelper = outcomeFormHelper;
        this.conceptService = conceptService;
    }

    @Override
    public List<RuleResult<PatientProgram>> evaluate() {
        Concept treatmentStopDateConcept = conceptService.getConceptByName(EOT_STOP_DATE);
        Concept treatmentOutcomeConcept = conceptService.getConceptByName(EOT_OUTCOME);
        return outcomeFormHelper.retrieveMissingTreatmentStopDate(Arrays.asList(treatmentStopDateConcept, treatmentOutcomeConcept));
    }
}
