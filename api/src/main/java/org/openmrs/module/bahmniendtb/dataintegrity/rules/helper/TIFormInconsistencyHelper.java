package org.openmrs.module.bahmniendtb.dataintegrity.rules.helper;

import org.bahmni.module.dataintegrity.rule.RuleResult;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniendtb.dataintegrity.service.DataintegrityRuleService;
import org.openmrs.module.bahmniendtb.dataintegrity.service.impl.EndTBObsServiceImpl;
import org.openmrs.module.episodes.Episode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.openmrs.module.bahmniendtb.EndTBConstants.DRUG_BDQ;
import static org.openmrs.module.bahmniendtb.EndTBConstants.DRUG_DELAMANID;

@Component
public class TIFormInconsistencyHelper {


    @Autowired
    private EndTBObsServiceImpl endTBObsService;

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private DataintegrityRuleService ruleService;


    public List<RuleResult<PatientProgram>> getInconsistenciesForQuestion(Concept question,List<Concept> unacceptableAnswers){

        List<RuleResult<PatientProgram>> ruleResultList = new ArrayList<>();

        List<Concept> tbDrugList = new ArrayList<>();

        addConceptByNameToList(Arrays.asList(DRUG_BDQ, DRUG_DELAMANID), tbDrugList);

        List<Episode> episodes = ruleService.getEpisodeForEncountersWithDrugs(tbDrugList);
        Set<Episode> episodesWithInconsistency = ruleService.filterEpisodesForObsWithSpecifiedValue(episodes, question, unacceptableAnswers);

        for (Episode episode : episodesWithInconsistency) {
            ruleResultList.add(transformEpisodeToRuleResult(episode,question));
        }

        return ruleResultList;

    }

    private RuleResult<PatientProgram> transformEpisodeToRuleResult(Episode episode, Concept consentQuestion){
        List<Obs> obsList = endTBObsService.getTreamentInitiationObsForEpisode(episode);
        PatientProgram patientProgram = episode.getPatientPrograms().iterator().next();
        RuleResult<PatientProgram> patientProgramRuleResult = new RuleResult<>();

        String patientUuid = patientProgram.getPatient().getUuid();
        String actionUrl = "";
        String notes = "";
        if (obsList.isEmpty()) {
            actionUrl = "#/default/patient/" + patientUuid
                    + "/dashboard?programUuid=" + patientProgram.getProgram().getUuid() + "&enrollment=" + patientProgram.getUuid();
            notes = "The Treatment Initiation form is not filled";
        } else {
            Obs treatmentInitiationObs = obsList.iterator().next();
            actionUrl = "#/default/patient/" + patientUuid + "/dashboard/observation/" + treatmentInitiationObs.getUuid();

            notes = getNotes(treatmentInitiationObs, consentQuestion);
        }
        patientProgramRuleResult.setActionUrl(actionUrl);
        patientProgramRuleResult.setEntity(patientProgram);
        patientProgramRuleResult.setNotes(notes);

        return patientProgramRuleResult;
    }

    private String getNotes(Obs formObs, Concept notesConcept) {
        String notes = "";
        Obs notesObs = endTBObsService.getChildObsByConcept(formObs, notesConcept);
        if (notesObs != null) {
            notes = notesObs.getComment();
        }
        return notes;
    }

    private void addConceptByNameToList(List<String> conceptNames, List<Concept> listToAdd) {
        for (String concept : conceptNames) {
            listToAdd.add(conceptService.getConceptByName(concept));
        }
    }

}
