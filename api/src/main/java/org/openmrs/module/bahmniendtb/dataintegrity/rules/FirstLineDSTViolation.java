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

public class FirstLineDSTViolation implements RuleDefn<PatientProgram> {

    private DataintegrityRuleService dataintegrityRuleService;
    private ConceptService conceptService;
    private DrugDSTViolationHelper drugDSTViolationHelper;


    public FirstLineDSTViolation() {

        this.conceptService = Context.getConceptService();
        this.dataintegrityRuleService = Context.getRegisteredComponent("dataintegrityRuleService", DataintegrityRuleService.class);
        this.drugDSTViolationHelper = Context.getRegisteredComponent("drugDSTViolationHelper", DrugDSTViolationHelper.class);
    }

    public FirstLineDSTViolation(ConceptService conceptService, DataintegrityRuleService ruleService,DrugDSTViolationHelper drugDSTViolationHelper) {
        this.conceptService = conceptService;
        this.dataintegrityRuleService = ruleService;
        this.drugDSTViolationHelper = drugDSTViolationHelper;
    }

    @Override
    public List<RuleResult<PatientProgram>> evaluate() {

        Concept treatmentStartDate = conceptService.getConceptByName(TI_TREATMENT_START_DATE);
        Concept baselineDrugResistance = conceptService.getConceptByName(BASELINE_DRUG_RESISTANCE);
        Concept confirmedDrugResistantTB = conceptService.getConceptByName(BASELINE_CONFIRMED_DRUG_RESISTANT_TB);
        Concept hainIsoniazid = conceptService.getConceptByName(BACTERIOLOGY_HAIN_ISONIAZID);
        Concept isoniazidTwoMg = conceptService.getConceptByName(BACTERIOLOGY_ISONIAZID_2);
        Concept susceptible = conceptService.getConceptByName(SUSCEPTIBLE);
        Concept resistant = conceptService.getConceptByName(RESISTANT);
        Concept bacteriologyRifampicin = conceptService.getConceptByName(BACTERIOLOGY_RIFAMPICIN);
        Concept xpertRifampicin = conceptService.getConceptByName(BACTERIOLOGY_XPERT_RIFAMPICIN);
        Concept dstRifampicin = conceptService.getConceptByName(BACTERIOLOGY_DST_RIFAMPICIN);
        Concept detected = conceptService.getConceptByName(DETECTED);
        Concept nonDetected = conceptService.getConceptByName(NOT_DETECTED);


        Set<Episode> episodesWithTreatmentStartDate = dataintegrityRuleService.getEpisodesWithRequiredObs(Arrays.asList(treatmentStartDate));
        Set<Episode> episodesWithTreatmentStartDateAndCDRTB = dataintegrityRuleService.filterEpisodesForCodedObsWithAnswersInList(new ArrayList<>(episodesWithTreatmentStartDate), baselineDrugResistance, Arrays.asList(confirmedDrugResistantTB));


        Set<Episode> episodesWithIsoniazidResistance =  dataintegrityRuleService.getEpisodesWithResistance(new ArrayList<>(episodesWithTreatmentStartDateAndCDRTB), treatmentStartDate ,Arrays.asList(hainIsoniazid, isoniazidTwoMg), Arrays.asList(susceptible, resistant) );
        Set<Episode> episodesWithRifampicinResistance = dataintegrityRuleService.getEpisodesWithResistance(new ArrayList<>(episodesWithIsoniazidResistance), treatmentStartDate ,Arrays.asList(bacteriologyRifampicin, xpertRifampicin, dstRifampicin), Arrays.asList(susceptible, resistant , detected, nonDetected));


        episodesWithTreatmentStartDateAndCDRTB.removeAll(episodesWithRifampicinResistance);

        return drugDSTViolationHelper.getInconsistenciesForQuestion(episodesWithTreatmentStartDateAndCDRTB, BASELINE_FORM, BASELINE_DRUG_RESISTANCE, FIRST_LINE_RESISTANCE_RESULT_COMMENT);
    }

}
