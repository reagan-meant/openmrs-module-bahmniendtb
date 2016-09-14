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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Scope("prototype")
public class FlowsheetClinicalAndBacteriologyMapper extends FlowsheetMapper {

    @Autowired
    public FlowsheetClinicalAndBacteriologyMapper(ObsDao obsDao, BahmniDrugOrderService bahmniDrugOrderService, ConceptService conceptService) {
        super(obsDao, bahmniDrugOrderService, conceptService);
        this.conceptTypes = new String[]{FlowsheetConstant.CLINICAL, FlowsheetConstant.BACTERIOLOGY};
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

    private void mapGroupConcept(FlowsheetMilestone milestone, Map<String, Set<String>> groupConcepts, Map<String, List<Obs>> conceptToObsMap, Map<String, Set<String>> commonGroupConcepts) throws ParseException {
        Map<String, Set<String>> groupConceptsRequiredForMilestone = getGroupConceptsFromFlowsheetEntities(milestone.getFlowsheetEntities());
        groupConceptsRequiredForMilestone.putAll(commonGroupConcepts);
        for (Map.Entry<String, Set<String>> groupConceptEntry : groupConcepts.entrySet()) {
            boolean conceptRequiredForMilestone = groupConceptsRequiredForMilestone.containsKey(groupConceptEntry.getKey());
            String colorCodeForGroupConcept = getColorCodeForGroupConcept(milestone, conceptRequiredForMilestone, groupConceptEntry.getValue(), conceptToObsMap);
            flowsheet.addFlowSheetData(groupConceptEntry.getKey(), colorCodeForGroupConcept);
        }
    }

    private void mapSingleConcept(FlowsheetMilestone milestone, Set<String> singleConcepts, Map<String, List<Obs>> conceptToObsMap, Set<String> commonSingleConcepts) throws ParseException {
        Set<String> singleConceptsRequiredForMilestone = getSingleConceptsFromFlowsheetEntities(milestone.getFlowsheetEntities());
        singleConceptsRequiredForMilestone.addAll(commonSingleConcepts);
        for (String concept : singleConcepts) {
            boolean conceptRequiredForMilestone = singleConceptsRequiredForMilestone.contains(concept);
            boolean conceptPresentInMilestoneRange = isConceptPresentInMilestoneRange(milestone, conceptToObsMap.get(concept));
            String colorCodeForSingleConcept = getColorCodeForSingleConcepts(milestone, conceptRequiredForMilestone, conceptPresentInMilestoneRange);
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
            List<Obs> observations = conceptToObsMap.get(obs.getConcept().getName().getName());
            if (CollectionUtils.isEmpty(observations)) {
                observations = new ArrayList<>();
                conceptToObsMap.put(obs.getConcept().getName().getName(), observations);
            }
            observations.add(obs);
        }
        return conceptToObsMap;
    }

    private String getColorCodeForGroupConcept(FlowsheetMilestone milestone, Boolean conceptRequiredForMilestone, Set<String> concepts, Map<String, List<Obs>> conceptToObsMap) throws ParseException {
        if (conceptRequiredForMilestone) {
            Set<String> colorCodes = new HashSet<>();
            for (String concept : concepts) {
                boolean conceptPresentInMilestoneRange = isConceptPresentInMilestoneRange(milestone, conceptToObsMap.get(concept));
                colorCodes.add(getColorCodeForSingleConcepts(milestone, true, conceptPresentInMilestoneRange));
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
