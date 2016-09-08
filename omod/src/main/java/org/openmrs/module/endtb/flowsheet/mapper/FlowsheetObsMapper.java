package org.openmrs.module.endtb.flowsheet.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.module.endtb.flowsheet.constants.ColourCode;
import org.openmrs.module.endtb.flowsheet.constants.FlowsheetContant;
import org.openmrs.module.endtb.flowsheet.models.Flowsheet;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetEntities;
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
public class FlowsheetObsMapper extends FlowsheetMapper {

    @Autowired
    public FlowsheetObsMapper(ObsDao obsDao, BahmniDrugOrderService bahmniDrugOrderService, ConceptService conceptService) {
        super(obsDao, bahmniDrugOrderService, conceptService);
    }

    @Override
    public void map(Flowsheet flowsheet, FlowsheetConfig flowsheetConfig, String patientUuid, String patientProgramUuid, Date startDate) throws ParseException {
        Set<String> allObsConcepts = getAllUniqueFlowsheetConcepts(flowsheetConfig, FlowsheetContant.CLINICAL);
        createBasicFlowsheet(flowsheet, flowsheetConfig, allObsConcepts);
        if (startDate == null) {
            return;
        }
        List<Obs> obsList = obsDao.getObsByPatientProgramUuidAndConceptNames(patientProgramUuid, new ArrayList<String>(allObsConcepts), null, null, startDate, null);
        Map<String, List<Obs>> conceptToObsMap = getConceptToObsMap(obsList);

        Set<String> commonConcepts = getAllConceptsFromFlowsheetConcepts(flowsheetConfig.getFlowsheetEntities());
        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            Set<String> milestoneConcepts = getAllConceptsFromFlowsheetConcepts(milestone.getFlowsheetEntities());
            for (String concept : allObsConcepts) {
                setObsMilestoneColourCode(flowsheet, commonConcepts, milestoneConcepts, milestone, concept, conceptToObsMap.get(concept), startDate);
            }
        }
    }

    private Map<String, List<Obs>> getConceptToObsMap(List<Obs> obsList) {
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

    private Set<String> getAllConceptsFromFlowsheetConcepts(FlowsheetEntities flowsheetEntities) {
        Set<String> concepts = new HashSet<>();
        if (flowsheetEntities != null) {
            concepts.addAll(getAllFlowsheetConcepts(flowsheetEntities.getClinicalConcepts()));
        }
        return concepts;
    }

    private void setObsMilestoneColourCode(Flowsheet flowsheet, Set<String> flowsheetCommonConcepts,
                                           Set<String> milestoneConcepts, FlowsheetMilestone milestone,
                                           String concept, List<Obs> obsList, Date startDate) {
        if (flowsheetCommonConcepts.contains(concept) || milestoneConcepts.contains(concept)) {
            if (isConceptPresentInMilestoneRange(milestone, startDate, obsList)) {
                flowsheet.addFlowSheetData(concept, ColourCode.GREEN.getColourCode());
            } else if (dateWithAddedDays(startDate, milestone.getMax()).before(new Date())) {
                flowsheet.addFlowSheetData(concept, ColourCode.PURPLE.getColourCode());
            } else {
                flowsheet.addFlowSheetData(concept, ColourCode.YELLOW.getColourCode());
            }
        } else {
            flowsheet.addFlowSheetData(concept, ColourCode.GREY.getColourCode());
        }
    }

    private boolean isConceptPresentInMilestoneRange(FlowsheetMilestone milestone, Date startDate, List<Obs> obsList) {
        if (CollectionUtils.isNotEmpty(obsList)) {
            for (Obs obs : obsList) {
                if (obs.getObsDatetime().after(dateWithAddedDays(startDate, milestone.getMin())) && obs.getObsDatetime().before(dateWithAddedDays(startDate, milestone.getMax()))) {
                    obsList.remove(obs);
                    return true;
                }
            }
        }
        return false;
    }
}
