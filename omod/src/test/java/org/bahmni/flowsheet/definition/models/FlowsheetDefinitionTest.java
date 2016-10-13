package org.bahmni.flowsheet.definition.models;

import org.bahmni.flowsheet.api.QuestionType;
import org.bahmni.flowsheet.api.models.Flowsheet;
import org.bahmni.flowsheet.definition.HandlerProvider;
import org.bahmni.flowsheet.api.models.Milestone;
import org.bahmni.flowsheet.api.models.Question;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.PatientProgram;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openmrs.module.bahmniendtb.EndTBConstants.*;

public class FlowsheetDefinitionTest {

    @Mock
    Concept systolic;

    @Mock
    Concept diastolic;

    @Mock
    HandlerProvider handlerProvider;


    @InjectMocks
    MilestoneDefinition milestoneDefinition;

    private FlowsheetDefinition flowsheetDefinition;
    private SimpleDateFormat simpleDateFormat;

    @Before
    public void setup() throws ParseException {
        initMocks(this);
        QuestionDefinition questionDefinition = new QuestionDefinition("Blood Pressure", new LinkedHashSet<>(Arrays.asList(systolic, diastolic)), QuestionType.OBS);

        Map<String, String> config = new HashMap<>();
        config.put("min", "0");
        config.put("max", "30");

        milestoneDefinition.setName("M1");
        milestoneDefinition.setConfig(config);
        milestoneDefinition.setQuestionDefinitions(new LinkedHashSet<>(Arrays.asList(questionDefinition)));

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse("2016-10-10");
        Set<MilestoneDefinition> milestoneDefinitions = new LinkedHashSet<>();
        milestoneDefinitions.add(milestoneDefinition);
        Set<QuestionDefinition> questionDefinitions = new LinkedHashSet<>();
        questionDefinitions.add(questionDefinition);

        flowsheetDefinition = new FlowsheetDefinition(date, milestoneDefinitions);
    }

    @Test
    public void shouldCreateFlowsheetFromDefinition() throws ParseException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Flowsheet flowsheet = flowsheetDefinition.createFlowsheet(new PatientProgram());
        Date date = simpleDateFormat.parse("2016-10-10");
        Set<Milestone> milestones = flowsheet.getMilestones();

        assertEquals(date, flowsheet.getStartDate());
        assertEquals(1, milestones.size());
    }

}