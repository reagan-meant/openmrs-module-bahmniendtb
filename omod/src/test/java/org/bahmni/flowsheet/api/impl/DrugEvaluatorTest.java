package org.bahmni.flowsheet.api.impl;

import org.bahmni.flowsheet.api.Status;
import org.bahmni.flowsheet.api.models.Result;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.module.bahmniemrapi.drugorder.contract.BahmniDrugOrder;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DrugEvaluatorTest {

    @Mock
    PatientProgram patientProgram;

    @Mock
    Date startDate, endDate;

    @Mock
    Concept delamanid;
    @Mock
    ConceptName delamanidConceptName;


    @Mock
    EncounterTransaction.Concept delamanidEtConcept;

    @Mock
    BahmniDrugOrder bahmniDrugOrder;


    @InjectMocks
    DrugEvaluator drugEvaluator;

    @Before
    public void setUp() {
        initMocks(this);

    }

    @Test
    public void shouldEvaluateStatusAsPendingWhenEndDateIsBeforeToday() throws ParseException {
        Patient patient = new Patient();
        patient.setUuid("patientUuid");
        when(patientProgram.getPatient()).thenReturn(patient);
        when(patientProgram.getUuid()).thenReturn("programUuid");
        when(endDate.before(any(Date.class))).thenReturn(true);

        Result result = drugEvaluator.evaluate(new HashSet<>(Arrays.asList(delamanid)),
                startDate, endDate);

        assertNotNull(result);
        assertEquals(Status.PENDING,result.getStatus());
    }

    @Test
    public void shouldEvaluateStatusAsPlannedWhenEndDateIsAfterToday() throws ParseException {
        Patient patient = new Patient();
        patient.setUuid("patientUuid");
        when(patientProgram.getPatient()).thenReturn(patient);
        when(patientProgram.getUuid()).thenReturn("programUuid");
        when(endDate.before(any(Date.class))).thenReturn(false);

        Result result = drugEvaluator.evaluate(new HashSet<>(Arrays.asList(delamanid)),
                startDate, endDate);

        assertNotNull(result);
        assertEquals(Status.PLANNED,result.getStatus());
    }


    @Test
    public void shouldEvaluateStatusAsDataAddedWhenDrugOrderIsPresentWithinStartDateAndEndDate() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = simpleDateFormat.parse("2016-1-10");
        Date endDate = simpleDateFormat.parse("2016-03-30");
        Date drugOrderStartDate = simpleDateFormat.parse("2016-03-30");
        Date drugOrderEndDate = simpleDateFormat.parse("2016-03-30");
        Patient patient = new Patient();
        patient.setUuid("patientUuid");


        when(delamanid.getName()).thenReturn(delamanidConceptName);
        when(bahmniDrugOrder.getEffectiveStartDate()).thenReturn(drugOrderStartDate);
        when(bahmniDrugOrder.getEffectiveStopDate()).thenReturn(drugOrderEndDate);
        when(bahmniDrugOrder.getConcept()).thenReturn(delamanidEtConcept);
        when(delamanidEtConcept.getName()).thenReturn("delamanid");
        when(delamanidConceptName.getName()).thenReturn("delamanid");
        when(patientProgram.getPatient()).thenReturn(patient);
        when(patientProgram.getUuid()).thenReturn("programUuid");

        drugEvaluator.setBahmniDrugOrders(Arrays.asList(bahmniDrugOrder));
        Result result = drugEvaluator.evaluate(new HashSet<>(Arrays.asList(delamanid)),
                startDate, endDate);

        assertNotNull(result);
        assertEquals(Status.DATA_ADDED,result.getStatus());
    }

}