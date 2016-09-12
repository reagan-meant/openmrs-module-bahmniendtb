package org.openmrs.module.endtb.flowsheet.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.drugorder.contract.BahmniDrugOrder;
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
public class FlowsheetDrugMapper extends FlowsheetMapper {

    @Autowired
    public FlowsheetDrugMapper(ObsDao obsDao, BahmniDrugOrderService bahmniDrugOrderService, ConceptService conceptService) {
        super(obsDao, bahmniDrugOrderService, conceptService);
    }

    @Override
    public void map(Flowsheet flowsheet, FlowsheetConfig flowsheetConfig, String patientUuid, String patientProgramUuid, Date startDate) throws ParseException {
        String typeOfConcepts = FlowsheetConstant.DRUGS;
        Set<String> singleConcepts = getAllSingleConceptsFromFlowsheetConfig(flowsheetConfig, typeOfConcepts);
        Map<String, Set<String>> groupConcepts = getAllGroupConceptsFromFlowsheetConfig(flowsheetConfig, typeOfConcepts);

        createBasicFlowsheet(flowsheet, flowsheetConfig, typeOfConcepts);
        if (startDate == null) {
            return;
        }

        Map<String, List<BahmniDrugOrder>> conceptToDrugMap = getConceptToDrugMap(flowsheetConfig, patientUuid, patientProgramUuid);

        Set<String> commonSingleConcepts = getSingleConceptsFromFlowsheetEntities(flowsheetConfig.getFlowsheetEntities(), typeOfConcepts);
        Map<String, Set<String>> commonGroupConcepts = getGroupConceptsFromFlowsheetEntities(flowsheetConfig.getFlowsheetEntities(), typeOfConcepts);
        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            mapSingleConcept(flowsheet, milestone, singleConcepts, conceptToDrugMap, commonSingleConcepts, typeOfConcepts, startDate);
            mapGroupConcept(flowsheet, milestone, groupConcepts, conceptToDrugMap, commonGroupConcepts, typeOfConcepts, startDate);
        }
    }

    private void mapGroupConcept(Flowsheet flowsheet, FlowsheetMilestone milestone, Map<String, Set<String>> groupConcepts, Map<String, List<BahmniDrugOrder>> conceptToDrugMap, Map<String, Set<String>> commonGroupConcepts, String typeOfConcepts, Date startDate) {
        Map<String, Set<String>> groupConceptsRequiredForMilestone = getGroupConceptsFromFlowsheetEntities(milestone.getFlowsheetEntities(), typeOfConcepts);
        groupConceptsRequiredForMilestone.putAll(commonGroupConcepts);
        for(Map.Entry<String, Set<String>> groupConceptEntry : groupConcepts.entrySet()) {
            boolean conceptRequiredForMilestone = groupConceptsRequiredForMilestone.containsKey(groupConceptEntry.getKey());
            String colorCodeForGroupConcepts = getColorCodeForGroupConcepts(milestone, conceptRequiredForMilestone, groupConceptEntry.getValue(), conceptToDrugMap, startDate);
            flowsheet.addFlowSheetData(groupConceptEntry.getKey(), colorCodeForGroupConcepts);
        }
    }

    private void mapSingleConcept(Flowsheet flowsheet, FlowsheetMilestone milestone, Set<String> singleConcepts, Map<String, List<BahmniDrugOrder>> conceptToDrugMap, Set<String> commonSingleConcepts, String typeOfConcepts, Date startDate) {
        Set<String> singleConceptsRequiredForMilestone = getSingleConceptsFromFlowsheetEntities(milestone.getFlowsheetEntities(), typeOfConcepts);
        singleConceptsRequiredForMilestone.addAll(commonSingleConcepts);
        for (String concept : singleConcepts) {
            boolean conceptRequiredForMilestone = singleConceptsRequiredForMilestone.contains(concept);
            String colorCodeForSingleConcepts = getColorCodeForSingleConcepts(milestone, conceptRequiredForMilestone, conceptToDrugMap.get(concept), startDate);
            flowsheet.addFlowSheetData(concept, colorCodeForSingleConcepts);
        }
    }

    private String getColorCodeForSingleConcepts(FlowsheetMilestone milestone, Boolean conceptRequiredForMilestone, List<BahmniDrugOrder> drugOrderList, Date startDate) {
        if (conceptRequiredForMilestone) {
            if (isDrugPresentInMilestoneRange(milestone, drugOrderList, startDate)) {
                return ColourCode.GREEN.getColourCode();
            } else if (dateWithAddedDays(startDate, milestone.getMax()).after(new Date())) {
                return ColourCode.YELLOW.getColourCode();
            } else {
                return ColourCode.PURPLE.getColourCode();
            }
        } else {
            return ColourCode.GREY.getColourCode();
        }
    }

    private String getColorCodeForGroupConcepts(FlowsheetMilestone milestone, Boolean conceptRequiredForMilestone, Set<String> concepts, Map<String, List<BahmniDrugOrder>> conceptToDrugMap, Date startDate) {
        if(conceptRequiredForMilestone) {
            Set<String> colorCodes = new HashSet<>();
            for(String concept : concepts) {
                colorCodes.add(getColorCodeForSingleConcepts(milestone, true, conceptToDrugMap.get(concept), startDate));
            }
            return colorCodeStrategy(colorCodes);
        }
        return ColourCode.GREY.getColourCode();
    }

    private boolean isDrugPresentInMilestoneRange(FlowsheetMilestone milestone, List<BahmniDrugOrder> drugOrderList, Date startDate) {
        if (CollectionUtils.isNotEmpty(drugOrderList)) {
            Date milestoneStartDate = dateWithAddedDays(startDate, milestone.getMin());
            Date milestoneEndDate = dateWithAddedDays(startDate, milestone.getMax());
            for (BahmniDrugOrder drug : drugOrderList) {
                Date drugStartDate = drug.getEffectiveStartDate();
                Date drugStopDate = drug.getEffectiveStopDate() != null ? drug.getEffectiveStopDate() : new Date();
                if ((milestoneStartDate.before(drugStopDate) || milestoneStartDate.equals(drugStopDate)) &&
                        (milestoneEndDate.after(drugStartDate) || milestoneEndDate.equals(drugStartDate))) {
                    return true;
                }
            }
        }
        return false;
    }

    private Map<String, List<BahmniDrugOrder>> getConceptToDrugMap(FlowsheetConfig flowsheetConfig, String patientUuid, String patientProgramUuid) throws ParseException {
        Set<String> allDrugConcepts = getAllConcepts(flowsheetConfig, FlowsheetConstant.DRUGS);
        List<BahmniDrugOrder> drugList = bahmniDrugOrderService.getDrugOrders(patientUuid, null, getConceptObjects(allDrugConcepts), null, patientProgramUuid);
        Map<String, List<BahmniDrugOrder>> conceptToDrugMap = new LinkedHashMap<>();
        if (CollectionUtils.isEmpty(drugList)) {
            return new LinkedHashMap<>();
        }
        for (BahmniDrugOrder drug : drugList) {
            List<BahmniDrugOrder> drugs = conceptToDrugMap.get(drug.getConcept().getName());
            if (CollectionUtils.isEmpty(drugs)) {
                drugs = new ArrayList<>();
                conceptToDrugMap.put(drug.getConcept().getName(), drugs);
            }
            drugs.add(drug);
        }
        return conceptToDrugMap;
    }
}
