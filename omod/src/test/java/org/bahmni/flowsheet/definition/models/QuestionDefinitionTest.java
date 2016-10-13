package org.bahmni.flowsheet.definition.models;

import org.bahmni.flowsheet.api.QuestionType;
import org.bahmni.flowsheet.api.models.Question;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;

import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertNotNull;

public class QuestionDefinitionTest {

    @Mock
    Concept systolic;

    @Mock
    Concept diastolic;

    QuestionDefinition questionDefinition;


    @Before
    public void setUp()  {
        questionDefinition = new QuestionDefinition("Blood Pressure", new LinkedHashSet<>(Arrays.asList(systolic, diastolic)), QuestionType.OBS);
    }


    @Test
    public void shouldCreateQuestion() {
        Question question = questionDefinition.createQuestion();
        assertNotNull(question);
    }

}