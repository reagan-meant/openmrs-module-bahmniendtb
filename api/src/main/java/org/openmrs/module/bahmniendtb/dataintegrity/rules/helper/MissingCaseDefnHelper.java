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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        Set<Episode> episodesOfInterest
                = ruleService.getAllEpisodes();

        for(Episode episode : episodesOfInterest) {
            Obs form = null;
            int requiredObsForForm = -1;
            List<Obs> formObs
                    = endTBObsService.getObsForEpisode(episode, parentTemplateConcept);

            if(formObs.size() > 0) {
                form = formObs.iterator().next();
                requiredObsForForm = getuniqueConceptCount(endTBObsService.getChildObsByConcepts(form, requiredConcepts));
            }

            if(form == null || requiredObsForForm != requiredConcepts.size()) {
                ruleResultList.add(episodeHelper.mapEpisodeToPatientProgram(episode, BASELINE_FORM, BASELINE_DATE, BASELINE_DEFAULT_COMMENT));
            }
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
