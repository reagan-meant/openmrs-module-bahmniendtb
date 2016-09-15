package org.openmrs.module.endtb.flowsheet.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bahmni.module.bahmnicore.dao.OrderDao;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.BahmniPatientProgram;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.PatientProgramAttribute;
import org.bahmni.module.bahmnicore.service.BahmniConceptService;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.OrderService;
import org.openmrs.module.bahmniendtb.EndTBConstants;
import org.openmrs.module.endtb.bahmniCore.EndTbObsDaoImpl;
import org.openmrs.module.endtb.flowsheet.mapper.FlowsheetMapper;
import org.openmrs.module.endtb.flowsheet.models.Flowsheet;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetAttribute;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetConfig;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetMilestone;
import org.openmrs.module.endtb.flowsheet.service.PatientMonitoringFlowsheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PatientMonitoringFlowsheetServiceImpl implements PatientMonitoringFlowsheetService {

    private OrderDao orderDao;
    private EndTbObsDaoImpl endTbObsDao;
    private List<FlowsheetMapper> flowsheetMappers;
    private OrderService orderService;
    private BahmniConceptService bahmniConceptService;

    @Autowired
    public PatientMonitoringFlowsheetServiceImpl(OrderDao orderDao, EndTbObsDaoImpl endTbObsDao, List<FlowsheetMapper> flowsheetMappers, OrderService orderService, BahmniConceptService bahmniConceptService) {
        this.endTbObsDao = endTbObsDao;
        this.flowsheetMappers = flowsheetMappers;
        this.orderDao = orderDao;
        this.orderService = orderService;
        this.bahmniConceptService = bahmniConceptService;
    }

    @Override
    public Flowsheet getFlowsheetForPatientProgram(String patientUuid, String patientProgramUuid, String configFilePath) throws Exception {
        Flowsheet flowsheet = new Flowsheet();
        FlowsheetConfig flowsheetConfig = getPatientMonitoringFlowsheetConfiguration(configFilePath);

        if(flowsheetConfig.getStartDateConcept()!=null && CollectionUtils.isNotEmpty(flowsheetConfig.getStartDateDrugConcepts())) {
            throw new APIException("Start Date Concept And Start Date Drugs Concepts Both Are Configured. Please Remove Any One.");
        }

        Date startDate = getFlowsheetStartDate(flowsheetConfig, patientProgramUuid);
        Date endDate = getFlowsheetEndDate(flowsheetConfig, patientProgramUuid);

        flowsheet.setStartDate(startDate);
        for (FlowsheetMapper flowsheetMapper : flowsheetMappers) {
            flowsheetMapper.map(flowsheet, flowsheetConfig, patientUuid, patientProgramUuid, startDate, endDate);
        }
        flowsheet.setCurrentMilestoneName(findCurrentMilestone(flowsheetConfig, startDate, endDate));
        return flowsheet;
    }

    @Override
    public FlowsheetAttribute getFlowsheetAttributesForPatientProgram(BahmniPatientProgram bahmniPatientProgram, PatientIdentifierType primaryIdentifierType, OrderType orderType, Set<Concept> concepts) {
        FlowsheetAttribute flowsheetAttribute = new FlowsheetAttribute();
        List<Obs> startDateConceptObs = endTbObsDao.getObsByPatientProgramUuidAndConceptNames(bahmniPatientProgram.getUuid(), Arrays.asList(EndTBConstants.TI_TREATMENT_START_DATE), null, null, null, null);
        Date startDate = null;
        if (CollectionUtils.isNotEmpty(startDateConceptObs)) {
            startDate = startDateConceptObs.get(0).getValueDate();
        }
        Date newDrugTreatmentStartDate = getNewDrugTreatmentStartDate(bahmniPatientProgram.getUuid(), orderType, concepts);
        flowsheetAttribute.setNewDrugTreatmentStartDate(newDrugTreatmentStartDate);
        flowsheetAttribute.setMdrtbTreatmentStartDate(startDate);
        flowsheetAttribute.setTreatmentRegistrationNumber(getProgramAttribute(bahmniPatientProgram, EndTBConstants.PROGRAM_ATTRIBUTE_REG_NO));
        flowsheetAttribute.setPatientEMRID(bahmniPatientProgram.getPatient().getPatientIdentifier(primaryIdentifierType).getIdentifier());
        return flowsheetAttribute;
    }

    private Date getFlowsheetStartDate(FlowsheetConfig flowsheetConfig, String patientProgramUuid) {
        Date startDate = null;
        if(flowsheetConfig.getStartDateConcept() != null) {
            List<Obs> startDateConceptObs = endTbObsDao.getObsByPatientProgramUuidAndConceptNames(patientProgramUuid, Arrays.asList(flowsheetConfig.getStartDateConcept()), null, null, null, null);
            if (CollectionUtils.isNotEmpty(startDateConceptObs)) {
                startDate = startDateConceptObs.get(0).getValueDate();
            }
        } else if(CollectionUtils.isNotEmpty(flowsheetConfig.getStartDateDrugConcepts())) {
            OrderType orderType = orderService.getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);
            startDate = getNewDrugTreatmentStartDate(patientProgramUuid, orderType, getConceptObjects(flowsheetConfig.getStartDateDrugConcepts()));
        }
        return startDate;
    }

    private Date getFlowsheetEndDate(FlowsheetConfig flowsheetConfig, String patientProgramUuid) {
        Date endDate = null;
        List<Obs> endDateConceptObs = endTbObsDao.getObsByPatientProgramUuidAndConceptNames(patientProgramUuid, Arrays.asList(flowsheetConfig.getEndDateConcept()), null, null, null, null);
        if(CollectionUtils.isNotEmpty(endDateConceptObs)) {
            endDate = endDateConceptObs.get(0).getValueDatetime();
        }
        return endDate;
    }

    private Set<Concept> getConceptObjects(Set<String> conceptNames) {
        Set<Concept> conceptsList = new HashSet<>();
        for (String concept : conceptNames) {
            conceptsList.add(bahmniConceptService.getConceptByFullySpecifiedName(concept));
        }
        return conceptsList;
    }

    private Date getNewDrugTreatmentStartDate(String patientProgramUuid, OrderType orderType, Set<Concept> concepts) {
        List<Order> orders = orderDao.getOrdersByPatientProgram(patientProgramUuid, orderType, concepts);
        if (orders.size() > 0) {
            Order firstOrder = orders.get(0);
            Date newDrugTreatmentStartDate = firstOrder.getScheduledDate() != null ? firstOrder.getScheduledDate() : firstOrder.getDateActivated();
            for (Order order : orders) {
                Date toCompare = order.getScheduledDate() != null ? order.getScheduledDate() : order.getDateActivated();
                if (newDrugTreatmentStartDate.compareTo(toCompare) > 0) {
                    newDrugTreatmentStartDate = toCompare;
                }
            }
            return newDrugTreatmentStartDate;
        }
        return null;
    }

    private String getProgramAttribute(BahmniPatientProgram bahmniPatientProgram, String attribute) {
        for (PatientProgramAttribute patientProgramAttribute : bahmniPatientProgram.getActiveAttributes()) {
            if (patientProgramAttribute.getAttributeType().getName().equals(attribute))
                return patientProgramAttribute.getValueReference();
        }
        return "";
    }

    private String findCurrentMilestone(FlowsheetConfig flowsheetConfig, Date startDate, Date endDate) {
        String currentMilestone = "";
        if(null != startDate && CollectionUtils.isNotEmpty(flowsheetConfig.getFlowsheetMilestones())) {
            for(FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
                if(DateUtils.addDays(startDate, milestone.getMin()).before(endDate) &&
                        DateUtils.addDays(startDate, milestone.getMax()).after(endDate)) {
                    currentMilestone = milestone.getName();
                    break;
                }
            }
        }
        return currentMilestone;
    }

    private FlowsheetConfig getPatientMonitoringFlowsheetConfiguration(String configFilePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FlowsheetConfig flowsheetConfig = mapper.readValue(new File(configFilePath), FlowsheetConfig.class);
        return flowsheetConfig;
    }

}
