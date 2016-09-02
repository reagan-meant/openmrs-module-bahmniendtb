package org.openmrs.module.endtb.flowsheet.service.impl;

import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.dao.impl.ObsDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Obs;
import org.openmrs.module.endtb.flowsheet.models.Flowsheet;
import org.openmrs.module.endtb.flowsheet.service.PatientMonitoringFlowsheetService;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientMonitoringFlowsheetServiceImplTest {
    private PatientMonitoringFlowsheetService patientMonitoringFlowsheetService;

    @Mock
    private ObsDao obsDao;

    @Before
    public void setUp() {
        initMocks(this);
        patientMonitoringFlowsheetService = new PatientMonitoringFlowsheetServiceImpl(obsDao);
    }

    @Test
    public void shouldReturnNullIfThereIsNoTreatmentStartDate() throws Exception {
        when(obsDao.getObsByPatientProgramUuidAndConceptNames(any(String.class), any(List.class), any(Integer.class), any(ObsDaoImpl.OrderBy.class), any(Date.class), any(Date.class))).thenReturn(null);
        Flowsheet actualFlowsheet = patientMonitoringFlowsheetService.getFlowsheetForPatientProgram("uuid", "src/test/resources/patientMonitoringConf.json");
        Flowsheet expectedFlowsheet = null;
        assertEquals(expectedFlowsheet, actualFlowsheet);
    }

    @Test
    public void shouldReturnEmptyFlowsheetIfThereIsNoObservationAfterTreatmentStartDate() throws Exception {
        Map<String, List<String>> flowsheetData = new LinkedHashMap<>();
        flowsheetData.put("Baseline, Prison", Arrays.asList("yellow","grey","grey"));
        flowsheetData.put("Height (cm)", Arrays.asList("yellow","yellow","yellow"));
        flowsheetData.put("Weight (cm)", Arrays.asList("yellow","yellow","yellow"));
        Obs obs = new Obs();
        obs.setValueDate(new Date());
        when(obsDao.getObsByPatientProgramUuidAndConceptNames(any(String.class), any(List.class), any(Integer.class), any(ObsDaoImpl.OrderBy.class), any(Date.class), any(Date.class))).thenReturn(Arrays.asList(obs)).thenReturn(null);
        Flowsheet actualFlowsheet = patientMonitoringFlowsheetService.getFlowsheetForPatientProgram("uuid", "src/test/resources/patientMonitoringConf.json");
        Flowsheet expectedFlowsheet = new Flowsheet();
        expectedFlowsheet.setFlowsheetHeader(Arrays.asList("M1", "M2", "M3"));
        expectedFlowsheet.setFlowsheetData(flowsheetData);
        assertEquals(expectedFlowsheet, actualFlowsheet);
    }
}