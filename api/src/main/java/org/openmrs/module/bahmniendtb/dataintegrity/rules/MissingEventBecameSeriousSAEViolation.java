package org.openmrs.module.bahmniendtb.dataintegrity.rules;

import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.dataintegrity.rules.helper.MissingValuesHelper;
import org.openmrs.module.dataintegrity.rule.RuleResult;

import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.bahmniendtb.EndTBConstants.*;

public class MissingEventBecameSeriousSAEViolation extends EndTbRuleDefinition<PatientProgram> {

    private ConceptService conceptService;

    private MissingValuesHelper missingValuesHelper;

    public MissingEventBecameSeriousSAEViolation(){
        conceptService = Context.getConceptService();
        missingValuesHelper = Context.getRegisteredComponent("missingValuesHelper",MissingValuesHelper.class);
    }

    public MissingEventBecameSeriousSAEViolation(MissingValuesHelper missingDatesHelper, ConceptService conceptService) {
        this.missingValuesHelper = missingDatesHelper;
        this.conceptService = conceptService;
    }

    @Override
    public List<RuleResult<PatientProgram>> evaluate() {

        Concept reportingDateQuestion = conceptService.getConceptByName(SAE_REPORTING_DATE);
        Concept eventBecameSeriousQuestion = conceptService.getConceptByName(SAE_EVENT_BECAME_SERIOUS_DATE);

        return missingValuesHelper
            .getMissingObsInObsSetViolations(SAE_ADVERSE_EVENT_TEMPLATE, SAE_REPORTING_DATE,
                    Arrays.asList(reportingDateQuestion, eventBecameSeriousQuestion));
    }
}