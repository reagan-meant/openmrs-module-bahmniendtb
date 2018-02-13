package org.openmrs.module.bahmniendtb.dataintegrity.rules.helper;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.module.dataintegrity.rule.RuleResult;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniendtb.dataintegrity.service.DataintegrityRuleService;
import org.openmrs.module.episodes.Episode;
import org.openmrs.module.episodes.service.EpisodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.openmrs.module.bahmniendtb.EndTBConstants.ALL_TB_DRUG;
import static org.openmrs.module.bahmniendtb.EndTBConstants.DRUG_BDQ;
import static org.openmrs.module.bahmniendtb.EndTBConstants.DRUG_DELAMANID;
import static org.openmrs.module.bahmniendtb.EndTBConstants.FSN_TREATMENT_INITIATION_FORM;
import static org.openmrs.module.bahmniendtb.EndTBConstants.TI_DEFAULT_COMMENT;
import static org.openmrs.module.bahmniendtb.EndTBConstants.TI_IS_TREATMENT_START_DATE;

@Component
public class TIFormInconsistencyHelper {

    private ConceptService conceptService;
    private DataintegrityRuleService dataintegrityRuleService;
    private EpisodeHelper episodeHelper;

    @Autowired
    public TIFormInconsistencyHelper(ConceptService conceptService,
                                     DataintegrityRuleService ruleService,
                                     EpisodeHelper episodeHelper) {
        this.conceptService = conceptService;
        this.dataintegrityRuleService = ruleService;
        this.episodeHelper = episodeHelper;
    }

    public List<RuleResult<PatientProgram>> getInconsistenciesForQuestion(String parentTemplateConcept, String questionConceptName, List<Concept> unacceptableAnswers) {
        Concept question = conceptService.getConcept(questionConceptName);
        List<RuleResult<PatientProgram>> ruleResultList = new ArrayList<>();
        List<Episode> episodes = getEpisodeForEncounterWithDrugs(addConceptByNameToList(Arrays.asList(DRUG_BDQ, DRUG_DELAMANID)));
        Set<Episode> episodesWithInconsistency;
        if(CollectionUtils.isNotEmpty(episodes)) {
            episodesWithInconsistency = dataintegrityRuleService.filterEpisodesForObsWithSpecifiedValue(episodes, question, unacceptableAnswers);
        } else {
            episodesWithInconsistency = new HashSet<>();
        }
        for (Episode episode : episodesWithInconsistency) {
            ruleResultList.add(transformEpisodeToRuleResult(episode, parentTemplateConcept, questionConceptName));
        }

        dataintegrityRuleService.clearHibernateSession();
        return ruleResultList;
    }

    public List<RuleResult<PatientProgram>> getDataWithMissingStartTreatmentDate(List<Concept> concepts) {
        List<RuleResult<PatientProgram>> ruleResults = new ArrayList<>();
        List<Concept> allTbDrugs = conceptService.getConcept(ALL_TB_DRUG).getSetMembers();
        List<Episode> episodes = getEpisodeForEncounterWithDrugs(allTbDrugs);
        Set<Episode> episodesWithStartTreatmentDate = dataintegrityRuleService.getUniqueEpisodeForEncountersWithConceptObs(concepts);

        episodes.removeAll(episodesWithStartTreatmentDate);

        for (Episode episode : episodes) {
            ruleResults.add(episodeHelper.mapEpisodeToPatientProgram(episode, FSN_TREATMENT_INITIATION_FORM, TI_IS_TREATMENT_START_DATE, TI_DEFAULT_COMMENT));
        }

        dataintegrityRuleService.clearHibernateSession();
        return ruleResults;
    }

    private List<Episode> getEpisodeForEncounterWithDrugs(List<Concept> drugConcepts) {
        return dataintegrityRuleService.getEpisodeForEncountersWithDrugs(drugConcepts);
    }

    private RuleResult<PatientProgram> transformEpisodeToRuleResult(Episode episode, String parentTemplateConcept, String consentQuestion) {
        return episodeHelper.mapEpisodeToPatientProgram(episode, parentTemplateConcept, consentQuestion, TI_DEFAULT_COMMENT);
    }

    private List<Concept> addConceptByNameToList(List<String> conceptNames) {
        List<Concept> listToAdd = new ArrayList<>();
        for (String concept : conceptNames) {
            listToAdd.add(conceptService.getConceptByName(concept));
        }
        return listToAdd;
    }

}
