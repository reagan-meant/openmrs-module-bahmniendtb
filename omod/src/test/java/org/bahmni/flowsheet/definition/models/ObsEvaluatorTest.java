package org.bahmni.flowsheet.definition.models;

import org.bahmni.flowsheet.api.Status;
import org.bahmni.flowsheet.api.impl.ObsEvaluator;
import org.bahmni.flowsheet.api.models.Result;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class ObsEvaluatorTest {

    @Mock
    PatientProgram patientProgram;

    @Mock
    ObsDao obsDao;

    @Mock
    Date startDate, endDate;

    @Mock
    Concept height;

    @InjectMocks
    ObsEvaluator obsEvaluator;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldEvaluateStatusToPendingWhenEndDateOfMilestoneIsInPast() throws ParseException {
        ConceptName heightConceptName = new ConceptName();
        heightConceptName.setName("height");
        Patient patient = new Patient();
        patient.setUuid("patientUuid");

        when(patientProgram.getPatient()).thenReturn(patient);
        when(patientProgram.getUuid()).thenReturn("programUuid");
        when(endDate.before(any(Date.class))).thenReturn(true);
        when(height.getName()).thenReturn(heightConceptName);

        Result result = obsEvaluator.evaluate(new HashSet<>(Arrays.asList(height)),
                startDate, endDate);

        assertNotNull(result);
        assertEquals(Status.PENDING, result.getStatus());

    }

    @Test
    public void shouldEvaluateStatusToPlannedWhenEndDateIsAfterToday() throws ParseException {
        ConceptName heightConceptName = new ConceptName();
        heightConceptName.setName("height");
        Patient patient = new Patient();
        patient.setUuid("patientUuid");
        when(patientProgram.getPatient()).thenReturn(patient);
        when(patientProgram.getUuid()).thenReturn("programUuid");
        when(endDate.before(any(Date.class))).thenReturn(false);
        when(height.getName()).thenReturn(heightConceptName);

        Result result = obsEvaluator.evaluate(new HashSet<>(Arrays.asList(height)),
                startDate, endDate);

        assertNotNull(result);
        assertEquals(Status.PLANNED, result.getStatus());

    }

    @Test
    public void shouldEvaluateStatusToDataAddedWhenObsPresentWithinMilestoneRange() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = simpleDateFormat.parse("2016-1-10");
        Date endDate = simpleDateFormat.parse("2016-03-30");
        ConceptName heightConceptName = new ConceptName();
        heightConceptName.setName("height");
        Patient patient = new Patient();
        patient.setUuid("patientUuid");
        Obs obs1 = new Obs();
        obs1.setObsDatetime(simpleDateFormat.parse("2016-4-10"));
        obs1.setConcept(height);

        Obs obs2 = new Obs();
        obs2.setObsDatetime(simpleDateFormat.parse("2016-2-10"));
        obs2.setConcept(height);

        when(patientProgram.getPatient()).thenReturn(patient);
        when(patientProgram.getUuid()).thenReturn("programUuid");
        when(height.getName()).thenReturn(heightConceptName);

        obsEvaluator.setObsList(Arrays.asList(obs1, obs2));
        Result result = obsEvaluator.evaluate(new HashSet<>(Arrays.asList(height)),
                startDate, endDate);

        assertNotNull(result);
        assertEquals(Status.DATA_ADDED, result.getStatus());
    }

}