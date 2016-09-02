package org.openmrs.module.endtb.flowsheet.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.javac.comp.Flow;
import org.apache.commons.collections.CollectionUtils;
import java.lang.String;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.hibernate.type.CalendarType;
import org.openmrs.Obs;
import org.openmrs.module.endtb.flowsheet.models.Flowsheet;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetConcepts;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetConfig;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetMilestone;
import org.openmrs.module.endtb.flowsheet.service.PatientMonitoringFlowsheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        List<Obs> observations = getAllObservationsAfterTreatmentStartDate(patientProgramUuid, flowsheetConcepts, treatmentStartDate);
        return new Flowsheet();
    }

    private Map<String, List<Obs>> getConceptToObservationMap(List<Obs> observations) {
        Map<String, List<Obs>> conceptToObservationMap = new LinkedHashMap<>();
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

//    private Flowsheet getFlowsheetFromObservations(Map<String, List<Obs>> conceptToObservationMap, FlowsheetConfig flowsheetConfig, Date treatmentStartDate) {
//        Flowsheet flowsheet = new Flowsheet();
//        for(FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
//            flowsheet.addFlowSheetHeader(milestone.getName());
//            for(String concept : milestone.getFlowsheetConcepts().getClinicalConcepts()) {
//                for(Obs obs : conceptToObservationMap.get(concept)) {
//                    if(obs.getObsDatetime().after(addDays(treatmentStartDate, milestone.getMin())) && obs.getObsDatetime().before(addDays(treatmentStartDate, milestone.getMax()))) {
//                        flowsheet.addFlowSheetData(concept, "green");
//                    }
//                }
//            }
//        }
//
//        return null;
//    }

    private Flowsheet getFlowsheetFromObservations(List<String> concepts, Map<String, List<Obs>> conceptToObservationMap, FlowsheetConfig flowsheetConfig, Date treatmentStartDate) {
        Flowsheet flowsheet = new Flowsheet();
        for(String concept : concepts) {
            if(flowsheetConfig.getFlowsheetConcepts().getClinicalConcepts().contains(concept)) {
                List<Obs> observationsForConcept = conceptToObservationMap.get(concept);
                for(FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
                    if(isObservationForConceptIsPresentInMilestoneDateRange(observationsForConcept, treatmentStartDate, milestone.getMin(), milestone.getMax())) {
                        flowsheet.addFlowSheetData(concept, "green");
                    } else if(dateWithAddedDays(treatmentStartDate, milestone.getMax()).before(new Date())) {
                        flowsheet.addFlowSheetData(concept, "red");
                    } else {
                        flowsheet.addFlowSheetData(concept, "yellow");
                    }
                }
            } else {
                for(FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
                    if(milestone.getFlowsheetConcepts().getClinicalConcepts().contains(concept)) {

                    } else {
                        flowsheet.addFlowSheetData(concept, "grey");
                    }
                }
            }
        }

        return null;
    }

    private void getMappedFlowsheet(FlowsheetConfig flowsheetConfig, Map<String, List<Obs>> conceptToObservationMap) {
        Flowsheet flowsheet = new Flowsheet();

        for(Map.Entry<String, List<Obs>> entry : conceptToObservationMap.entrySet()) {

        }

        for(FlowsheetMilestone flowsheetMilestone : flowsheetConfig.getFlowsheetMilestones()) {
            if(null != flowsheetMilestone.getFlowsheetConcepts()) {

            } else {

            }
            flowsheet.getFlowsheetData()
        }
    }

    private boolean isObservationForConceptIsPresentInMilestoneDateRange(List<Obs> observationsForConcept, Date treatmentStartDate, Integer min, Integer max) {
        for(Obs observation : observationsForConcept) {
            if(observation.getObsDatetime().after(dateWithAddedDays(treatmentStartDate, min)) && observation.getObsDatetime().before(dateWithAddedDays(treatmentStartDate, max))) {
                return true;
            }
        }
        return false;
    }

    private Flowsheet setColorForConceptData(Flowsheet flowsheet, FlowsheetMilestone milestone, String concept,  List<Obs> observationsForConcept, Date treatmentStartDate, Boolean requiredForMilestone) {
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

    private List<Obs> getAllObservationsAfterTreatmentStartDate(String patientProgramUuid, FlowsheetConcepts flowsheetConcepts, Date treatmentStartDate) {
        return obsDao.getObsByPatientProgramUuidAndConceptNames(patientProgramUuid, new ArrayList<String>(flowsheetConcepts.getClinicalConcepts()), null, null, treatmentStartDate, null);
    }

    private FlowsheetConfig getPatientMonitoringFlowsheetConfigurationJSON(String configFilePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FlowsheetConfig flowsheetConfig = mapper.readValue(new File(configFilePath), FlowsheetConfig.class);
        return flowsheetConfig;
    }

}
