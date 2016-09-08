package org.openmrs.module.endtb.flowsheet.mapper;

import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.account.StringToAccountDomainWrapperConverter;
import org.openmrs.module.endtb.flowsheet.models.Flowsheet;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetConcepts;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetConfig;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetMilestone;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

    protected FlowsheetConcepts getUniqueFlowsheetConcepts(FlowsheetConfig flowsheetConfig) {
        FlowsheetConcepts flowsheetConcepts = new FlowsheetConcepts();
        for (FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
            if (milestone.getFlowsheetConcepts() != null) {
                flowsheetConcepts.getClinicalConcepts().addAll(milestone.getFlowsheetConcepts().getClinicalConcepts());
                flowsheetConcepts.getBacteriologyConcepts().addAll(milestone.getFlowsheetConcepts().getBacteriologyConcepts());
                flowsheetConcepts.getDrugConcepts().addAll(milestone.getFlowsheetConcepts().getDrugConcepts());
            }
        }
        if (flowsheetConfig.getFlowsheetConcepts() != null) {
            flowsheetConcepts.getClinicalConcepts().addAll(flowsheetConfig.getFlowsheetConcepts().getClinicalConcepts());
            flowsheetConcepts.getBacteriologyConcepts().addAll(flowsheetConfig.getFlowsheetConcepts().getBacteriologyConcepts());
            flowsheetConcepts.getDrugConcepts().addAll(flowsheetConfig.getFlowsheetConcepts().getDrugConcepts());
        }
        return flowsheetConcepts;
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
