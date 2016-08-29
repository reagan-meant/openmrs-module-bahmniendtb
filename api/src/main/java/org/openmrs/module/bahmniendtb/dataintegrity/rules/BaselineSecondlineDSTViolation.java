package org.openmrs.module.bahmniendtb.dataintegrity.rules;


import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.dataintegrity.rules.helper.DrugDSTViolationHelper;
import org.openmrs.module.bahmniendtb.dataintegrity.service.DataintegrityRuleService;
import org.openmrs.module.dataintegrity.rule.RuleDefn;
import org.openmrs.module.dataintegrity.rule.RuleResult;
import org.openmrs.module.episodes.Episode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.openmrs.module.bahmniendtb.EndTBConstants.*;

public class BaselineSecondlineDSTViolation implements RuleDefn<PatientProgram> {

    private DataintegrityRuleService dataintegrityRuleService;
    private ConceptService conceptService;
    private DrugDSTViolationHelper drugDSTViolationHelper;


    public BaselineSecondlineDSTViolation() {

        this.conceptService = Context.getConceptService();
        this.dataintegrityRuleService = Context.getRegisteredComponent("dataintegrityRuleService", DataintegrityRuleService.class);
        this.drugDSTViolationHelper = Context.getRegisteredComponent("drugDSTViolationHelper", DrugDSTViolationHelper.class);
    }

    public BaselineSecondlineDSTViolation(ConceptService conceptService, DataintegrityRuleService ruleService, DrugDSTViolationHelper drugDSTViolationHelper) {
        this.conceptService = conceptService;
        this.dataintegrityRuleService = ruleService;
        this.drugDSTViolationHelper = drugDSTViolationHelper;
    }

    @Override
    public List<RuleResult<PatientProgram>> evaluate() {

        Concept treatmentStartDate = conceptService.getConceptByName(TI_TREATMENT_START_DATE);
        Concept baselineDrugResistance = conceptService.getConceptByName(BASELINE_DRUG_RESISTANCE);
        Concept confirmedDrugResistantTB = conceptService.getConceptByName(BASELINE_CONFIRMED_DRUG_RESISTANT_TB);

        Concept bacteriologyFluoroquinolone = conceptService.getConceptByName(BACTERIOLOGY_FLUOROQUINOLONE);
        Concept ofloxacin = conceptService.getConceptByName(BACTERIOLOGY_OFLOXACIN);
        Concept levofloxacin = conceptService.getConceptByName(BACTERIOLOGY_LEVOFLOXACIN);
        Concept moxifloxacin = conceptService.getConceptByName(BACTERIOLOGY_MOXIFLOXACIN_5);

        Concept bacteriologyMTBDRslInjectable = conceptService.getConceptByName(BACTERIOLOGY_MTBDRSL_INJECTABLE);
        Concept amikacin = conceptService.getConceptByName(BACTERIOLOGY_AMIKACIN);
        Concept kanamycin = conceptService.getConceptByName(BACTERIOLOGY_KANAMYCIN);
        Concept capreomycin = conceptService.getConceptByName(BACTERIOLOGY_CAPREOMYCIN);

        Concept susceptible = conceptService.getConceptByName(SUSCEPTIBLE);
        Concept resistant = conceptService.getConceptByName(RESISTANT);

        Set<Episode> episodesWithTreatmentStartDate = dataintegrityRuleService.getEpisodesWithRequiredObs(Arrays.asList(treatmentStartDate));
        Set<Episode> episodesWithTreatmentStartDateAndCDRTB = dataintegrityRuleService.filterEpisodesForCodedObsWithAnswersInList(new ArrayList<>(episodesWithTreatmentStartDate), baselineDrugResistance, Arrays.asList(confirmedDrugResistantTB));


        Set<Episode> episodesWithFluoroquinoloneResistance =  dataintegrityRuleService.getEpisodesWithResistance(new ArrayList<>(episodesWithTreatmentStartDateAndCDRTB), treatmentStartDate ,Arrays.asList(levofloxacin, moxifloxacin, ofloxacin, bacteriologyFluoroquinolone), Arrays.asList(susceptible, resistant) );
        Set<Episode> episodesWithInjectableResistance = dataintegrityRuleService.getEpisodesWithResistance(new ArrayList<>(episodesWithFluoroquinoloneResistance), treatmentStartDate ,Arrays.asList(bacteriologyMTBDRslInjectable, amikacin, capreomycin, kanamycin), Arrays.asList(susceptible, resistant));


        episodesWithTreatmentStartDateAndCDRTB.removeAll(episodesWithInjectableResistance);

        return drugDSTViolationHelper.getInconsistenciesForQuestion(episodesWithTreatmentStartDateAndCDRTB, BASELINE_FORM, BASELINE_DRUG_RESISTANCE, FIRST_LINE_RESISTANCE_RESULT_COMMENT);
    }

}
