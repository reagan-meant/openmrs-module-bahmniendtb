package org.openmrs.module.bahmniendtb.dataintegrity.rules.helper;

import org.bahmni.module.dataintegrity.rule.RuleResult;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniendtb.dataintegrity.service.EndTBObsService;
import org.openmrs.module.episodes.Episode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EpisodeHelper {

    private EndTBObsService endTBObsService;
    private ConceptService conceptService;

    @Autowired
    public EpisodeHelper(EndTBObsService endTBObsService,
                         ConceptService conceptService) {
        this.endTBObsService = endTBObsService;
        this.conceptService = conceptService;
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
