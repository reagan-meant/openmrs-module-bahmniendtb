package org.openmrs.module.bahmniendtb.dataintegrity.rules;

import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.dataintegrity.rules.helper.CultureStatusAtStartMissingHelper;
import org.openmrs.module.bahmniendtb.dataintegrity.rules.helper.MissingValuesHelper;
import org.openmrs.module.dataintegrity.rule.RuleDefn;
import org.openmrs.module.dataintegrity.rule.RuleResult;


import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.bahmniendtb.EndTBConstants.*;


public class CultureStatusAtStartMissing implements RuleDefn<PatientProgram> {

    private ConceptService conceptService;
    private CultureStatusAtStartMissingHelper cultureStatusAtStartMissingHelper;

    public CultureStatusAtStartMissing() {
        conceptService = Context.getConceptService();
        cultureStatusAtStartMissingHelper = Context.getRegisteredComponent("cultureStatusAtStartMissingHelper", CultureStatusAtStartMissingHelper.class);
    }

    public CultureStatusAtStartMissing(CultureStatusAtStartMissingHelper cultureStatusAtStartMissingHelper, ConceptService conceptService) {
        this.cultureStatusAtStartMissingHelper = cultureStatusAtStartMissingHelper;
        this.conceptService = conceptService;
    }

    @Override
    public List<RuleResult<PatientProgram>> evaluate() {
        Concept mtbConfirmation = conceptService.getConceptByName(BASELINE_CASEDEFINITION_MDR_TB_DIAGNOSIS_METHOD);
        Concept treatmentInitiationStartDate = conceptService.getConceptByName(TI_START_DATE);
        Concept bacteriologySpecimenCollectionDate = conceptService.getConceptByName(BACTERIOLOGY_SPECIMEN_COLLECTION_DATE);
        Concept bacteriologyCultureResults = conceptService.getConceptByName(BACTERIOLOGY_CULTURE_RESULTS);
        return cultureStatusAtStartMissingHelper.fetchCultureStatusAtStartMissing(Arrays.asList(mtbConfirmation, treatmentInitiationStartDate, bacteriologySpecimenCollectionDate, bacteriologyCultureResults));
    }
}
