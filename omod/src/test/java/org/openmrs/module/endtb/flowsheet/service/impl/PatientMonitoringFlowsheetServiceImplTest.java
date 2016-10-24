package org.openmrs.module.endtb.flowsheet.service.impl;

import org.bahmni.flowsheet.api.Evaluator;
import org.bahmni.flowsheet.api.QuestionType;
import org.bahmni.flowsheet.api.models.Flowsheet;
import org.bahmni.flowsheet.config.Config;
import org.bahmni.flowsheet.config.FlowsheetConfig;
import org.bahmni.flowsheet.config.MilestoneConfig;
import org.bahmni.flowsheet.config.QuestionConfig;
import org.bahmni.flowsheet.definition.HandlerProvider;
import org.bahmni.flowsheet.api.models.QuestionEvaluatorFactory;
import org.bahmni.flowsheet.definition.models.FlowsheetDefinition;
import org.bahmni.flowsheet.definition.models.MilestoneDefinition;
import org.bahmni.flowsheet.definition.models.QuestionDefinition;
import org.bahmni.flowsheet.ui.FlowsheetUI;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.dao.OrderDao;
import org.bahmni.module.bahmnicore.service.BahmniConceptService;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.OrderService;
import org.openmrs.module.endtb.flowsheet.service.PatientMonitoringFlowsheetService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientMonitoringFlowsheetServiceImplTest {
    private PatientMonitoringFlowsheetService patientMonitoringFlowsheetService;

    @Mock
    private ObsDao obsDao;
    @Mock
    private BahmniDrugOrderService bahmniDrugOrderService;
    @Mock
    private BahmniConceptService bahmniConceptService;
    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderService orderService;
    @Mock
    private HandlerProvider handlerProvider;
    @Mock
    private List<Evaluator> evaluatorList;
    @Mock
    private PatientProgram patientProgram;

    @Mock
    private QuestionEvaluatorFactory questionEvaluatorFactory;


    @Before
    public void setUp() {
        initMocks(this);
        patientMonitoringFlowsheetService = new PatientMonitoringFlowsheetServiceImpl(orderDao, obsDao, bahmniConceptService, handlerProvider, questionEvaluatorFactory);
        when(bahmniConceptService.getConceptByFullySpecifiedName("Systolic")).thenReturn(new Concept(1));
        when(bahmniConceptService.getConceptByFullySpecifiedName("Diastolic")).thenReturn(new Concept(2));
    }


    @Test
    public void shouldCreateFlowsheetDefinitionFromConfig() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = simpleDateFormat.parse("2016-1-10");
        FlowsheetConfig flowsheetConfig = new FlowsheetConfig();
        MilestoneConfig milestoneConfig = new MilestoneConfig();

        Config config = new Config();
        config.setMin("0");
        config.setMax("30");

        QuestionConfig questionConfig = new QuestionConfig();
        questionConfig.setName("Blood Pressure");
        questionConfig.setType("Obs");
        questionConfig.setConcepts(Arrays.asList("Systolic", "Diastolic"));

        milestoneConfig.setConfig(config);
        milestoneConfig.setName("MTx");
        milestoneConfig.setQuestionNames(Arrays.asList("Blood Pressure"));
        flowsheetConfig.setMilestoneConfigs(Arrays.asList(milestoneConfig));
        flowsheetConfig.setQuestionConfigs(Arrays.asList(questionConfig));
        FlowsheetDefinition flowsheetDefinition = patientMonitoringFlowsheetService.getFlowsheetDefinitionFromConfig(flowsheetConfig, startDate);

        Set<MilestoneDefinition> milestoneDefinitions = flowsheetDefinition.getMilestoneDefinitionList();
        MilestoneDefinition milestoneDefinition = milestoneDefinitions.iterator().next();
        QuestionDefinition questionDefinition = milestoneDefinition.getQuestionDefinitions().iterator().next();
        Map<String, String> config1 = milestoneDefinition.getConfig();

        assertEquals(startDate, flowsheetDefinition.getStartDate());
        assertEquals("MTx", milestoneDefinition.getName());
        assertEquals(handlerProvider, milestoneDefinition.getHandlerProvider());
        assertEquals("0", config1.get("min"));
        assertEquals("30", config1.get("max"));
        assertEquals("Blood Pressure", questionDefinition.getName());
        assertEquals(QuestionType.OBS, questionDefinition.getQuestionType());
        assertEquals(2, questionDefinition.getConcepts().size());
        verify(bahmniConceptService, times(2)).getConceptByFullySpecifiedName(anyString());
    }

    @Test
    public void shouldReturnEmptyFlowsheetWhenStartDateIsNotDefined() throws Exception {
        FlowsheetUI flowsheetUI = patientMonitoringFlowsheetService.getFlowsheetForPatientProgram(patientProgram, null, null, null);
        assertNotNull(flowsheetUI);
    }
}