package org.openmrs.module.endtb.flowsheet.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.openmrs.Obs;
import org.openmrs.module.endtb.flowsheet.models.Flowsheet;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetConcepts;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetConfig;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetMilestone;
import org.openmrs.module.endtb.flowsheet.service.PatientMonitoringFlowsheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class PatientMonitoringFlowsheetServiceImpl implements PatientMonitoringFlowsheetService {

    private ObsDao obsDao;

    @Autowired
    public PatientMonitoringFlowsheetServiceImpl(ObsDao obsDao) {
        this.obsDao = obsDao;
    }

    @Override
    public Flowsheet getFlowsheetForPatientProgram(String patientProgramUuid, String configFilePath) throws Exception {
        FlowsheetConfig configurationJSON = getPatientMonitoringFlowsheetConfigurationJSON(configFilePath);
        FlowsheetConcepts flowsheetConcepts = getUniqueFlowsheetConcepts(configurationJSON);
        List<Obs> treatmentStartDateObs = obsDao.getObsByPatientProgramUuidAndConceptNames(patientProgramUuid, Arrays.asList("TUBERCULOSIS DRUG TREATMENT START DATE"), null, null, null, null);
        Date treatmentStartDate = null;
        if(CollectionUtils.isNotEmpty(treatmentStartDateObs)) {
            treatmentStartDate = treatmentStartDateObs.get(0).getValueDate();
        }
        if(treatmentStartDate == null) {
            return null;
        }
        List<Obs> observations = obsDao.getObsByPatientProgramUuidAndConceptNames(patientProgramUuid, new ArrayList<String>(flowsheetConcepts.getClinicalConcepts()), null, null, treatmentStartDate, null);
        Map<String, List<Obs>> conceptToObservationMap = getConceptToObservationMap(observations);
        return getFlowsheetFromObservations(flowsheetConcepts, conceptToObservationMap, configurationJSON, treatmentStartDate);
    }

    private Map<String, List<Obs>> getConceptToObservationMap(List<Obs> observations) {
        Map<String, List<Obs>> conceptToObservationMap = new LinkedHashMap<>();
        if(CollectionUtils.isEmpty(observations)) {
            return new LinkedHashMap<>();
        }
        for(Obs obs : observations) {
            List<Obs> observationList = conceptToObservationMap.get(obs.getConcept().getName());
            if(CollectionUtils.isEmpty(observationList)) {
                observationList = new ArrayList<>();
            }
            observationList.add(obs);
            conceptToObservationMap.put(obs.getConcept().getName().getName(), observationList);
        }
        return conceptToObservationMap;
    }

    private Flowsheet getFlowsheetFromObservations(FlowsheetConcepts flowsheetConcepts, Map<String, List<Obs>> conceptToObservationMap, FlowsheetConfig flowsheetConfig, Date treatmentStartDate) {
        Flowsheet flowsheet = getFlowsheetWithHeader(flowsheetConfig);
        for(String concept : flowsheetConcepts.getClinicalConcepts()) {
            List<Obs> observationsForConcept = conceptToObservationMap.get(concept);
            for(FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
                if(CollectionUtils.isNotEmpty(flowsheetConfig.getFlowsheetConcepts().getClinicalConcepts()) && flowsheetConfig.getFlowsheetConcepts().getClinicalConcepts().contains(concept)) {
                    flowsheet = getFlowsheetAfterAddingConceptForAMilestone(flowsheet, milestone, concept, observationsForConcept, treatmentStartDate, true);
                } else if(milestone.getFlowsheetConcepts()!=null && CollectionUtils.isNotEmpty(milestone.getFlowsheetConcepts().getClinicalConcepts()) && milestone.getFlowsheetConcepts().getClinicalConcepts().contains(concept)) {
                    flowsheet = getFlowsheetAfterAddingConceptForAMilestone(flowsheet, milestone, concept, observationsForConcept, treatmentStartDate, true);
                } else {
                    flowsheet = getFlowsheetAfterAddingConceptForAMilestone(flowsheet, milestone, concept, observationsForConcept, treatmentStartDate, false);
                }
            }
        }
        return flowsheet;
    }

    private Flowsheet getFlowsheetWithHeader(FlowsheetConfig flowsheetConfig) {
        Flowsheet flowsheet = new Flowsheet();
        for(FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            flowsheet.addFlowSheetHeader(milestone.getName());
        }
        return flowsheet;
    }

    private boolean isObservationForConceptIsPresentInMilestoneDateRange(List<Obs> observationsForConcept, Date treatmentStartDate, Integer min, Integer max) {
        if(CollectionUtils.isEmpty(observationsForConcept)) {
            return false;
        }
        for(Obs observation : observationsForConcept) {
            if(observation.getObsDatetime().after(dateWithAddedDays(treatmentStartDate, min)) && observation.getObsDatetime().before(dateWithAddedDays(treatmentStartDate, max))) {
                return true;
            }
        }
        return false;
    }

    private Flowsheet getFlowsheetAfterAddingConceptForAMilestone(Flowsheet flowsheet, FlowsheetMilestone milestone, String concept,  List<Obs> observationsForConcept, Date treatmentStartDate, Boolean requiredForMilestone) {
        if(!requiredForMilestone) {
            flowsheet.addFlowSheetData(concept, "grey");
        } else if(isObservationForConceptIsPresentInMilestoneDateRange(observationsForConcept, treatmentStartDate, milestone.getMin(), milestone.getMax())) {
            flowsheet.addFlowSheetData(concept, "green");
        } else if(dateWithAddedDays(treatmentStartDate, milestone.getMax()).before(new Date())) {
            flowsheet.addFlowSheetData(concept, "red");
        } else {
            flowsheet.addFlowSheetData(concept, "yellow");
        }
        return flowsheet;
    }

    private Date dateWithAddedDays(Date date, Integer days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    private FlowsheetConcepts getUniqueFlowsheetConcepts(FlowsheetConfig flowsheetConfig) {
        FlowsheetConcepts flowsheetConcepts = new FlowsheetConcepts();
        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            if (milestone.getFlowsheetConcepts() != null) {
                flowsheetConcepts.getClinicalConcepts().addAll(milestone.getFlowsheetConcepts().getClinicalConcepts());
                flowsheetConcepts.getBacteriologyConcepts().addAll(milestone.getFlowsheetConcepts().getBacteriologyConcepts());
                flowsheetConcepts.getDrugConcepts().addAll(milestone.getFlowsheetConcepts().getDrugConcepts());
            }
        }
        if (flowsheetConfig.getFlowsheetConcepts() != null) {
            flowsheetConcepts.getClinicalConcepts().addAll(flowsheetConfig.getFlowsheetConcepts().getClinicalConcepts());
            flowsheetConcepts.getBacteriologyConcepts().addAll(flowsheetConfig.getFlowsheetConcepts().getBacteriologyConcepts());
            flowsheetConcepts.getDrugConcepts().addAll(flowsheetConfig.getFlowsheetConcepts().getDrugConcepts());
        }
        return flowsheetConcepts;
    }

    private FlowsheetConfig getPatientMonitoringFlowsheetConfigurationJSON(String configFilePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FlowsheetConfig flowsheetConfig = mapper.readValue(new File(configFilePath), FlowsheetConfig.class);
        return flowsheetConfig;
    }

}
