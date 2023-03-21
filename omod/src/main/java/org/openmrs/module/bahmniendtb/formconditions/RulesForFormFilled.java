package org.openmrs.module.bahmniendtb.formconditions;


import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.api.EncounterService;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.EncounterDataPreSaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.episodes.Episode;
import org.openmrs.module.episodes.dao.impl.EpisodeDAO;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class RulesForFormFilled implements EncounterDataPreSaveCommand {

    private ObsDao obsDao;
    private EpisodeDAO episodeDAO;
    private EncounterService encounterService;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");

    @Autowired
    public RulesForFormFilled(ObsDao obsDao, EpisodeDAO episodeDAO, EncounterService encounterService) {
        this.obsDao = obsDao;
        this.episodeDAO = episodeDAO;
        this.encounterService = encounterService;
    }

    @Override
    public BahmniEncounterTransaction update(BahmniEncounterTransaction bahmniEncounterTransaction) {
        rulesForFilledForms(bahmniEncounterTransaction);
        return bahmniEncounterTransaction;
    }

    private void rulesForFilledForms(BahmniEncounterTransaction bahmniEncounterTransaction) {
        String patientProgramUuid = bahmniEncounterTransaction.getPatientProgramUuid();
        HashMap<String, List<BahmniObservation>> mapOfTemplateForms= new HashMap();
        for (BahmniObservation observation : bahmniEncounterTransaction.getObservations()) {
            if (mapOfTemplateForms.containsKey(observation.getConcept().getName())) {
                mapOfTemplateForms.get(observation.getConcept().getName()).add(observation);
            }
            else mapOfTemplateForms.put(observation.getConcept().getName(), new ArrayList<>(Arrays.asList(observation)));
        }
        for (Map.Entry<String, List<BahmniObservation>> templateObs: mapOfTemplateForms.entrySet()) {
            String conceptName = templateObs.getKey();
            switch (conceptName) {
                case "Baseline Template":
                case "Treatment Initiation Template":
                case "Outcome End of Treatment Template":
                    if (isFormFilledAlready(templateObs.getValue(), conceptName, patientProgramUuid)) {
                        throw new RuntimeException(conceptName + " is already filled for this treatment");
                    }
                    break;
                case "Monthly Treatment Completeness Template":
                    if (isFormFilledAlreadyForCurrentMonthYear(templateObs.getValue(), conceptName, bahmniEncounterTransaction)) {
                        throw new RuntimeException(conceptName + " is already filled for this month and year for this treatment");
                    }
                    break;
            }
        }
    }

    private boolean isFormFilledAlready(List<BahmniObservation> newObservations, String conceptName, String patientProgramUuid) {
        if (newObservations.size() > 1) return true;
        List<Obs> obsList = obsDao.getObsByPatientProgramUuidAndConceptNames(patientProgramUuid, Arrays.asList(conceptName), null, null, null, null);
        for (BahmniObservation newObservation : newObservations) {
            if (obsList.size() > 0) {
                for(Obs obs : obsList){
                    if(obs.getUuid().equals(newObservation.getUuid())) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean isFormFilledAlreadyForCurrentMonthYear(List<BahmniObservation> newObservations, String conceptName, BahmniEncounterTransaction bahmniEncounterTransaction) {
        String patientProgramUuid = bahmniEncounterTransaction.getPatientProgramUuid();
        if(patientProgramUuid == null) {
            patientProgramUuid = getPatientProgramUuidByEncounterUuid(bahmniEncounterTransaction.getEncounterUuid());
        }

        Set<String> dateSet = new HashSet<>();
        Set<String> processedObservationUuids = new HashSet<>();
        for (BahmniObservation newObservation : newObservations) {
            if (!checkIfVoided(newObservation) && newObservation.getObservationDateTime() != null && isDatePresentInDateSet(dateSet, newObservation.getObservationDateTime())) {
                return true;
            }
            processedObservationUuids.add(newObservation.getUuid());
        }

        List<Obs> existingObs = obsDao.getObsByPatientProgramUuidAndConceptNames(patientProgramUuid, Arrays.asList(conceptName), null, null, null, null);
        for(Obs obs : existingObs) {
            if(!processedObservationUuids.contains(obs.getUuid())) {
                if (isDatePresentInDateSet(dateSet, obs.getObsDatetime())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkIfVoided(BahmniObservation observation) {
        for (BahmniObservation bahmniObservation : observation.getGroupMembers()) {
            if(bahmniObservation.getValue() != null && !StringUtils.isEmpty(bahmniObservation.getValue())) {
                return false;
            }
        }
        return true;
    }

    private boolean isDatePresentInDateSet(Set<String> dateSet, Date obsDateTime) {
        String monthYear = dateFormat.format(obsDateTime);
        if(dateSet.contains(monthYear)) {
            return true;
        }
        dateSet.add(monthYear);
        return false;
    }

    private String getPatientProgramUuidByEncounterUuid(String encounterUuid) {
        Encounter encounter = encounterService.getEncounterByUuid(encounterUuid);
        Episode episode = episodeDAO.getEpisodeForEncounter(encounter);
        PatientProgram patientProgram = episode.getPatientPrograms().iterator().next();
        return patientProgram.getUuid();
    }

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }
}
