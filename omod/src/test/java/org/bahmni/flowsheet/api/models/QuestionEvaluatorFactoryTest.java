package org.bahmni.flowsheet.api.models;

import org.bahmni.flowsheet.api.Evaluator;
import org.bahmni.flowsheet.api.QuestionType;
import org.bahmni.flowsheet.api.impl.DrugEvaluator;
import org.bahmni.flowsheet.api.impl.ObsEvaluator;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.dao.impl.ObsDaoImpl;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.*;
import org.openmrs.module.bahmniemrapi.drugorder.contract.BahmniDrugOrder;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class QuestionEvaluatorFactoryTest {


    @Mock
    BahmniDrugOrderService bahmniDrugOrderService;

    @Mock
    ObsDao obsDao;

    @Mock
    PatientProgram patientProgram;

    @Mock
    Concept systolic, diastolic;


    @Mock
    ConceptName systolicName, diastolicName;

    @InjectMocks
    QuestionEvaluatorFactory questionEvaluatorFactory;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldGetDrugEvaluatorIfQuestionTypeIsDrug() {
        Evaluator evaluator = questionEvaluatorFactory.getEvaluator(QuestionType.DRUG);
        assertEquals(DrugEvaluator.class, evaluator.getClass());
        assertNotNull(evaluator);
    }

    @Test
    public void shouldGetObsEvaluatorIfQuestionTypeIsObs() {
        Evaluator evaluator = questionEvaluatorFactory.getEvaluator(QuestionType.OBS);
        assertEquals(ObsEvaluator.class, evaluator.getClass());
        assertNotNull(evaluator);
    }

    @Test
    public void shouldSetObsListForObsEvaluatorAndDrugOrderListForDrugEvaluator() throws ParseException {
        Obs obs1 = new Obs();
        Obs obs2 = new Obs();

        BahmniDrugOrder bahmniDrugOrder = new BahmniDrugOrder();
        Patient patient = new Patient();
        patient.setUuid("patientUuid");

        when(obsDao.getObsByPatientProgramUuidAndConceptNames(eq("patientProgramUuid"), anyListOf(String.class), anyInt(), any(ObsDaoImpl.OrderBy.class), any(Date.class), any(Date.class)))
                .thenReturn(Arrays.asList(obs1, obs2));
        when(bahmniDrugOrderService.getDrugOrders("patientUuid", null, null, null, "patientProgramUuid")).thenReturn(Arrays.asList(bahmniDrugOrder));
        when(patientProgram.getUuid()).thenReturn("patientProgramUuid");
        when(patientProgram.getPatient()).thenReturn(patient);
        when(systolic.getName()).thenReturn(systolicName);
        when(diastolic.getName()).thenReturn(diastolicName);

        questionEvaluatorFactory.init(
                patientProgram, new HashSet<>(Arrays.asList(systolic, diastolic)));
        ObsEvaluator obsEvaluator = (ObsEvaluator) questionEvaluatorFactory.getEvaluator(QuestionType.OBS);
        DrugEvaluator drugEvaluator = (DrugEvaluator) questionEvaluatorFactory.getEvaluator(QuestionType.DRUG);

        assertEquals(2, obsEvaluator.getObsList().size());
        assertEquals(1, drugEvaluator.getBahmniDrugOrders().size());
    }

}