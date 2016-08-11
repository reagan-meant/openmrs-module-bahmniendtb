package org.openmrs.module.bahmniendtb.dataintegrity.rules.helper;

import org.bahmni.module.dataintegrity.rule.RuleResult;
import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniendtb.dataintegrity.service.DataintegrityRuleService;
import org.openmrs.module.episodes.Episode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.openmrs.module.bahmniendtb.EndTBConstants.DRUG_BDQ;
import static org.openmrs.module.bahmniendtb.EndTBConstants.DRUG_DELAMANID;
import static org.openmrs.module.bahmniendtb.EndTBConstants.TI_DEFAULT_COMMENT;

@Component
public class TIFormInconsistencyHelper {

    private ConceptService conceptService;
    private DataintegrityRuleService ruleService;
    private EpisodeHelper episodeHelper;

    @Autowired
    public TIFormInconsistencyHelper(ConceptService conceptService,
                                     DataintegrityRuleService ruleService,
                                     EpisodeHelper episodeHelper) {
        this.conceptService = conceptService;
        this.ruleService = ruleService;
        this.episodeHelper = episodeHelper;
    }


    public List<RuleResult<PatientProgram>> getInconsistenciesForQuestion(String parentTemplateConcept, String questionConceptName, List<Concept> unacceptableAnswers) {
        Concept question = conceptService.getConcept(questionConceptName);
        List<RuleResult<PatientProgram>> ruleResultList = new ArrayList<>();

        List<Concept> tbDrugList = new ArrayList<>();

        addConceptByNameToList(Arrays.asList(DRUG_BDQ, DRUG_DELAMANID), tbDrugList);

        List<Episode> episodes = ruleService.getEpisodeForEncountersWithDrugs(tbDrugList);
        Set<Episode> episodesWithInconsistency = ruleService.filterEpisodesForObsWithSpecifiedValue(episodes, question, unacceptableAnswers);

        for (Episode episode : episodesWithInconsistency) {
            ruleResultList.add(transformEpisodeToRuleResult(episode, parentTemplateConcept, questionConceptName));
        }

        return ruleResultList;

    }

    private RuleResult<PatientProgram> transformEpisodeToRuleResult(Episode episode, String parentTemplateConcept, String consentQuestion) {
        return episodeHelper.mapEpisodeToPatientProgram(episode, parentTemplateConcept, consentQuestion, TI_DEFAULT_COMMENT);
    }

    private void addConceptByNameToList(List<String> conceptNames, List<Concept> listToAdd) {
        for (String concept : conceptNames) {
            listToAdd.add(conceptService.getConceptByName(concept));
        }
    }

}
