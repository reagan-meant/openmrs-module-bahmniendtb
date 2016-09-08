package org.openmrs.module.endtb.flowsheet.mapper;

import org.apache.commons.collections.MapUtils;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.endtb.flowsheet.models.Flowsheet;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetConcept;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetConfig;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetMilestone;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class FlowsheetMapper {

    protected ObsDao obsDao;
    protected BahmniDrugOrderService bahmniDrugOrderService;
    protected ConceptService conceptService;

    public FlowsheetMapper(ObsDao obsDao, BahmniDrugOrderService bahmniDrugOrderService, ConceptService conceptService) {
        this.obsDao = obsDao;
        this.bahmniDrugOrderService = bahmniDrugOrderService;
        this.conceptService = conceptService;
    }


    public abstract void map(Flowsheet flowsheet, FlowsheetConfig flowsheetConfig, String patientUuid, String patientProgramUuid, Date startDate) throws ParseException;


    protected Set<Concept> getConceptObjects(Set<String> conceptNames) {
        Set<Concept> conceptsList = new HashSet<>();
        for (String concept : conceptNames) {
            conceptsList.add(conceptService.getConcept(concept));
        }
        return conceptsList;
    }

    protected Date dateWithAddedDays(Date date, Integer days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    protected Set<String> getAllUniqueFlowsheetConcepts(FlowsheetConfig flowsheetConfig, String type) {
        Set<String> conceptNames = new HashSet<>();
        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            if (milestone.getFlowsheetEntities() != null) {
                conceptNames.addAll(getAllFlowsheetConcepts(milestone.getFlowsheetEntities().getFlowSheetConceptByType(type)));
            }
        }
        if (flowsheetConfig.getFlowsheetEntities() != null) {
            conceptNames.addAll(getAllFlowsheetConcepts(flowsheetConfig.getFlowsheetEntities().getFlowSheetConceptByType(type)));
        }
        return conceptNames;
    }

    protected Set<String> getAllFlowsheetConcepts(FlowsheetConcept flowsheetConcept) {
        Set<String> conceptNames = new HashSet<>();
        conceptNames.addAll(flowsheetConcept.getSingleConcepts());
        if(MapUtils.isNotEmpty(flowsheetConcept.getGroupConcepts())) {
            for(Map.Entry<String, Set<String>> entry : flowsheetConcept.getGroupConcepts().entrySet()) {
                conceptNames.addAll(entry.getValue());
            }
        }
        return conceptNames;
    }

    protected void createBasicFlowsheet(Flowsheet flowsheet, FlowsheetConfig flowsheetConfig, Set<String> conceptList) {
        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            flowsheet.addFlowSheetHeader(milestone.getName());
        }
        Map<String, List<String>> flowsheetData = flowsheet.getFlowsheetData();
        for (String conceptName : conceptList) {
            flowsheetData.put(conceptName, new ArrayList<String>());
        }
    }
}
