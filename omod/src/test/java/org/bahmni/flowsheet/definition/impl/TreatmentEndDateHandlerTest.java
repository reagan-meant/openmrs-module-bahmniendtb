package org.bahmni.flowsheet.definition.impl;

import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.module.bahmniendtb.EndTBConstants;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TreatmentEndDateHandlerTest {

    @Mock
    ObsDao obsDao;

    @InjectMocks
    private TreatmentEndDateHandler treatmentEndDateHandler;

    private PatientProgram patientProgram;
    private String patientProgramUuid;

    @Before
    public void setup() {
        initMocks(this);
        patientProgramUuid = "patientProgramUuid";
        patientProgram = new PatientProgram();
        patientProgram.setUuid(patientProgramUuid);
    }

    @Test
    public void shouldReturnNullIfTreatmentEndDateNotPresent() {
        when(obsDao.getObsByPatientProgramUuidAndConceptNames(patientProgramUuid, Arrays.asList(EndTBConstants.EOT_STOP_DATE), null, null, null, null)).thenReturn(null);

        Date actualTreatmentStartDate = treatmentEndDateHandler.getDate(patientProgram);
        assertNull(actualTreatmentStartDate);
    }

    @Test
    public void shouldReturnDateWhenTreatmentEndDateIsPresentForPatientProgram() {
        Date today = new Date();
        Obs obs = new Obs();
        obs.setValueDate(today);

        when(obsDao.getObsByPatientProgramUuidAndConceptNames(patientProgramUuid, Arrays.asList(EndTBConstants.EOT_STOP_DATE), null, null, null, null)).thenReturn(Arrays.asList(obs));

        Date actualTreatmentStartDate = treatmentEndDateHandler.getDate(patientProgram);

        assertEquals(today, actualTreatmentStartDate);
    }
}