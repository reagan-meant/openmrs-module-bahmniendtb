package org.openmrs.module.endtb.flowsheet.mapper;

import org.apache.commons.collections.MapUtils;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.endtb.flowsheet.constants.ColourCode;
import org.openmrs.module.endtb.flowsheet.models.*;

import java.text.ParseException;
import java.util.*;

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

    protected Set<String> getAllConcepts(FlowsheetConfig flowsheetConfig, String type) {
        Set<String> concepts = new HashSet<>();
        concepts.addAll(getAllSingleConceptsFromFlowsheetConfig(flowsheetConfig, type));
        for (Map.Entry<String, Set<String>> entry : getAllGroupConceptsFromFlowsheetConfig(flowsheetConfig, type).entrySet()) {
           concepts.addAll(entry.getValue());
        }
        return concepts;
    }

    protected Set<String> getAllSingleConceptsFromFlowsheetConfig(FlowsheetConfig flowsheetConfig, String type) {
        Set<String> concepts = new HashSet<>();
        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            concepts.addAll(getSingleConceptsFromFlowsheetEntities(milestone.getFlowsheetEntities(), type));
        }
        concepts.addAll(getSingleConceptsFromFlowsheetEntities(flowsheetConfig.getFlowsheetEntities(), type));
        return concepts;
    }

    protected Set<String> getSingleConceptsFromFlowsheetEntities(FlowsheetEntities flowsheetEntities, String type) {
        if(flowsheetEntities != null) {
            return flowsheetEntities.getFlowSheetConceptByType(type).getSingleConcepts();
        }
        return new LinkedHashSet<>();
    }

    protected Map<String, Set<String>> getAllGroupConceptsFromFlowsheetConfig(FlowsheetConfig flowsheetConfig, String type) {
        Map<String, Set<String>> concepts = new LinkedHashMap<>();
        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            concepts.putAll(getGroupConceptsFromFlowsheetEntities(milestone.getFlowsheetEntities(), type));
        }
        concepts.putAll(getGroupConceptsFromFlowsheetEntities(flowsheetConfig.getFlowsheetEntities(), type));
        return concepts;
    }

    protected Map<String, Set<String>> getGroupConceptsFromFlowsheetEntities(FlowsheetEntities flowsheetEntities, String type) {
        if(flowsheetEntities != null) {
            return flowsheetEntities.getFlowSheetConceptByType(type).getGroupConcepts();
        }
        return new LinkedHashMap<>();
    }

    protected FlowsheetConcept getFlowsheetConceptFromFlowsheetConfig(FlowsheetConfig flowsheetConfig, String type) {
        FlowsheetConcept flowsheetConcept = new FlowsheetConcept();
        flowsheetConcept.setSingleConcepts(getAllSingleConceptsFromFlowsheetConfig(flowsheetConfig, type));
        flowsheetConcept.setGroupConcepts(getAllGroupConceptsFromFlowsheetConfig(flowsheetConfig, type));
        return flowsheetConcept;
    }

    protected String colorCodeStrategy(Set<String> colorCodes) {
        if(colorCodes.contains(ColourCode.PURPLE.getColourCode())) {
            return ColourCode.PURPLE.getColourCode();
        } else if(colorCodes.contains(ColourCode.YELLOW.getColourCode())) {
            return ColourCode.YELLOW.getColourCode();
        } else {
            return ColourCode.GREEN.getColourCode();
        }
    }

    protected void createBasicFlowsheet(Flowsheet flowsheet, FlowsheetConfig flowsheetConfig, String type) {
        FlowsheetConcept flowsheetConcept = getFlowsheetConceptFromFlowsheetConfig(flowsheetConfig, type);
        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            flowsheet.addFlowSheetHeader(milestone.getName());
        }
        Map<String, List<String>> flowsheetData = flowsheet.getFlowsheetData();
        for (String conceptName : flowsheetConcept.getSingleConcepts()) {
            flowsheetData.put(conceptName, new ArrayList<String>());
        }
        for (Map.Entry<String, Set<String>> entry : flowsheetConcept.getGroupConcepts().entrySet()) {
            flowsheetData.put(entry.getKey(), new ArrayList<String>());
        }
    }
}
