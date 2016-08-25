package org.openmrs.module.bahmniendtb.dataintegrity.rules.helper;


import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.module.bahmniendtb.dataintegrity.service.EndTBObsService;
import org.openmrs.module.dataintegrity.rule.RuleResult;
import org.openmrs.module.episodes.Episode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class DrugDSTViolationHelper {

    private EpisodeHelper episodeHelper;
    private EndTBObsService endTBObsService;

    @Autowired
    public DrugDSTViolationHelper(EpisodeHelper episodeHelper, EndTBObsService endTBObsService) {
        this.episodeHelper = episodeHelper;
        this.endTBObsService = endTBObsService;
    }

    public List<RuleResult<PatientProgram>> getInconsistenciesForQuestion(Set<Episode> episodesWithCDRTB, String parentTemplateName, String commentConcept, String defaultComment) {

        List<RuleResult<PatientProgram>> ruleResultList = new ArrayList<>();
        for (Episode episode : episodesWithCDRTB) {
            RuleResult<PatientProgram> patientProgramRuleResult = new RuleResult<>();
            patientProgramRuleResult = episodeHelper.mapEpisodeToPatientProgram(episode, null, null, defaultComment);

            List<Obs> obsList = endTBObsService.getObsForEpisode(episode, parentTemplateName);

            if (!obsList.isEmpty()) {
                String notes = episodeHelper.getNotes(obsList.iterator().next(), commentConcept);

                if (!(notes == null) && !notes.isEmpty()) {
                    patientProgramRuleResult.setNotes(notes);
                }

            }
            ruleResultList.add(patientProgramRuleResult);
        }
        return ruleResultList;
    }
}
