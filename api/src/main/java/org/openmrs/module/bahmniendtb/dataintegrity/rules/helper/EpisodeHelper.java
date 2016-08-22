package org.openmrs.module.bahmniendtb.dataintegrity.rules.helper;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.module.dataintegrity.rule.RuleResult;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniendtb.dataintegrity.service.DataintegrityRuleService;
import org.openmrs.module.bahmniendtb.dataintegrity.service.EndTBObsService;
import org.openmrs.module.episodes.Episode;
import org.openmrs.module.episodes.service.EpisodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.bahmniendtb.EndTBConstants.DRUG_BDQ;
import static org.openmrs.module.bahmniendtb.EndTBConstants.DRUG_DELAMANID;

@Component
public class EpisodeHelper {

    private EndTBObsService endTBObsService;
    private ConceptService conceptService;
    private EpisodeService episodeService;
    private DataintegrityRuleService dataintegrityRuleService;

    @Autowired
    public EpisodeHelper(EndTBObsService endTBObsService,
                         ConceptService conceptService,
                         EpisodeService episodeService,
                         DataintegrityRuleService dataintegrityRuleService) {
        this.endTBObsService = endTBObsService;
        this.conceptService = conceptService;
        this.episodeService = episodeService;
        this.dataintegrityRuleService = dataintegrityRuleService;
    }

    public RuleResult<PatientProgram> mapEpisodeToPatientProgram(Episode episode, String parentTemplateConceptName, String notesConceptName, String defaultNoteComment) {
        List<Obs> obsList = endTBObsService.getObsForEpisode(episode, parentTemplateConceptName);
        PatientProgram patientProgram = episode.getPatientPrograms().iterator().next();
        RuleResult<PatientProgram> patientProgramRuleResult = new RuleResult<>();

        String patientUuid = patientProgram.getPatient().getUuid();
        String actionUrl = "";
        String notes = "";
        if (obsList.isEmpty()) {
            actionUrl = "#/default/patient/" + patientUuid
                    + "/dashboard?programUuid=" + patientProgram.getProgram().getUuid() + "&enrollment=" + patientProgram.getUuid();
            notes = defaultNoteComment;
        } else {
            Obs obsData = obsList.iterator().next();
            actionUrl = "#/default/patient/" + patientUuid + "/dashboard/observation/" + obsData.getUuid();
            notes = getNotes(obsData, notesConceptName);
        }
        patientProgramRuleResult.setActionUrl(actionUrl);
        patientProgramRuleResult.setEntity(patientProgram);
        patientProgramRuleResult.setNotes(notes);

        return patientProgramRuleResult;
    }

    public Map<Episode, List<Obs>> retrieveAllEpisodesWithObs(List<Concept> concepts) {
        Map<Episode, List<Obs>> filteredEpisode = new HashMap<>();
        Set<Episode> episodes = dataintegrityRuleService.getUniqueEpisodeForEncountersWithConceptObs(concepts);
        for (Episode episode : episodes) {
            Patient patient = episode.getPatientPrograms().iterator().next().getPatient();
            List<Obs> obsList = dataintegrityRuleService.getObsListForAPatient(patient, new ArrayList<Encounter>(episode.getEncounters()), concepts);
            if (CollectionUtils.isNotEmpty(obsList)) {
                filteredEpisode.put(episode, obsList);
            }
        }
        return filteredEpisode;
    }

    public List<RuleResult<PatientProgram>> getAllEpisodeWithDrugOrder(List<Concept> concepts) {
        List<RuleResult<PatientProgram>> ruleResults = new ArrayList<>();
        List<Episode> filteredEpisode = filterAllEpisodeWithBothBlqAndDelDrug(concepts);
        for (Episode episode : filteredEpisode) {
            ruleResults.add(mapEpisodeDrugToPatientProgram(episode));
        }
        return ruleResults;
    }

    private RuleResult<PatientProgram> mapEpisodeDrugToPatientProgram(Episode episode) {
        PatientProgram patientProgram = episode.getPatientPrograms().iterator().next();
        RuleResult<PatientProgram> patientProgramRuleResult = new RuleResult<>();

        String patientProgramId = episode.getPatientPrograms().iterator().next().getUuid();
        String patientUuid = patientProgram.getPatient().getUuid();
        String actionUrl = "";
        actionUrl = "#/default/patient/" + patientUuid
                + "/dashboard/treatment?tabConfigName=tbTabConfig&enrollment=" + patientProgramId;
        patientProgramRuleResult.setActionUrl(actionUrl);
        patientProgramRuleResult.setEntity(patientProgram);

        return patientProgramRuleResult;
    }

    private List<Episode> filterAllEpisodeWithBothBlqAndDelDrug(List<Concept> tbDrugConcepts) {
        List<Episode> episodes = dataintegrityRuleService.getEpisodeForEncountersWithDrugs(tbDrugConcepts);
        List<Episode> filteredEpisode = new ArrayList<>();
        Map<Episode, Set<Concept>> episodeConceptMap = new HashMap<>();
        List<String> tbDrugConceptsName = getAllTbDrudConceptName(tbDrugConcepts);
        for (Episode episode : episodes) {
            Set<Concept> concepts;
            if (!episodeConceptMap.containsKey(episode)) {
                concepts = new HashSet<>();
                episodeConceptMap.put(episode, concepts);
            }
            concepts = episodeConceptMap.get(episode);
            for (Encounter encounter : episode.getEncounters()) {
                for (Order order : encounter.getOrders()) {
                    if (order.isActive() && tbDrugConceptsName.contains(order.getConcept().getName().getName())) {
                        concepts.add(order.getConcept());
                    }
                }
            }
        }
        for (Map.Entry<Episode, Set<Concept>> entry : episodeConceptMap.entrySet()) {
            if (entry.getValue().size() <= 3) {
                for (Concept concept : entry.getValue()) {
                    if (concept.getName().getName().equals(DRUG_BDQ) ||
                            concept.getName().getName().equals(DRUG_DELAMANID)) {
                        filteredEpisode.add(entry.getKey());
                        break;
                    }
                }
            }
        }

        return filteredEpisode;
    }

    private List<String> getAllTbDrudConceptName(List<Concept> tbDrugConcepts) {
        List<String> tbDrugConceptNames = new ArrayList<>();
        for (Concept concept : tbDrugConcepts) {
            tbDrugConceptNames.add(concept.getName().getName());
        }
        return tbDrugConceptNames;
    }


    private String getNotes(Obs formObs, String notesConceptName) {
        Concept notesConcept = conceptService.getConcept(notesConceptName);
        String notes = "";
        Obs notesObs = endTBObsService.getChildObsByConcept(formObs, notesConcept);
        if (notesObs != null) {
            notes = notesObs.getComment();
        }
        return notes;
    }
}
