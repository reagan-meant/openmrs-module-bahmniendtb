package org.openmrs.module.endtb.flowsheet.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.drugorder.contract.BahmniDrugOrder;
import org.openmrs.module.endtb.flowsheet.models.Flowsheet;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetConfig;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetMilestone;
import org.springframework.beans.factory.annotation.Autowired;
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
public class FlowsheetDrugMapper extends FlowsheetMapper {

    @Autowired
    public FlowsheetDrugMapper(ObsDao obsDao, BahmniDrugOrderService bahmniDrugOrderService, ConceptService conceptService) {
        super(obsDao, bahmniDrugOrderService, conceptService);
    }

    @Override
    public void map(Flowsheet flowsheet, FlowsheetConfig flowsheetConfig, String patientUuid, String patientProgramUuid, Date startDate) throws ParseException {
        Set<String> allDrugConcepts = getUniqueFlowsheetConcepts(flowsheetConfig).getDrugConcepts();
        createBasicFlowsheet(flowsheet, flowsheetConfig, allDrugConcepts);
        if (startDate == null) {
            return;
        }

        List<BahmniDrugOrder> drugOrders = bahmniDrugOrderService.getDrugOrders(patientUuid, null, getConceptObjects(allDrugConcepts), null, patientProgramUuid);
        Map<String, List<BahmniDrugOrder>> conceptToDrugMap = getConceptToDrugMap(drugOrders);

        Set<String> commonDrugConcepts = flowsheetConfig.getFlowsheetEntities().getDrugConcepts();
        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            Set<String> milestoneDrugConcepts = new HashSet<>();
            if (milestone.getFlowsheetEntities() != null) {
                milestoneDrugConcepts = milestone.getFlowsheetEntities().getDrugConcepts();
            }
            for (String concept : allDrugConcepts) {
                setDrugMilestoneColourCode(flowsheet, milestone, commonDrugConcepts, milestoneDrugConcepts, conceptToDrugMap.get(concept), concept, startDate);
            }
        }
    }

    private void setDrugMilestoneColourCode(Flowsheet flowsheet, FlowsheetMilestone milestone, Set<String> commonDrugConcepts, Set<String> milestoneDrugConcepts, List<BahmniDrugOrder> drugOrderList, String concept, Date startDate) {
        if (commonDrugConcepts.contains(concept) || milestoneDrugConcepts.contains(concept)) {
            if (isDrugPresentInMilestoneRange(milestone, drugOrderList, startDate)) {
                flowsheet.addFlowSheetData(concept, "green");
            } else if (dateWithAddedDays(startDate, milestone.getMax()).after(new Date())) {
                flowsheet.addFlowSheetData(concept, "yellow");
            } else {
                flowsheet.addFlowSheetData(concept, "purple");
            }
        } else {
            flowsheet.addFlowSheetData(concept, "grey");
        }
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

    private Map<String, List<BahmniDrugOrder>> getConceptToDrugMap(List<BahmniDrugOrder> drugList) {
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
