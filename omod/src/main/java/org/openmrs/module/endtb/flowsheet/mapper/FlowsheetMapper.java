package org.openmrs.module.endtb.flowsheet.mapper;

import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.endtb.flowsheet.constants.ColourCode;
import org.openmrs.module.endtb.flowsheet.models.Flowsheet;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetConcept;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetConfig;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetEntities;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetMilestone;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class FlowsheetMapper {

    protected ObsDao obsDao;
    protected BahmniDrugOrderService bahmniDrugOrderService;
    protected ConceptService conceptService;

    protected FlowsheetConfig flowsheetConfig;
    protected Flowsheet flowsheet;
    protected String patientUuid;
    protected String patientProgramUuid;
    protected String[] conceptTypes;
    protected Date startDate;
    protected Date endDate;

    public FlowsheetMapper(ObsDao obsDao, BahmniDrugOrderService bahmniDrugOrderService, ConceptService conceptService) {
        this.obsDao = obsDao;
        this.bahmniDrugOrderService = bahmniDrugOrderService;
        this.conceptService = conceptService;
    }


    protected abstract void createFlowSheet() throws ParseException;

    public void map(Flowsheet flowsheet, FlowsheetConfig flowsheetConfig, String patientUuid, String patientProgramUuid, Date startDate, Date endDate) throws ParseException {
        this.flowsheet = flowsheet;
        this.flowsheetConfig = flowsheetConfig;
        this.patientUuid = patientUuid;
        this.patientProgramUuid = patientProgramUuid;
        this.startDate = startDate;
        this.endDate = endDate;

        createFlowSheet();
    }

    protected Set<Concept> getConceptObjects(Set<String> conceptNames) {
        Set<Concept> conceptsList = new HashSet<>();
        for (String concept : conceptNames) {
            conceptsList.add(conceptService.getConcept(concept));
        }
        return conceptsList;
    }

    protected boolean isSameDate(Date date1, Date date2) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date1).equals(dateFormat.format(date2));
    }

    protected Date dateWithAddedDays(Date date, Integer days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    protected Set<String> getAllConcepts() {
        Set<String> concepts = new HashSet<>();
        concepts.addAll(getAllSingleConceptsFromFlowsheetConfig());
        for (Map.Entry<String, Set<String>> entry : getAllGroupConceptsFromFlowsheetConfig().entrySet()) {
           concepts.addAll(entry.getValue());
        }
        return concepts;
    }

    protected Set<String> getAllSingleConceptsFromFlowsheetConfig() {
        Set<String> concepts = new HashSet<>();
        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            concepts.addAll(getSingleConceptsFromFlowsheetEntities(milestone.getFlowsheetEntities()));
        }
        concepts.addAll(getSingleConceptsFromFlowsheetEntities(flowsheetConfig.getFlowsheetEntities()));
        return concepts;
    }

    protected Set<String> getSingleConceptsFromFlowsheetEntities(FlowsheetEntities flowsheetEntities) {
        Set<String> singleConcepts = new LinkedHashSet<>();
        if(flowsheetEntities != null) {
            for(String type : conceptTypes) {
                singleConcepts.addAll(flowsheetEntities.getFlowSheetConceptByType(type).getSingleConcepts());
            }
        }
        return singleConcepts;
    }

    protected Map<String, Set<String>> getAllGroupConceptsFromFlowsheetConfig() {
        Map<String, Set<String>> concepts = new LinkedHashMap<>();
        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            concepts.putAll(getGroupConceptsFromFlowsheetEntities(milestone.getFlowsheetEntities()));
        }
        concepts.putAll(getGroupConceptsFromFlowsheetEntities(flowsheetConfig.getFlowsheetEntities()));
        return concepts;
    }

    protected Map<String, Set<String>> getGroupConceptsFromFlowsheetEntities(FlowsheetEntities flowsheetEntities) {
        Map<String, Set<String>> groupConcepts = new LinkedHashMap<>();
        if(flowsheetEntities != null) {
            for(String type : conceptTypes) {
                groupConcepts.putAll(flowsheetEntities.getFlowSheetConceptByType(type).getGroupConcepts());
            }
        }
        return groupConcepts;
    }

    protected FlowsheetConcept getFlowsheetConceptFromFlowsheetConfig() {
        FlowsheetConcept flowsheetConcept = new FlowsheetConcept();
        flowsheetConcept.setSingleConcepts(getAllSingleConceptsFromFlowsheetConfig());
        flowsheetConcept.setGroupConcepts(getAllGroupConceptsFromFlowsheetConfig());
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

    protected String getColorCodeForSingleConcepts(FlowsheetMilestone milestone, Boolean conceptRequiredForMilestone, Boolean conceptValueRecorded) throws ParseException {
        if (conceptRequiredForMilestone) {
            if (conceptValueRecorded) {
                return ColourCode.GREEN.getColourCode();
            } else if (dateWithAddedDays(startDate, milestone.getMax()).before(endDate) ||
                    (dateWithAddedDays(startDate, milestone.getMin()).before(endDate) && !isSameDate(endDate, new Date()))) {
                return ColourCode.PURPLE.getColourCode();
            } else {
                return ColourCode.YELLOW.getColourCode();
            }
        } else {
            return ColourCode.GREY.getColourCode();
        }
    }

    protected void createBasicFlowsheet() {
        FlowsheetConcept flowsheetConcept = getFlowsheetConceptFromFlowsheetConfig();
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
