package org.openmrs.module.endtb.flowsheet.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.module.endtb.flowsheet.constants.ColourCode;
import org.openmrs.module.endtb.flowsheet.constants.FlowsheetConstant;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetMilestone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class FlowsheetObsMapper extends FlowsheetMapper {

    @Autowired
    public FlowsheetObsMapper(ObsDao obsDao, BahmniDrugOrderService bahmniDrugOrderService, ConceptService conceptService) {
        super(obsDao, bahmniDrugOrderService, conceptService);
        this.conceptType = FlowsheetConstant.CLINICAL;
    }

    @Override
    public void createFlowSheet() throws ParseException {
        Set<String> singleConcepts = getAllSingleConceptsFromFlowsheetConfig();
        Map<String, Set<String>> groupConcepts = getAllGroupConceptsFromFlowsheetConfig();

        createBasicFlowsheet();
        if (startDate == null) {
            return;
        }

        Map<String, List<Obs>> conceptToObsMap = getConceptToObsMap();

        Set<String> commonSingleConcepts = getSingleConceptsFromFlowsheetEntities(flowsheetConfig.getFlowsheetEntities());
        Map<String, Set<String>> commonGroupConcepts = getGroupConceptsFromFlowsheetEntities(flowsheetConfig.getFlowsheetEntities());

        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            mapSingleConcept(milestone, singleConcepts, conceptToObsMap, commonSingleConcepts);
            mapGroupConcept(milestone, groupConcepts, conceptToObsMap, commonGroupConcepts);
        }
    }

    private void mapGroupConcept(FlowsheetMilestone milestone, Map<String, Set<String>> groupConcepts, Map<String, List<Obs>> conceptToObsMap, Map<String, Set<String>> commonGroupConcepts) {
        Map<String, Set<String>> groupConceptsRequiredForMilestone = getGroupConceptsFromFlowsheetEntities(milestone.getFlowsheetEntities());
        groupConceptsRequiredForMilestone.putAll(commonGroupConcepts);
        for (Map.Entry<String, Set<String>> groupConceptEntry : groupConcepts.entrySet()) {
            boolean conceptRequiredForMilestone = groupConceptsRequiredForMilestone.containsKey(groupConceptEntry.getKey());
            String colorCodeForGroupConcept = getColorCodeForGroupConcept(milestone, conceptRequiredForMilestone, groupConceptEntry.getValue(), conceptToObsMap);
            flowsheet.addFlowSheetData(groupConceptEntry.getKey(), colorCodeForGroupConcept);
        }
    }

    private void mapSingleConcept(FlowsheetMilestone milestone, Set<String> singleConcepts, Map<String, List<Obs>> conceptToObsMap, Set<String> commonSingleConcepts) {
        Set<String> singleConceptsRequiredForMilestone = getSingleConceptsFromFlowsheetEntities(milestone.getFlowsheetEntities());
        singleConceptsRequiredForMilestone.addAll(commonSingleConcepts);
        for (String concept : singleConcepts) {
            boolean conceptRequiredForMilestone = singleConceptsRequiredForMilestone.contains(concept);
            String colorCodeForSingleConcept = getColorCodeForSingleConcept(milestone, conceptRequiredForMilestone, conceptToObsMap.get(concept));
            flowsheet.addFlowSheetData(concept, colorCodeForSingleConcept);
        }
    }

    private Map<String, List<Obs>> getConceptToObsMap() {
        Set<String> allObsConcepts = getAllConcepts();
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

    private String getColorCodeForSingleConcept(FlowsheetMilestone milestone, Boolean conceptRequiredForMilestone, List<Obs> obsList) {
        if (conceptRequiredForMilestone) {
            if (isConceptPresentInMilestoneRange(milestone, obsList)) {
                return ColourCode.GREEN.getColourCode();
            } else if (dateWithAddedDays(startDate, milestone.getMax()).before(endDate)) {
                return ColourCode.PURPLE.getColourCode();
            } else {
                return ColourCode.YELLOW.getColourCode();
            }
        } else {
            return ColourCode.GREY.getColourCode();
        }
    }

    private String getColorCodeForGroupConcept(FlowsheetMilestone milestone, Boolean conceptRequiredForMilestone, Set<String> concepts, Map<String, List<Obs>> conceptToObsMap) {
        if (conceptRequiredForMilestone) {
            Set<String> colorCodes = new HashSet<>();
            for (String concept : concepts) {
                colorCodes.add(getColorCodeForSingleConcept(milestone, true, conceptToObsMap.get(concept)));
            }
            return colorCodeStrategy(colorCodes);
        }
        return ColourCode.GREY.getColourCode();
    }

    private boolean isConceptPresentInMilestoneRange(FlowsheetMilestone milestone, List<Obs> obsList) {
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
