package org.openmrs.module.endtb.flowsheet.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.module.endtb.flowsheet.constants.ColourCode;
import org.openmrs.module.endtb.flowsheet.constants.FlowsheetConstant;
import org.openmrs.module.endtb.flowsheet.models.Flowsheet;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetConfig;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetMilestone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

@Component
public class FlowsheetObsMapper extends FlowsheetMapper {

    @Autowired
    public FlowsheetObsMapper(ObsDao obsDao, BahmniDrugOrderService bahmniDrugOrderService, ConceptService conceptService) {
        super(obsDao, bahmniDrugOrderService, conceptService);
    }

    @Override
    public void map(Flowsheet flowsheet, FlowsheetConfig flowsheetConfig, String patientUuid, String patientProgramUuid, Date startDate) throws ParseException {
        String typeOfConcepts = FlowsheetConstant.CLINICAL;
        Set<String> singleConcepts = getAllSingleConceptsFromFlowsheetConfig(flowsheetConfig, typeOfConcepts);
        Map<String, Set<String>> groupConcepts = getAllGroupConceptsFromFlowsheetConfig(flowsheetConfig, typeOfConcepts);

        createBasicFlowsheet(flowsheet, flowsheetConfig, typeOfConcepts);
        if (startDate == null) {
            return;
        }

        Map<String, List<Obs>> conceptToObsMap = getConceptToObsMap(flowsheetConfig, patientProgramUuid, startDate);

        Set<String> commonSingleConcepts = getSingleConceptsFromFlowsheetEntities(flowsheetConfig.getFlowsheetEntities(), typeOfConcepts);
        Map<String, Set<String>> commonGroupConcepts = getGroupConceptsFromFlowsheetEntities(flowsheetConfig.getFlowsheetEntities(), typeOfConcepts);

        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            mapSingleConcept(flowsheet, milestone, singleConcepts, conceptToObsMap, commonSingleConcepts, typeOfConcepts, startDate);
            mapGroupConcept(flowsheet, milestone, groupConcepts, conceptToObsMap, commonGroupConcepts, typeOfConcepts, startDate);
        }
    }

    private void mapGroupConcept(Flowsheet flowsheet, FlowsheetMilestone milestone, Map<String, Set<String>> groupConcepts, Map<String, List<Obs>> conceptToObsMap, Map<String, Set<String>> commonGroupConcepts, String typeOfConcepts, Date startDate) {
        Map<String, Set<String>> groupConceptsRequiredForMilestone = getGroupConceptsFromFlowsheetEntities(milestone.getFlowsheetEntities(), typeOfConcepts);
        groupConceptsRequiredForMilestone.putAll(commonGroupConcepts);
        for (Map.Entry<String, Set<String>> groupConceptEntry : groupConcepts.entrySet()) {
            boolean conceptRequiredForMilestone = groupConceptsRequiredForMilestone.containsKey(groupConceptEntry.getKey());
            String colorCodeForGroupConcept = getColorCodeForGroupConcept(milestone, conceptRequiredForMilestone, groupConceptEntry.getValue(), conceptToObsMap, startDate);
            flowsheet.addFlowSheetData(groupConceptEntry.getKey(), colorCodeForGroupConcept);
        }
    }

    private void mapSingleConcept(Flowsheet flowsheet, FlowsheetMilestone milestone, Set<String> singleConcepts, Map<String, List<Obs>> conceptToObsMap, Set<String> commonSingleConcepts, String typeOfConcepts, Date startDate) {
        Set<String> singleConceptsRequiredForMilestone = getSingleConceptsFromFlowsheetEntities(milestone.getFlowsheetEntities(), typeOfConcepts);
        singleConceptsRequiredForMilestone.addAll(commonSingleConcepts);
        for (String concept : singleConcepts) {
            boolean conceptRequiredForMilestone = singleConceptsRequiredForMilestone.contains(concept);
            String colorCodeForSingleConcept = getColorCodeForSingleConcept(milestone, conceptRequiredForMilestone, conceptToObsMap.get(concept), startDate);
            flowsheet.addFlowSheetData(concept, colorCodeForSingleConcept);
        }
    }

    private Map<String, List<Obs>> getConceptToObsMap(FlowsheetConfig flowsheetConfig, String patientProgramUuid, Date startDate) {
        Set<String> allObsConcepts = getAllConcepts(flowsheetConfig, FlowsheetConstant.CLINICAL);
        List<Obs> obsList = obsDao.getObsByPatientProgramUuidAndConceptNames(patientProgramUuid, new ArrayList<>(allObsConcepts), null, null, startDate, null);
        Map<String, List<Obs>> conceptToObsMap = new LinkedHashMap<>();
        if (CollectionUtils.isEmpty(obsList)) {
            return new LinkedHashMap<>();
        }
        for (Obs obs : obsList) {
            List<Obs> observations = conceptToObsMap.get(obs.getConcept().getName());
            if (CollectionUtils.isEmpty(observations)) {
                observations = new ArrayList<>();
                conceptToObsMap.put(obs.getConcept().getName().getName(), observations);
            }
            observations.add(obs);
        }
        return conceptToObsMap;
    }

    private String getColorCodeForSingleConcept(FlowsheetMilestone milestone, Boolean conceptRequiredForMilestone, List<Obs> obsList, Date startDate) {
        if (conceptRequiredForMilestone) {
            if (isConceptPresentInMilestoneRange(milestone, startDate, obsList)) {
                return ColourCode.GREEN.getColourCode();
            } else if (dateWithAddedDays(startDate, milestone.getMax()).before(new Date())) {
                return ColourCode.PURPLE.getColourCode();
            } else {
                return ColourCode.YELLOW.getColourCode();
            }
        } else {
            return ColourCode.GREY.getColourCode();
        }
    }

    private String getColorCodeForGroupConcept(FlowsheetMilestone milestone, Boolean conceptRequiredForMilestone, Set<String> concepts, Map<String, List<Obs>> conceptToObsMap, Date startDate) {
        if (conceptRequiredForMilestone) {
            Set<String> colorCodes = new HashSet<>();
            for (String concept : concepts) {
                colorCodes.add(getColorCodeForSingleConcept(milestone, true, conceptToObsMap.get(concept), startDate));
            }
            return colorCodeStrategy(colorCodes);
        }
        return ColourCode.GREY.getColourCode();
    }

    private boolean isConceptPresentInMilestoneRange(FlowsheetMilestone milestone, Date startDate, List<Obs> obsList) {
        if (CollectionUtils.isNotEmpty(obsList)) {
            for (Obs obs : obsList) {
                if (obs.getObsDatetime().after(dateWithAddedDays(startDate, milestone.getMin())) && obs.getObsDatetime().before(dateWithAddedDays(startDate, milestone.getMax()))) {
                    return true;
                }
            }
        }
        return false;
    }
}
