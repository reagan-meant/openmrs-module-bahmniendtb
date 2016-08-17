package org.openmrs.module.bahmniendtb.dataintegrity.rules.helper;


import org.openmrs.module.dataintegrity.rule.RuleResult;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.module.episodes.Episode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.openmrs.module.bahmniendtb.EndTBConstants.EOT_DEFAULT_COMMENT;
import static org.openmrs.module.bahmniendtb.EndTBConstants.EOT_OUTCOME;
import static org.openmrs.module.bahmniendtb.EndTBConstants.EOT_STOP_DATE;
import static org.openmrs.module.bahmniendtb.EndTBConstants.EOT_TREATMENT_TEMPLATE;
import static org.openmrs.module.bahmniendtb.EndTBConstants.TI_START_DATE;
import static org.openmrs.module.bahmniendtb.EndTBConstants.TWENTY_SEVEN_MONTHS_IN_DAYS;

@Component
public class OutcomeFormHelper {

    private EpisodeHelper episodeHelper;

    @Autowired
    public OutcomeFormHelper(EpisodeHelper episodeHelper) {
        this.episodeHelper = episodeHelper;
    }

    public List<RuleResult<PatientProgram>> fetchMissingOutComeData(List<Concept> concepts) {
        List<RuleResult<PatientProgram>> patientPrograms = new ArrayList<>();
        Map<Episode, List<Obs>> episodeObsMap = episodeHelper.retrieveAllEpisodesWithObs(concepts);
        for (Map.Entry<Episode, List<Obs>> episodeObsMapEntry : episodeObsMap.entrySet()) {
            if (hasInvalidOutcomeValidation(episodeObsMapEntry.getValue())) {
                patientPrograms.add(convertToPatientProgram(episodeObsMapEntry.getKey(), EOT_TREATMENT_TEMPLATE, EOT_OUTCOME, EOT_DEFAULT_COMMENT));
            }
        }
        return patientPrograms;
    }

    public List<RuleResult<PatientProgram>> retrieveMissingTreatmentStopDate(List<Concept> conceptNames) {
        List<RuleResult<PatientProgram>> patientPrograms = new ArrayList<>();
        Map<Episode, List<Obs>> episodeObsMap = episodeHelper.retrieveAllEpisodesWithObs(conceptNames);
        for (Map.Entry<Episode, List<Obs>> episodeObsMapEntry : episodeObsMap.entrySet()) {
            Obs outcomeObs = null;
            Obs treatmentStopDate = null;
            for (Obs obs : episodeObsMapEntry.getValue()) {
                if (obs.getConcept().getName().getName().equals(EOT_OUTCOME)) {
                    outcomeObs = obs;
                } else if (obs.getConcept().getName().getName().equals(EOT_STOP_DATE)) {
                    treatmentStopDate = obs;
                }
            }
            if (null != outcomeObs && null == treatmentStopDate) {
                patientPrograms.add(convertToPatientProgram(episodeObsMapEntry.getKey(), EOT_TREATMENT_TEMPLATE, EOT_OUTCOME, EOT_DEFAULT_COMMENT));
            }
        }
        return patientPrograms;
    }

    private RuleResult<PatientProgram> convertToPatientProgram(Episode episode, String parentConcept, String notesConceptName, String defaultNoteComment) {
        return episodeHelper.mapEpisodeToPatientProgram(episode, parentConcept, notesConceptName, defaultNoteComment);
    }

    private boolean hasInvalidOutcomeValidation(List<Obs> obsList) {
        Date startTreatmentDate = null;
        Date stopTreatmentDate = null;
        for (Obs obs : obsList) {
            if (obs.getConcept().getName().getName().equals(TI_START_DATE)) {
                startTreatmentDate = obs.getValueDatetime();
            } else if (obs.getConcept().getName().getName().equals(EOT_STOP_DATE)) {
                stopTreatmentDate = obs.getValueDatetime();
            } else if (obs.getConcept().getName().getName().equals(EOT_OUTCOME)) {
                return false;
            }
        }
        if (null == startTreatmentDate) return false;
        if (null == stopTreatmentDate) stopTreatmentDate = new Date();
        long dayDifference = getDateDiff(startTreatmentDate, stopTreatmentDate, TimeUnit.DAYS);
        if (dayDifference > TWENTY_SEVEN_MONTHS_IN_DAYS) {
            return true;
        }
        return false;
    }

    private long getDateDiff(Date startDate, Date stopDate, TimeUnit timeUnit) {
        long diffInMillies = stopDate.getTime() - startDate.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
