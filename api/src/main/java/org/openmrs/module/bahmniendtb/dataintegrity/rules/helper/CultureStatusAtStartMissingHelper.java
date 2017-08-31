package org.openmrs.module.bahmniendtb.dataintegrity.rules.helper;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniendtb.dataintegrity.service.EndTBObsService;
import org.openmrs.module.dataintegrity.rule.RuleResult;
import org.openmrs.module.episodes.Episode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.openmrs.module.bahmniendtb.EndTBConstants.*;

@Component
public class CultureStatusAtStartMissingHelper {

    private EpisodeHelper episodeHelper;
    private EndTBObsService endTBObsService;
    private ConceptService conceptService;

    @Autowired
    public CultureStatusAtStartMissingHelper(EpisodeHelper episodeHelper, EndTBObsService endTBObsService, ConceptService conceptService) {
        this.episodeHelper = episodeHelper;
        this.endTBObsService = endTBObsService;
        this.conceptService = conceptService;
    }


    public List<RuleResult<PatientProgram>> fetchCultureStatusAtStartMissing(List<Concept> concepts) {
        List<RuleResult<PatientProgram>> patientPrograms = new ArrayList<>();
        Map<Episode, List<Obs>> episodeObsMap = episodeHelper.retrieveAllEpisodesWithObs(concepts);
        for (Map.Entry<Episode, List<Obs>> episodeObsMapEntry : episodeObsMap.entrySet()) {
            if (hasInvalidCultureStatusForGivenTimePeriod(episodeObsMapEntry.getValue())) {
                patientPrograms.add(convertToPatientProgram(episodeObsMapEntry.getKey(), BASELINE_FORM, BASELINE_CASEDEFINITION_MDR_TB_DIAGNOSIS_METHOD, CULTURE_STATUS_MISSING_DEFAULT_COMMENT));
            }
        }

        episodeHelper.clearHibernateSession();
        return patientPrograms;
    }

    private RuleResult<PatientProgram> convertToPatientProgram(Episode episode, String parentConcept, String notesConceptName, String defaultNoteComment) {
        return mapEpisodeToPatientProgram(episode, parentConcept, notesConceptName, defaultNoteComment);
    }

    private RuleResult<PatientProgram> mapEpisodeToPatientProgram(Episode episode, String parentTemplateConceptName, String notesConceptName, String defaultNoteComment) {
        List<Obs> obsList = endTBObsService.getObsForEpisode(episode, parentTemplateConceptName);
        PatientProgram patientProgram = episode.getPatientPrograms().iterator().next();
        RuleResult<PatientProgram> patientProgramRuleResult = new RuleResult<>();

        String patientUuid = patientProgram.getPatient().getUuid();
        String actionUrl = "#/default/patient/" + patientUuid
                + "/dashboard?programUuid=" + patientProgram.getProgram().getUuid() + "&enrollment=" + patientProgram.getUuid();
        Obs obsData = obsList.iterator().next();
        String notes = getNotes(obsData, notesConceptName, defaultNoteComment);
        patientProgramRuleResult.setActionUrl(actionUrl);
        patientProgramRuleResult.setEntity(patientProgram);
        patientProgramRuleResult.setNotes(notes);
        return patientProgramRuleResult;
    }

    private String getNotes(Obs formObs, String notesConceptName, String defaultNoteComment) {
        Concept notesConcept = conceptService.getConcept(notesConceptName);
        String notes = defaultNoteComment;
        Obs notesObs = endTBObsService.getChildObsByConcept(formObs, notesConcept);
        if (notesObs != null && notesObs.getComment() != null  ) {
            notes = notesObs.getComment();
        }
        return notes;
    }

    private boolean hasInvalidCultureStatusForGivenTimePeriod(List<Obs> obsList) {
        Date treatmentInitiationStartDate = null;
        Date bacteriologySpecimenCollectionDate = null;
        String mtbConfirmation = null;

        for (Obs obs : obsList) {
            if (obs.getConcept().getName().getName().equals(TI_START_DATE)) {
                treatmentInitiationStartDate = obs.getValueDatetime();

            } else if (obs.getConcept().getName().getName().equals(BASELINE_CASEDEFINITION_MDR_TB_DIAGNOSIS_METHOD)) {
                mtbConfirmation = obs.getValueCoded().getDisplayString();
            }
            else if (obs.getConcept().getName().getName().equals(BACTERIOLOGY_SPECIMEN_COLLECTION_DATE)) {
                bacteriologySpecimenCollectionDate = getEarliestCultureResult(bacteriologySpecimenCollectionDate, obs, obsList);
            }
        }
        return checkIfInvalidResultExists(mtbConfirmation, bacteriologySpecimenCollectionDate ,treatmentInitiationStartDate);
    }

    private Date getEarliestCultureResult(Date bacteriologySpecimenCollectionDate, Obs obs, List<Obs> obsList) {
        if(bacteriologySpecimenCollectionDate == null || getDateDiff(bacteriologySpecimenCollectionDate,obs.getValueDatetime(),TimeUnit.DAYS) < 0) {
            Obs obsGroup = obs.getObsGroup();
            String bacteriologyCultureResults = getCultureResultForObsGroup(obsGroup, obsList);
            if(bacteriologyCultureResults != null){
                bacteriologySpecimenCollectionDate = obs.getValueDatetime();
            }
        }
        return bacteriologySpecimenCollectionDate;
    }

    private boolean checkIfInvalidResultExists(String mtbConfirmation, Date bacteriologySpecimenCollectionDate, Date treatmentInitiationStartDate) {
        if(BACTERIOLOGICALLY_CONFIRMED.equals(mtbConfirmation) && treatmentInitiationStartDate != null) {
            if (bacteriologySpecimenCollectionDate == null) {
                return true;
            }
            long dayDifference = getDateDiff(treatmentInitiationStartDate, bacteriologySpecimenCollectionDate, TimeUnit.DAYS);
            if (dayDifference >= 30.5) {
                return true;
            }
        }
        return false;
    }

    private String getCultureResultForObsGroup(Obs obsGroup, List<Obs> obsList){
        String bacteriologyCultureResults = null;
        for (Obs obs : obsList) {
            if (obs.getConcept().getName().getName().equals(BACTERIOLOGY_CULTURE_RESULTS)
                    && obs.getObsGroup().getObsGroup().getObsGroup() == obsGroup) {
                bacteriologyCultureResults = obs.getValueCoded().getDisplayString();
            }
        }
        return  bacteriologyCultureResults;
    }

    private long getDateDiff(Date startDate, Date stopDate, TimeUnit timeUnit) {
        long diffInMillies = stopDate.getTime() - startDate.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
