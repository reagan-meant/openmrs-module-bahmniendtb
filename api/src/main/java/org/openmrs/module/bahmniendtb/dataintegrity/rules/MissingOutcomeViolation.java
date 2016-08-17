package org.openmrs.module.bahmniendtb.dataintegrity.rules;

import org.openmrs.module.dataintegrity.rule.RuleDefn;
import org.openmrs.module.dataintegrity.rule.RuleResult;
import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.dataintegrity.rules.helper.OutcomeFormHelper;

import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.bahmniendtb.EndTBConstants.EOT_OUTCOME;
import static org.openmrs.module.bahmniendtb.EndTBConstants.EOT_STOP_DATE;
import static org.openmrs.module.bahmniendtb.EndTBConstants.TI_START_DATE;


public class MissingOutcomeViolation implements RuleDefn<PatientProgram> {

    private ConceptService conceptService;
    private OutcomeFormHelper outcomeFormHelper;

    public MissingOutcomeViolation() {
        conceptService = Context.getConceptService();
        outcomeFormHelper = Context.getRegisteredComponent("outcomeFormHelper", OutcomeFormHelper.class);
    }

    public MissingOutcomeViolation(OutcomeFormHelper outcomeFormHelper, ConceptService conceptService) {
        this.outcomeFormHelper = outcomeFormHelper;
        this.conceptService = conceptService;
    }

    @Override
    public List<RuleResult<PatientProgram>> evaluate() {
        Concept treatmentStartDateConcept = conceptService.getConceptByName(TI_START_DATE);
        Concept treatmentStopDateConcept = conceptService.getConceptByName(EOT_STOP_DATE);
        Concept outcomeConcept = conceptService.getConceptByName(EOT_OUTCOME);
        return outcomeFormHelper.fetchMissingOutComeData(Arrays.asList(treatmentStartDateConcept, treatmentStopDateConcept, outcomeConcept));
    }
}
