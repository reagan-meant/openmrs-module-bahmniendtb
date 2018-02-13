package org.openmrs.module.bahmniendtb.dataintegrity.rules.helper;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniendtb.dataintegrity.service.DataintegrityRuleService;
import org.openmrs.module.bahmniendtb.dataintegrity.service.EndTBObsService;
import org.openmrs.module.dataintegrity.rule.RuleResult;
import org.openmrs.module.episodes.Episode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.openmrs.module.bahmniendtb.EndTBConstants.*;

@Component
public class MissingCaseDefnHelper {

    private ConceptService conceptService;
    private DataintegrityRuleService ruleService;
    private EndTBObsService endTBObsService;
    private EpisodeHelper episodeHelper;

    @Autowired
    public MissingCaseDefnHelper(ConceptService conceptService,
                                 DataintegrityRuleService ruleService,
                                 EndTBObsService endTBObsService,
                                 EpisodeHelper episodeHelper) {
        this.conceptService = conceptService;
        this.ruleService = ruleService;
        this.endTBObsService = endTBObsService;
        this.episodeHelper = episodeHelper;
    }

    public List<RuleResult<PatientProgram>> getMissingObsInObsSetViolations(String parentTemplateConcept,
                                                                            List<Concept> requiredConcepts) {
        List<RuleResult<PatientProgram>> ruleResultList = new ArrayList<>();
        if(requiredConcepts.size() == 0) return null;

        Set<Episode> episodesOfInterest = ruleService.getAllEpisodes();

        Set<Episode> episodesContainingAllConcepts = null;
        for(Concept concept : requiredConcepts) {
            Set<Episode> episodesWithConcept = ruleService.getUniqueEpisodeForEncountersWithConceptObs(Arrays.asList(concept));
            if (episodesContainingAllConcepts != null) {
                episodesContainingAllConcepts.retainAll(episodesWithConcept);
            } else {
                episodesContainingAllConcepts = episodesWithConcept;
            }
        }

        episodesOfInterest.removeAll(episodesContainingAllConcepts);

        for(Episode episode : episodesOfInterest) {
            ruleResultList.add(episodeHelper.mapEpisodeToPatientProgram(episode, BASELINE_FORM, BASELINE_DATE, BASELINE_DEFAULT_COMMENT));
        }

        ruleService.clearHibernateSession();
        return ruleResultList;
    }

    private int getuniqueConceptCount(List<Obs> childObsByConcepts) {
        Set<Integer> uniqueConcepts = new HashSet<>();

        for (Obs obs : childObsByConcepts){
            int conceptId = obs.getConcept().getConceptId();
            if(!uniqueConcepts.contains(conceptId))
                uniqueConcepts.add(conceptId);
        }
        return uniqueConcepts.size();
    }
}
