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

import static org.openmrs.module.bahmniendtb.EndTBConstants.*;

@Component
public class MissingValuesHelper {

    private ConceptService conceptService;
    private DataintegrityRuleService ruleService;
    private EpisodeHelper episodeHelper;

    @Autowired
    public MissingValuesHelper(ConceptService conceptService,
                               DataintegrityRuleService ruleService,
                               EpisodeHelper episodeHelper) {
        this.conceptService = conceptService;
        this.ruleService = ruleService;
        this.episodeHelper = episodeHelper;
    }


    public List<RuleResult<PatientProgram>> getInconsistenciesForMissingValues(String parentTemplateConcept, String targetQuestionConceptName, List<Concept> requiredConcepts) {
        List<RuleResult<PatientProgram>> ruleResultList = new ArrayList<>();

        List<Episode> episodes = null;
        for(Concept concept : requiredConcepts)
            episodes = ruleService.getEpisodesWithRequiredObsValues(episodes, concept);

        for (Episode episode : episodes) {
            ruleResultList.add(
                    transformEpisodeToRuleResult(episode, parentTemplateConcept, targetQuestionConceptName));
        }
        return ruleResultList;
    }

    private RuleResult<PatientProgram> transformEpisodeToRuleResult(Episode episode, String parentTemplateConcept, String consentQuestion) {
        return episodeHelper
                .mapEpisodeToPatientProgram(episode, parentTemplateConcept,
                                            consentQuestion, TI_DEFAULT_COMMENT);
    }
}
