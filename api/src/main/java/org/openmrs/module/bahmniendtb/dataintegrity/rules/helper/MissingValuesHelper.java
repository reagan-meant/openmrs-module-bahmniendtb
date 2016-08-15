package org.openmrs.module.bahmniendtb.dataintegrity.rules.helper;

import org.bahmni.module.dataintegrity.rule.RuleResult;
import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniendtb.dataintegrity.service.DataintegrityRuleService;
import org.openmrs.module.episodes.Episode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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

    public List<RuleResult<PatientProgram>> getMissingObsInObsSetViolations(String parentTemplateConcept,
                                                                            String targetQuestionForEdit,
                                                                            String defaultComment,
                                                                            List<Concept> requiredConcepts) {
        List<Set<Episode>> obsEpisodeSets = new ArrayList<>();
        List<RuleResult<PatientProgram>> ruleResultList = new ArrayList<>();

        for(Concept concept : requiredConcepts) {
            obsEpisodeSets.add(ruleService.getEpisodesWithRequiredObs(concept));
        }

        for (Episode episode : UnionMinusIntersection(obsEpisodeSets)) {
            ruleResultList.add(
                    episodeHelper.mapEpisodeToPatientProgram(
                            episode, parentTemplateConcept, targetQuestionForEdit, defaultComment));
        }
        return ruleResultList;
    }

    private Set<Episode> UnionMinusIntersection(List<Set<Episode>> obsEpisodeSets) {
        Set<Episode> union = new HashSet<>();
        for(Set<Episode> episodes : obsEpisodeSets) union.addAll(episodes);

        Set<Episode> intersection = new HashSet<>(obsEpisodeSets.iterator().next());
        for(Set<Episode> episodes : obsEpisodeSets) intersection.retainAll(episodes);

        union.removeAll(intersection);

        return union;
    }
}
