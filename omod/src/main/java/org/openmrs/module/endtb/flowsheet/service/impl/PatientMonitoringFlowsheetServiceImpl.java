package org.openmrs.module.endtb.flowsheet.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.openmrs.Obs;
import org.openmrs.module.endtb.flowsheet.mapper.FlowsheetMapper;
import org.openmrs.module.endtb.flowsheet.models.Flowsheet;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetConfig;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetMilestone;
import org.openmrs.module.endtb.flowsheet.service.PatientMonitoringFlowsheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class PatientMonitoringFlowsheetServiceImpl implements PatientMonitoringFlowsheetService {

    private ObsDao obsDao;
    private List<FlowsheetMapper> flowsheetMappers;

    @Autowired
    public PatientMonitoringFlowsheetServiceImpl(ObsDao obsDao, List<FlowsheetMapper> flowsheetMappers) {
        this.obsDao = obsDao;
        this.flowsheetMappers = flowsheetMappers;
    }

    @Override
    public Flowsheet getFlowsheetForPatientProgram(String patientUuid, String patientProgramUuid, String configFilePath) throws Exception {
        Flowsheet flowsheet = new Flowsheet();
        FlowsheetConfig flowsheetConfig = getPatientMonitoringFlowsheetConfiguration(configFilePath);
        List<Obs> startDateConceptObs = obsDao.getObsByPatientProgramUuidAndConceptNames(patientProgramUuid, Arrays.asList(flowsheetConfig.getStartDateConcept()), null, null, null, null);
        Date startDate = null;
        if (CollectionUtils.isNotEmpty(startDateConceptObs)) {
            startDate = startDateConceptObs.get(0).getValueDate();
        }
        List<Obs> endDateConceptObs = obsDao.getObsByPatientProgramUuidAndConceptNames(patientProgramUuid, Arrays.asList(flowsheetConfig.getEndDateConcept()), null, null, null, null);
        Date endDate = new Date();
        if(CollectionUtils.isNotEmpty(endDateConceptObs)) {
            endDate = endDateConceptObs.get(0).getValueDatetime();
        }

        flowsheet.setStartDate(startDate);
        for (FlowsheetMapper flowsheetMapper : flowsheetMappers) {
            flowsheetMapper.map(flowsheet, flowsheetConfig, patientUuid, patientProgramUuid, startDate, endDate);
        }
        flowsheet.setCurrentMilestoneName(findCurrentMilestone(flowsheetConfig, startDate, endDate));
        return flowsheet;
    }

    private String findCurrentMilestone(FlowsheetConfig flowsheetConfig, Date startDate, Date endDate) {
        String currentMilestone = "";
        if(null != startDate && CollectionUtils.isNotEmpty(flowsheetConfig.getFlowsheetMilestones())) {
            for(FlowsheetMilestone milestone : flowsheetConfig.getFlowsheetMilestones()) {
                if(dateWithAddedDays(startDate, milestone.getMin()).before(endDate) &&
                        dateWithAddedDays(startDate, milestone.getMax()).after(endDate)) {
                    currentMilestone = milestone.getName();
                    break;
                }
            }
        }
        return currentMilestone;
    }

    protected Date dateWithAddedDays(Date date, Integer days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    private FlowsheetConfig getPatientMonitoringFlowsheetConfiguration(String configFilePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FlowsheetConfig flowsheetConfig = mapper.readValue(new File(configFilePath), FlowsheetConfig.class);
        return flowsheetConfig;
    }

}
