package org.openmrs.module.endtb.flowsheet.mapper;

import org.apache.commons.lang3.time.DateUtils;
import org.bahmni.module.bahmnicore.service.BahmniConceptService;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;
import org.openmrs.module.endtb.flowsheet.constants.ColourCode;
import org.openmrs.module.endtb.flowsheet.models.Flowsheet;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetConcept;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetConfig;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetEntities;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetMilestone;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class FlowsheetMapper {

    protected FlowsheetConfig flowsheetConfig;
    protected Flowsheet flowsheet;
    protected String patientUuid;
    protected String patientProgramUuid;
    protected String[] conceptTypes;
    protected Date startDate;
    protected Date endDate;
    protected Map<String, String> fullySpecifiedNameToShortNameMap;


    protected abstract void createFlowSheet() throws ParseException;

    public void map(Flowsheet flowsheet, FlowsheetConfig flowsheetConfig, String patientUuid, String patientProgramUuid, Date startDate, Date endDate) throws ParseException {
        this.flowsheet = flowsheet;
        this.flowsheetConfig = flowsheetConfig;
        this.patientUuid = patientUuid;
        this.patientProgramUuid = patientProgramUuid;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fullySpecifiedNameToShortNameMap = new HashMap<>();

        createFlowSheet();
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
        concepts.addAll(getSingleConceptsFromFlowsheetEntities(flowsheetConfig.getFlowsheetEntities()));
        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            concepts.addAll(getSingleConceptsFromFlowsheetEntities(milestone.getFlowsheetEntities()));
        }
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
        concepts.putAll(getGroupConceptsFromFlowsheetEntities(flowsheetConfig.getFlowsheetEntities()));
        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            concepts.putAll(getGroupConceptsFromFlowsheetEntities(milestone.getFlowsheetEntities()));
        }
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
            } else if (DateUtils.addDays(startDate, milestone.getMax()).before(endDate) ||
                    (DateUtils.addDays(startDate, milestone.getMin()).before(endDate) && !DateUtils.isSameDay(endDate, new Date()))) {
                return ColourCode.PURPLE.getColourCode();
            } else {
                return ColourCode.YELLOW.getColourCode();
            }
        } else {
            return ColourCode.GREY.getColourCode();
        }
    }

    protected void createBasicFlowsheet(BahmniConceptService bahmniConceptService) {
        FlowsheetConcept flowsheetConcept = getFlowsheetConceptFromFlowsheetConfig();
        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            flowsheet.addFlowSheetHeader(milestone.getName());
        }
        Map<String, List<String>> flowsheetData = flowsheet.getFlowsheetData();
        for (String fullySpecifiedConceptName : flowsheetConcept.getSingleConcepts()) {
            ConceptName conceptShortName = bahmniConceptService.getConceptByFullySpecifiedName(fullySpecifiedConceptName).getShortNameInLocale(Context.getLocale());
            String shortName = null != conceptShortName ? conceptShortName.getName() : fullySpecifiedConceptName;
            fullySpecifiedNameToShortNameMap.put(fullySpecifiedConceptName, shortName);
            flowsheetData.put(shortName, new ArrayList<String>());
        }
        for (Map.Entry<String, Set<String>> entry : flowsheetConcept.getGroupConcepts().entrySet()) {
            flowsheetData.put(entry.getKey(), new ArrayList<String>());
        }
    }

    private FlowsheetConcept getFlowsheetConceptFromFlowsheetConfig() {
        FlowsheetConcept flowsheetConcept = new FlowsheetConcept();
        flowsheetConcept.setSingleConcepts(getAllSingleConceptsFromFlowsheetConfig());
        flowsheetConcept.setGroupConcepts(getAllGroupConceptsFromFlowsheetConfig());
        return flowsheetConcept;
    }
}
