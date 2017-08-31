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
import java.util.List;
import java.util.Set;

@Component
public class MissingValuesHelper {

    private ConceptService conceptService;
    private DataintegrityRuleService ruleService;
    private EndTBObsService endTBObsService;

    @Autowired
    public MissingValuesHelper(ConceptService conceptService,
                               DataintegrityRuleService ruleService,
                               EndTBObsService endTBObsService) {
        this.conceptService = conceptService;
        this.ruleService = ruleService;
        this.endTBObsService = endTBObsService;
    }

    public List<RuleResult<PatientProgram>> getMissingObsInObsSetViolations(String parentTemplateConcept,
                                                                            String targetQuestionForEdit,
                                                                            List<Concept> requiredConcepts) {
        List<RuleResult<PatientProgram>> ruleResultList = new ArrayList<>();
        if(requiredConcepts.size() == 0) return null;

        Set<Episode> episodesOfInterest
                = ruleService.getEpisodesWithRequiredObs(requiredConcepts);

        for(Episode episode : episodesOfInterest) {
            List<Obs> formObs
                    = endTBObsService.getAllObsForEpisode(episode, parentTemplateConcept);

            for(Obs form : formObs) {
                List<Obs> requiredObsForForm = endTBObsService.getChildObsByConcepts(form, requiredConcepts);
                if(requiredObsForForm.size() != requiredConcepts.size()) {
                    String notes = "";
                    RuleResult<PatientProgram> patientProgramRuleResult = new RuleResult<>();

                    PatientProgram patientProgram = episode.getPatientPrograms().iterator().next();

                    Concept notesConcept = conceptService.getConcept(targetQuestionForEdit);
                    Obs notesObs = endTBObsService.getChildObsByConcept(form, notesConcept);
                    if (notesObs != null) {
                        notes = notesObs.getComment();
                    }
                    String patientUuid = patientProgram.getPatient().getUuid();
                    String actionUrl = "#/default/patient/" + patientUuid + "/dashboard/observation/" + form.getUuid() + "?programUuid=" + patientProgram.getProgram().getUuid() + "&enrollment=" + patientProgram.getUuid();

                    patientProgramRuleResult.setActionUrl(actionUrl);
                    patientProgramRuleResult.setEntity(patientProgram);
                    patientProgramRuleResult.setNotes(notes);
                    ruleResultList.add(patientProgramRuleResult);
                }
            }
        }

        ruleService.clearHibernateSession();
        return ruleResultList;
    }
}
