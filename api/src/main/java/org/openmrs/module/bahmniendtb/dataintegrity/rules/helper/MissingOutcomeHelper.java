package org.openmrs.module.bahmniendtb.dataintegrity.rules.helper;


import org.bahmni.module.dataintegrity.rule.RuleResult;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.module.bahmniendtb.dataintegrity.service.DataintegrityRuleService;
import org.openmrs.module.episodes.Episode;
import org.openmrs.module.episodes.service.EpisodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openmrs.module.bahmniendtb.EndTBConstants.EOT_DEFAULT_COMMENT;
import static org.openmrs.module.bahmniendtb.EndTBConstants.EOT_OUTCOME;
import static org.openmrs.module.bahmniendtb.EndTBConstants.EOT_STOP_DATE;
import static org.openmrs.module.bahmniendtb.EndTBConstants.EOT_TREATMENT_TEMPLATE;
import static org.openmrs.module.bahmniendtb.EndTBConstants.TI_START_DATE;

@Component
public class MissingOutcomeHelper {

    private DataintegrityRuleService dataintegrityRuleService;
    private EpisodeService episodeService;
    private EpisodeHelper episodeHelper;

    @Autowired
    public MissingOutcomeHelper(DataintegrityRuleService dataintegrityRuleService,
                                EpisodeService episodeService,
                                EpisodeHelper episodeHelper) {
        this.dataintegrityRuleService = dataintegrityRuleService;
        this.episodeService = episodeService;
        this.episodeHelper = episodeHelper;
    }

    public List<RuleResult<PatientProgram>> fetchMissingOutComeData(List<Concept> conceptNames) {
        List<RuleResult<PatientProgram>> patientPrograms = new ArrayList<>();
        List<Episode> episodes = episodeService.getAllEpisodes();
        for (Episode episode : episodes) {
            Patient patient = episode.getPatientPrograms().iterator().next().getPatient();
            List<Obs> obsList = dataintegrityRuleService.getObsListForAPatient(patient, new ArrayList<Encounter>(episode.getEncounters()), conceptNames);
            if (hasValidDateDifference(obsList)) {
                patientPrograms.add(convertToPatientProgram(episode));
            }
        }
        return patientPrograms;
    }

    private RuleResult<PatientProgram> convertToPatientProgram(Episode episode) {
        return episodeHelper.mapEpisodeToPatientProgram(episode, EOT_TREATMENT_TEMPLATE, EOT_OUTCOME, EOT_DEFAULT_COMMENT);
    }

    private boolean hasValidDateDifference(List<Obs> obsList) {
        Date startTreatmentDate = null;
        Date stopTreatmentDate = null;
        for (Obs obs : obsList) {
            if (obs.getConcept().getName().getName().equals(TI_START_DATE)) {
                startTreatmentDate = obs.getValueDatetime();
            } else if (obs.getConcept().getName().getName().equals(EOT_STOP_DATE)) {
                stopTreatmentDate = obs.getValueDatetime();
            }
        }
        if (null == startTreatmentDate) return false;
        if (null == stopTreatmentDate) stopTreatmentDate = new Date();
        long dayDifference = getDateDiff(startTreatmentDate, stopTreatmentDate, TimeUnit.DAYS);
        if (dayDifference > 821) {
            return true;
        }
        return false;
    }

    private long getDateDiff(Date startDate, Date stopDate, TimeUnit timeUnit) {
        long diffInMillies = stopDate.getTime() - startDate.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
