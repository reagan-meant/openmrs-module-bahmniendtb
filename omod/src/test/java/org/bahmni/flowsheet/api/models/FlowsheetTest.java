package org.bahmni.flowsheet.api.models;

import org.bahmni.flowsheet.api.QuestionType;
import org.bahmni.flowsheet.api.Status;
import org.bahmni.flowsheet.api.impl.DrugEvaluator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.PatientProgram;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class FlowsheetTest {

    @Mock
    Milestone baseline;

    @Mock
    Question question;

    @Mock
    PatientProgram patientProgram;

    @Mock
    QuestionEvaluatorFactory questionEvaluatorFactory;

    @Mock
    DrugEvaluator drugEvaluator;

    @Mock
    Concept concept;

    @Mock
    Date startDate, endDate;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldEvaluateItself() {
        Flowsheet flowsheet = new Flowsheet();

        flowsheet.setMilestones(new LinkedHashSet<>(Arrays.asList(baseline)));

        Set<Concept> concepts = new LinkedHashSet<>(Arrays.asList(concept));

        when(baseline.getQuestions()).thenReturn(new LinkedHashSet<>(Arrays.asList(question)));
        when(baseline.getStartDate()).thenReturn(startDate);
        when(baseline.getEndDate()).thenReturn(endDate);
        when(question.getType()).thenReturn(QuestionType.DRUG);
        when(question.getConcepts()).thenReturn(concepts);

        when(questionEvaluatorFactory.getEvaluator(QuestionType.DRUG)).thenReturn(drugEvaluator);
        when(drugEvaluator.evaluate(concepts, startDate, endDate)).thenReturn(new Result(Status.DATA_ADDED));
        flowsheet.evaluate(questionEvaluatorFactory);

        verify(drugEvaluator).evaluate(concepts, startDate, endDate);
    }

    @Test
    public void shouldNotThrowExceptionForMilestonesWithEmptyQuestions() {
        Flowsheet flowsheet = new Flowsheet();
        flowsheet.setMilestones(new LinkedHashSet<>(Arrays.asList(baseline)));

        flowsheet.evaluate(questionEvaluatorFactory);

        assertNotNull(baseline.getQuestions());
    }
}