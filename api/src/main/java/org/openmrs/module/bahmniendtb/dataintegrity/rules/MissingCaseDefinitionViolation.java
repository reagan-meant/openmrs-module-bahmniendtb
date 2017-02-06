package org.openmrs.module.bahmniendtb.dataintegrity.rules;

import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.dataintegrity.rules.helper.MissingCaseDefnHelper;
import org.openmrs.module.dataintegrity.rule.RuleResult;

import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.bahmniendtb.EndTBConstants.*;

public class MissingCaseDefinitionViolation extends EndTbRuleDefinition<PatientProgram> {

    private ConceptService conceptService;

    private MissingCaseDefnHelper missingCaseDefnHelper;

    public MissingCaseDefinitionViolation(){
        conceptService = Context.getConceptService();
        missingCaseDefnHelper = Context.getRegisteredComponent("missingCaseDefnHelper", MissingCaseDefnHelper.class);
    }

    public MissingCaseDefinitionViolation(MissingCaseDefnHelper missingDatesHelper, ConceptService conceptService) {
        this.missingCaseDefnHelper = missingDatesHelper;
        this.conceptService = conceptService;
    }

    @Override
    public List<RuleResult<PatientProgram>> evaluate() {

        Concept whoGroupQuestion = conceptService.getConceptByName(BASELINE_CASEDEFINITION_WHO_GROUP);
        Concept siteDateQuestion = conceptService.getConceptByName(BASELINE_CASEDEFINITION_DISEASE_SITE);
        Concept methodQuestion = conceptService.getConceptByName(BASELINE_CASEDEFINITION_CONFIRMATION);

        return missingCaseDefnHelper
            .getMissingObsInObsSetViolations(BASELINE_FORM,
                    Arrays.asList(whoGroupQuestion, siteDateQuestion, methodQuestion));
    }
}


