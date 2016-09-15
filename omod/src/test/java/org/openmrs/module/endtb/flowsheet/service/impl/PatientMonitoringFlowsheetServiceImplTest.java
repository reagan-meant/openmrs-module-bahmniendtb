package org.openmrs.module.endtb.flowsheet.service.impl;

import org.bahmni.module.bahmnicore.dao.OrderDao;
import org.bahmni.module.bahmnicore.service.BahmniConceptService;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.OrderService;
import org.openmrs.module.endtb.bahmniCore.EndTbObsDaoImpl;
import org.openmrs.module.endtb.flowsheet.mapper.FlowsheetClinicalAndBacteriologyMapper;
import org.openmrs.module.endtb.flowsheet.mapper.FlowsheetDrugMapper;
import org.openmrs.module.endtb.flowsheet.mapper.FlowsheetMapper;
import org.openmrs.module.endtb.flowsheet.models.Flowsheet;
import org.openmrs.module.endtb.flowsheet.service.PatientMonitoringFlowsheetService;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientMonitoringFlowsheetServiceImplTest {
    private PatientMonitoringFlowsheetService patientMonitoringFlowsheetService;

    @Mock
    private EndTbObsDaoImpl endTbObsDao;
    @Mock
    private BahmniDrugOrderService bahmniDrugOrderService;
    @Mock
    private BahmniConceptService bahmniConceptService;
    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderService orderService;

    private FlowsheetClinicalAndBacteriologyMapper flowsheetObsMapper;
    private FlowsheetDrugMapper flowsheetDrugMapper;
    private List<FlowsheetMapper> flowsheetMappers;

    @Before
    public void setUp() {
        initMocks(this);
        flowsheetObsMapper = new FlowsheetClinicalAndBacteriologyMapper(endTbObsDao, bahmniDrugOrderService, bahmniConceptService);
        flowsheetDrugMapper = new FlowsheetDrugMapper(endTbObsDao, bahmniDrugOrderService, bahmniConceptService);
        flowsheetMappers = Arrays.asList(flowsheetObsMapper, flowsheetDrugMapper);
        patientMonitoringFlowsheetService = new PatientMonitoringFlowsheetServiceImpl(orderDao, endTbObsDao, flowsheetMappers, orderService, bahmniConceptService);
        when(bahmniConceptService.getConceptByFullySpecifiedName(any(String.class))).thenReturn(new Concept());
    }

    @Test
    public void shouldReturnFlowsheetStartDateAsNullForNoStartDateObs() throws Exception {
        Flowsheet actualFlowsheet = patientMonitoringFlowsheetService.getFlowsheetForPatientProgram("patientUuid", "programUuid", "src/test/resources/patientMonitoringConf.json");
        assertNull(actualFlowsheet.getStartDate());
    }

    @Test
    public void shouldReturnFlowsheetStartDateAsNullForNoStartDateForDrugs() throws Exception {
        Flowsheet actualFlowsheet = patientMonitoringFlowsheetService.getFlowsheetForPatientProgram("patientUuid", "programUuid", "src/test/resources/patientMonitoringConfWithStartDateDrugConcepts.json");
        assertNull(actualFlowsheet.getStartDate());
    }

    @Test(expected=APIException.class)
    public void shouldThrowExceptionIfBothStartDateObsAndDrugsIsConfigured() throws Exception {
        patientMonitoringFlowsheetService.getFlowsheetForPatientProgram("patientUuid", "programUuid", "src/test/resources/patientMonitoringConfWithBothStartDateObsAndDrugs.json");
    }

    @Test
    public void shouldReturnFlowsheetIfTreatmentStartDateTodayForObsAndDrug() throws Exception {
        Obs obs = new Obs();
        obs.setValueDate(new Date());
        when(endTbObsDao.getObsByPatientProgramUuidAndConceptNames(any(String.class), any(List.class), any(Integer.class), any(EndTbObsDaoImpl.OrderBy.class), any(Date.class), any(Date.class))).thenReturn(Arrays.asList(obs)).thenReturn(null);
        when(bahmniDrugOrderService.getDrugOrders(any(String.class), any(Boolean.class), any(Set.class), any(Set.class), any(String.class))).thenReturn(null);
        Flowsheet actualFlowsheet = patientMonitoringFlowsheetService.getFlowsheetForPatientProgram("patientUuid", "programUuid", "src/test/resources/patientMonitoringConf.json");
        Flowsheet expectedFlowsheet = getDummyFlowsheet();
        assertTrue(actualFlowsheet.equals(expectedFlowsheet));
    }

    @Test
    public void shouldReturnFlowsheetForBothSingleConceptsAndGroupConcepts() throws Exception {
        Flowsheet expectedFlowsheet = getDummyFlowsheet();
        expectedFlowsheet.getFlowsheetData().put("group1", Arrays.asList("yellow", "grey", "grey"));
        Obs obs = new Obs();
        obs.setValueDate(new Date());
        when(endTbObsDao.getObsByPatientProgramUuidAndConceptNames(any(String.class), any(List.class), any(Integer.class), any(EndTbObsDaoImpl.OrderBy.class), any(Date.class), any(Date.class))).thenReturn(Arrays.asList(obs)).thenReturn(null);
        when(bahmniDrugOrderService.getDrugOrders(any(String.class), any(Boolean.class), any(Set.class), any(Set.class), any(String.class))).thenReturn(null);
        Flowsheet actualFlowsheet = patientMonitoringFlowsheetService.getFlowsheetForPatientProgram("patientUuid", "programUuid", "src/test/resources/patientMonitoringConfWithGroupConcepts.json");
        assertTrue(actualFlowsheet.equals(expectedFlowsheet));
    }

    private Flowsheet getDummyFlowsheet() {
        Set<String> flowsheetHeader = new LinkedHashSet<>();
        flowsheetHeader.add("M1");
        flowsheetHeader.add("M2");
        flowsheetHeader.add("M3");
        Map<String, List<String>> flowsheetData = new LinkedHashMap<>();
        flowsheetData.put("Weight (kg)", Arrays.asList("yellow", "yellow", "yellow"));
        flowsheetData.put("Height (cm)", Arrays.asList("yellow", "yellow", "yellow"));
        flowsheetData.put("Baseline, Prison", Arrays.asList("yellow", "grey", "grey"));
        flowsheetData.put("Isoniazid", Arrays.asList("yellow", "grey", "grey"));
        flowsheetData.put("Delamanid", Arrays.asList("yellow", "yellow", "yellow"));
        flowsheetData.put("Bacteriology, Fluoroquinolone", Arrays.asList("yellow", "grey", "grey"));
        flowsheetData.put("Bacteriology, Culture results", Arrays.asList("yellow", "yellow", "yellow"));
        Flowsheet flowsheet = new Flowsheet();
        flowsheet.setFlowsheetHeader(flowsheetHeader);
        flowsheet.setFlowsheetData(flowsheetData);
        return flowsheet;
    }
}