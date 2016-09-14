package org.openmrs.module.endtb.flowsheet.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.drugorder.contract.BahmniDrugOrder;
import org.openmrs.module.endtb.bahmniCore.EndTbObsDaoImpl;
import org.openmrs.module.endtb.flowsheet.constants.ColourCode;
import org.openmrs.module.endtb.flowsheet.constants.FlowsheetConstant;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetMilestone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Scope("prototype")
public class FlowsheetDrugMapper extends FlowsheetMapper {

    @Autowired
    public FlowsheetDrugMapper(EndTbObsDaoImpl endTbObsDao, BahmniDrugOrderService bahmniDrugOrderService, ConceptService conceptService) {
        super(endTbObsDao, bahmniDrugOrderService, conceptService);
        this.conceptTypes = new String[]{FlowsheetConstant.DRUGS};
    }

    @Override
    public void createFlowSheet() throws ParseException {
        Set<String> singleConcepts = getAllSingleConceptsFromFlowsheetConfig();
        Map<String, Set<String>> groupConcepts = getAllGroupConceptsFromFlowsheetConfig();

        createBasicFlowsheet();
        if (startDate == null) {
            return;
        }

        Map<String, List<BahmniDrugOrder>> conceptToDrugMap = getConceptToDrugMap();

        Set<String> commonSingleConcepts = getSingleConceptsFromFlowsheetEntities(flowsheetConfig.getFlowsheetEntities());
        Map<String, Set<String>> commonGroupConcepts = getGroupConceptsFromFlowsheetEntities(flowsheetConfig.getFlowsheetEntities());
        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            mapSingleConcept(milestone, singleConcepts, conceptToDrugMap, commonSingleConcepts);
            mapGroupConcept(milestone, groupConcepts, conceptToDrugMap, commonGroupConcepts);
        }
    }

    private void mapGroupConcept(FlowsheetMilestone milestone, Map<String, Set<String>> groupConcepts, Map<String, List<BahmniDrugOrder>> conceptToDrugMap, Map<String, Set<String>> commonGroupConcepts) throws ParseException {
        Map<String, Set<String>> groupConceptsRequiredForMilestone = getGroupConceptsFromFlowsheetEntities(milestone.getFlowsheetEntities());
        groupConceptsRequiredForMilestone.putAll(commonGroupConcepts);
        for(Map.Entry<String, Set<String>> groupConceptEntry : groupConcepts.entrySet()) {
            boolean conceptRequiredForMilestone = groupConceptsRequiredForMilestone.containsKey(groupConceptEntry.getKey());
            String colorCodeForGroupConcepts = getColorCodeForGroupConcepts(milestone, conceptRequiredForMilestone, groupConceptEntry.getValue(), conceptToDrugMap);
            flowsheet.addFlowSheetData(groupConceptEntry.getKey(), colorCodeForGroupConcepts);
        }
    }

    private void mapSingleConcept(FlowsheetMilestone milestone, Set<String> singleConcepts, Map<String, List<BahmniDrugOrder>> conceptToDrugMap, Set<String> commonSingleConcepts) throws ParseException {
        Set<String> singleConceptsRequiredForMilestone = getSingleConceptsFromFlowsheetEntities(milestone.getFlowsheetEntities());
        singleConceptsRequiredForMilestone.addAll(commonSingleConcepts);
        for (String concept : singleConcepts) {
            boolean conceptRequiredForMilestone = singleConceptsRequiredForMilestone.contains(concept);
            boolean drugPresentInMilestoneRange = isDrugPresentInMilestoneRange(milestone, conceptToDrugMap.get(concept));
            String colorCodeForSingleConcepts = getColorCodeForSingleConcepts(milestone, conceptRequiredForMilestone, drugPresentInMilestoneRange);
            flowsheet.addFlowSheetData(concept, colorCodeForSingleConcepts);
        }
    }

    private String getColorCodeForGroupConcepts(FlowsheetMilestone milestone, Boolean conceptRequiredForMilestone, Set<String> concepts, Map<String, List<BahmniDrugOrder>> conceptToDrugMap) throws ParseException {
        if(conceptRequiredForMilestone) {
            Set<String> colorCodes = new HashSet<>();
            for(String concept : concepts) {
                boolean drugPresentInMilestoneRange = isDrugPresentInMilestoneRange(milestone, conceptToDrugMap.get(concept));
                colorCodes.add(getColorCodeForSingleConcepts(milestone, true, drugPresentInMilestoneRange));
            }
            return colorCodeStrategy(colorCodes);
        }
        return ColourCode.GREY.getColourCode();
    }

    private boolean isDrugPresentInMilestoneRange(FlowsheetMilestone milestone, List<BahmniDrugOrder> drugOrderList) {
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

    private Map<String, List<BahmniDrugOrder>> getConceptToDrugMap() throws ParseException {
        Set<String> allDrugConcepts = getAllConcepts();
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
